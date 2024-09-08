package krause.vna.device.sample;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSampleDIB extends VNADeviceInfoBlock {
   public VNADriverSampleDIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, true, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_RSS1, false, false, true, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TEST, true, true, true, true, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("Sample");
      this.setLongName("vna/J sample driver");
      this.setType("0");
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore", prefix);
      TraceHelper.exit(this, "restore");
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      TraceHelper.exit(this, "store");
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(100L);
      this.setMaxFrequency(9999999999L);
      this.setMinLoss(5.0D);
      this.setMaxLoss(-100.0D);
      this.setMinPhase(0.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(2000);
      this.setDdsTicksPerMHz(8589934L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      return driverBaudrate;
   }
}
