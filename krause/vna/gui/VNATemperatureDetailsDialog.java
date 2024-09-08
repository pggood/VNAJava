package krause.vna.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.export.JFSeries;
import krause.vna.gui.util.SwingUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class VNATemperatureDetailsDialog extends KrauseDialog {
   private static final float STROKE_WIDTH = 1.0F;
   private final Font fontLabel = new Font("SansSerif", 0, 10);
   private final Font fontTick = new Font("SansSerif", 0, 10);
   private JFreeChart chart;
   private ChartPanel lblImage;
   private JButton btCancel;

   public VNATemperatureDetailsDialog(Window pMainFrame, double[] tempList) {
      super(pMainFrame, true);
      TraceHelper.entry(this, "VNATemperatureDetailsDialog.java");
      this.chart = this.createChart(tempList);
      this.setTitle("Device temperature history");
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
      TraceHelper.exit(this, "VNATemperatureDetailsDialog.java");
   }

   private JFreeChart createChart(double[] tempList) {
      TraceHelper.entry(this, "createChart");
      JFSeries series1 = new JFSeries();
      XYSeries xySeries1 = new XYSeries("Temperature °C");
      int i = 0;
      double[] var10 = tempList;
      int var9 = tempList.length;

      for(int var8 = 0; var8 < var9; ++var8) {
         double data = var10[var8];
         xySeries1.add((double)(i++), data);
      }

      series1.setSeries(xySeries1);
      JFreeChart rcChart = ChartFactory.createXYLineChart("Temperature", "Samples", (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, false, false);
      XYPlot plot = rcChart.getXYPlot();
      NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
      rangeAxis1.setLabelFont(this.fontLabel);
      rangeAxis1.setTickLabelFont(this.fontTick);
      rangeAxis1.setAutoRange(false);
      rangeAxis1.setRange(xySeries1.getMinY() - 5.0D, xySeries1.getMaxY() + 5.0D);
      plot.setRangeAxis(0, rangeAxis1);
      plot.setDataset(0, series1.getDataset());
      plot.mapDatasetToRangeAxis(0, 0);
      rcChart.setBackgroundPaint(Color.white);
      plot = rcChart.getXYPlot();
      plot.setBackgroundPaint(Color.white);
      plot.setDomainGridlinePaint(Color.darkGray);
      plot.setRangeGridlinePaint(Color.darkGray);
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, Color.RED);
      plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1.0F));
      plot.getDomainAxis().setLabelFont(this.fontLabel);
      plot.getDomainAxis().setTickLabelFont(this.fontTick);
      TraceHelper.exit(this, "createChart");
      return rcChart;
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
      this.showCentered(this.getWidth(), this.getHeight());
      TraceHelper.exit(this, "doInit");
   }
}
