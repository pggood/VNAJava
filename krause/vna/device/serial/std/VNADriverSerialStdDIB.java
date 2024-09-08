package krause.vna.device.serial.std;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialStdDIB extends VNASerialDeviceInfoBlock {
   public static final int DEFAULT_TICKS = 10737904;

   public VNADriverSerialStdDIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA");
      this.setLongName("mini radio solutions - miniVNA");
      this.setType("1");
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(100000L);
      this.setMaxFrequency(180000000L);
      this.setMinLoss(5.0D);
      this.setMaxLoss(-60.0D);
      this.setMinPhase(0.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(3000);
      this.setDdsTicksPerMHz(10737904L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
   }

   public void restore(TypedProperties config, String prefix) {
      String methodName = "restore";
      TraceHelper.entry(this, "restore");
      this.reset();
      super.restore(config, prefix);
      this.setMaxLoss(config.getDouble(prefix + "maxLoss", this.getMaxLoss()));
      TraceHelper.exit(this, "restore");
   }

   public void store(TypedProperties config, String prefix) {
      String methodName = "store";
      TraceHelper.entry(this, "store");
      super.store(config, prefix);
      config.putDouble(prefix + "maxLoss", this.getMaxLoss());
      TraceHelper.exit(this, "store");
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      String methodName = "calculateRealBaudrate";
      TraceHelper.entry(this, "calculateRealBaudrate", "in=%d", driverBaudrate);
      int realBaudrate = driverBaudrate / 15;
      TraceHelper.exitWithRC(this, "calculateRealBaudrate", "out=%d", realBaudrate);
      return realBaudrate;
   }
}
