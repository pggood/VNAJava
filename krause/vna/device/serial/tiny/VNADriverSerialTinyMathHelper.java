package krause.vna.device.serial.tiny;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationContextTiny;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverMathBaseHelper;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialTinyMathHelper extends VNADriverMathBaseHelper {
   private static final double SMALL_PHASE = 0.1D;

   public VNADriverSerialTinyMathHelper(IVNADriver driver) {
      super(driver);
      TraceHelper.entry(this, "VNADriverSerialTinyMathHelper");
      TraceHelper.exit(this, "VNADriverSerialTinyMathHelper");
   }

   public void applyFilter(VNABaseSample[] samples) {
      TraceHelper.entry(this, "applyFilter");
      VNADeviceInfoBlock dib = this.getDriver().getDeviceInfoBlock();
      super.applyPreFilter(samples, dib);
      super.applyPostFilter(samples, dib);
      TraceHelper.exit(this, "applyFilter");
   }

   private static VNABaseSample calculateCorrectedBaseSample(VNACalibrationContextTiny context, VNABaseSample sample, double temp) {
      if (sample != null) {
         VNABaseSample rc = new VNABaseSample(sample);
         double oldReal = sample.getLoss();
         double oldImag = sample.getAngle();
         double deltaTemp = 40.0D - temp;
         double corrTempFactor = 1.0D - deltaTemp * context.getTempCorrection();
         oldReal *= corrTempFactor;
         oldImag *= corrTempFactor;
         int newReal = (int)oldReal;
         int newImag = (int)((oldImag * context.getGainCorrection() - oldReal * 1.0D * context.getSineCorrection()) / context.getCosineCorrection());
         rc.setLoss((double)newReal);
         rc.setAngle((double)newImag);
         return rc;
      } else {
         return null;
      }
   }

   public VNACalibratedSample createCalibratedSample(VNACalibrationContext pContext, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
      VNACalibrationContextTiny context = (VNACalibrationContextTiny)pContext;
      VNABaseSample correctedRawSample = calculateCorrectedBaseSample(context, rawSample, context.getConversionTemperature());
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)context.getDib();
      double deltaTemp = context.getCalibrationTemperature() - context.getConversionTemperature();
      double ifPhaseCorrection = deltaTemp * dib.getIfPhaseCorrection();
      return context.getScanMode().isTransmissionMode() ? this.createCalibratedSampleForTransmission(context, correctedRawSample, calibPoint, ifPhaseCorrection) : this.createCalibratedSampleForReflection(context, correctedRawSample, calibPoint, ifPhaseCorrection);
   }

   private VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calib, double ifPhaseCorrection) {
      VNADeviceInfoBlock dib = context.getDib();
      Complex rhoM = rawSample.asComplex();
      Complex rho = rhoM.subtract(calib.getE00()).divide(rhoM.multiply(calib.getE11()).subtract(calib.getDeltaE()));
      double mag = rho.abs();
      if (mag > 1.0D) {
         mag = 1.0D;
      }

      double swr = (1.0D + mag) / (1.0D - mag);
      double returnLoss = 20.0D * Math.log10(mag);
      returnLoss = Math.max(returnLoss, dib.getMaxLoss());
      double returnPhase = Math.toDegrees(rho.getArgument());
      if (returnPhase >= 0.0D && returnPhase < 0.1D) {
         returnPhase = 0.1D;
      } else if (returnPhase > -0.1D && returnPhase < 0.0D) {
         returnPhase = -0.1D;
      }

      returnPhase += ifPhaseCorrection;
      if (returnPhase > 180.0D) {
         returnPhase += -360.0D;
      } else if (returnPhase < -180.0D) {
         returnPhase += 360.0D;
      }

      double f = Math.cos(Math.toRadians(returnPhase));
      double g = Math.sin(Math.toRadians(returnPhase));
      double rr = f * mag;
      double ss = g * mag;
      double xImp = 2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * context.getDib().getReferenceResistance().getReal();
      double rImp = (1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * context.getDib().getReferenceResistance().getReal();
      if (rImp < 0.0D) {
         rImp = 0.0D;
      }

      double zImp = Math.sqrt(rImp * rImp + xImp * xImp);
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(rawSample.getFrequency());
      rc.setRHO(rho);
      rc.setMag(mag);
      rc.setReflectionLoss(returnLoss);
      rc.setReflectionPhase(returnPhase);
      rc.setSWR(swr);
      rc.setR(rImp);
      rc.setX(xImp);
      rc.setZ(zImp);
      return rc;
   }

   private VNACalibratedSample createCalibratedSampleForTransmission(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calPoint, double ifPhaseCorrection) {
      VNADeviceInfoBlock dib = context.getDib();
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(rawSample.getFrequency());
      Complex mDUT = new Complex((rawSample.getAngle() - 512.0D) * 0.003D, (rawSample.getLoss() - 512.0D) * 0.003D);
      Complex gDUT = mDUT.subtract(calPoint.getE11()).divide(calPoint.getDeltaE());
      rc.setMag(gDUT.abs());
      double tl = Math.max(20.0D * Math.log10(rc.getMag()), dib.getMaxLoss());
      double tp = Math.toDegrees(-gDUT.getArgument());
      tp += ifPhaseCorrection;
      if (tp > 180.0D) {
         tp -= 180.0D;
      } else if (tp < -180.0D) {
         tp += -180.0D;
      }

      rc.setTransmissionPhase(tp);
      double mag1 = Math.pow(10.0D, tl / 20.0D);
      double mag = Math.pow(10.0D, -tl / 20.0D);
      double dRef = 2.0D * context.getDib().getReferenceResistance().getReal();
      double rs = dRef * mag / Math.sqrt(1.0D + Math.pow(Math.tan(Math.toRadians(tp)), 2.0D)) - dRef;
      double xs = -(rs + 100.0D) * Math.tan(Math.toRadians(tp));
      double z = Math.sqrt(rs * rs + xs * xs);
      rc.setTransmissionLoss(tl);
      rc.setTransmissionPhase(tp);
      rc.setMag(mag1);
      rc.setR(rs);
      rc.setX(xs);
      rc.setZ(z);
      rc.setFrequency(rawSample.getFrequency());
      return rc;
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContextTiny context = new VNACalibrationContextTiny();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      context.setCalibrationTemperature(calBlock.getTemperature());
      double correctionRadian = dib.getPhaseCorrection() * 3.141592653589793D / 180.0D;
      context.setSineCorrection(Math.sin(correctionRadian));
      context.setCosineCorrection(Math.cos(correctionRadian));
      context.setGainCorrection(dib.getGainCorrection());
      context.setTempCorrection(dib.getTempCorrection());
      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContextTiny context = new VNACalibrationContextTiny();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      calBlock.calculateCalibrationTemperature();
      context.setCalibrationTemperature(calBlock.getTemperature());
      double correctionRadian = dib.getPhaseCorrection() * 3.141592653589793D / 180.0D;
      context.setSineCorrection(Math.sin(correctionRadian));
      context.setCosineCorrection(Math.cos(correctionRadian));
      context.setGainCorrection(dib.getGainCorrection());
      context.setTempCorrection(dib.getTempCorrection());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext pContext, VNABaseSample sampleOpen, VNABaseSample sampleShort, VNABaseSample sampleLoad, VNABaseSample sampleLoop) {
      VNACalibrationContextTiny context = (VNACalibrationContextTiny)pContext;
      VNABaseSample corrOpen;
      VNABaseSample corrShort;
      if (context.getScanMode().isTransmissionMode()) {
         corrOpen = calculateCorrectedBaseSample(context, sampleOpen, context.getCalibrationTemperature());
         corrShort = calculateCorrectedBaseSample(context, sampleLoop, context.getCalibrationTemperature());
         return this.createCalibrationPointForTransmission(corrOpen, corrShort);
      } else {
         corrOpen = calculateCorrectedBaseSample(context, sampleOpen, context.getCalibrationTemperature());
         corrShort = calculateCorrectedBaseSample(context, sampleShort, context.getCalibrationTemperature());
         VNABaseSample corrLoad = calculateCorrectedBaseSample(context, sampleLoad, context.getCalibrationTemperature());
         return this.createCalibrationPointForReflection(corrOpen, corrShort, corrLoad);
      }
   }

   private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample sampleOpen, VNABaseSample sampleShort, VNABaseSample sampleLoad) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      Complex m1 = sampleOpen.asComplex();
      Complex m2 = sampleShort.asComplex();
      Complex m3 = sampleLoad.asComplex();
      double A1 = 1.0D;
      double A2 = -1.0D;
      double A3 = 0.0D;
      Complex p1 = m2.multiply(-1.0D).subtract(m1.multiply(1.0D)).multiply(m1.subtract(m3));
      Complex p2 = m3.multiply(0.0D).subtract(m1.multiply(1.0D)).multiply(m2.subtract(m1));
      Complex p3 = m2.multiply(-1.0D).subtract(m1.multiply(1.0D)).multiply(-1.0D);
      Complex p4 = m3.multiply(0.0D).subtract(m1.multiply(1.0D)).multiply(-2.0D);
      rc.setDeltaE(p1.add(p2).divide(p3.subtract(p4)));
      rc.setE11(m2.subtract(m1).add(rc.getDeltaE().multiply(-2.0D)).divide(m2.multiply(-1.0D).subtract(m1.multiply(1.0D))));
      rc.setE00(m1.subtract(m1.multiply(1.0D).multiply(rc.getE11())).add(rc.getDeltaE().multiply(1.0D)));
      rc.setFrequency(sampleOpen.getFrequency());
      return rc;
   }

   private VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample sampleOpen, VNABaseSample sampleLoop) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(sampleLoop.getFrequency());
      rc.setE00(new Complex((sampleLoop.getAngle() - 512.0D) * 0.003D, (sampleLoop.getLoss() - 512.0D) * 0.003D));
      rc.setE11(new Complex((sampleOpen.getAngle() - 512.0D) * 0.003D, (sampleOpen.getLoss() - 512.0D) * 0.003D));
      rc.setDeltaE(rc.getE00().subtract(rc.getE11()));
      return rc;
   }
}
