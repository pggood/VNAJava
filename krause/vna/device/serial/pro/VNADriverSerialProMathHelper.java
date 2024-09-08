package krause.vna.device.serial.pro;

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

public class VNADriverSerialProMathHelper extends VNADriverMathBaseHelper {
   public VNADriverSerialProMathHelper(IVNADriver driver) {
      super(driver);
      TraceHelper.entry(this, "VNADriverSerialProMathHelper");
      TraceHelper.exit(this, "VNADriverSerialProMathHelper");
   }

   public void applyFilter(VNABaseSample[] samples) {
      TraceHelper.entry(this, "applyFilter");
      VNADeviceInfoBlock dib = this.getDriver().getDeviceInfoBlock();
      super.applyPreFilter(samples, dib);
      super.applyPostFilter(samples, dib);
      TraceHelper.exit(this, "applyFilter");
   }

   public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
      return context.getScanMode().isTransmissionMode() ? this.createCalibratedSampleForTransmission(context.getDib(), rawSample, calibPoint) : this.createCalibratedSampleForReflection(context.getDib(), rawSample, calibPoint);
   }

   private VNACalibratedSample createCalibratedSampleForReflection(VNADeviceInfoBlock dib, VNABaseSample rawSample, VNACalibrationPoint calib) {
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(rawSample.getFrequency());
      Complex rhoM = rawSample.asComplex();
      Complex rho = rhoM.subtract(calib.getE00()).divide(rhoM.multiply(calib.getE11()).subtract(calib.getDeltaE()));
      rc.setRHO(rho);
      rc.setReflectionPhase(Math.toDegrees(rho.getArgument()));
      Complex zComplex50Ohms = dib.getReferenceResistance().multiply(C_1.add(rc.getRHO())).divide(C_1.subtract(rc.getRHO()));
      rc.setZComplex50Ohms(zComplex50Ohms);
      rc.setZ(zComplex50Ohms.abs());
      rc.setX(zComplex50Ohms.getImaginary());
      rc.setR(zComplex50Ohms.getReal());
      double mag = rc.getRHO().abs();
      if (mag > 1.0D) {
         mag = 1.0D;
      }

      rc.setMag(mag);
      double swr = (1.0D + mag) / (1.0D - mag);
      rc.setSWR(swr);
      double loss = 20.0D * Math.log10(rc.getMag());
      loss = Math.max(loss, this.getDriver().getDeviceInfoBlock().getMaxLoss());
      rc.setReflectionLoss(loss);
      return rc;
   }

   private VNACalibratedSample createCalibratedSampleForTransmission(VNADeviceInfoBlock dib, VNABaseSample rawSample, VNACalibrationPoint calPoint) {
      TraceHelper.entry(this, "createCalibratedSampleForTransmission");
      VNACalibratedSample rc = new VNACalibratedSample();
      Complex mDUT = new Complex((rawSample.getAngle() - 512.0D) * 0.003D, (rawSample.getLoss() - 512.0D) * 0.003D);
      Complex gDUT = mDUT.subtract(calPoint.getE11()).divide(calPoint.getDeltaE());
      rc.setMag(gDUT.abs());
      double loss = Math.max(20.0D * Math.log10(rc.getMag()), dib.getMaxLoss());
      double phase = Math.toDegrees(-gDUT.getArgument());
      double mag1 = Math.pow(10.0D, loss / 20.0D);
      double mag = Math.pow(10.0D, -loss / 20.0D);
      double dRef = 2.0D * dib.getReferenceResistance().getReal();
      double rs = dRef * mag / Math.sqrt(1.0D + Math.pow(Math.tan(Math.toRadians(phase)), 2.0D)) - dRef;
      double xs = -(rs + 100.0D) * Math.tan(Math.toRadians(phase));
      double z = Math.sqrt(rs * rs + xs * xs);
      rc.setTransmissionLoss(loss);
      rc.setTransmissionPhase(phase);
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
      VNADriverSerialProDIB dib = (VNADriverSerialProDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      String methodName = "createCalibrationContextForCalibrationPoints";
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNADriverSerialProDIB dib = (VNADriverSerialProDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      context.setDib(dib);
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
      return context.getScanMode().isTransmissionMode() ? this.createCalibrationPointForTransmission(context.getDib(), numOpen, numLoop) : this.createCalibrationPointForReflection(context.getDib(), numOpen, numShort, numLoad);
   }

   private VNACalibrationPoint createCalibrationPointForReflection(VNADeviceInfoBlock dib, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(numOpen.getFrequency());
      Complex cOpen = numOpen.asComplex();
      Complex cShort = numShort.asComplex();
      Complex cLoad = numLoad.asComplex();
      Complex p1 = cShort.multiply(-1.0D).subtract(cOpen.multiply(1.0D)).multiply(cOpen.subtract(cLoad));
      Complex p2 = cLoad.multiply(0.0D).subtract(cOpen.multiply(1.0D)).multiply(cShort.subtract(cOpen));
      Complex p3 = cShort.multiply(-1.0D).subtract(cOpen.multiply(1.0D)).multiply(-1.0D);
      Complex p4 = cLoad.multiply(0.0D).subtract(cOpen.multiply(1.0D)).multiply(-2.0D);
      rc.setDeltaE(p1.add(p2).divide(p3.subtract(p4)));
      rc.setE11(cShort.subtract(cOpen).add(rc.getDeltaE().multiply(-2.0D)).divide(cShort.multiply(-1.0D).subtract(cOpen.multiply(1.0D))));
      rc.setE00(cOpen.subtract(cOpen.multiply(1.0D).multiply(rc.getE11())).add(rc.getDeltaE().multiply(1.0D)));
      return rc;
   }

   private VNACalibrationPoint createCalibrationPointForTransmission(VNADeviceInfoBlock dib, VNABaseSample numOpen, VNABaseSample numLoop) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(numLoop.getFrequency());
      rc.setE00(new Complex((numLoop.getAngle() - 512.0D) * 0.003D, (numLoop.getLoss() - 512.0D) * 0.003D));
      rc.setE11(new Complex((numOpen.getAngle() - 512.0D) * 0.003D, (numOpen.getLoss() - 512.0D) * 0.003D));
      rc.setDeltaE(rc.getE00().subtract(rc.getE11()));
      return rc;
   }
}
