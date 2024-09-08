package krause.vna.device.serial.max6;

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

public class VNADriverSerialMax6MathHelper extends VNADriverMathBaseHelper {
   public static final int DEFAULT_ADC_BITS = 1024;
   public static final double DEFAULT_PHASE_PER_BIT = 0.17595307917888564D;
   public static final double DEFAULT_TRANSMISSION_SCALE = 0.145D;
   public static final double DEFAULT_TRANSMISSION_OFFSET = 0.0D;
   public static final double DEFAULT_REFLECTION_OFFSET = 0.0D;
   public static final double DEFAULT_REFLECTION_SCALE = 0.05865103D;
   public static final double DEFAULT_RSS_OFFSET = 80.0D;
   public static final double DEFAULT_RSS_SCALE = 0.145D;
   public final double RAD2DEG = 57.29577951308232D;
   public final double R_50 = 50.0D;

   public VNADriverSerialMax6MathHelper(IVNADriver driver) {
      super(driver);
   }

   public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample rawSample, VNACalibrationPoint calibPoint) {
      VNADriverSerialMax6DIB myDib = (VNADriverSerialMax6DIB)context.getDib();
      if (context.getScanMode().isRss1Mode()) {
         return this.createCalibratedSampleForRss1(myDib, rawSample, calibPoint);
      } else if (context.getScanMode().isRss3Mode()) {
         return this.createCalibratedSampleForRss3(myDib, rawSample, calibPoint);
      } else {
         return context.getScanMode().isReflectionMode() ? this.createCalibratedSampleForReflectionMode0(myDib, rawSample, calibPoint) : null;
      }
   }

   private VNACalibratedSample createCalibratedSampleForReflectionMode0(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
      double rl = -((raw.getLoss() - calib.getLoss()) * dib.getReflectionScale() - dib.getReflectionOffset());
      double phase = raw.getAngle() * 0.17595307917888564D;
      double mag = Math.pow(10.0D, rl / 20.0D);
      double swr = Math.abs((1.0D + mag) / (1.0D - mag));
      double f = Math.cos(phase / 57.29577951308232D);
      double g = Math.sin(phase / 57.29577951308232D);
      double rr = f * mag;
      double ss = g * mag;
      double x_imp = Math.abs(2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * dib.getReferenceResistance().getReal());
      double r_imp = Math.abs((1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * dib.getReferenceResistance().getReal());
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

   private VNACalibratedSample createCalibratedSampleForRss1(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
      VNACalibratedSample rc = this.createCalibratedSampleForReflectionMode2(dib, raw, calib);
      rc.setTransmissionLoss(-((double)(calib.getRss1() - raw.getRss1()) * dib.getTransmissionScale()));
      rc.setRelativeSignalStrength1((double)raw.getRss1() * dib.getRss1Scale() - dib.getRss1Offset());
      return rc;
   }

   private VNACalibratedSample createCalibratedSampleForRss3(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
      VNACalibratedSample rc = this.createCalibratedSampleForRss1(dib, raw, calib);
      rc.setRelativeSignalStrength2((double)(raw.getRss2() - calib.getRss2()) * dib.getRss2Scale() - dib.getRss2Offset());
      rc.setRelativeSignalStrength3((double)(raw.getRss3() - calib.getRss3()) * dib.getRss3Scale() - dib.getRss3Offset());
      return rc;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample pOpen, VNABaseSample pShort, VNABaseSample pLoad, VNABaseSample pLoop) {
      if (context.getScanMode().isRss1Mode()) {
         return this.createCalibrationPointForRss1(pOpen, pLoop);
      } else if (context.getScanMode().isRss3Mode()) {
         return this.createCalibrationPointForRss3(pOpen, pLoop);
      } else {
         return context.getScanMode().isReflectionMode() ? this.createCalibrationPointForReflection(pOpen) : null;
      }
   }

   private VNACalibrationPoint createCalibrationPointForReflection(VNABaseSample numOpen) {
      VNACalibrationPoint rc = null;
      rc = new VNACalibrationPoint();
      rc.setFrequency(numOpen.getFrequency());
      rc.setLoss(numOpen.getLoss());
      return rc;
   }

   private VNACalibrationPoint createCalibrationPointForRss1(VNABaseSample numOpen, VNABaseSample numLoop) {
      VNACalibrationPoint rc = null;
      rc = new VNACalibrationPoint();
      rc.setFrequency(numLoop.getFrequency());
      rc.setLoss(numOpen.getLoss());
      rc.setRss1(numLoop.getRss1());
      return rc;
   }

   private VNACalibrationPoint createCalibrationPointForRss3(VNABaseSample numOpen, VNABaseSample numLoop) {
      VNACalibrationPoint rc = null;
      rc = new VNACalibrationPoint();
      rc.setFrequency(numLoop.getFrequency());
      rc.setLoss(numLoop.getLoss());
      rc.setRss1(numLoop.getRss1());
      rc.setRss2(numLoop.getRss2());
      rc.setRss3(numLoop.getRss3());
      return rc;
   }

   private VNACalibratedSample createCalibratedSampleForReflectionMode2(VNADriverSerialMax6DIB dib, VNABaseSample raw, VNACalibrationPoint calib) {
      double rl = -(-(calib.getLoss() - raw.getLoss()) * dib.getReflectionScale() - dib.getReflectionOffset());
      double phase = raw.getAngle() * 0.17595307917888564D;
      double mag = Math.pow(10.0D, rl / 20.0D);
      double swr = Math.abs((1.0D + mag) / (1.0D - mag));
      double f = Math.cos(phase / 57.29577951308232D);
      double g = Math.sin(phase / 57.29577951308232D);
      double rr = f * mag;
      double ss = g * mag;
      double x_imp = Math.abs(2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * 50.0D);
      double r_imp = Math.abs((1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * 50.0D);
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

   public void applyFilter(VNABaseSample[] samples) {
      String methodName = "applyFilter";
      TraceHelper.entry(this, "applyFilter");
      VNADeviceInfoBlock dib = this.getDriver().getDeviceInfoBlock();
      super.applyPreFilter(samples, dib);
      super.applyPostFilter(samples, dib);
      TraceHelper.exit(this, "applyFilter");
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      String methodName = "createCalibrationContextForCalibratedSamples";
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
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
      VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }
}
