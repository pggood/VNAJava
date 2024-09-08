package krause.vna.gui.calibrate;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.export.JFSeries;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class VNACalibrationDataDetailsDialog extends KrauseDialog {
   private static final float STROKE_WIDTH = 0.5F;
   private final Font LABEL_FONT = new Font("SansSerif", 0, 10);
   private final Font TICK_FONT = new Font("SansSerif", 0, 10);
   private JFreeChart chart;
   private ChartPanel lblImage;
   private JButton btOK;
   private VNASampleBlock sampleBlock;
   private final VNAConfig config = VNAConfig.getSingleton();
   private String typeId;

   public VNACalibrationDataDetailsDialog(Window pMainFrame, VNASampleBlock samples, String headerID) {
      super(pMainFrame, true);
      TraceHelper.entry(this, "VNACalibrationDataDetailsDialog");
      this.typeId = headerID;
      this.sampleBlock = samples;
      this.chart = this.createChart();
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
      this.btOK = SwingUtil.createJButton("Button.OK", (e) -> {
         this.setVisible(false);
      });
      buttonPane.add(this.btOK, "East");
      this.btOK = SwingUtil.createJButton("Button.EXPORT", (e) -> {
         this.doExport();
      });
      buttonPane.add(this.btOK, "West");
      this.btOK.setActionCommand("Cancel");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalibrationDataDetailsDialog");
   }

   protected void doExport() {
      String methodName = "doExport";
      TraceHelper.entry(this, "doExport");

      try {
         HSSFWorkbook workBook = new HSSFWorkbook();
         HSSFSheet workSheet = workBook.createSheet("vnaJ");
         int cell;
         HSSFRow row;
         VNABaseSample data;
         int var8;
         int var9;
         VNABaseSample[] var10;
         int rowNum;
         int var13;
         if (this.sampleBlock.getSamples().length > 0 && this.sampleBlock.getSamples()[0].hasPData()) {
            rowNum = 0;
            cell = 0;
            rowNum = rowNum + 1;
            row = workSheet.createRow(rowNum);
            var13 = cell + 1;
            row.createCell(cell).setCellValue(new HSSFRichTextString(VNAMessages.getString("Plot.frequency")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p1")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p2")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p3")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p4")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p1Ref")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p2Ref")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p3Ref")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p4Ref")));
            var9 = (var10 = this.sampleBlock.getSamples()).length;

            for(var8 = 0; var8 < var9; ++var8) {
               data = var10[var8];
               cell = 0;
               row = workSheet.createRow(rowNum);
               var13 = cell + 1;
               row.createCell(cell).setCellValue((double)data.getFrequency());
               row.createCell(var13++).setCellValue((double)data.getP1());
               row.createCell(var13++).setCellValue((double)data.getP2());
               row.createCell(var13++).setCellValue((double)data.getP3());
               row.createCell(var13++).setCellValue((double)data.getP4());
               row.createCell(var13++).setCellValue((double)data.getP1Ref());
               row.createCell(var13++).setCellValue((double)data.getP2Ref());
               row.createCell(var13++).setCellValue((double)data.getP3Ref());
               row.createCell(var13++).setCellValue((double)data.getP4Ref());
               ++rowNum;
            }
         } else {
            rowNum = 0;
            cell = 0;
            rowNum = rowNum + 1;
            row = workSheet.createRow(rowNum);
            var13 = cell + 1;
            row.createCell(cell).setCellValue(new HSSFRichTextString(VNAMessages.getString("Plot.frequency")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.1")));
            row.createCell(var13++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.2")));
            var9 = (var10 = this.sampleBlock.getSamples()).length;

            for(var8 = 0; var8 < var9; ++var8) {
               data = var10[var8];
               cell = 0;
               row = workSheet.createRow(rowNum);
               var13 = cell + 1;
               row.createCell(cell).setCellValue((double)data.getFrequency());
               row.createCell(var13++).setCellValue(data.getLoss());
               row.createCell(var13++).setCellValue(data.getAngle());
               ++rowNum;
            }
         }

         String fn = this.config.getExportDirectory() + System.getProperty("file.separator") + "CalData_" + VNAMessages.getString(this.typeId) + "." + System.currentTimeMillis() + ".xls";
         FileOutputStream fileOut = new FileOutputStream(fn);
         workBook.write(fileOut);
         fileOut.close();
      } catch (IOException var11) {
         ErrorLogHelper.exception(this, "doExport", var11);
      }

      TraceHelper.exit(this, "doExport");
   }

   private JFreeChart createChart() {
      TraceHelper.entry(this, "createChart");
      JFreeChart rcChart = null;
      XYPlot plot;
      JFSeries series1;
      JFSeries series2;
      if (this.sampleBlock.getSamples().length > 0 && this.sampleBlock.getSamples()[0].hasPData()) {
         series1 = new JFSeries();
         series2 = new JFSeries();
         JFSeries series3 = new JFSeries();
         JFSeries series4 = new JFSeries();
         XYSeries xySeries1 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p1"));
         XYSeries xySeries2 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p2"));
         XYSeries xySeries3 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p3"));
         XYSeries xySeries4 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p4"));
         VNABaseSample[] var14;
         int var13 = (var14 = this.sampleBlock.getSamples()).length;

         for(int var12 = 0; var12 < var13; ++var12) {
            VNABaseSample data = var14[var12];
            long f = data.getFrequency();
            xySeries1.add((double)f, (double)data.getP1());
            xySeries2.add((double)f, (double)data.getP2());
            xySeries3.add((double)f, (double)data.getP3());
            xySeries4.add((double)f, (double)data.getP4());
         }

         series1.setSeries(xySeries1);
         series2.setSeries(xySeries2);
         series3.setSeries(xySeries3);
         series4.setSeries(xySeries4);
         rcChart = ChartFactory.createXYLineChart(VNAMessages.getString(this.typeId), VNAMessages.getString("Plot.frequency"), (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, false, false);
         plot = rcChart.getXYPlot();
         NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
         rangeAxis1.setLabelFont(this.LABEL_FONT);
         rangeAxis1.setTickLabelFont(this.TICK_FONT);
         rangeAxis1.setAutoRange(false);
         rangeAxis1.setRange(xySeries1.getMinY(), xySeries1.getMaxY());
         plot.setRangeAxis(0, rangeAxis1);
         plot.setDataset(0, series1.getDataset());
         plot.mapDatasetToRangeAxis(0, 0);
         plot = rcChart.getXYPlot();
         NumberAxis rangeAxis2 = new NumberAxis(series2.getDataset().getSeries(0).getKey().toString());
         rangeAxis2.setLabelFont(this.LABEL_FONT);
         rangeAxis2.setTickLabelFont(this.TICK_FONT);
         rangeAxis2.setAutoRange(false);
         rangeAxis2.setRange(xySeries2.getMinY(), xySeries2.getMaxY());
         plot.setRangeAxis(1, rangeAxis2);
         plot.setDataset(1, series2.getDataset());
         plot.mapDatasetToRangeAxis(1, 1);
         plot = rcChart.getXYPlot();
         NumberAxis rangeAxis3 = new NumberAxis(series3.getDataset().getSeries(0).getKey().toString());
         rangeAxis3.setLabelFont(this.LABEL_FONT);
         rangeAxis3.setTickLabelFont(this.TICK_FONT);
         rangeAxis3.setAutoRange(false);
         rangeAxis3.setRange(xySeries3.getMinY(), xySeries3.getMaxY());
         plot.setRangeAxis(2, rangeAxis3);
         plot.setDataset(2, series3.getDataset());
         plot.mapDatasetToRangeAxis(2, 2);
         plot = rcChart.getXYPlot();
         NumberAxis rangeAxis4 = new NumberAxis(series4.getDataset().getSeries(0).getKey().toString());
         rangeAxis4.setLabelFont(this.LABEL_FONT);
         rangeAxis4.setTickLabelFont(this.TICK_FONT);
         rangeAxis4.setAutoRange(false);
         rangeAxis4.setRange(xySeries4.getMinY(), xySeries4.getMaxY());
         plot.setRangeAxis(3, rangeAxis4);
         plot.setDataset(3, series4.getDataset());
         plot.mapDatasetToRangeAxis(3, 3);
         rcChart.setBackgroundPaint(Color.white);
         plot = rcChart.getXYPlot();
         plot.setBackgroundPaint(Color.white);
         plot.setDomainGridlinePaint(Color.darkGray);
         plot.setRangeGridlinePaint(Color.darkGray);
         plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
         plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
         plot.setRenderer(2, new XYLineAndShapeRenderer(true, false));
         plot.setRenderer(3, new XYLineAndShapeRenderer(true, false));
         plot.getRenderer(0).setSeriesPaint(0, Color.RED);
         plot.getRenderer(1).setSeriesPaint(0, Color.BLUE);
         plot.getRenderer(2).setSeriesPaint(0, Color.GREEN);
         plot.getRenderer(3).setSeriesPaint(0, Color.BLACK);
         plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(0.5F));
         plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(0.5F));
         plot.getRenderer(2).setSeriesStroke(0, new BasicStroke(0.5F));
         plot.getRenderer(3).setSeriesStroke(0, new BasicStroke(0.5F));
         plot.getDomainAxis().setLabelFont(this.LABEL_FONT);
         plot.getDomainAxis().setTickLabelFont(this.TICK_FONT);
      } else {
         series1 = new JFSeries();
         series2 = new JFSeries();
         XYSeries xySeries1 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.1"));
         XYSeries xySeries2 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.2"));
         VNABaseSample[] var10;
         int var9 = (var10 = this.sampleBlock.getSamples()).length;

         for(int var8 = 0; var8 < var9; ++var8) {
            VNABaseSample data = var10[var8];
            xySeries1.add((double)data.getFrequency(), data.getLoss());
            xySeries2.add((double)data.getFrequency(), data.getAngle());
         }

         series1.setSeries(xySeries1);
         series2.setSeries(xySeries2);
         rcChart = ChartFactory.createXYLineChart(VNAMessages.getString(this.typeId), VNAMessages.getString("Plot.frequency"), (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, false, false);
         plot = rcChart.getXYPlot();
         NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
         rangeAxis1.setLabelFont(this.LABEL_FONT);
         rangeAxis1.setTickLabelFont(this.TICK_FONT);
         rangeAxis1.setAutoRange(false);
         rangeAxis1.setRange(xySeries1.getMinY(), xySeries1.getMaxY());
         plot.setRangeAxis(0, rangeAxis1);
         plot.setDataset(0, series1.getDataset());
         plot.mapDatasetToRangeAxis(0, 0);
         plot = rcChart.getXYPlot();
         NumberAxis rangeAxis2 = new NumberAxis(series2.getDataset().getSeries(0).getKey().toString());
         rangeAxis2.setLabelFont(this.LABEL_FONT);
         rangeAxis2.setTickLabelFont(this.TICK_FONT);
         rangeAxis2.setAutoRange(false);
         rangeAxis2.setRange(xySeries2.getMinY(), xySeries2.getMaxY());
         plot.setRangeAxis(1, rangeAxis2);
         plot.setDataset(1, series2.getDataset());
         plot.mapDatasetToRangeAxis(1, 1);
         rcChart.setBackgroundPaint(Color.white);
         plot = rcChart.getXYPlot();
         plot.setBackgroundPaint(Color.white);
         plot.setDomainGridlinePaint(Color.darkGray);
         plot.setRangeGridlinePaint(Color.darkGray);
         plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
         plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
         plot.getRenderer(0).setSeriesPaint(0, Color.RED);
         plot.getRenderer(1).setSeriesPaint(0, Color.BLUE);
         plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(0.5F));
         plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(0.5F));
         plot.getDomainAxis().setLabelFont(this.LABEL_FONT);
         plot.getDomainAxis().setTickLabelFont(this.TICK_FONT);
      }

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
