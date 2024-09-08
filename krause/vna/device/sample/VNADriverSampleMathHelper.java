package krause.vna.device.sample;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverMathBaseHelper;

public class VNADriverSampleMathHelper extends VNADriverMathBaseHelper {
   private static final int DEFAULT_ADC_BITS = 1024;
   private static final double DEFAULT_PHASE_PER_BIT = 0.17595307917888564D;
   private static final double DEFAULT_RETURNLOSS_PER_BIT = 0.05865102639296188D;
   private static final double RAD2DEG = 57.29577951308232D;

   public VNADriverSampleMathHelper(IVNADriver driver) {
      super(driver);
   }

   private VNACalibratedSample createDummyCalibratedSample(VNABaseSample raw, VNACalibrationPoint calib) {
      double rl = (raw.getLoss() - calib.getLoss()) * 0.05865102639296188D;
      double phase = raw.getAngle() * 0.17595307917888564D;
      double mag = Math.pow(10.0D, -rl / 20.0D);
      double swr = Math.abs((1.0D + mag) / (1.0D - mag));
      double f = Math.cos(phase / 57.29577951308232D);
      double g = Math.sin(phase / 57.29577951308232D);
      double rr = f * mag;
      double ss = g * mag;
      double xImp = Math.abs(2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * 50.0D);
      double rImp = Math.abs((1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * 50.0D);
      double zImp = Math.sqrt(rImp * rImp + xImp * xImp);
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(raw.getFrequency());
      rc.setMag(mag);
      rc.setReflectionLoss(rl);
      rc.setTransmissionLoss(-rl);
      rc.setSWR(swr);
      rc.setReflectionPhase(phase);
      rc.setR(rImp);
      rc.setX(xImp);
      rc.setZ(zImp);
      rc.setRelativeSignalStrength1((double)raw.getRss1() * 0.05865102639296188D);
      rc.setRelativeSignalStrength2((double)raw.getRss2() * 0.05865102639296188D);
      rc.setRelativeSignalStrength3((double)raw.getRss3() * 0.05865102639296188D);
      return rc;
   }

   public VNACalibratedSample createCalibratedSample(VNACalibrationContext context, VNABaseSample sample, VNACalibrationPoint calib) {
      VNACalibratedSample rc = null;
      rc = this.createDummyCalibratedSample(sample, calib);
      return rc;
   }

   public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample numOpen, VNABaseSample numShort, VNABaseSample numLoad, VNABaseSample numLoop) {
      VNACalibrationPoint rc = new VNACalibrationPoint();
      VNAScanMode mode = context.getScanMode();
      if (mode.isTransmissionMode()) {
         rc.setFrequency(numShort.getFrequency());
         rc.setLoss(numShort.getLoss());
         rc.setRss1(numShort.getRss1());
         rc.setRss2(numShort.getRss2());
         rc.setRss3(numShort.getRss3());
      } else if (mode.isReflectionMode()) {
         rc.setFrequency(numOpen.getFrequency());
         rc.setLoss(numOpen.getLoss());
         rc.setRss1(numOpen.getRss1());
         rc.setRss2(numOpen.getRss2());
         rc.setRss3(numOpen.getRss3());
      } else if (mode.isRss1Mode()) {
         rc.setFrequency(numLoad.getFrequency());
         rc.setLoss(numLoad.getLoss());
         rc.setRss1(numLoad.getRss1());
         rc.setRss2(numLoad.getRss2());
         rc.setRss3(numLoad.getRss3());
      } else if (mode.isRss2Mode()) {
         rc.setFrequency(numLoop.getFrequency());
         rc.setLoss(numLoop.getLoss());
         rc.setRss1(numLoop.getRss1());
         rc.setRss2(numLoop.getRss2());
         rc.setRss3(numLoop.getRss3());
      }

      return rc;
   }

   public void applyFilter(VNABaseSample[] samples) {
      TraceHelper.entry(this, "applyFilter");
      VNADeviceInfoBlock dib = this.getDriver().getDeviceInfoBlock();
      super.applyPreFilter(samples, dib);
      super.applyPostFilter(samples, dib);
      TraceHelper.exit(this, "applyFilter");
   }

   public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit kit) {
      String methodName = "createCalibrationContextForCalibrationPoints";
      TraceHelper.entry(this, "createCalibrationContextForCalibrationPoints");
      VNADriverSampleDIB dib = (VNADriverSampleDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibrationPoints");
      return context;
   }

   public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock) {
      TraceHelper.entry(this, "createCalibrationContextForCalibratedSamples");
      VNADriverSampleDIB dib = (VNADriverSampleDIB)calBlock.getMathHelper().getDriver().getDeviceInfoBlock();
      VNACalibrationContext context = new VNACalibrationContext();
      context.setDib(dib);
      context.setCalibrationBlock(calBlock);
      context.setScanMode(calBlock.getScanMode());
      TraceHelper.exit(this, "createCalibrationContextForCalibratedSamples");
      return context;
   }
}
