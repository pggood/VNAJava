package krause.vna.device.serial.pro;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;

public class VNADriverSerialProDIB extends VNASerialDeviceInfoBlock {
   public static final int FILTER_NONE = 0;
   public static final int FIRMWARE_ORG = 0;
   public static final int FIRMWARE_2_3 = 1;
   private int firmwareVersion = 1;
   private boolean fixed6dBOnThru = true;
   private double attenOffsetI = 0.0D;
   private double attenOffsetQ = 0.0D;
   public static final float DDS_MHZ = 520.0F;
   public static final int DEFAULT_TICKS = 8259552;
   private static final long MIN_FREQ = 100000L;
   private static final long MAX_FREQ = 200000000L;
   private static final double MIN_LOSS = 10.0D;
   private static final double MAX_LOSS = -110.0D;

   public VNADriverSerialProDIB() {
      TraceHelper.entry(this, "VNADriverSerialProDIB");
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA-pro");
      this.setLongName("mini radio solutions - miniVNA pro");
      this.setType("2");
      TraceHelper.exit(this, "VNADriverSerialProDIB");
   }

   public int getFirmwareVersion() {
      return this.firmwareVersion;
   }

   public boolean isFixed6dBOnThru() {
      return this.fixed6dBOnThru;
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(100000L);
      this.setMaxFrequency(200000000L);
      this.setMinLoss(10.0D);
      this.setMaxLoss(-110.0D);
      this.setMinPhase(-180.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(1000);
      this.setDdsTicksPerMHz(8259552L);
      this.setAttenOffsetI(0.0D);
      this.setAttenOffsetQ(0.0D);
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore", prefix);
      this.reset();
      super.restore(config, prefix);
      this.setFirmwareVersion(config.getInteger(prefix + "firmwareVersion", this.getFirmwareVersion()));
      this.setFixed6dBOnThru(config.getBoolean(prefix + "fixed6Db", this.isFixed6dBOnThru()));
      this.setAttenOffsetI(config.getDouble(prefix + "attenuatorOffsetI", this.getAttenOffsetI()));
      this.setAttenOffsetQ(config.getDouble(prefix + "attenuatorOffsetQ", this.getAttenOffsetQ()));
      TraceHelper.exit(this, "restore");
   }

   public void setFirmwareVersion(int firmwareVersion) {
      this.firmwareVersion = firmwareVersion;
   }

   public void setFixed6dBOnThru(boolean fixed6dBOnThru) {
      this.fixed6dBOnThru = fixed6dBOnThru;
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      super.store(config, prefix);
      config.putInteger(prefix + "firmwareVersion", this.getFirmwareVersion());
      config.putBoolean(prefix + "fixed6Db", this.isFixed6dBOnThru());
      config.putDouble(prefix + "attenuatorOffsetI", this.getAttenOffsetI());
      config.putDouble(prefix + "attenuatorOffsetQ", this.getAttenOffsetQ());
      TraceHelper.exit(this, "store");
   }

   public double getAttenOffsetI() {
      return this.attenOffsetI;
   }

   public void setAttenOffsetI(double attenOffsetI) {
      this.attenOffsetI = attenOffsetI;
   }

   public double getAttenOffsetQ() {
      return this.attenOffsetQ;
   }

   public void setAttenOffsetQ(double attenOffsetQ) {
      this.attenOffsetQ = attenOffsetQ;
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      return driverBaudrate / 30;
   }
}
