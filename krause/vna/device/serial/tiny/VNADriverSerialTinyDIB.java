package krause.vna.device.serial.tiny;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;

public class VNADriverSerialTinyDIB extends VNASerialDeviceInfoBlock {
   public static final int AFTERCOMMANDDELAY = 0;
   public static final int BAURATE_BOOT = 230400;
   public static final int BAURATE_SCAN = 921600;
   public static final int CAL_STEPS = 1000;
   public static final long DDS_TICKS = 10000000L;
   public static final double GAIN_CORR = 1.0D;
   public static final double IF_PHASE_CORR = 1.1D;
   public static final long MAX_FREQ = 3000000000L;
   public static final long MIN_FREQ = 1000000L;
   public static final double MAX_CORR_GAIN = 2.0D;
   public static final double MAX_CORR_IF_PHASE = 20.0D;
   public static final double MAX_CORR_PHASE = 20.0D;
   public static final double MAX_CORR_TEMP = 0.5D;
   public static final double MIN_CORR_GAIN = 0.5D;
   public static final double MIN_CORR_IF_PHASE = -20.0D;
   public static final double MIN_CORR_PHASE = -20.0D;
   public static final double MIN_CORR_TEMP = -0.5D;
   public static final double MIN_PHASE = -180.0D;
   public static final double MAX_PHASE = 180.0D;
   public static final int PRESCALER_DEFAULT = 10;
   public static final double PHASE_CORR = 0.0D;
   public static final double TEMP_CORR = 0.011D;
   public static final double TEMP_REFERENCE = 40.0D;
   public static final double MIN_LOSS = 0.0D;
   public static final double MAX_LOSS = -120.0D;
   public static final int MAX_BOOTBAUD = 921600;
   public static final int MIN_BOOTBAUD = 19200;
   public static final int AUTOCAL_NUM_SAMPLES = 800;
   public static final long AUTOCAL_START_FREQ = 100000000L;
   public static final long AUTOCAL_STOP_FREQ = 200000000L;
   public static final int AUTOCAL_NUM_OVERSAMPLES = 4;
   private int bootloaderBaudrate;
   private double gainCorrection;
   private double ifPhaseCorrection;
   private double phaseCorrection;
   private int prescaler;
   private String scanCommandReflection;
   private String scanCommandTransmission;
   private double tempCorrection;

   public VNADriverSerialTinyDIB() {
      TraceHelper.entry(this, "VNADriverSerialTinyDIB");
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA Tiny");
      this.setLongName("mini radio solutions - miniVNA Tiny");
      this.setType("20");
      TraceHelper.exit(this, "VNADriverSerialTinyDIB");
   }

   public int getBootloaderBaudrate() {
      return this.bootloaderBaudrate;
   }

   public double getGainCorrection() {
      return this.gainCorrection;
   }

   public double getIfPhaseCorrection() {
      return this.ifPhaseCorrection;
   }

   public double getPhaseCorrection() {
      return this.phaseCorrection;
   }

   public int getPrescaler() {
      return this.prescaler;
   }

   public String getScanCommandReflection() {
      return this.scanCommandReflection;
   }

   public String getScanCommandTransmission() {
      return this.scanCommandTransmission;
   }

   public double getTempCorrection() {
      return this.tempCorrection;
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(1000000L);
      this.setMaxFrequency(3000000000L);
      this.setMinLoss(0.0D);
      this.setMaxLoss(-120.0D);
      this.setMinPhase(-180.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(1000);
      this.setNumberOfOverscans4Calibration(1);
      this.setPrescaler(10);
      this.setScanCommandTransmission("6");
      this.setScanCommandReflection("7");
      this.setDdsTicksPerMHz(10000000L);
      this.setAfterCommandDelay(0);
      this.setBaudrate(921600);
      this.setBootloaderBaudrate(230400);
      this.setPhaseCorrection(0.0D);
      this.setGainCorrection(1.0D);
      this.setTempCorrection(0.011D);
      this.setIfPhaseCorrection(1.1D);
      this.setFilterMode(0);
      this.setPeakSuppression(true);
   }

   public void restore(TypedProperties config, String prefix) {
      this.reset();
      super.restore(config, prefix);
      this.setPhaseCorrection(config.getDouble(prefix + "phaseCorrection", this.getPhaseCorrection()));
      this.setGainCorrection(config.getDouble(prefix + "gainCorrection", this.getGainCorrection()));
      this.setTempCorrection(config.getDouble(prefix + "tempCorrection", this.getTempCorrection()));
      this.setIfPhaseCorrection(config.getDouble(prefix + "ifPhaseCorrection", this.getIfPhaseCorrection()));
      this.setBootloaderBaudrate(config.getInteger(prefix + "bootloaderBaudrate", this.getBootloaderBaudrate()));
   }

   public void setBootloaderBaudrate(int bootloaderBaudrate) {
      this.bootloaderBaudrate = bootloaderBaudrate;
   }

   public void setGainCorrection(double gainCorrection) {
      this.gainCorrection = gainCorrection;
   }

   public void setIfPhaseCorrection(double ifPhaseCorrection) {
      this.ifPhaseCorrection = ifPhaseCorrection;
   }

   public void setPhaseCorrection(double phaseCorrection) {
      this.phaseCorrection = phaseCorrection;
   }

   public void setPrescaler(int prescaler) {
      this.prescaler = prescaler;
   }

   public void setScanCommandReflection(String scanCommandReflection) {
      this.scanCommandReflection = scanCommandReflection;
   }

   public void setScanCommandTransmission(String scanCommandTransmission) {
      this.scanCommandTransmission = scanCommandTransmission;
   }

   public void setTempCorrection(double tempCorrection) {
      this.tempCorrection = tempCorrection;
   }

   public void store(TypedProperties config, String prefix) {
      super.store(config, prefix);
      config.putDouble(prefix + "phaseCorrection", this.getPhaseCorrection());
      config.putDouble(prefix + "gainCorrection", this.getGainCorrection());
      config.putDouble(prefix + "tempCorrection", this.getTempCorrection());
      config.putDouble(prefix + "ifPhaseCorrection", this.getIfPhaseCorrection());
   }

   public long[] getSwitchPoints() {
      return new long[]{1045000000L, 1525000000L};
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      return driverBaudrate / 300;
   }
}
