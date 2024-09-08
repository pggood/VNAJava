package krause.vna.device.serial.max6_500;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialMax6_500_DIB extends VNADriverSerialMax6DIB {
   public static final float DDS_MHZ = 1250.0F;
   public static final int DEFAULT_TICKS = 3435973;

   public VNADriverSerialMax6_500_DIB() {
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, false, false, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_RSS1, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("MAX6-500MHz");
      this.setLongName("MAX6-500MHz - SP3SWJ");
      this.setType("5");
   }

   public void reset() {
      super.reset();
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
      this.setDdsTicksPerMHz(3435973L);
      this.setMinFrequency(100000L);
      this.setMaxFrequency(500000000L);
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
}
