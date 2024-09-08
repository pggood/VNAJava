package krause.vna.device.serial.max6;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialMax6DIB extends VNASerialDeviceInfoBlock {
   public static final float DDS_MHZ = 400.0F;
   public static final int DEFAULT_TICKS = 10737418;
   public static final int MAX_LEVEL = 16383;
   public static final int MIN_LEVEL = 0;
   private double levelMin;
   private double levelMax;
   private double ReflectionScale;
   private double reflectionOffset;
   private double transmissionScale;
   private double transmissionOffset;
   private double rss1Scale;
   private double rss1Offset;
   private double rss2Scale;
   private double rss2Offset;
   private double rss3Scale;
   private double rss3Offset;

   public VNADriverSerialMax6DIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_RSS1, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
      this.setShortName("MAX6");
      this.setLongName("MAX6 - SP3SWJ");
      this.setType("4");
   }

   public double getLevelMax() {
      return this.levelMax;
   }

   public double getLevelMin() {
      return this.levelMin;
   }

   public double getReflectionOffset() {
      return this.reflectionOffset;
   }

   public double getReflectionScale() {
      return this.ReflectionScale;
   }

   public double getRss1Offset() {
      return this.rss1Offset;
   }

   public double getRss1Scale() {
      return this.rss1Scale;
   }

   public double getRss2Offset() {
      return this.rss2Offset;
   }

   public double getRss2Scale() {
      return this.rss2Scale;
   }

   public double getRss3Offset() {
      return this.rss3Offset;
   }

   public double getRss3Scale() {
      return this.rss3Scale;
   }

   public double getTransmissionOffset() {
      return this.transmissionOffset;
   }

   public double getTransmissionScale() {
      return this.transmissionScale;
   }

   public void reset() {
      super.reset();
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
      this.setDdsTicksPerMHz(10737418L);
      this.setMinFrequency(100000L);
      this.setMaxFrequency(180000000L);
      this.setMinLoss(5.0D);
      this.setMaxLoss(-80.0D);
      this.setMinPhase(0.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(2000);
      this.setLevelMax(20.0D);
      this.setLevelMin(-80.0D);
      this.setRss1Scale(0.145D);
      this.setRss1Offset(80.0D);
      this.setRss2Scale(0.145D);
      this.setRss2Offset(80.0D);
      this.setRss3Scale(0.145D);
      this.setRss3Offset(80.0D);
      this.setReflectionOffset(0.0D);
      this.setReflectionScale(0.05865103D);
      this.setTransmissionOffset(0.0D);
      this.setTransmissionScale(0.145D);
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore", prefix);
      this.reset();
      super.restore(config, prefix);
      this.setRss1Scale(config.getDouble(prefix + "rss1Scale", 0.145D));
      this.setRss1Offset(config.getDouble(prefix + "rss1Offset", 80.0D));
      this.setRss2Scale(config.getDouble(prefix + "rss2Scale", 0.145D));
      this.setRss2Offset(config.getDouble(prefix + "rss2Offset", 80.0D));
      this.setRss3Scale(config.getDouble(prefix + "rss3Scale", 0.145D));
      this.setRss3Offset(config.getDouble(prefix + "rss3Offset", 80.0D));
      this.setReflectionScale(config.getDouble(prefix + "reflectionScale", 0.05865103D));
      this.setReflectionOffset(config.getDouble(prefix + "reflectionOffset", 0.0D));
      this.setTransmissionScale(config.getDouble(prefix + "transmissionScale", 0.145D));
      this.setTransmissionOffset(config.getDouble(prefix + "transmissionOffset", 0.0D));
      TraceHelper.exit(this, "restore");
   }

   public void setLevelMax(double levelMax) {
      this.levelMax = levelMax;
   }

   public void setLevelMin(double levelMin) {
      this.levelMin = levelMin;
   }

   public void setReflectionOffset(double reflectionOffset) {
      this.reflectionOffset = reflectionOffset;
   }

   public void setReflectionScale(double reflectionScale) {
      this.ReflectionScale = reflectionScale;
   }

   public void setRss1Offset(double rss1Offset) {
      this.rss1Offset = rss1Offset;
   }

   public void setRss1Scale(double rss1Scale) {
      this.rss1Scale = rss1Scale;
   }

   public void setRss2Offset(double rss2Offset) {
      this.rss2Offset = rss2Offset;
   }

   public void setRss2Scale(double rss2Scale) {
      this.rss2Scale = rss2Scale;
   }

   public void setRss3Offset(double rss3Offset) {
      this.rss3Offset = rss3Offset;
   }

   public void setRss3Scale(double rss3Scale) {
      this.rss3Scale = rss3Scale;
   }

   public void setTransmissionOffset(double transmissionOffset) {
      this.transmissionOffset = transmissionOffset;
   }

   public void setTransmissionScale(double transmissionScale) {
      this.transmissionScale = transmissionScale;
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      super.store(config, prefix);
      config.putDouble(prefix + "rss1Scale", this.getRss1Scale());
      config.putDouble(prefix + "rss1Offset", this.getRss1Offset());
      config.putDouble(prefix + "rss2Scale", this.getRss2Scale());
      config.putDouble(prefix + "rss2Offset", this.getRss2Offset());
      config.putDouble(prefix + "rss3Scale", this.getRss3Scale());
      config.putDouble(prefix + "rss3Offset", this.getRss3Offset());
      config.putDouble(prefix + "reflectionOffset", this.getReflectionOffset());
      config.putDouble(prefix + "reflectionScale", this.getReflectionScale());
      config.putDouble(prefix + "transmissionOffset", this.getTransmissionOffset());
      config.putDouble(prefix + "transmissionScale", this.getTransmissionScale());
      TraceHelper.exit(this, "store");
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      return driverBaudrate / 10;
   }
}
