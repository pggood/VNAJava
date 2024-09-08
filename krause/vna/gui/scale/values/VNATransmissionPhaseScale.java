package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNATransmissionPhaseScale extends VNAPhaseScale {
   public VNATransmissionPhaseScale() {
      super(VNAMessages.getString("Scale.TransmissionPhase"), VNAMessages.getString("Scale.TransmissionPhase.description"), VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getTransmissionPhase(), height);
   }

   public int getScaledSampleValue(double value, int height) {
      return this.internalGetScaleSampleValue(value, height - 1);
   }
}
