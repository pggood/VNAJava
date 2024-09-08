package krause.vna.gui.calibrate;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.export.JFSeries;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class VNACalibrationBlockDetailsDialog extends KrauseDialog {
   private static final float STROKE_WIDTH = 1.0F;
   private final Font LABEL_FONT = new Font("SansSerif", 0, 10);
   private final Font TICK_FONT = new Font("SansSerif", 0, 10);
   private JFreeChart chart;
   private ChartPanel lblImage;
   private JButton btCancel;

   public VNACalibrationBlockDetailsDialog(Frame pMainFrame, VNACalibrationBlock block, String headerID) {
      super((Window)pMainFrame, true);
      TraceHelper.entry(this, "VNACalibrationBlockDetailsDialog");
      this.chart = this.createChart(block, headerID);
      this.setTitle(VNAMessages.getString("VNACalibrationDataDetailsDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setBounds(100, 100, 689, 602);
      this.getContentPane().setLayout(new BorderLayout());
      this.lblImage = new ChartPanel(this.chart, true);
      this.getContentPane().add(this.lblImage, "Center");
      this.lblImage.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.lblImage.setIgnoreRepaint(true);
      this.lblImage.setPreferredSize(new Dimension(640, 480));
      this.lblImage.setLayout(new FlowLayout(1, 5, 5));
      JPanel buttonPane = new JPanel();
      this.getContentPane().add(buttonPane, "South");
      buttonPane.setLayout(new BorderLayout(0, 0));
      this.btCancel = SwingUtil.createJButton("Button.OK", (e) -> {
         this.setVisible(false);
      });
      buttonPane.add(this.btCancel, "East");
      this.btCancel.setActionCommand("Cancel");
      this.getRootPane().setDefaultButton(this.btCancel);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalibrationBlockDetailsDialog");
   }

   private JFreeChart createChart(VNACalibrationBlock block, String headerID) {
      TraceHelper.entry(this, "createChart");
      JFSeries series1 = new JFSeries();
      JFSeries series2 = new JFSeries();
      XYSeries xySeries1 = new XYSeries("E00-real");
      XYSeries xySeries2 = new XYSeries("E00-imag");
      VNACalibrationPoint[] var12;
      int var11 = (var12 = block.getCalibrationPoints()).length;

      for(int var10 = 0; var10 < var11; ++var10) {
         VNACalibrationPoint data = var12[var10];
         xySeries1.add((double)data.getFrequency(), data.getDeltaE().getReal());
         xySeries2.add((double)data.getFrequency(), data.getDeltaE().getImaginary());
      }

      series1.setSeries(xySeries1);
      series2.setSeries(xySeries2);
      JFreeChart chart = ChartFactory.createXYLineChart(VNAMessages.getString(headerID), VNAMessages.getString("Plot.frequency"), (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, false, false);
      XYPlot plot = chart.getXYPlot();
      NumberAxis rangeAxis = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
      rangeAxis.setLabelFont(this.LABEL_FONT);
      rangeAxis.setTickLabelFont(this.TICK_FONT);
      plot.setRangeAxis(0, rangeAxis);
      plot.setDataset(0, series1.getDataset());
      plot.mapDatasetToRangeAxis(0, 0);
      plot = chart.getXYPlot();
      rangeAxis = new NumberAxis(series2.getDataset().getSeries(0).getKey().toString());
      rangeAxis.setLabelFont(this.LABEL_FONT);
      rangeAxis.setTickLabelFont(this.TICK_FONT);
      plot.setRangeAxis(1, rangeAxis);
      plot.setDataset(1, series2.getDataset());
      plot.mapDatasetToRangeAxis(1, 1);
      chart.setBackgroundPaint(Color.white);
      plot = chart.getXYPlot();
      plot.setBackgroundPaint(Color.white);
      plot.setDomainGridlinePaint(Color.darkGray);
      plot.setRangeGridlinePaint(Color.darkGray);
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, Color.RED);
      plot.getRenderer(1).setSeriesPaint(0, Color.BLUE);
      plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1.0F));
      plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(1.0F));
      rangeAxis = (NumberAxis)plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      plot.getDomainAxis().setLabelFont(this.LABEL_FONT);
      plot.getDomainAxis().setTickLabelFont(this.TICK_FONT);
      TraceHelper.exit(this, "createChart");
      return chart;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doExit");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doExit");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.lblImage.setChart(this.chart);
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }
}
