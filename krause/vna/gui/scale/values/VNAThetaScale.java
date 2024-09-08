package krause.vna.gui.scale.values;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNAThetaScale extends VNALinearScale {
   private static final double ABSOLUTE_MIN = -95.0D;
   private static final double ABSOLUTE_MAX = 95.0D;
   private static final double DEFAULT_MIN = -90.0D;
   private static final double DEFAULT_MAX = 90.0D;

   public VNAThetaScale() {
      super(VNAMessages.getString("Scale.THETA"), VNAMessages.getString("Scale.THETA.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_THETA, "°", VNAFormatFactory.getThetaFormat(), -95.0D, 95.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getTheta(), height);
   }

   public int getScaledSampleValue(double theta, int height) {
      int rc = 0;
      --height;
      if (this.getCurrentMaxValue() > 0.0D && this.getCurrentMinValue() >= 0.0D) {
         rc = (int)((double)height * 1.0D * ((theta - this.getCurrentMinValue()) / this.getRange()));
      } else if (this.getCurrentMaxValue() > 0.0D && this.getCurrentMinValue() < 0.0D) {
         rc = (int)((double)height * 1.0D * ((theta - this.getCurrentMinValue()) / this.getRange()));
      } else {
         rc = (int)((double)height * 1.0D * ((theta - this.getCurrentMinValue()) / this.getRange()));
      }

      return height - rc;
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMinValue(-95.0D);
      this.setAbsolutMaxValue(95.0D);
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", -90.0D));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", 90.0D));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }
}
