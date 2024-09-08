package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNATransmissionLossScale extends VNALossScale {
   public VNATransmissionLossScale() {
      super(VNAMessages.getString("Scale.Transmissionloss"), VNAMessages.getString("Scale.Transmissionloss.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, "dB", VNAFormatFactory.getReflectionLossFormat(), -999.0D, 999.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getTransmissionLoss(), height);
   }

   public int getScaledSampleValue(double value, int height) {
      return this.internalGetScaledSampleValue(value, height - 1);
   }
}
