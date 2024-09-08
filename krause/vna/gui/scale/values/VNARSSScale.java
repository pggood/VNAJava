package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNARSSScale extends VNALinearScale {
   private static final double ABSOLUTE_MIN = -80.0D;
   private static final double ABSOLUTE_MAX = 10.0D;
   private static final double DEFAULT_MIN = -80.0D;
   private static final double DEFAULT_MAX = 0.0D;

   public VNARSSScale() {
      super(VNAMessages.getString("Scale.RSS"), VNAMessages.getString("Scale.RSS.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_RSS, "dBm", VNAFormatFactory.getRSSFormat(), -99999.0D, 99999.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getRelativeSignalStrength1(), height);
   }

   public int getScaledSampleValue(double rss, int height) {
      return height - (int)((double)height * ((rss - this.getCurrentMinValue()) / this.getRange()));
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMinValue(-80.0D);
      this.setAbsolutMaxValue(10.0D);
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", -80.0D));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", 0.0D));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }
}
