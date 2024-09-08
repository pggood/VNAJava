package krause.vna.device;

import krause.common.exception.ProcessingException;
import krause.util.file.TimestampControlledFile;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.filter.VNABaseFilterHelper;
import krause.vna.gui.format.VNAFormatFactory;
import org.apache.commons.math3.complex.Complex;

public abstract class VNADriverMathBaseHelper implements IVNADriverMathHelper {
   public static final double PHASE_FULL = 360.0D;
   public static final double PHASE_HALF = 180.0D;
   public static final double PI_HALF = 1.5707963267948966D;
   public static final double PHASE_SWITCH = 170.0D;
   public static final double PHASE_MINDIFF = 0.02D;
   public static final double ONE_GHZ = 1.0E9D;
   public static final Complex C_1 = new Complex(1.0D, 0.0D);
   public static final double R_MINUS_ONE = -1.0D;
   public static final double R_PLUS_ONE = 1.0D;
   public static final double R_ZERO = 0.0D;
   public static final double SOL = 2.99792458E8D;
   private IVNADriver driver;
   private VNAConfig config = VNAConfig.getSingleton();
   private TimestampControlledFile gaussianFilterFile = null;
   private double[] gaussianFilter = null;

   public VNADriverMathBaseHelper(IVNADriver driver) {
      TraceHelper.entry(this, "VNADriverMathBaseHelper");
      this.driver = driver;
      this.gaussianFilterFile = new TimestampControlledFile(this.config.getGaussianFilterFileName());
      TraceHelper.exit(this, "VNADriverMathBaseHelper");
   }

   public VNACalibratedSampleBlock createCalibratedSamples(VNACalibrationContext context, VNASampleBlock raw) {
      TraceHelper.entry(this, "createCalibratedSamples");
      context.setConversionTemperature(raw.getDeviceTemperature());
      int listLength = raw.getSamples().length;
      VNACalibrationBlock calBlock = context.getCalibrationBlock();
      TraceHelper.text(this, "createCalibratedSamples", "calibration data created at temp=" + context.getCalibrationTemperature());
      TraceHelper.text(this, "createCalibratedSamples", "raw data created at temp=" + context.getConversionTemperature());
      if (this.config.isPortExtensionEnabled()) {
         context.setPortExtensionPhaseConstant(720.0D * this.config.getPortExtensionCableLength() / (2.99792458E8D * this.config.getPortExtensionVf()));
         TraceHelper.text(this, "createCalibratedSamples", "port extension will be enabled with constant " + context.getPortExtensionPhaseConstant());
      }

      VNACalibratedSampleBlock rc = new VNACalibratedSampleBlock(listLength);

      for(int i = 0; i < listLength; ++i) {
         VNACalibratedSample s = this.createCalibratedSample(context, raw.getSamples()[i], calBlock.getCalibrationPoints()[i]);
         this.postProcessCalibratedSample(s, context);
         rc.consumeCalibratedSample(s, i);
      }

      this.postProcessCalibratedSamples(rc, context);
      TraceHelper.exit(this, "createCalibratedSamples");
      return rc;
   }

   private void postProcessCalibratedSamples(VNACalibratedSampleBlock csb, VNACalibrationContext context) {
      TraceHelper.entry(this, "postProcessCalibratedSamples");
      VNACalibratedSample[] samples = csb.getCalibratedSamples();
      int len = samples.length;
      if (len > 1) {
         VNAMinMaxPair mmGroupDelay = csb.getMmGRPDLY();
         double diffFreq = 1.0D * (double)samples[1].getFrequency() - (double)samples[0].getFrequency();
         TraceHelper.text(this, "postProcessCalibratedSamples", "Hz/step=" + VNAFormatFactory.getFrequencyFormat().format(diffFreq));
         VNACalibratedSample lastSample = samples[0];
         lastSample.setGroupDelay(0.0D);
         int i;
         VNACalibratedSample currentSample;
         double lastPhase;
         double currentPhase;
         double diffPhase;
         double groupDelay;
         if (context.getScanMode().isTransmissionMode()) {
            for(i = 1; i < len; ++i) {
               currentSample = samples[i];
               lastPhase = lastSample.getTransmissionPhase();
               currentPhase = currentSample.getTransmissionPhase();
               diffPhase = currentPhase - lastPhase;
               if (diffPhase > 170.0D) {
                  diffPhase -= 360.0D;
               } else if (diffPhase < -170.0D) {
                  diffPhase += 360.0D;
               }

               groupDelay = -0.002777777777777778D * (diffPhase / diffFreq) * 1.0E9D;
               currentSample.setGroupDelay(groupDelay);
               mmGroupDelay.consume(groupDelay, i);
               lastSample = currentSample;
            }
         } else if (context.getScanMode().isReflectionMode()) {
            for(i = 1; i < len; ++i) {
               currentSample = samples[i];
               lastPhase = lastSample.getReflectionPhase();
               currentPhase = currentSample.getReflectionPhase();
               diffPhase = currentPhase - lastPhase;
               if (diffPhase > 170.0D) {
                  diffPhase -= 360.0D;
               } else if (diffPhase < -170.0D) {
                  diffPhase += 360.0D;
               }

               groupDelay = -0.002777777777777778D * (diffPhase / diffFreq) * 1.0E9D;
               currentSample.setGroupDelay(groupDelay);
               mmGroupDelay.consume(groupDelay, i);
               lastSample = currentSample;
            }
         }
      }

      TraceHelper.exit(this, "postProcessCalibratedSamples");
   }

   private void postProcessCalibratedSample(VNACalibratedSample sample, VNACalibrationContext context) {
      if (!context.getScanMode().isTransmissionMode() && context.getScanMode().isReflectionMode()) {
         sample.setTheta(Math.toDegrees(1.5707963267948966D - Math.atan2(sample.getR(), sample.getX())));
         double refRes;
         double phaseShift;
         double newPhase;
         double L3;
         if (sample.getRHO() == null) {
            refRes = Math.toRadians(sample.getReflectionPhase());
            phaseShift = Math.pow(10.0D, sample.getReflectionLoss() / 20.0D);
            newPhase = phaseShift * Math.cos(refRes);
            L3 = phaseShift * Math.sin(refRes);
            sample.setRHO(new Complex(newPhase, L3));
         }

         if (this.config.isPortExtensionEnabled()) {
            refRes = context.getDib().getReferenceResistance().getReal();
            phaseShift = context.getPortExtensionPhaseConstant() * (double)sample.getFrequency();

            for(newPhase = sample.getReflectionPhase() + phaseShift; newPhase < -180.0D; newPhase += 360.0D) {
            }

            while(newPhase > 180.0D) {
               newPhase -= 360.0D;
            }

            L3 = Math.cos(Math.toRadians(newPhase)) * sample.getMag();
            double M3 = Math.sin(Math.toRadians(newPhase)) * sample.getMag();
            double newR = (1.0D - L3 * L3 - M3 * M3) / ((1.0D - L3) * (1.0D - L3) + M3 * M3) * refRes;
            double newX = 2.0D * M3 / ((1.0D - L3) * (1.0D - L3) + M3 * M3) * refRes;
            double newZ = Math.sqrt(newR * newR + newX * newX);
            double newReflCoeff = Math.sqrt(((newR - refRes) * (newR - refRes) + newX * newX) / ((newR + refRes) * (newR + refRes) + newX * newX));
            double newSWR = (1.0D + newReflCoeff) / (1.0D - newReflCoeff);
            sample.setR(newR);
            sample.setX(newX);
            sample.setZ(newZ);
            sample.setReflectionPhase(newPhase);
            sample.setSWR(newSWR);
         }
      }

   }

   public void createCalibrationPoints(VNACalibrationContext context, VNACalibrationBlock calBlock) {
      TraceHelper.entry(this, "createCalibrationPoints");
      if (context != null && calBlock != null) {
         TraceHelper.text(this, "createCalibrationPoints", "creating calibration points for temp=" + calBlock.getTemperature());
         int listLength = calBlock.getNumberOfSteps();
         VNACalibrationPoint[] rc = new VNACalibrationPoint[listLength];
         boolean useOpen = calBlock.getCalibrationData4Open() != null && calBlock.getCalibrationData4Open().getSamples() != null;
         boolean useLoad = calBlock.getCalibrationData4Load() != null && calBlock.getCalibrationData4Load().getSamples() != null;
         boolean useLoop = calBlock.getCalibrationData4Loop() != null && calBlock.getCalibrationData4Loop().getSamples() != null;
         boolean useShort = calBlock.getCalibrationData4Short() != null && calBlock.getCalibrationData4Short().getSamples() != null;

         for(int i = 0; i < listLength; ++i) {
            VNABaseSample sOpen = null;
            VNABaseSample sShort = null;
            VNABaseSample sLoad = null;
            VNABaseSample sLoop = null;
            if (useLoad) {
               sLoad = calBlock.getCalibrationData4Load().getSamples()[i];
            }

            if (useOpen) {
               sOpen = calBlock.getCalibrationData4Open().getSamples()[i];
            }

            if (useShort) {
               sShort = calBlock.getCalibrationData4Short().getSamples()[i];
            }

            if (useLoop) {
               sLoop = calBlock.getCalibrationData4Loop().getSamples()[i];
            }

            rc[i] = this.createCalibrationPoint(context, sOpen, sShort, sLoad, sLoop);
         }

         calBlock.setCalibrationPoints(rc);
      } else {
         ErrorLogHelper.text(this, "createCalibrationPoints", "Parameter(s) are null");
      }

      TraceHelper.exit(this, "createCalibrationPoints");
   }

   public void calculateMovingAverage(VNASampleBlock rawBlock, int degree) {
      VNABaseSample[] rawSamples = rawBlock.getSamples();
      int max = rawSamples.length - degree;

      for(int i = degree; i < max; ++i) {
         double loc = rawSamples[i].getLoss();

         int j;
         for(j = i - degree; j < i; ++j) {
            loc += rawSamples[i].getLoss();
         }

         for(j = i + 1; j <= i + degree; ++j) {
            loc += rawSamples[i].getLoss();
         }

         loc /= (double)(2 * degree + 1);
         rawSamples[i].setLoss(loc);
      }

   }

   public void calculateExponentialAverage4Loss(VNASampleBlock rawBlock, int n) {
      int min = 1;
      VNABaseSample[] rawSamples = rawBlock.getSamples();
      int max = rawSamples.length;
      int div = 1 << n;
      double output = rawSamples[0].getLoss();

      for(int i = min; i < max; ++i) {
         VNABaseSample current = rawSamples[i];
         double input = current.getLoss();
         output = input / (double)div + output - output / (double)div;
         current.setLoss(output);
      }

   }

   public void calculateExponentialAverage4Phase(VNASampleBlock rawBlock, int n) {
      int min = 1;
      VNABaseSample[] rawSamples = rawBlock.getSamples();
      int max = rawSamples.length;
      int div = 1 << n;
      double output = rawSamples[0].getAngle();

      for(int i = min; i < max; ++i) {
         VNABaseSample current = rawSamples[i];
         double input = current.getAngle();
         output = input / (double)div + output - output / (double)div;
         current.setAngle(output);
      }

   }

   public final VNACalibrationBlock createCalibrationBlockFromRaw(VNACalibrationContext context, VNASampleBlock listOpen, VNASampleBlock listShort, VNASampleBlock listLoad, VNASampleBlock listLoop) throws ProcessingException {
      TraceHelper.entry(this, "createCalibrationBlockFromRaw");
      VNASampleBlock blk;
      if (listLoad != null) {
         blk = listLoad;
      } else if (listOpen != null) {
         blk = listOpen;
      } else if (listShort != null) {
         blk = listShort;
      } else {
         if (listLoop == null) {
            throw new ProcessingException("No data set on raw data");
         }

         blk = listLoop;
      }

      VNACalibrationBlock rc = new VNACalibrationBlock(blk);
      rc.setCalibrationData4Load(listLoad);
      rc.setCalibrationData4Open(listOpen);
      rc.setCalibrationData4Short(listShort);
      rc.setCalibrationData4Loop(listLoop);
      this.createCalibrationPoints(context, rc);
      TraceHelper.exit(this, "createCalibrationBlockFromRaw");
      return rc;
   }

   public void applyPreFilter(VNABaseSample[] samples, VNADeviceInfoBlock dib) {
      String methodName = "applyPreFilter";
      TraceHelper.entry(this, "applyPreFilter");
      TraceHelper.exit(this, "applyPreFilter");
   }

   public void applyPostFilter(VNABaseSample[] samples, VNADeviceInfoBlock dib) {
      String methodName = "applyPostFilter";
      TraceHelper.entry(this, "applyPostFilter");
      if (this.config.isApplyGaussianFilter()) {
         if (this.gaussianFilter == null || this.gaussianFilterFile.needsReload()) {
            String fn = this.gaussianFilterFile.getFilename();
            this.gaussianFilter = VNABaseFilterHelper.loadFilterParameters(fn);
            LogHelper.text(this, "applyPostFilter", "Filter file [" + fn + "] read/reloaded");
         }

         int numSamples = samples.length;
         int filterLen = this.gaussianFilter.length;
         int filterMid = filterLen / 2;

         for(int i = filterMid; i < numSamples - filterMid; ++i) {
            double newLoss = 0.0D;
            double newPhase = 0.0D;

            for(int j = 0; j < filterLen; ++j) {
               newLoss += samples[i - filterMid + j].getLoss() * this.gaussianFilter[j];
               newPhase += samples[i - filterMid + j].getAngle() * this.gaussianFilter[j];
            }

            samples[i].setLoss(newLoss);
            samples[i].setAngle(newPhase);
         }
      }

      TraceHelper.exit(this, "applyPostFilter");
   }

   public IVNADriver getDriver() {
      return this.driver;
   }

   public void setDriver(IVNADriver pDriver) {
      this.driver = pDriver;
   }
}
