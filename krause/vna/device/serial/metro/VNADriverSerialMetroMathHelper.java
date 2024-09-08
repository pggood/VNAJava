package krause.vna.device.serial.metro;

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

public class VNADriverSerialMetroMathHelper extends VNADriverMathBaseHelper {
   private static final int DEFAULT_ADC_BITS = 1024;
   private static final double DEFAULT_PHASE_PER_BIT = 0.17595307917888564D;
   private final double RAD2DEG = 57.29577951308232D;

   public VNADriverSerialMetroMathHelper(IVNADriver driver) {
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
      return context.getScanMode().isTransmissionMode() ? this.createCalibratedSampleForTransmission(rhoMSample, calib) : this.createCalibratedSampleForReflection(context, rhoMSample, calib);
   }

   private VNACalibratedSample createCalibratedSampleForReflection(VNACalibrationContext context, VNABaseSample raw, VNACalibrationPoint calib) {
      double lossPerBit = -((VNADriverSerialMetroDIB)context.getDib()).getMaxReflectionLoss() / 1023.0D;
      double rl = -((raw.getLoss() - calib.getLoss()) * lossPerBit);
      double phase = raw.getAngle() * 0.17595307917888564D;
      double mag = Math.pow(10.0D, rl / 20.0D);
      double swr = Math.abs((1.0D + mag) / (1.0D - mag));
      double f = Math.cos(phase / 57.29577951308232D);
      double g = Math.sin(phase / 57.29577951308232D);
      double rr = f * mag;
      double ss = g * mag;
      double x_imp = Math.abs(2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * context.getDib().getReferenceResistance().getReal());
      double r_imp = Math.abs((1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * context.getDib().getReferenceResistance().getReal());
      double z_imp = Math.sqrt(r_imp * r_imp + x_imp * x_imp);
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(raw.getFrequency());
      rc.setMag(mag);
      rc.setReflectionLoss(rl);
      rc.setSWR(swr);
      rc.setReflectionPhase(phase);
      rc.setR(r_imp);
      rc.setX(x_imp);
      rc.setZ(z_imp);
      return rc;
   }

   private VNACalibratedSample createCalibratedSampleForTransmission(VNABaseSample raw, VNACalibrationPoint calib) {
      double lossPerBit = -((VNADriverSerialMetroDIB)this.getDriver().getDeviceInfoBlock()).getMaxTransmissionLoss() / 1023.0D;
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setTransmissionLoss(-((raw.getLoss() - calib.getLoss()) * lossPerBit));
      rc.setTransmissionPhase(raw.getAngle() * 0.17595307917888564D);
      rc.setFrequency(raw.getFrequency());
      return rc;
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNADriverSerialMetroDIB dib = (VNADriverSerialMetroDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNADriverSerialMetroDIB dib = (VNADriverSerialMetroDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
      return context.getScanMode().isTransmissionMode() ? this.createCalibrationPointForTransmission(numLoop) : this.createCalibrationPointForReflection(numOpen);
   }

   private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample numOpen) {
      VNACalibrationPoint rc = null;
      rc = new VNACalibrationPoint();
      rc.setFrequency(numOpen.getFrequency());
      rc.setLoss(numOpen.getLoss());
      return rc;
   }

   private VNACalibrationPoint createCalibrationPointForTransmission(VNABaseSample numLoop) {
      VNACalibrationPoint rc = null;
      rc = new VNACalibrationPoint();
      rc.setFrequency(numLoop.getFrequency());
      rc.setLoss(numLoop.getLoss());
      return rc;
   }
}
