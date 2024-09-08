package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNARsScale extends VNALinearScale {
   private static final double ABSOLUTE_MIN = -99999.0D;
   private static final double ABSOLUTE_MAX = 99999.0D;
   private static final double DEFAULT_MIN = -1000.0D;
   private static final double DEFAULT_MAX = 1000.0D;

   public VNARsScale() {
      super(VNAMessages.getString("Scale.RS"), VNAMessages.getString("Scale.RS.description"), VNAScaleSymbols.SCALE_TYPE.SCALE_RS, "Ohm", VNAFormatFactory.getRsFormat(), -1.0D, 99999.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getR(), height);
   }

   public int getScaledSampleValue(double x, int height) {
      int rc = 1;
      --height;
      if (this.getCurrentMaxValue() > 0.0D && this.getCurrentMinValue() >= 0.0D) {
         rc = (int)((double)height * ((x - this.getCurrentMinValue()) / this.getRange()));
      } else if (this.getCurrentMaxValue() > 0.0D && this.getCurrentMinValue() < 0.0D) {
         rc = (int)((double)height * ((x - this.getCurrentMinValue()) / this.getRange()));
      } else {
         rc = (int)((double)height * ((x - this.getCurrentMinValue()) / this.getRange()));
      }

      return height - rc;
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMinValue(-99999.0D);
      this.setAbsolutMaxValue(99999.0D);
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", -1000.0D));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", 1000.0D));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }
}
