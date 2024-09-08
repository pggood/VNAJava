package krause.vna.device.serial.pro2;

import krause.common.TypedProperties;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;

public class VNADriverSerialPro2DIB extends VNASerialDeviceInfoBlock {
   public static final int AFTERCOMMANDDELAY = 50;
   public static final int BAURATE_BOOT = 115200;
   public static final int BAURATE_SCAN = 921600;
   public static final long DDS_TICKS = 8259595L;
   public static final double IF_PHASE_CORR = 1.1D;
   public static final long MAX_FREQ = 230000000L;
   public static final long MIN_FREQ = 10000L;
   public static final double MIN_PHASE = -180.0D;
   public static final double MAX_PHASE = 180.0D;
   public static final double MIN_LOSS = 0.0D;
   public static final double MAX_LOSS = -110.0D;
   public static final int RESOLUTION_16BIT = 1;
   public static final int RESOLUTION_24BIT = 2;
   public static final int SAMPLE_RATE = 4;
   private int bootloaderBaudrate;
   private int resolution;
   private int sampleRate;

   public VNADriverSerialPro2DIB() {
      TraceHelper.entry(this, "VNADriverSerialPro2DIB");
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA-pro2");
      this.setLongName("mini radio solutions - miniVNA pro2");
      this.setType("50");
      TraceHelper.exit(this, "VNADriverSerialPro2DIB");
   }

   public int getBootloaderBaudrate() {
      return this.bootloaderBaudrate;
   }

   public String getScanCommandReflection() {
      String rc = "1";
      switch(this.getResolution()) {
      case 1:
         rc = "1";
         break;
      case 2:
         rc = "101";
         break;
      default:
         ErrorLogHelper.text(this, "getScanCommandReflection", "Illegal resolution " + this.getResolution());
      }

      return rc;
   }

   public String getScanCommandTransmission() {
      String rc = "0";
      switch(this.getResolution()) {
      case 1:
         rc = "0";
         break;
      case 2:
         rc = "100";
         break;
      default:
         ErrorLogHelper.text(this, "getScanCommandTransmission", "Illegal resolution [" + this.getResolution() + "]");
      }

      return rc;
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(10000L);
      this.setMaxFrequency(230000000L);
      this.setMinLoss(0.0D);
      this.setMaxLoss(-110.0D);
      this.setMinPhase(-180.0D);
      this.setMaxPhase(180.0D);
      this.setDdsTicksPerMHz(8259595L);
      this.setAfterCommandDelay(50);
      this.setBaudrate(921600);
      this.setBootloaderBaudrate(115200);
      this.setResolution(2);
      this.setSampleRate(4);
      this.setPeakSuppression(false);
      this.setFirmwareFileFilter("PCV36*.bin");
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore", prefix);
      this.reset();
      super.restore(config, prefix);
      this.setBootloaderBaudrate(config.getInteger(prefix + "bootloaderBaudrate", this.getBootloaderBaudrate()));
      this.setSampleRate(config.getInteger(prefix + "sampleRate", this.getSampleRate()));
      this.setResolution(config.getInteger(prefix + "resolution", this.getResolution()));
      TraceHelper.exit(this, "restore");
   }

   public void setBootloaderBaudrate(int bootloaderBaudrate) {
      this.bootloaderBaudrate = bootloaderBaudrate;
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      super.store(config, prefix);
      config.putInteger(prefix + "bootloaderBaudrate", this.getBootloaderBaudrate());
      config.putInteger(prefix + "sampleRate", this.getSampleRate());
      config.putInteger(prefix + "resolution", this.getResolution());
      TraceHelper.exit(this, "store");
   }

   public int getResolution() {
      return this.resolution;
   }

   public void setResolution(int resolution) {
      this.resolution = resolution;
   }

   public int getSampleRate() {
      return this.sampleRate;
   }

   public void setSampleRate(int sampleRate) {
      this.sampleRate = sampleRate;
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      return driverBaudrate / 300;
   }
}
