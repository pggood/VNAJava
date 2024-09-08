package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;

public abstract class VNAPhaseScale extends VNALinearScale {
   public VNAPhaseScale(String scaleName, String desc, VNAScaleSymbols.SCALE_TYPE scaleType) {
      super(scaleName, desc, scaleType, "°", VNAFormatFactory.getPhaseFormat(), -180.0D, 180.0D);
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMaxValue(block.getMaxPhase());
      this.setAbsolutMinValue(block.getMinPhase());
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", block.getMinPhase()));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", block.getMaxPhase()));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }

   protected int internalGetScaleSampleValue(double val, int height) {
      int rc = (int)((double)height * 1.0D * ((val - this.getCurrentMinValue()) / this.getRange()));
      rc = height - rc;
      return rc;
   }
}
