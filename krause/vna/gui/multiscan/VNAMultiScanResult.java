package krause.vna.gui.multiscan;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import javax.swing.JInternalFrame;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.export.JFSeries;
import krause.vna.export.SWRLogarithmicAxis;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class VNAMultiScanResult extends JInternalFrame {
   private VNADataPool datapool = VNADataPool.getSingleton();
   private final Font LABEL_FONT = new Font("SansSerif", 0, 10);
   private ChartPanel lblDiag;
   private VNAMeasurementScale mainFrameLeftScale;
   private long startFrequency = 1000000L;
   private long stopFrequency = 2000000L;
   private final Font TICK_FONT = new Font("SansSerif", 0, 10);

   public VNAMultiScanResult(VNAMultiScanControl vnaMultiScanControl, long startFrq, long stopFrq, VNAMeasurementScale pScale) {
      super("Result", true, false, false, true);
      this.startFrequency = startFrq;
      this.stopFrequency = stopFrq;
      this.mainFrameLeftScale = pScale;
      this.setLocation(50, 50);
      this.setSize(538, 306);
      this.setBackground(Color.YELLOW);
      this.lblDiag = new ChartPanel((JFreeChart)null);
      this.lblDiag.setChart(this.createChart());
      this.getContentPane().add(this.lblDiag, "Center");
      this.updateTitle();
      this.setVisible(true);
   }

   public void consumeSampleBlock(VNASampleBlock data) {
      TraceHelper.entry(this, "consumeSampleBlock");
      IVNADriverMathHelper mathHelper = data.getMathHelper();
      if (mathHelper != null) {
         mathHelper.applyFilter(data.getSamples());
         if (this.datapool.getMainCalibrationBlock() != null) {
            VNACalibrationBlock calBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(this.datapool.getMainCalibrationBlock(), data.getStartFrequency(), data.getStopFrequency(), data.getNumberOfSteps());
            VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(calBlock);
            context.setConversionTemperature(data.getDeviceTemperature());
            VNACalibratedSampleBlock scanResult = mathHelper.createCalibratedSamples(context, data);
            this.updateSeriesInChart(scanResult);
         }
      }

      TraceHelper.exit(this, "consumeSampleBlock");
   }

   protected JFreeChart createChart() {
      TraceHelper.entry(this, "createChart");
      JFreeChart chart = ChartFactory.createXYLineChart("", VNAMessages.getString("Plot.frequency"), (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, true, false);
      chart.getLegend().setItemFont(this.LABEL_FONT);
      chart.getTitle().setFont(this.LABEL_FONT);
      XYPlot plot = chart.getXYPlot();
      chart.setBackgroundPaint(Color.white);
      plot.setBackgroundPaint(Color.white);
      plot.setDomainGridlinePaint(Color.darkGray);
      plot.setRangeGridlinePaint(Color.darkGray);
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, Color.BLACK);
      plot.getDomainAxis().setLabelFont(this.LABEL_FONT);
      plot.getDomainAxis().setTickLabelFont(this.TICK_FONT);
      TraceHelper.exit(this, "createChart");
      return chart;
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   private NumberAxis generateRangeAxisBasedOnScale(JFSeries series) {
      NumberAxis rangeAxis = null;
      VNAScaleSymbols.SCALE_TYPE scaleTypeNo = series.getScale().getScale().getType();
      if (scaleTypeNo != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         if (scaleTypeNo == VNAScaleSymbols.SCALE_TYPE.SCALE_SWR) {
            rangeAxis = new SWRLogarithmicAxis(series.getDataset().getSeries(0).getKey().toString());
            NumberFormat nf = new DecimalFormat("0:1");
            ((NumberAxis)rangeAxis).setNumberFormatOverride(nf);
            ((NumberAxis)rangeAxis).setAutoRange(false);
            ((NumberAxis)rangeAxis).setRange(series.getScale().getScale().getCurrentMinValue(), series.getScale().getScale().getCurrentMaxValue());
            ((NumberAxis)rangeAxis).setRangeType(RangeType.FULL);
            ((NumberAxis)rangeAxis).setAutoTickUnitSelection(true);
            ((NumberAxis)rangeAxis).setTickMarksVisible(true);
            ((NumberAxis)rangeAxis).setTickLabelsVisible(true);
         } else {
            rangeAxis = new NumberAxis(series.getDataset().getSeries(0).getKey().toString());
            ((NumberAxis)rangeAxis).setAutoRange(false);
            ((NumberAxis)rangeAxis).setRange(series.getScale().getScale().getCurrentMinValue(), series.getScale().getScale().getCurrentMaxValue());
            ((NumberAxis)rangeAxis).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            ((NumberAxis)rangeAxis).setAutoRangeIncludesZero(false);
            ((NumberAxis)rangeAxis).setInverted(false);
         }

         ((NumberAxis)rangeAxis).setLabelFont(this.LABEL_FONT);
         ((NumberAxis)rangeAxis).setTickLabelFont(this.TICK_FONT);
      }

      return (NumberAxis)rangeAxis;
   }

   public VNAScanMode getScanMode() {
      return this.datapool.getScanMode();
   }

   public long getStartFrequency() {
      return this.startFrequency;
   }

   public long getStopFrequency() {
      return this.stopFrequency;
   }

   public void setStartFrequency(long startFrequency) {
      this.startFrequency = startFrequency;
   }

   public void setStopFrequency(long stopFrequency) {
      this.stopFrequency = stopFrequency;
   }

   public String toString() {
      return this.getTitle();
   }

   private void updateSeriesInChart(VNACalibratedSampleBlock calibratedSamples) {
      VNACalibratedSample[] dataList = calibratedSamples.getCalibratedSamples();
      JFSeries series = new JFSeries(this.mainFrameLeftScale);
      XYPlot plot = this.lblDiag.getChart().getXYPlot();
      VNAScaleSymbols.SCALE_TYPE scaleTypeNo = this.mainFrameLeftScale.getScale().getType();
      if (scaleTypeNo != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         XYSeries xySeries = new XYSeries(this.mainFrameLeftScale.getScale().toString());

         for(int i = 0; i < dataList.length; ++i) {
            VNACalibratedSample data = dataList[i];
            xySeries.add((double)data.getFrequency(), data.getDataByScaleType(scaleTypeNo));
         }

         series.setSeries(xySeries);
         NumberAxis rangeAxis1 = this.generateRangeAxisBasedOnScale(series);
         plot.setRangeAxis(0, rangeAxis1);
         plot.setDataset(0, series.getDataset());
         plot.mapDatasetToRangeAxis(0, 0);
      }

   }

   private void updateTitle() {
      String msg = "Scan {0}-{1}";
      this.setTitle(MessageFormat.format(msg, this.getStartFrequency(), this.getStopFrequency()));
   }
}
