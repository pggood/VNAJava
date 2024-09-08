package krause.vna.gui.scale;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.NumberFormat;
import javax.swing.UIManager;
import krause.common.TypedProperties;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;

public abstract class VNAGenericScale {
   private double absolutMaxValue = Double.MAX_VALUE;
   private double absolutMinValue = Double.MIN_VALUE;
   private double currentMaxValue = Double.MAX_VALUE;
   private double currentMinValue = Double.MIN_VALUE;
   private double defaultMaxValue = Double.MAX_VALUE;
   private double defaultMinValue = Double.MIN_VALUE;
   private Double guideLineValue = null;
   private String description = null;
   private final Font font = new Font("Dialog", 0, 10);
   private final Color fontColor = UIManager.getColor("Panel.foreground");
   private NumberFormat format = null;
   private String name = null;
   private int noOfTicks = 10;
   private double range = 1.0D;
   private int[] tickCoordinates = new int[0];
   private VNAScaleSymbols.SCALE_TYPE type;
   private String unit;

   public VNAGenericScale(String scaleName, String scaleDescription, VNAScaleSymbols.SCALE_TYPE scaleType, String pUnit, NumberFormat pFormat, double absMinVal, double absMaxVal) {
      this.type = VNAScaleSymbols.SCALE_TYPE.SCALE_NONE;
      this.unit = null;
      this.name = scaleName;
      this.setDescription(scaleDescription);
      this.type = scaleType;
      this.unit = pUnit;
      this.format = pFormat;
      this.absolutMaxValue = absMaxVal;
      this.absolutMinValue = absMinVal;
      this.resetDefault();
   }

   public double getAbsolutMaxValue() {
      return this.absolutMaxValue;
   }

   public double getAbsolutMinValue() {
      return this.absolutMinValue;
   }

   public double getCurrentMaxValue() {
      return this.currentMaxValue;
   }

   public double getCurrentMinValue() {
      return this.currentMinValue;
   }

   public VNAMinMaxPair getCurrentMinMaxValue() {
      return new VNAMinMaxPair(this.currentMinValue, this.currentMaxValue);
   }

   public double getDefaultMaxValue() {
      return this.defaultMaxValue;
   }

   public double getDefaultMinValue() {
      return this.defaultMinValue;
   }

   public String getDescription() {
      return this.description;
   }

   public Font getFont() {
      return this.font;
   }

   public NumberFormat getFormat() {
      return this.format;
   }

   public String getName() {
      return this.name;
   }

   public int getNoOfTicks() {
      return this.noOfTicks;
   }

   public double getRange() {
      return this.range;
   }

   public abstract int getScaledSampleValue(double var1, int var3);

   public abstract int getScaledSampleValue(VNACalibratedSample var1, int var2);

   public int[] getTickCoordinates() {
      return this.tickCoordinates;
   }

   public VNAScaleSymbols.SCALE_TYPE getType() {
      return this.type;
   }

   public String getUnit() {
      return this.unit;
   }

   public abstract void initScaleFromConfigOrDib(VNADeviceInfoBlock var1, VNAConfig var2);

   public abstract void paintScale(int var1, int var2, Graphics var3);

   public void rescale() {
      if (this.currentMaxValue > 0.0D && this.currentMinValue >= 0.0D) {
         this.range = this.currentMaxValue - this.currentMinValue;
      } else if (this.currentMaxValue > 0.0D && this.currentMinValue < 0.0D) {
         this.range = this.currentMaxValue - this.currentMinValue;
      } else {
         this.range = -this.currentMinValue + this.currentMaxValue;
      }

   }

   public final void resetDefault() {
      this.currentMaxValue = this.defaultMaxValue;
      this.currentMinValue = this.defaultMinValue;
      this.rescale();
   }

   public void restoreFromProperties(TypedProperties props) {
      this.currentMaxValue = props.getDouble(this.type + ".currentMaxValue", this.defaultMaxValue);
      this.currentMinValue = props.getDouble(this.type + ".currentMinValue", this.defaultMinValue);
   }

   public void saveToProperties(TypedProperties props) {
      props.putDouble(this.type + ".currentMaxValue", this.currentMaxValue);
      props.putDouble(this.type + ".currentMinValue", this.currentMinValue);
   }

   public void setAbsolutMaxValue(double absolutMaxValue) {
      this.absolutMaxValue = absolutMaxValue;
   }

   public void setAbsolutMinValue(double absolutMinValue) {
      this.absolutMinValue = absolutMinValue;
   }

   public final void setCurrentMaxValue(double maxValue) {
      if (maxValue > this.absolutMaxValue) {
         this.currentMaxValue = this.absolutMaxValue;
      } else if (maxValue < this.absolutMinValue) {
         this.currentMaxValue = this.absolutMinValue;
      } else {
         this.currentMaxValue = maxValue;
      }

   }

   public final void setCurrentMinValue(double minValue) {
      if (minValue < this.absolutMinValue) {
         this.currentMinValue = this.absolutMinValue;
      } else if (minValue > this.absolutMaxValue) {
         this.currentMinValue = this.absolutMaxValue;
      } else {
         this.currentMinValue = minValue;
      }

   }

   public void setCurrentMinMaxValue(VNAMinMaxPair mm) {
      if (mm.getMaxValue() > this.getAbsolutMaxValue()) {
         this.currentMaxValue = this.getAbsolutMaxValue();
      } else {
         this.currentMaxValue = mm.getMaxValue();
      }

      if (mm.getMinValue() < this.getAbsolutMinValue()) {
         this.currentMinValue = this.getAbsolutMinValue();
      } else {
         this.currentMinValue = mm.getMinValue();
      }

   }

   public final void setDefaultMaxValue(double defaultMaxValue) {
      this.defaultMaxValue = defaultMaxValue;
   }

   public final void setDefaultMinValue(double defaultMinValue) {
      this.defaultMinValue = defaultMinValue;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setFormat(NumberFormat format) {
      this.format = format;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setNoOfTicks(int noOfTicks) {
      this.noOfTicks = noOfTicks;
   }

   public void setTickCoordinates(int[] tickCoordinates) {
      this.tickCoordinates = tickCoordinates;
   }

   public void setType(VNAScaleSymbols.SCALE_TYPE type) {
      this.type = type;
   }

   public void setUnit(String unit) {
      this.unit = unit;
   }

   public boolean supportsCustomScaling() {
      return false;
   }

   public String toString() {
      return this.unit != null ? this.name + " (" + this.unit + ")" : this.name;
   }

   public Color getFontColor() {
      return this.fontColor;
   }

   public Double getGuideLineValue() {
      return this.guideLineValue;
   }

   public void setGuideLineValue(Double glv) {
      this.guideLineValue = glv;
   }

   public String getFormattedValueAsString(double val) {
      return this.getFormat().format(val);
   }

   public String getFormattedValueAsStringWithUnit(double val) {
      return this.getUnit() != null ? this.getFormat().format(val) + this.getUnit() : this.getFormat().format(val);
   }
}
