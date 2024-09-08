package krause.vna.device.serial.std2;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverMathBaseHelper;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialStd2MathHelper extends VNADriverMathBaseHelper {
   private static final int DEFAULT_ADC_TICKS = 1024;
   private static final double DEFAULT_MAX_LOSS = 60.0D;
   private static final double DEFAULT_MAX_PHASE = 180.0D;
   private static final double DEFAULT_PHASE_PER_BIT = 0.17595307917888564D;
   private static final double DEFAULT_RETURNLOSS_PER_BIT = 0.05865102639296188D;

   public VNADriverSerialStd2MathHelper(IVNADriver driver) {
      super(driver);
   }

   public void applyFilter(VNABaseSample[] samples) {
      TraceHelper.entry(this, "applyFilter");
      VNADeviceInfoBlock dib = this.getDriver().getDeviceInfoBlock();
      super.applyPreFilter(samples, dib);
      super.applyPostFilter(samples, dib);
      TraceHelper.exit(this, "applyFilter");
   }

   public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample rhoMSample, VNACalibrationPoint calib) {
      return context.getScanMode().isTransmissionMode() ? this.createCalibratedSampleForTransmission(context, rhoMSample, calib) : this.createCalibratedSampleForReflection(rhoMSample, calib);
   }

   private VNACalibratedSample createCalibratedSampleForReflection(VNABaseSample pRaw, VNACalibrationPoint pCalPoint) {
      double calMag = pCalPoint.getDeltaE().getReal();
      double calPhsOffset = pCalPoint.getE00().getReal();
      double calPhsRange = pCalPoint.getE11().getReal();
      double dutPhs = pRaw.getAngle();
      double dutMag = pRaw.getLoss();
      double calibratedMag = calMag - dutMag;
      double calcRL = calibratedMag * 60.0D / 1023.0D;
      double calcRho;
      double calcSwr;
      if (calcRL > -0.173741D) {
         calcRL = 0.0D;
         calcRho = 1.0D;
         calcSwr = 99.99D;
      } else {
         calcRho = Math.pow(10.0D, calcRL / 20.0D);
         calcSwr = (1.0D + calcRho) / (1.0D - calcRho);
      }

      double calibratedPhase = dutPhs - calPhsOffset;
      double calcPhi;
      double re;
      double im;
      double denominator;
      if (calibratedPhase < 0.0D) {
         calcPhi = 0.0D;
         re = calcRho;
         im = 0.0D;
      } else {
         calcPhi = calibratedPhase * 180.0D / calPhsRange;
         if (calcPhi > 180.0D) {
            calcPhi = 180.0D;
            re = -calcRho;
            im = 0.0D;
         } else {
            denominator = Math.toRadians(calcPhi);
            re = calcRho * Math.cos(denominator);
            im = calcRho * Math.sin(denominator);
         }
      }

      denominator = 1.0D - re;
      denominator = (denominator * denominator + im * im) / 50.0D;
      double calcXs;
      double calcRs;
      if (denominator < 1.0E-5D) {
         calcRs = 9999.0D;
         calcXs = 9999.0D;
      } else {
         calcRs = (1.0D - re * re - im * im) / denominator;
         calcXs = 2.0D * im / denominator;
         if (calcRs > 9999.0D) {
            calcRs = 9999.0D;
         } else if (calcRs < 0.0D) {
            calcRs = 0.0D;
         }

         if (calcXs > 9999.0D) {
            calcXs = 9999.0D;
         } else if (calcXs < 0.0D) {
            calcXs = 0.0D;
         }
      }

      double calcZ = Math.sqrt(calcRs * calcRs + calcXs * calcXs);
      if (calcZ > 9999.0D) {
         calcZ = 9999.0D;
      }

      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(pRaw.getFrequency());
      rc.setX(calcXs);
      rc.setR(calcRs);
      rc.setZ(calcZ);
      rc.setRHO(new Complex(re, im));
      rc.setReflectionLoss(calcRL);
      rc.setReflectionPhase(calcPhi);
      rc.setMag(calibratedMag);
      rc.setSWR(calcSwr);
      return rc;
   }

   private VNACalibratedSample createCalibratedSampleForTransmission(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
      VNACalibratedSample rc = new VNACalibratedSample();
      double tl = -((raw.getLoss() - calib.getLoss()) * 0.05865102639296188D);
      double tp = (raw.getAngle() - calib.getPhase()) * 0.17595307917888564D;
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
      rc.setFrequency(raw.getFrequency());
      return rc;
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(calBlock.getMathHelper().getDriver().getDeviceInfoBlock());
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      String methodName = "createCalibrationContextForCalibrationPoints";
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(calBlock.getMathHelper().getDriver().getDeviceInfoBlock());
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
      return context.getScanMode().isTransmissionMode() ? this.createCalibrationPointForTransmission(numLoop) : this.createCalibrationPointForReflection(numOpen, numShort);
   }

   private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample pOpen, VNABaseSample pShort) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(pOpen.getFrequency());
      double calPhsOffset = pOpen.getAngle();
      double calMag = pOpen.getLoss();
      double shortPhs = pShort.getAngle();
      double shortMag = pShort.getLoss();
      if (shortMag > calMag) {
         calMag = shortMag;
      }

      double calPhsRange;
      if (calPhsOffset + 300.0D > shortPhs) {
         calPhsRange = 1023.0D;
      } else {
         calPhsRange = shortPhs - calPhsOffset;
      }

      rc.setDeltaE(new Complex(calMag));
      rc.setE00(new Complex(calPhsOffset));
      rc.setE11(new Complex(calPhsRange));
      return rc;
   }

   private VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample numLoop) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(numLoop.getFrequency());
      rc.setLoss(numLoop.getLoss());
      rc.setPhase(numLoop.getAngle());
      return rc;
   }
}
