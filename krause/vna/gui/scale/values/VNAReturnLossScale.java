package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNAReturnLossScale extends VNALossScale {
   public VNAReturnLossScale() {
      super(VNAMessages.getString("Scale.Returnloss"), VNAMessages.getString("Scale.Returnloss.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, "dB", VNAFormatFactory.getReflectionLossFormat(), -999.0D, 999.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      double rl = sample.getReflectionLoss();
      return this.internalGetScaledSampleValue(rl, height - 1);
   }

   public int getScaledSampleValue(double value, int height) {
      return this.internalGetScaledSampleValue(value, height - 1);
   }
}
