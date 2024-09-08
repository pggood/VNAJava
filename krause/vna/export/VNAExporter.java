package krause.vna.export;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

public abstract class VNAExporter {
   protected VNAConfig config = VNAConfig.getSingleton();
   protected VNADataPool datapool = VNADataPool.getSingleton();
   protected static final Color COLOR_LEFT_SCALE;
   protected static final Color COLOR_RIGHT_SCALE;
   protected static final Font LABEL_FONT;
   protected static final Font TICK_FONT;
   protected static final Font MARKER_FONT;
   protected VNAMainFrame mainFrame;
   private JFSeries series1;
   private JFSeries series2;
   private VNAScaleSymbols.SCALE_TYPE scaleType1;
   private VNAScaleSymbols.SCALE_TYPE scaleType2;
   private NumberAxis rangeAxis1;
   private NumberAxis rangeAxis2;

   static {
      COLOR_LEFT_SCALE = Color.BLUE;
      COLOR_RIGHT_SCALE = Color.RED;
      LABEL_FONT = new Font("SansSerif", 0, 30);
      TICK_FONT = new Font("SansSerif", 0, 25);
      MARKER_FONT = new Font("Courier", 1, 15);
   }

   protected JFSeries generateSeriesBasedOnScale(VNAMeasurementScale scale, VNACalibratedSample[] dataList) {
      TraceHelper.entry(this, "generateSeriesBasedOnScale");
      JFSeries series = new JFSeries(scale);
      VNAScaleSymbols.SCALE_TYPE scaleTypeNo = scale.getScale().getType();
      if (scaleTypeNo != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         XYSeries xySeries = new XYSeries(scale.getScale().toString());

         for(int i = 0; i < dataList.length; ++i) {
            VNACalibratedSample data = dataList[i];
            xySeries.add((double)data.getFrequency(), data.getDataByScaleType(scaleTypeNo));
         }

         series.setSeries(xySeries);
      }

      TraceHelper.exit(this, "generateSeriesBasedOnScale");
      return series;
   }

   public String replaceParameters(String in) {
      String rc = null;
      NumberFormat nf = NumberFormat.getNumberInstance();
      nf.setMinimumFractionDigits(0);
      nf.setMaximumFractionDigits(0);
      nf.setMinimumIntegerDigits(1);
      nf.setMaximumIntegerDigits(10);
      nf.setGroupingUsed(false);
      nf.setParseIntegerOnly(true);
      String pexVf = "<not set>";
      String pexLen = "<not set>";
      if (this.config.isPortExtensionEnabled()) {
         double vf = this.config.getPortExtensionVf();
         double len = this.config.getPortExtensionCableLength();
         pexVf = VNAFormatFactory.getVelocityFormat().format(vf);
         pexLen = VNAFormatFactory.getLengthFormat().format(len);
      }

      Object[] parms = new Object[]{new Time(System.currentTimeMillis()), this.datapool.getScanMode().toString(), this.datapool.getDriver().getDeviceInfoBlock().getShortName(), this.datapool.getDriver().getDeviceInfoBlock().getLongName(), nf.format(this.datapool.getFrequencyRange().getStart()), nf.format(this.datapool.getFrequencyRange().getStop()), VNAFormatFactory.getFrequencyFormat().format(this.datapool.getFrequencyRange().getStart()), VNAFormatFactory.getFrequencyFormat().format(this.datapool.getFrequencyRange().getStop()), nf.format((long)this.datapool.getCalibratedData().getCalibratedSamples().length), "?", nf.format((long)this.datapool.getMainCalibrationBlock().getNumberOfSteps()), nf.format((long)this.datapool.getMainCalibrationBlock().getNumberOfOverscans()), this.datapool.getMainCalibrationBlock().getFile() != null ? this.datapool.getMainCalibrationBlock().getFile().getName() : "---", System.getProperty("user.name"), this.config.getExportTitle(), pexLen, pexVf};
      rc = MessageFormat.format(in, parms);
      return rc;
   }

   protected String check4FileToDelete(String filenamePattern, boolean overwrite) {
      String currFilename = this.replaceParameters(filenamePattern + this.getExtension());
      File fi = new File(currFilename);
      currFilename = fi.getAbsolutePath();
      if (fi.exists()) {
         if (overwrite) {
            fi.delete();
         } else {
            String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), currFilename);
            int n = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), msg, VNAMessages.getString("Message.Export.2"), 0, 3, (Icon)null, (Object[])null, (Object)null);
            if (n == 0) {
               fi.delete();
            } else {
               currFilename = null;
            }
         }
      }

      return currFilename;
   }

   protected void createChartMarkers(XYPlot plot) {
      VNAMarkerPanel mp = this.mainFrame.getMarkerPanel();
      VNAMarker[] markers = mp.getMarkers();
      VNAMarker[] var7 = markers;
      int var6 = markers.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         VNAMarker marker = var7[var5];
         if (marker.isVisible()) {
            this.createChartMarker(marker, plot, Color.BLACK);
         }
      }

   }

   private void createChartMarker(VNAMarker marker, XYPlot plot, Color col) {
      int xFactor = 120 - 20 * this.config.getMarkerSize();
      int yFactor = 70 - 10 * this.config.getMarkerSize();
      ValueAxis da = plot.getDomainAxis(0);
      double x_w = da.getRange().getLength() / (double)xFactor;
      double x = (double)marker.getFrequency();
      XYItemRenderer rend;
      double y;
      double y_w;
      double ub;
      if (this.series1 != null) {
         rend = plot.getRenderer(0);
         y = marker.getSample().getDataByScaleType(this.scaleType1);
         y_w = this.rangeAxis1.getRange().getLength() / (double)yFactor;
         ub = this.rangeAxis1.getRange().getUpperBound();
         if (ub > 0.0D) {
            if (y + y_w > ub) {
               this.createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
            } else {
               this.createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
            }
         } else if (ub - y_w < y) {
            this.createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
         } else {
            this.createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
         }
      }

      if (this.series2 != null) {
         rend = plot.getRenderer(1);
         y = marker.getSample().getDataByScaleType(this.scaleType2);
         y_w = this.rangeAxis2.getRange().getLength() / (double)yFactor;
         ub = this.rangeAxis2.getRange().getUpperBound();
         if (ub > 0.0D) {
            if (y + y_w > ub) {
               this.createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
            } else {
               this.createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
            }
         } else if (ub - y_w < y) {
            this.createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
         } else {
            this.createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
         }
      }

   }

   void createMarkerUp(XYItemRenderer rend, Color col, double x, double y, double wx, double wy, String name) {
      TraceHelper.entry(this, "createMarkerUp", x + " " + y);
      XYPolygonAnnotation annShape = new XYPolygonAnnotation(new double[]{x, y, x + wx, y - wy, x - wx, y - wy, x, y}, new BasicStroke(2.0F), col, (Paint)null);
      rend.addAnnotation(annShape);
      XYTextAnnotation annText = new XYTextAnnotation(name, x, y - 2.0D * wy);
      annText.setFont(LABEL_FONT);
      rend.addAnnotation(annText);
   }

   void createMarkerDown(XYItemRenderer rend, Color col, double x, double y, double wx, double wy, String name) {
      TraceHelper.entry(this, "createMarkerDown", x + " " + y);
      XYPolygonAnnotation annShape = new XYPolygonAnnotation(new double[]{x, y, x + wx, y + wy, x - wx, y + wy, x, y}, new BasicStroke(2.0F), col, (Paint)null);
      rend.addAnnotation(annShape);
      XYTextAnnotation annText = new XYTextAnnotation(name, x, y + 2.0D * wy);
      annText.setFont(LABEL_FONT);
      rend.addAnnotation(annText);
   }

   public JFreeChart createChart(VNACalibratedSample[] dataList) {
      TraceHelper.entry(this, "createChart");
      VNAMeasurementScale scale1 = this.mainFrame.getDiagramPanel().getScaleLeft();
      this.scaleType1 = scale1.getScale().getType();
      if (this.scaleType1 != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         this.series1 = this.generateSeriesBasedOnScale(scale1, dataList);
      }

      VNAMeasurementScale scale2 = this.mainFrame.getDiagramPanel().getScaleRight();
      this.scaleType2 = scale2.getScale().getType();
      if (this.scaleType2 != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         if (this.series1 == null) {
            this.series1 = this.generateSeriesBasedOnScale(scale2, dataList);
            this.scaleType1 = scale2.getScale().getType();
         } else {
            this.series2 = this.generateSeriesBasedOnScale(scale2, dataList);
         }
      }

      String title = this.config.isPrintMainLegend() ? this.replaceParameters(this.config.getExportTitle()) : null;
      JFreeChart chart = ChartFactory.createXYLineChart(title, VNAMessages.getString("Plot.frequency"), (String)null, (XYDataset)null, PlotOrientation.VERTICAL, this.config.isPrintFooter(), true, false);
      if (this.config.isPrintFooter()) {
         chart.getLegend().setItemFont(LABEL_FONT);
      }

      if (this.config.isPrintMainLegend()) {
         chart.getTitle().setFont(LABEL_FONT);
      }

      if (this.config.isPrintSubLegend()) {
         TextTitle source = new TextTitle(this.generateLegend(scale1, scale2));
         source.setFont(LABEL_FONT);
         source.setPosition(RectangleEdge.TOP);
         source.setHorizontalAlignment(HorizontalAlignment.CENTER);
         chart.addSubtitle(source);
      }

      float[] dash = new float[]{10.0F};
      XYPlot plot;
      Double av;
      ValueMarker marker;
      VNAGenericScale theScale;
      if (this.series1 != null) {
         plot = chart.getXYPlot();
         this.rangeAxis1 = this.generateRangeAxisBasedOnScale(this.series1);
         plot.setRangeAxis(0, this.rangeAxis1);
         plot.setDataset(0, this.series1.getDataset());
         plot.mapDatasetToRangeAxis(0, 0);
         this.rangeAxis1.setLabelPaint(COLOR_LEFT_SCALE);
         av = scale1.getScale().getGuideLineValue();
         if (av != null) {
            marker = new ValueMarker(av);
            theScale = scale1.getScale();
            marker.setLabelOffset(new RectangleInsets(5.0D, 70.0D, 0.0D, 0.0D));
            marker.setPaint(COLOR_LEFT_SCALE);
            marker.setLabelFont(LABEL_FONT);
            marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
            marker.setLabelTextAnchor(TextAnchor.BASELINE_CENTER);
            marker.setLabel(theScale.getFormattedValueAsStringWithUnit(av));
            marker.setStroke(new BasicStroke(2.0F, 0, 0, 10.0F, dash, 0.0F));
            plot.addRangeMarker(0, marker, Layer.FOREGROUND);
         }
      }

      if (this.series2 != null) {
         plot = chart.getXYPlot();
         this.rangeAxis2 = this.generateRangeAxisBasedOnScale(this.series2);
         plot.setRangeAxis(1, this.rangeAxis2);
         plot.setDataset(1, this.series2.getDataset());
         plot.mapDatasetToRangeAxis(1, 1);
         this.rangeAxis2.setLabelPaint(COLOR_RIGHT_SCALE);
         av = scale2.getScale().getGuideLineValue();
         if (av != null) {
            marker = new ValueMarker(av);
            theScale = scale2.getScale();
            marker.setLabelOffset(new RectangleInsets(5.0D, 0.0D, 100.0D, 70.0D));
            marker.setPaint(COLOR_RIGHT_SCALE);
            marker.setLabelFont(LABEL_FONT);
            marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.BASELINE_CENTER);
            marker.setLabel(theScale.getFormattedValueAsStringWithUnit(av));
            marker.setStroke(new BasicStroke(2.0F, 0, 0, 10.0F, dash, 0.0F));
            plot.addRangeMarker(1, marker, Layer.FOREGROUND);
         }
      }

      chart.setBackgroundPaint(Color.white);
      plot = chart.getXYPlot();
      plot.setBackgroundPaint(Color.white);
      plot.setDomainGridlinePaint(Color.darkGray);
      plot.setRangeGridlinePaint(Color.darkGray);
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, COLOR_LEFT_SCALE);
      plot.getRenderer(1).setSeriesPaint(0, COLOR_RIGHT_SCALE);
      plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1.0F));
      plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(1.0F));
      this.createChartMarkers(plot);
      plot.getDomainAxis().setLabelFont(LABEL_FONT);
      plot.getDomainAxis().setTickLabelFont(TICK_FONT);
      if (this.config.isPrintMarkerDataInDiagramm()) {
         this.createTextMarkers(chart);
      }

      TraceHelper.exit(this, "createChart");
      return chart;
   }

   private String generateLegend(VNAMeasurementScale scale11, VNAMeasurementScale scale22) {
      String rc = "";
      if (scale11.getScale().getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         rc = rc + scale11.getScale().getName() + "=" + scale11.getScale().getDescription() + "    ";
      }

      if (scale22.getScale().getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         rc = rc + scale22.getScale().getName() + "=" + scale22.getScale().getDescription();
      }

      return rc;
   }

   private NumberAxis generateRangeAxisBasedOnScale(JFSeries series) {
      NumberAxis rangeAxis = null;
      VNAScaleSymbols.SCALE_TYPE scaleTypeNo = series.getScale().getScale().getType();
      if (scaleTypeNo != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         if (scaleTypeNo == VNAScaleSymbols.SCALE_TYPE.SCALE_SWR) {
            SWRLogarithmicAxis logAxis = new SWRLogarithmicAxis(series.getDataset().getSeries(0).getKey().toString());
            logAxis.setLog10TickLabelsFlag(false);
            logAxis.setExpTickLabelsFlag(false);
            NumberFormat nf = new DecimalFormat("0.0:1");
            logAxis.setNumberFormatOverride(nf);
            logAxis.setAutoRange(false);
            this.setRange(logAxis, series.getScale().getScale());
            logAxis.setRangeType(RangeType.FULL);
            logAxis.setMinorTickMarksVisible(true);
            logAxis.setTickMarksVisible(true);
            logAxis.setTickLabelsVisible(true);
            rangeAxis = logAxis;
         } else {
            rangeAxis = new NumberAxis(series.getDataset().getSeries(0).getKey().toString());
            ((NumberAxis)rangeAxis).setAutoRange(false);
            this.setRange((NumberAxis)rangeAxis, series.getScale().getScale());
            ((NumberAxis)rangeAxis).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            ((NumberAxis)rangeAxis).setAutoRangeIncludesZero(false);
            ((NumberAxis)rangeAxis).setInverted(false);
         }

         ((NumberAxis)rangeAxis).setLabelFont(LABEL_FONT);
         ((NumberAxis)rangeAxis).setTickLabelFont(TICK_FONT);
      }

      return (NumberAxis)rangeAxis;
   }

   private void setRange(NumberAxis pAxis, VNAGenericScale pScale) {
      double lower = pScale.getCurrentMinValue();
      double upper = pScale.getCurrentMaxValue();
      pAxis.setRange(Math.floor(lower), Math.ceil(upper));
   }

   public abstract String export(String var1, boolean var2) throws ProcessingException;

   public VNAExporter(VNAMainFrame mainFrame) {
      this.mainFrame = mainFrame;
   }

   public abstract String getExtension();

   private void createTextMarkers(JFreeChart chart) {
      TraceHelper.entry(this, "createTextMarkers");
      XYPlot plot = chart.getXYPlot();
      VNAMarkerPanel mp = this.mainFrame.getMarkerPanel();
      VNAMarker[] markers = mp.getMarkers();
      String txtFormat = "";
      txtFormat = txtFormat + "Marker {7}\n";
      txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.Frequency") + " {0}\n";
      if (this.datapool.getScanMode().isReflectionMode()) {
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.RL") + "    {1}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.PhaseRL") + "     {2}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.SWR") + "        {3}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.Z") + "    {4}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.R") + "     {5}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.X") + "     {6}\n";
      } else {
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.TL") + "    {1}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.PhaseTL") + "     {2}\n";
         txtFormat = txtFormat + "  " + VNAMessages.getString("Marker.GrpDelay") + "        {3}\n";
      }

      String legend = "";
      VNAMarker marker;
      int var8;
      int var9;
      VNAMarker[] var10;
      Object[] parms;
      String msg;
      if (this.config.isPrintMarkerDataHorizontal()) {
         var10 = markers;
         var9 = markers.length;

         for(var8 = 0; var8 < var9; ++var8) {
            marker = var10[var8];
            if (marker.isVisible()) {
               parms = new Object[]{marker.getFrequency(), marker.getTxtLoss().getText(), marker.getTxtPhase().getText(), marker.getTxtSwrGrpDelay().getText(), marker.getTxtZAbsolute().getText(), marker.getTxtRs().getText(), marker.getTxtXsAbsolute().getText(), marker.getName()};
               msg = MessageFormat.format(txtFormat, parms);
               legend = legend + msg + "  \n";
            }
         }

         Font lf = MARKER_FONT.deriveFont((float)this.config.getFontSizeTextMarker());
         TextTitle tt = new TextTitle(legend, lf);
         tt.setTextAlignment(HorizontalAlignment.LEFT);
         XYTitleAnnotation annotation = new XYTitleAnnotation(0.99D, 1.0D, tt, RectangleAnchor.TOP_RIGHT);
         plot.addAnnotation(annotation);
      } else {
         var10 = markers;
         var9 = markers.length;

         for(var8 = 0; var8 < var9; ++var8) {
            marker = var10[var8];
            if (marker.isVisible()) {
               parms = new Object[]{marker.getFrequency(), marker.getTxtLoss().getText(), marker.getTxtPhase().getText(), marker.getTxtSwrGrpDelay().getText(), marker.getTxtZAbsolute().getText(), marker.getTxtRs().getText(), marker.getTxtXsAbsolute().getText(), marker.getName()};
               msg = MessageFormat.format(txtFormat, parms);
               legend = legend + msg + "  \n";
            }
         }

         TextTitle tt = new TextTitle(legend, MARKER_FONT);
         tt.setTextAlignment(HorizontalAlignment.LEFT);
         XYTitleAnnotation annotation = new XYTitleAnnotation(0.01D, 1.0D, tt, RectangleAnchor.TOP_LEFT);
         plot.addAnnotation(annotation);
      }

      TraceHelper.exit(this, "createTextMarkers");
   }
}
