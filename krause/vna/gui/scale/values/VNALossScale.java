package krause.vna.gui.scale.values;

import java.text.NumberFormat;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols;

public abstract class VNALossScale extends VNALinearScale {
   public VNALossScale(String scaleName, String scaleDescription, VNAScaleSymbols.SCALE_TYPE scaleType, String pUnit, NumberFormat pFormat, double min, double max) {
      super(scaleName, scaleDescription, scaleType, pUnit, pFormat, min, max);
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMaxValue(block.getMinLoss());
      this.setAbsolutMinValue(block.getMaxLoss());
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", block.getMaxLoss()));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", block.getMinLoss()));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }

   protected int internalGetScaledSampleValue(double val, int height) {
      return (int)((double)height * 1.0D * ((this.getCurrentMaxValue() - val) / this.getRange()));
   }
}
