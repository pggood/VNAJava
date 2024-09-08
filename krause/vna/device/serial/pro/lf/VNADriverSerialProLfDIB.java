package krause.vna.device.serial.pro.lf;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialProLfDIB extends VNADriverSerialProDIB {
   public void reset() {
      super.reset();
      this.setMinFrequency(15000L);
      this.setMaxFrequency(1000000L);
      this.setMinLoss(10.0D);
      this.setMaxLoss(-90.0D);
      this.setMinPhase(-180.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(2000);
      this.setDdsTicksPerMHz(8259552L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
   }

   public VNADriverSerialProLfDIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA-pro-LF");
      this.setLongName("mini radio solutions - miniVNA pro - LF version");
      this.setType("12");
   }
}
