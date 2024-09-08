package krause.vna.device.serial.std.lf;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.std.VNADriverSerialStdDIB;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public final class VNADriverSerialStdLfDIB extends VNADriverSerialStdDIB {
   public void reset() {
      super.reset();
      this.setMinFrequency(15000L);
      this.setMaxFrequency(1000000L);
      this.setMinLoss(5.0D);
      this.setMaxLoss(-70.0D);
      this.setMinPhase(0.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(2000);
      this.setDdsTicksPerMHz(10737904L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
   }

   public VNADriverSerialStdLfDIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA-LF");
      this.setLongName("mini radio solutions - miniVNA LF-Version");
      this.setType("10");
   }
}
