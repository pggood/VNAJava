package krause.vna.gui.smith.data;

import java.awt.Polygon;
import krause.vna.data.calibrated.VNACalibratedSample;

public class SmithDiagramCurve extends Polygon {
   private String label;
   private boolean realCurve;
   private VNACalibratedSample[] samples;

   public SmithDiagramCurve() {
   }

   public SmithDiagramCurve(int[] xpoints, int[] ypoints, int npoints) {
      super(xpoints, ypoints, npoints);
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getLabel() {
      return this.label;
   }

   public void setRealCurve(boolean realCurve) {
      this.realCurve = realCurve;
   }

   public boolean isRealCurve() {
      return this.realCurve;
   }

   public VNACalibratedSample[] getSamples() {
      return this.samples;
   }

   public void setSamples(VNACalibratedSample[] samples) {
      this.samples = samples;
   }
}
