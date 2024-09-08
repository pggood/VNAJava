package krause.vna.data;

import java.io.Serializable;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.jdom.Element;

public class VNAMinMaxPair implements Serializable {
   private double minValue = Double.MAX_VALUE;
   private double maxValue = -1.7976931348623157E308D;
   private int minIndex = -1;
   private int maxIndex = -1;
   private VNAScaleSymbols.SCALE_TYPE type;

   public VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE ptype) {
      this.setType(ptype);
   }

   public VNAMinMaxPair() {
   }

   public VNAMinMaxPair(double pMin, double pMax) {
      this.minValue = pMin;
      this.maxValue = pMax;
   }

   public void consume(VNAMinMaxPair pair) {
      this.consume(pair.getMinValue(), pair.getMinIndex());
      this.consume(pair.getMaxValue(), pair.getMaxIndex());
   }

   public void consume(double val, int idx) {
      if (val < this.minValue) {
         this.minValue = val;
         this.minIndex = idx;
      }

      if (val > this.maxValue) {
         this.maxValue = val;
         this.maxIndex = idx;
      }

   }

   public double getMinValue() {
      return this.minValue;
   }

   public void setMinValue(double minValue) {
      this.minValue = minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(double maxValue) {
      this.maxValue = maxValue;
   }

   public int getMinIndex() {
      return this.minIndex;
   }

   public void setMinIndex(int minIndex) {
      this.minIndex = minIndex;
   }

   public int getMaxIndex() {
      return this.maxIndex;
   }

   public void setMaxIndex(int maxIndex) {
      this.maxIndex = maxIndex;
   }

   public void setType(VNAScaleSymbols.SCALE_TYPE type) {
      this.type = type;
   }

   public VNAScaleSymbols.SCALE_TYPE getType() {
      return this.type;
   }

   public Element asElement() {
      Element rc = new Element(this.getType().toString());
      rc.addContent((new Element("min")).setText(Double.toString(this.getMinValue())));
      rc.addContent((new Element("max")).setText(Double.toString(this.getMaxValue())));
      rc.addContent((new Element("minindex")).setText(Integer.toString(this.getMinIndex())));
      rc.addContent((new Element("maxindex")).setText(Integer.toString(this.getMaxIndex())));
      return rc;
   }

   public static VNAMinMaxPair fromElement(Element root, String name) {
      VNAMinMaxPair rc = new VNAMinMaxPair();
      Element e = root.getChild(name);
      if (e != null) {
         rc.setMinValue(Double.parseDouble(e.getChildText("min")));
         rc.setMaxValue(Double.parseDouble(e.getChildText("max")));
         rc.setMinIndex(Integer.parseInt(e.getChildText("minindex")));
         rc.setMaxIndex(Integer.parseInt(e.getChildText("maxindex")));
      }

      return rc;
   }
}
