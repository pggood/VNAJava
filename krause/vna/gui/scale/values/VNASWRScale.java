package krause.vna.gui.scale.values;

import java.awt.Color;
import java.awt.Graphics;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNASWRScale extends VNAGenericScale {
   public static final double ABSOLUTE_MIN = 1.0D;
   public static final double ABSOLUTE_MAX = 50.0D;
   public static final double DEFAULT_MIN = 1.0D;
   public static final double DEFAULT_MAX = 5.0D;

   private double swr2Relative(double swr) {
      return Math.log10(swr);
   }

   private double relative2Swr(double relative) {
      return Math.pow(10.0D, relative);
   }

   public VNASWRScale() {
      super(VNAMessages.getString("Scale.SWR"), VNAMessages.getString("Scale.SWR.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_SWR, (String)null, VNAFormatFactory.getSwrFormat(), 1.0D, 50.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return this.getScaledSampleValue(sample.getSWR(), height);
   }

   public int getScaledSampleValue(double swr, int height) {
      int rc = 0;
      double newSwr = swr;
      if (swr > this.getCurrentMaxValue()) {
         newSwr = this.getCurrentMaxValue();
      } else if (swr < this.getCurrentMinValue()) {
         newSwr = this.getCurrentMinValue();
      }

      double scaleVal = this.swr2Relative(newSwr);
      double relativeMin = this.swr2Relative(this.getCurrentMinValue());
      double relativeMax = this.swr2Relative(this.getCurrentMaxValue());
      double range = relativeMax - relativeMin;
      scaleVal -= relativeMin;
      scaleVal /= range;
      rc = height - 1 - (int)((double)height * scaleVal);
      return rc;
   }

   public void paintScale(int width, int height, Graphics g) {
      String methodName = "paintScale";
      TraceHelper.entry(this, "paintScale");
      TraceHelper.text(this, "paintScale", "ScaleMax=%d", this.getCurrentMaxValue());
      TraceHelper.text(this, "paintScale", "ScaleMin=%d", this.getCurrentMinValue());
      TraceHelper.text(this, "paintScale", "RangeScale=%d", this.getRange());
      TraceHelper.text(this, "paintScale", "NoOfTicks=%d", this.getNoOfTicks());
      --height;
      g.setColor(Color.BLACK);
      g.setFont(g.getFont().deriveFont(10.0F));
      int[] tc = new int[this.getNoOfTicks() + 1];
      this.setTickCoordinates(tc);
      double stepDiagram = (double)height * 1.0D / (double)this.getNoOfTicks();
      double relMinValue = this.swr2Relative(this.getCurrentMinValue());
      double relMaxValue = this.swr2Relative(this.getCurrentMaxValue());
      double relRange = relMaxValue - relMinValue;
      double relStep = relRange / (double)this.getNoOfTicks();
      TraceHelper.text(this, "paintScale", "ScaleMaxRel=%d", relMaxValue);
      TraceHelper.text(this, "paintScale", "ScaleMinRel=%d", relMinValue);
      TraceHelper.text(this, "paintScale", "RangeRel=%d", relRange);
      TraceHelper.text(this, "paintScale", "StepRel=%d", relStep);

      for(int i = 0; i <= this.getNoOfTicks(); ++i) {
         int y = height - (int)((double)i * stepDiagram);
         g.drawLine(0, y, width, y);
         tc[i] = y;
         double curVal = relMinValue + (double)i * relStep;
         curVal = this.relative2Swr(curVal);
         if (i == this.getNoOfTicks()) {
            y += 9;
         }

         g.drawString(this.getFormat().format(curVal) + ":1", 1, y);
      }

   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
      TraceHelper.entry(this, "initScaleFromConfigOrDib");
      this.setAbsolutMinValue(1.0D);
      this.setAbsolutMaxValue(50.0D);
      this.setDefaultMinValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMinValue", 1.0D));
      this.setDefaultMaxValue(config.getDouble(this.getClass().getSimpleName() + ".defaultMaxValue", 5.0D));
      this.resetDefault();
      TraceHelper.exit(this, "initScaleFromConfigOrDib");
   }

   public boolean supportsCustomScaling() {
      return true;
   }
}
