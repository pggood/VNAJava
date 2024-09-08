package krause.vna.device.serial.std2;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialStd2DIB extends VNASerialDeviceInfoBlock {
   public static final int DEFAULT_TICKS = 10737904;

   public VNADriverSerialStd2DIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, false, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA V2");
      this.setLongName("mini radio solutions - miniVNA V2");
      this.setType("51");
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(100000L);
      this.setMaxFrequency(180000000L);
      this.setMinLoss(5.0D);
      this.setMaxLoss(-70.0D);
      this.setMinPhase(-180.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(3000);
      this.setDdsTicksPerMHz(10737904L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
   }

   public int calculateRealBaudrate(int driverBaudrate) {
      String methodName = "calculateRealBaudrate";
      TraceHelper.entry(this, "calculateRealBaudrate", "driverBaudrate=%d", driverBaudrate);
      int realBaudrate = driverBaudrate / 30;
      TraceHelper.exitWithRC(this, "calculateRealBaudrate", "usedBaurate=%d", realBaudrate);
      return realBaudrate;
   }
}
