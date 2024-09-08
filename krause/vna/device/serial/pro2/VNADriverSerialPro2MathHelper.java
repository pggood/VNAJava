package krause.vna.device.serial.pro2;

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

public class VNADriverSerialPro2MathHelper extends VNADriverMathBaseHelper {
   static final double R_MINUS_ONE = -1.0D;
   static final double R_PLUS_ONE = 1.0D;
   static final double R_ZERO = 0.0D;
   private static final double SMALL_PHASE = 0.1D;

   public VNADriverSerialPro2MathHelper(IVNADriver driver) {
      super(driver);
      TraceHelper.entry(this, "VNADriverSerialPro2MathHelper");
      TraceHelper.exit(this, "VNADriverSerialPro2MathHelper");
   }

   public void applyFilter(VNABaseSample[] samples) {
      TraceHelper.entry(this, "applyFilter");
      VNADeviceInfoBlock dib = this.getDriver().getDeviceInfoBlock();
      super.applyPreFilter(samples, dib);
      super.applyPostFilter(samples, dib);
      TraceHelper.exit(this, "applyFilter");
   }

   private VNABaseSample calculateCorrectedBaseSample(VNACalibrationContextPro2 context, VNABaseSample sample) {
      return sample;
   }

   public VNACalibratedSample createCalibratedSample(VNACalibrationContext pContext, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
      VNACalibrationContextPro2 context = (VNACalibrationContextPro2)pContext;
      VNABaseSample correctedRawSample = this.calculateCorrectedBaseSample(context, rawSample);
      return context.getScanMode().isTransmissionMode() ? this.createCalibratedSampleForTransmission(context, correctedRawSample, calibPoint) : this.createCalibratedSampleForReflection(context, correctedRawSample, calibPoint);
   }

   private VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calib) {
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

   private VNACalibratedSample createCalibratedSampleForTransmission(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calPoint) {
      TraceHelper.entry(this, "createCalibratedSampleForTransmission");
      VNACalibratedSample rc = new VNACalibratedSample();
      Complex mDUT = new Complex((rawSample.getAngle() - 512.0D) * 0.003D, (rawSample.getLoss() - 512.0D) * 0.003D);
      Complex gDUT = mDUT.subtract(calPoint.getE11()).divide(calPoint.getDeltaE());
      rc.setMag(gDUT.abs());
      double tl = Math.max(20.0D * Math.log10(rc.getMag()), context.getDib().getMaxLoss());
      double tp = Math.toDegrees(-gDUT.getArgument());
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
      TraceHelper.exitWithRC(this, "createCalibratedSampleForTransmission", rc);
      return rc;
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContextPro2 context = new VNACalibrationContextPro2();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      context.setCalibrationTemperature(calBlock.getTemperature());
      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContextPro2 context = new VNACalibrationContextPro2();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      calBlock.calculateCalibrationTemperature();
      context.setCalibrationTemperature(calBlock.getTemperature());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext pContext, VNABaseSample sampleOpen, VNABaseSample sampleShort, VNABaseSample sampleLoad, VNABaseSample sampleLoop) {
      VNACalibrationContextPro2 context = (VNACalibrationContextPro2)pContext;
      VNABaseSample corrOpen;
      VNABaseSample corrShort;
      if (context.getScanMode().isTransmissionMode()) {
         corrOpen = this.calculateCorrectedBaseSample(context, sampleOpen);
         corrShort = this.calculateCorrectedBaseSample(context, sampleLoop);
         return this.createCalibrationPointForTransmission(corrOpen, corrShort);
      } else {
         corrOpen = this.calculateCorrectedBaseSample(context, sampleOpen);
         corrShort = this.calculateCorrectedBaseSample(context, sampleShort);
         VNABaseSample corrLoad = this.calculateCorrectedBaseSample(context, sampleLoad);
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
