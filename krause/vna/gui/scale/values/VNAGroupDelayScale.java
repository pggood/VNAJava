package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNAGroupDelayScale extends VNALinearScale {
   private static final double ABSOLUTE_MAX = 1000.0D;
   private static final double ABSOLUTE_MIN = -1000.0D;
   private static final double DEFAULT_MAX = 100.0D;
   private static final double DEFAULT_MIN = -100.0D;

   public VNAGroupDelayScale() {
      super(VNAMessages.getString("Scale.GRPDLY"), VNAMessages.getString("Scale.GRPDLY.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY, "ns", VNAFormatFactory.getGroupDelayFormat(), -1000.0D, 1000.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getGroupDelay(), height);
   }

   public int getScaledSampleValue(double value, int height) {
      return height - (int)((double)height * ((value - this.getCurrentMinValue()) / this.getRange()));
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMaxValue(1000.0D);
      this.setAbsolutMinValue(-1000.0D);
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", -100.0D));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", 100.0D));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }
}
