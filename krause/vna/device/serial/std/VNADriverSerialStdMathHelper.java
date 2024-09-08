package krause.vna.device.serial.std;

import krause.util.ras.logging.ErrorLogHelper;
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

public class VNADriverSerialStdMathHelper extends VNADriverMathBaseHelper {
   private static final int DEFAULT_ADC_BITS = 1024;
   private static final double DEFAULT_PHASE_PER_BIT = 0.17595307917888564D;

   public VNADriverSerialStdMathHelper(IVNADriver driver) {
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
      return context.getScanMode().isTransmissionMode() ? createCalibratedSampleForTransmission(context, rhoMSample, calib) : createCalibratedSampleForReflection(context, rhoMSample, calib);
   }

   private static VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
      double loss = -((raw.getLoss() - calib.getLoss()) * context.getReturnLossPerBit());
      double phase = (raw.getAngle() - calib.getPhase()) * 0.17595307917888564D;
      double mag = Math.pow(10.0D, loss / 20.0D);
      double swr = Math.abs((1.0D + mag) / (1.0D - mag));
      double f = Math.cos(Math.toRadians(phase));
      double g = Math.sin(Math.toRadians(phase));
      double rr = f * mag;
      double ss = g * mag;
      double x_imp = Math.abs(2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * context.getDib().getReferenceResistance().getReal());
      double r_imp = Math.abs((1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * context.getDib().getReferenceResistance().getReal());
      double z_imp = Math.sqrt(r_imp * r_imp + x_imp * x_imp);
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(raw.getFrequency());
      rc.setMag(mag);
      rc.setReflectionLoss(loss);
      rc.setReflectionPhase(phase);
      rc.setSWR(swr);
      rc.setR(r_imp);
      rc.setX(x_imp);
      rc.setZ(z_imp);
      return rc;
   }

   private static VNACalibratedSample createCalibratedSampleForTransmission(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
      VNACalibratedSample rc = new VNACalibratedSample();
      double loss = -((raw.getLoss() - calib.getLoss()) * context.getTransmissionLossPerBit());
      double phase = Math.abs((raw.getAngle() - calib.getPhase()) * 0.17595307917888564D);
      double mag1 = Math.pow(10.0D, loss / 20.0D);
      double mag = Math.pow(10.0D, -loss / 20.0D);
      double dRef = 2.0D * context.getDib().getReferenceResistance().getReal();
      double rs = dRef * mag / Math.sqrt(1.0D + Math.pow(Math.tan(Math.toRadians(phase)), 2.0D)) - dRef;
      double xs = -(rs + 100.0D) * Math.tan(Math.toRadians(phase));
      double z = Math.sqrt(rs * rs + xs * xs);
      rc.setTransmissionLoss(loss);
      rc.setTransmissionPhase(phase);
      rc.setMag(mag1);
      rc.setR(rs);
      rc.setX(xs);
      rc.setZ(z);
      rc.setFrequency(raw.getFrequency());
      return rc;
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      String methodName = "createCalibrationContextForCalibratedSamples";
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNADriverSerialStdDIB dib = (VNADriverSerialStdDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      context.setAdcBits(10);
      int maxVal = -(1 << context.getAdcBits()) - 1;
      if (context.getScanMode().isReflectionMode()) {
         context.setReturnLossPerBit(dib.getMaxLoss() / (double)maxVal);
      } else if (context.getScanMode().isTransmissionMode()) {
         context.setTransmissionLossPerBit(dib.getMaxLoss() / (double)maxVal);
      } else {
         ErrorLogHelper.text(this, "createCalibrationContextForCalibratedSamples", "Not supported scan mode [%d]", context.getScanMode());
      }

      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      String methodName = "createCalibrationContextForCalibrationPoints";
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNADriverSerialStdDIB dib = (VNADriverSerialStdDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
      return context.getScanMode().isTransmissionMode() ? createCalibrationPointForTransmission(numLoop) : createCalibrationPointForReflection(numOpen);
   }

   private static VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample numOpen) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(numOpen.getFrequency());
      rc.setLoss(numOpen.getLoss());
      rc.setPhase(numOpen.getAngle());
      return rc;
   }

   private static VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample numLoop) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      rc.setFrequency(numLoop.getFrequency());
      rc.setLoss(numLoop.getLoss());
      rc.setPhase(numLoop.getAngle());
      return rc;
   }
}
