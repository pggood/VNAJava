package krause.vna.gui.scale.values;

import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNAReturnPhaseScale extends VNAPhaseScale {
   public VNAReturnPhaseScale() {
      super(VNAMessages.getString("Scale.ReflectionPhase"), VNAMessages.getString("Scale.ReflectionPhase.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      double ph = sample.getReflectionPhase();
      return this.internalGetScaleSampleValue(ph, height - 1);
   }

   public int getScaledSampleValue(double value, int height) {
      return this.internalGetScaleSampleValue(value, height - 1);
   }
}
