package krause.vna.device.serial.metro;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialMetroDIB extends VNASerialDeviceInfoBlock {
   public static final float DDS_MHZ = 400.0F;
   public static final int DEFAULT_TICKS = 10737418;
   public static final long MAX_FREQUENCY = 4400000000L;
   public static final double MAX_RETURNLOSS = -60.0D;
   public static final double MAX_TRANSMISSIONLOSS = -76.0D;
   public static final double MAX_PHASE = 180.0D;
   public static final long MIN_FREQUENCY = 100000L;
   public static final double MIN_LOSS = 5.0D;
   public static final double MIN_PHASE = 0.0D;
   public static final boolean INVERT_REFLECTION_LOSS = true;
   public static final boolean INVERT_TRANSMISSION_LOSS = true;
   private double maxReflectionLoss;
   private double maxTransmissionLoss;

   public VNADriverSerialMetroDIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("MetroVNA");
      this.setLongName("IZ7LDG - MetroVNA");
      this.setType("30");
   }

   public double getMaxReflectionLoss() {
      return this.maxReflectionLoss;
   }

   public double getMaxTransmissionLoss() {
      return this.maxTransmissionLoss;
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(100000L);
      this.setMaxFrequency(180000000L);
      this.setMinPhase(0.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(1000);
      this.setDdsTicksPerMHz(10737418L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
      this.setMaxReflectionLoss(-60.0D);
      this.setMaxTransmissionLoss(-76.0D);
      this.setMinLoss(5.0D);
      this.setMaxLoss(-76.0D);
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore", prefix);
      this.reset();
      super.restore(config, prefix);
      this.setMaxTransmissionLoss(config.getDouble(prefix + "maxTransmissionLoss", this.getMaxTransmissionLoss()));
      this.setMaxReflectionLoss(config.getDouble(prefix + "maxReflectionLoss", this.getMaxReflectionLoss()));
      TraceHelper.exit(this, "restore");
   }

   public void setMaxReflectionLoss(double maxReflectionLoss) {
      this.maxReflectionLoss = maxReflectionLoss;
   }

   public void setMaxTransmissionLoss(double maxTransmissionLoss) {
      this.maxTransmissionLoss = maxTransmissionLoss;
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      super.store(config, prefix);
      config.putDouble(prefix + "maxTransmissionLoss", this.getMaxTransmissionLoss());
      config.putDouble(prefix + "maxReflectionLoss", this.getMaxReflectionLoss());
      TraceHelper.exit(this, "store");
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      return driverBaudrate / 30;
   }
}
