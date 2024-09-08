package krause.vna.gui.analyse;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.export.JFSeries;
import krause.vna.export.SWRLogarithmicAxis;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.VNAScaleSelectComboBox;
import krause.vna.gui.raw.VNARawHandler;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.smith.VNASmithDiagramDialog;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ExtensionFileFilter;

public class VNADataAnalysisDialog extends KrauseDialog {
   private static final float STROKE_WIDTH = 1.0F;
   private static final int CHART_WIDTH = 1000;
   private static final int CHART_HEIGHT = 800;
   private VNACalibratedSampleBlock blockLeft;
   private VNACalibratedSampleBlock blockRight;
   JButton buttonExport;
   JButton buttonLoadLeft;
   JButton buttonLoadRight;
   JButton buttonSmithLeft;
   JButton buttonSmithRight;
   private VNAScaleSelectComboBox cbScaleLeft;
   private VNAScaleSelectComboBox cbScaleRight;
   JFreeChart chart = null;
   private ChartPanel chartPanel;
   private VNAConfig config = VNAConfig.getSingleton();
   private final String imgExtension = "jpg";
   private Font LABEL_FONT = new Font("Monospaced", 0, 10);
   private VNAMainFrame mainFrame;
   private final Font TICK_FONT = new Font("Monospaced", 0, 10);
   private JTextField txtFilenameLeft;
   private JTextField txtFilenameRight;

   public VNADataAnalysisDialog(VNAMainFrame pMainFrame) {
      super((Dialog)null, false);
      TraceHelper.entry(this, "VNADataAnalysisDialog");
      this.setConfigurationPrefix("VNADataAnalysisDialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setDefaultCloseOperation(0);
      this.mainFrame = pMainFrame;
      this.setBounds(100, 100, 800, 636);
      this.setTitle(VNAMessages.getString("Dlg.Analysis.Title"));
      JPanel pnlButton = new JPanel();
      this.getContentPane().add(pnlButton, "South");
      this.buttonExport = SwingUtil.createJButton("Button.Save.JPG", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNADataAnalysisDialog.this.doExport();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      pnlButton.add(this.buttonExport);
      JPanel pnlSelect = new JPanel();
      this.getContentPane().add(pnlSelect, "North");
      pnlSelect.setLayout(new BorderLayout(0, 0));
      JPanel pnlLeft = new JPanel();
      pnlSelect.add(pnlLeft, "West");
      this.buttonLoadLeft = SwingUtil.createToolbarButton("Button.Load", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNADataAnalysisDialog.this.doLoadLeft();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      pnlLeft.add(this.buttonLoadLeft);
      this.txtFilenameLeft = new JTextField(VNAMessages.getString("Dlg.Analysis.NoDatafile"));
      this.txtFilenameLeft.setEditable(false);
      this.txtFilenameLeft.setColumns(15);
      pnlLeft.add(this.txtFilenameLeft);
      this.cbScaleLeft = new VNAScaleSelectComboBox();
      this.cbScaleLeft.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNADataAnalysisDialog.this.updateChart();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      this.cbScaleLeft.setBackground(this.config.getColorScaleLeft());
      pnlLeft.add(this.cbScaleLeft);
      pnlLeft.add(this.buttonSmithLeft = SwingUtil.createToolbarButton("Panel.Scale.Smith", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            if (VNADataAnalysisDialog.this.blockLeft != null) {
               new VNASmithDiagramDialog(VNADataAnalysisDialog.this.blockLeft, VNADataAnalysisDialog.this.blockLeft.getFile().getName());
            }

            TraceHelper.exit(this, "actionPerformed");
         }
      }));
      JPanel pnlRight = new JPanel();
      pnlSelect.add(pnlRight, "East");
      this.cbScaleRight = new VNAScaleSelectComboBox();
      this.cbScaleRight.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNADataAnalysisDialog.this.updateChart();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      pnlRight.add(this.buttonSmithRight = SwingUtil.createToolbarButton("Panel.Scale.Smith", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            if (VNADataAnalysisDialog.this.blockRight != null) {
               new VNASmithDiagramDialog(VNADataAnalysisDialog.this.blockRight, VNADataAnalysisDialog.this.blockRight.getFile().getName());
            }

            TraceHelper.exit(this, "actionPerformed");
         }
      }));
      this.cbScaleRight.setBackground(this.config.getColorScaleRight());
      pnlRight.add(this.cbScaleRight);
      this.txtFilenameRight = new JTextField(VNAMessages.getString("Dlg.Analysis.NoDatafile"));
      this.txtFilenameRight.setEditable(false);
      this.txtFilenameRight.setColumns(15);
      pnlRight.add(this.txtFilenameRight);
      this.buttonLoadRight = SwingUtil.createToolbarButton("Button.Load", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNADataAnalysisDialog.this.doLoadRight();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      pnlRight.add(this.buttonLoadRight);
      this.createChart();
      this.chartPanel = new ChartPanel(this.chart, true);
      this.getContentPane().add(this.chartPanel, "Center");
      this.doDialogInit();
      TraceHelper.exit(this, "VNADataAnalysisDialog");
   }

   private void createChart() {
      this.chart = ChartFactory.createXYLineChart((String)null, VNAMessages.getString("Plot.frequency"), (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, true, false);
      XYPlot plot = this.chart.getXYPlot();
      plot.setBackgroundPaint(this.config.getColorDiagram());
      plot.setDomainGridlinePaint(this.config.getColorDiagramLines());
      plot.setRangeGridlinePaint(this.config.getColorDiagramLines());
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, this.config.getColorScaleLeft());
      plot.getRenderer(1).setSeriesPaint(0, this.config.getColorScaleRight());
      plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1.0F));
      plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(1.0F));
      plot.getRenderer(0).setBaseToolTipGenerator(new VNATooltipRenderer(this.cbScaleLeft));
      plot.getRenderer(1).setBaseToolTipGenerator(new VNATooltipRenderer(this.cbScaleRight));
   }

   private NumberAxis createRangeAxisForScale(JFSeries series, VNAGenericScale scale, VNACalibratedSampleBlock block, HashMap<VNAScaleSymbols.SCALE_TYPE, VNAMinMaxPair> minMaxPairs) {
      TraceHelper.entry(this, "createRangeAxisForScale");
      String scaleText = scale.getName();
      if (block != null) {
         scaleText = scaleText + " / " + block.getFile().getName();
      }

      Object rc;
      VNAMinMaxPair pair;
      if (scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS && scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS) {
         if (scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_RS && scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_XS && scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS && scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE && scale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE) {
            if (scale.getType() == VNAScaleSymbols.SCALE_TYPE.SCALE_SWR) {
               rc = new SWRLogarithmicAxis(scaleText);
               NumberFormat nf = new DecimalFormat("0:1");
               ((NumberAxis)rc).setNumberFormatOverride(nf);
               ((NumberAxis)rc).setAutoRange(false);
               ((NumberAxis)rc).setRange(1.0D, 10.0D);
               ((NumberAxis)rc).setRangeType(RangeType.FULL);
               ((NumberAxis)rc).setAutoTickUnitSelection(true);
               ((NumberAxis)rc).setTickMarksVisible(true);
               ((NumberAxis)rc).setTickLabelsVisible(true);
            } else {
               rc = new NumberAxis(scaleText);
            }
         } else {
            rc = new NumberAxis(scaleText);
            ((NumberAxis)rc).setAutoRange(false);
            ((NumberAxis)rc).setAutoRangeIncludesZero(false);
            ((NumberAxis)rc).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            ((NumberAxis)rc).setInverted(false);
            if (block != null) {
               pair = (VNAMinMaxPair)minMaxPairs.get(scale.getType());
               if (pair != null) {
                  ((NumberAxis)rc).setRange(pair.getMinValue(), pair.getMaxValue());
               }
            }
         }
      } else {
         rc = new NumberAxis(scaleText);
         ((NumberAxis)rc).setAutoRange(false);
         ((NumberAxis)rc).setAutoRangeIncludesZero(false);
         ((NumberAxis)rc).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
         ((NumberAxis)rc).setInverted(false);
         if (block != null) {
            pair = (VNAMinMaxPair)minMaxPairs.get(scale.getType());
            if (pair != null) {
               ((NumberAxis)rc).setRange(pair.getMinValue(), pair.getMaxValue());
            }
         }
      }

      ((NumberAxis)rc).setLabelFont(this.LABEL_FONT);
      ((NumberAxis)rc).setTickLabelFont(this.TICK_FONT);
      TraceHelper.exit(this, "createRangeAxisForScale");
      return (NumberAxis)rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   private void doExport() {
      TraceHelper.entry(this, "doExport");
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new ExtensionFileFilter("JPEG image(*.jpg)", "jpg"));
      fc.setSelectedFile(new File(this.config.getExportDirectory() + "/."));
      int returnVal = fc.showSaveDialog(this.mainFrame.getJFrame());
      if (returnVal == 0) {
         File file = fc.getSelectedFile();
         if (!file.getName().endsWith(".jpg")) {
            file = new File(file.getAbsolutePath() + "." + "jpg");
         }

         this.config.setExportDirectory(file.getParent());
         if (file.exists()) {
            String msg = MessageFormat.format("File\r\n[{0}]\r\nalready exists. Overwrite?", file.getName());
            int response = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), msg, "Export to JPEG file", 0, 3, (Icon)null, (Object[])null, (Object)null);
            if (response == 2) {
               return;
            }
         }

         FileOutputStream fos = null;

         try {
            fos = new FileOutputStream(file.getAbsoluteFile());
            ChartUtilities.writeChartAsJPEG(fos, this.chart, 1000, 800);
         } catch (Exception var14) {
            ErrorLogHelper.exception(this, "doExport", var14);
         } finally {
            if (fos != null) {
               try {
                  fos.close();
               } catch (IOException var13) {
                  ErrorLogHelper.exception(this, "doExport", var13);
               }
            }

         }
      }

      TraceHelper.exit(this, "doExport");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.cbScaleLeft.setSelectedIndex(1);
      this.cbScaleRight.setSelectedIndex(1);
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void doLoadLeft() {
      TraceHelper.entry(this, "doLoadLeft");
      this.blockLeft = (new VNARawHandler(this.getOwner())).doImport();
      if (this.blockLeft != null) {
         this.txtFilenameLeft.setText(this.blockLeft.getFile().getName());
         this.txtFilenameLeft.setToolTipText(this.blockLeft.getFile().getAbsolutePath());
         this.updateChart();
      }

      TraceHelper.exit(this, "doLoadLeft");
   }

   protected void doLoadRight() {
      TraceHelper.entry(this, "doLoadRight");
      this.blockRight = (new VNARawHandler(this.getOwner())).doImport();
      if (this.blockRight != null) {
         this.txtFilenameRight.setText(this.blockRight.getFile().getName());
         this.txtFilenameRight.setToolTipText(this.blockRight.getFile().getAbsolutePath());
         this.updateChart();
      }

      TraceHelper.exit(this, "doLoadRight");
   }

   private void updateChart() {
      TraceHelper.entry(this, "createChart");
      HashMap<VNAScaleSymbols.SCALE_TYPE, VNAMinMaxPair> minMaxPairs = new HashMap<VNAScaleSymbols.SCALE_TYPE, VNAMinMaxPair>() {
         {
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_XS, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_XS));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RS, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RS));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS));
            this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_SWR, new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_SWR));
         }
      };
      VNAScaleSymbols.SCALE_TYPE scaleTypeLeft = ((VNAGenericScale)this.cbScaleLeft.getSelectedItem()).getType();
      VNAScaleSymbols.SCALE_TYPE scaleTypeRight = ((VNAGenericScale)this.cbScaleRight.getSelectedItem()).getType();
      JFSeries seriesLeft = new JFSeries();
      JFSeries seriesRight = new JFSeries();
      XYSeries xySeriesLeft = new XYSeries("Left");
      XYSeries xySeriesRight = new XYSeries("Right");
      VNACalibratedSample data;
      int var11;
      int var12;
      VNACalibratedSample[] var13;
      if (this.blockLeft != null && scaleTypeLeft != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         this.updateMinMaxValues(this.blockLeft, minMaxPairs);
         var12 = (var13 = this.blockLeft.getCalibratedSamples()).length;

         for(var11 = 0; var11 < var12; ++var11) {
            data = var13[var11];
            xySeriesLeft.add((double)data.getFrequency(), data.getDataByScaleType(scaleTypeLeft));
         }
      }

      if (this.blockRight != null && scaleTypeRight != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
         this.updateMinMaxValues(this.blockRight, minMaxPairs);
         var12 = (var13 = this.blockRight.getCalibratedSamples()).length;

         for(var11 = 0; var11 < var12; ++var11) {
            data = var13[var11];
            xySeriesRight.add((double)data.getFrequency(), data.getDataByScaleType(scaleTypeRight));
         }
      }

      seriesLeft.setSeries(xySeriesLeft);
      seriesRight.setSeries(xySeriesRight);
      XYPlot plot = this.chart.getXYPlot();
      NumberAxis rangeAxis = this.createRangeAxisForScale(seriesLeft, (VNAGenericScale)this.cbScaleLeft.getSelectedItem(), this.blockLeft, minMaxPairs);
      plot.setRangeAxis(0, rangeAxis);
      plot.setDataset(0, seriesLeft.getDataset());
      plot.mapDatasetToRangeAxis(0, 0);
      plot = this.chart.getXYPlot();
      rangeAxis = this.createRangeAxisForScale(seriesRight, (VNAGenericScale)this.cbScaleRight.getSelectedItem(), this.blockRight, minMaxPairs);
      plot.setRangeAxis(1, rangeAxis);
      plot.setDataset(1, seriesRight.getDataset());
      plot.mapDatasetToRangeAxis(1, 1);
      TraceHelper.exit(this, "createChart");
   }

   private void updateMinMaxValues(VNACalibratedSampleBlock block, HashMap<VNAScaleSymbols.SCALE_TYPE, VNAMinMaxPair> minMaxPairs) {
      Iterator it = minMaxPairs.entrySet().iterator();

      while(it.hasNext()) {
         Entry<VNAScaleSymbols.SCALE_TYPE, VNAMinMaxPair> pair = (Entry)it.next();
         ((VNAMinMaxPair)pair.getValue()).consume(block.getMinMaxPair((VNAScaleSymbols.SCALE_TYPE)pair.getKey()));
      }

   }
}
