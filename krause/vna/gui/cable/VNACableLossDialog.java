package krause.vna.gui.cable;

import com.l2fprod.common.swing.StatusBar;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class VNACableLossDialog extends KrauseDialog implements IVNADataConsumer {
   private static final Color COLOR_LEFT_SCALE;
   private static final float STROKE_WIDTH = 1.0F;
   private final Font LABEL_FONT = new Font("SansSerif", 0, 10);
   private final Font TICK_FONT = new Font("SansSerif", 0, 10);
   private JButton btMeasure;
   private JButton btOK;
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private ChartPanel lblImage;
   private StatusBar statusBar;
   private JCheckBox cbPeakSuppression;
   private VNAPhaseCrossingTable tblCrossings;

   static {
      COLOR_LEFT_SCALE = Color.BLUE;
   }

   public VNACableLossDialog(Frame pMainFrame) {
      super((Window)pMainFrame, true);
      String methodName = "VNACableLossDialog";
      TraceHelper.entry(this, "VNACableLossDialog");
      this.setConfigurationPrefix("VNACableLossDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNACableLossDialog"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(850, 600));
      this.getContentPane().setLayout(new MigLayout("", "[grow,fill][][]", "[grow,fill][][]"));
      this.lblImage = new ChartPanel((JFreeChart)null, true);
      this.lblImage.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.lblImage.setIgnoreRepaint(true);
      this.lblImage.setPreferredSize(new Dimension(400, 300));
      this.lblImage.setMinimumSize(new Dimension(400, 300));
      this.getContentPane().add(this.lblImage, "span 2");
      this.tblCrossings = new VNAPhaseCrossingTable();
      JScrollPane scrollPane = new JScrollPane(this.tblCrossings);
      scrollPane.setViewportBorder((Border)null);
      this.getContentPane().add(scrollPane, "wrap");
      this.btOK = new JButton(VNAMessages.getString("Button.Close"));
      this.getContentPane().add(this.btOK, "left");
      this.btOK.addActionListener((e) -> {
         this.doDialogCancel();
      });
      this.cbPeakSuppression = new JCheckBox(VNAMessages.getString("VNACableLengthDialog.cbAverage"));
      this.cbPeakSuppression.setSelected(true);
      this.getContentPane().add(this.cbPeakSuppression, "center");
      this.btMeasure = new JButton(VNAMessages.getString("VNACableLengthDialog.btMeasure.text"));
      this.getContentPane().add(this.btMeasure, "right,wrap");
      this.btMeasure.addActionListener((e) -> {
         this.doMeasure();
      });
      this.statusBar = new StatusBar();
      this.getContentPane().add(this.statusBar, "span 3,grow");
      JLabel lbl = new JLabel();
      lbl.setOpaque(true);
      this.statusBar.addZone("status", lbl, "*");
      this.doDialogInit();
      TraceHelper.exit(this, "VNACableLossDialog");
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      TraceHelper.entry(this, "consumeDataBlock");
      if (jobs.size() == 1) {
         VNASampleBlock rawData = ((VNABackgroundJob)jobs.get(0)).getResult();
         if (rawData != null) {
            IVNADriverMathHelper mathHelper = rawData.getMathHelper();
            if (mathHelper != null) {
               VNACalibrationBlock mainCalibrationBlock = this.datapool.getMainCalibrationBlock();
               if (mainCalibrationBlock != null) {
                  VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mainCalibrationBlock, rawData.getStartFrequency(), rawData.getStopFrequency(), rawData.getNumberOfSteps());
                  VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
                  context.setConversionTemperature(rawData.getDeviceTemperature());
                  VNACalibratedSampleBlock samples = mathHelper.createCalibratedSamples(context, rawData);
                  VNACableMeasurementHelper helper = new VNACableMeasurementHelper(mathHelper.getDriver().getDeviceInfoBlock().getMinPhase() < 0.0D, false);
                  List<VNACalibratedSample> allPoints = helper.findAllCrossingPoints(samples);
                  if (allPoints.size() < 4) {
                     JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNACableLossDialog.TooShort.msg"), VNAMessages.getString("VNACableLossDialog.TooShort.title"), 2);
                  } else {
                     this.tblCrossings.getModel().clear();
                     Iterator var11 = allPoints.iterator();

                     while(var11.hasNext()) {
                        VNACalibratedSample aSample = (VNACalibratedSample)var11.next();
                        this.tblCrossings.getModel().add(aSample);
                     }

                     this.tblCrossings.updateUI();
                     this.lblImage.setChart(this.createChart(allPoints));
                  }
               } else {
                  JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), 2);
               }
            } else {
               JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), 2);
            }
         } else {
            JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), 2);
         }
      } else {
         JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), 2);
      }

      this.btMeasure.setEnabled(true);
      this.btOK.setEnabled(true);
      this.setCursor(Cursor.getPredefinedCursor(0));
      TraceHelper.exit(this, "consumeDataBlock");
   }

   private JFreeChart createChart(List<VNACalibratedSample> allPoints) {
      TraceHelper.entry(this, "createChart");
      Iterator var3 = allPoints.iterator();

      while(var3.hasNext()) {
         VNACalibratedSample data = (VNACalibratedSample)var3.next();
         data.setReflectionLoss(-data.getReflectionLoss() / 2.0D);
      }

      if (this.cbPeakSuppression.isSelected()) {
         this.calculateMovingAverage(allPoints);
      }

      XYSeries xySeries = new XYSeries(VNAMessages.getString("VNACableLossDialog.1"));
      Iterator var4 = allPoints.iterator();

      while(var4.hasNext()) {
         VNACalibratedSample data = (VNACalibratedSample)var4.next();
         xySeries.add((double)data.getFrequency(), data.getReflectionLoss());
      }

      XYSeriesCollection xysc = new XYSeriesCollection();
      xysc.addSeries(xySeries);
      JFreeChart chart = ChartFactory.createXYLineChart((String)null, VNAMessages.getString("VNACableLossDialog.3"), VNAMessages.getString("VNACableLossDialog.1"), xysc, PlotOrientation.VERTICAL, false, false, false);
      XYPlot plot = chart.getXYPlot();
      NumberAxis rangeAxis = new NumberAxis(xySeries.getKey().toString());
      rangeAxis.setLabelFont(this.LABEL_FONT);
      rangeAxis.setTickLabelFont(this.TICK_FONT);
      plot.setRangeAxis(0, rangeAxis);
      plot.setDataset(0, xysc);
      if (rangeAxis.getUpperBound() < 1.0D) {
         rangeAxis.setAutoRange(false);
         rangeAxis.setUpperBound(1.0D);
      }

      plot.mapDatasetToRangeAxis(0, 0);
      chart.setBackgroundPaint(Color.white);
      plot = chart.getXYPlot();
      plot.setBackgroundPaint(Color.white);
      plot.setDomainGridlinePaint(Color.darkGray);
      plot.setRangeGridlinePaint(Color.darkGray);
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, COLOR_LEFT_SCALE);
      plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1.0F));
      rangeAxis = (NumberAxis)plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      plot.getDomainAxis().setLabelFont(this.LABEL_FONT);
      plot.getDomainAxis().setTickLabelFont(this.TICK_FONT);
      TraceHelper.exit(this, "createChart");
      return chart;
   }

   private void calculateMovingAverage(List<VNACalibratedSample> pData) {
      TraceHelper.entry(this, "calculateMovingAverage");
      double[] coeff = new double[]{0.25D, 0.25D, 0.25D, 0.25D};
      int numCoeff = coeff.length;
      if (pData.size() > numCoeff) {
         double[] latches = new double[numCoeff];
         int numSamples = pData.size();

         int i;
         for(i = 0; i < numCoeff; ++i) {
            latches[numCoeff - i - 1] = ((VNACalibratedSample)pData.get(i)).getReflectionLoss();
         }

         for(i = numCoeff; i < numSamples; ++i) {
            for(int x = numCoeff - 1; x > 0; --x) {
               latches[x] = latches[x - 1];
            }

            latches[0] = ((VNACalibratedSample)pData.get(i)).getReflectionLoss();
            double newSample = 0.0D;

            for(int c = 0; c < numCoeff; ++c) {
               newSample += coeff[c] * latches[c];
            }

            ((VNACalibratedSample)pData.get(i)).setReflectionLoss(newSample);
         }
      }

      TraceHelper.exit(this, "calculateMovingAverage");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      ((JLabel)this.statusBar.getZone("status")).setText(VNAMessages.getString("VNACableLossDialog.description"));
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void doMeasure() {
      TraceHelper.entry(this, "doMeasure");
      long startFreq = this.datapool.getMainCalibrationBlock().getStartFrequency();
      long stopFreq = this.datapool.getMainCalibrationBlock().getStopFrequency();
      this.setCursor(Cursor.getPredefinedCursor(3));
      this.btMeasure.setEnabled(false);
      this.btOK.setEnabled(false);
      VNABackgroundJob job = new VNABackgroundJob();
      job.setNumberOfSamples(1000);
      job.setFrequencyRange(new VNAFrequencyRange(startFreq, stopFreq));
      job.setScanMode(VNAScanMode.MODE_REFLECTION);
      job.setSpeedup(1);
      VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
      backgroundTask.addJob(job);
      backgroundTask.setStatusLabel((JLabel)this.statusBar.getZone("status"));
      backgroundTask.addDataConsumer(this);
      backgroundTask.execute();
      TraceHelper.exit(this, "doMeasure");
   }
}
