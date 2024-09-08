package krause.vna.export;

import krause.vna.gui.scale.VNAMeasurementScale;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class JFSeries {
   private XYSeries mSeries;
   private VNAMeasurementScale mScale;
   private XYSeriesCollection mDataset = new XYSeriesCollection();

   public JFSeries(VNAMeasurementScale scale) {
      this.mScale = scale;
   }

   public JFSeries() {
   }

   public XYSeries getSeries() {
      return this.mSeries;
   }

   public void setSeries(XYSeries series) {
      this.mSeries = series;
      this.mDataset.addSeries(series);
   }

   public VNAMeasurementScale getScale() {
      return this.mScale;
   }

   public void setScale(VNAMeasurementScale scale) {
      this.mScale = scale;
   }

   public XYSeriesCollection getDataset() {
      return this.mDataset;
   }

   public void setDataset(XYSeriesCollection dataset) {
      this.mDataset = dataset;
   }
}
