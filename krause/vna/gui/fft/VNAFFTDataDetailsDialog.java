package krause.vna.gui.fft;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.text.ParseException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.export.JFSeries;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class VNAFFTDataDetailsDialog extends KrauseDialog implements IVNADataConsumer {
   private static final float STROKE_WIDTH = 1.0F;
   private static final Font LABEL_FONT = new Font("SansSerif", 0, 10);
   private static final Font TICK_FONT = new Font("SansSerif", 0, 10);
   private JFreeChart chart;
   private ChartPanel lblImage;
   private JButton btCancel;
   private JTextField txtPeakAt;
   private JTextField txtTickLen;
   private JButton btScan;
   private transient VNADataPool datapool = VNADataPool.getSingleton();
   private transient VNAConfig config = VNAConfig.getSingleton();
   private transient IVNADriver driver;
   private VNADeviceInfoBlock dib;
   private JLabel lblStatus;
   private JTextField txtVelocityFactor;
   private VNAFFTPeakTable tblPeaks;
   private JTextField txtPeakLimit;

   public VNAFFTDataDetailsDialog(Window wnd) {
      super(wnd, true);
      this.driver = this.datapool.getDriver();
      this.dib = this.driver.getDeviceInfoBlock();
      String methodName = "VNAFFTDataDetailsDialog";
      TraceHelper.entry(this, "VNAFFTDataDetailsDialog");
      this.setTitle(VNAMessages.getString("FFT.title"));
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNAFFTDataDetailsDialog");
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(689, 602));
      this.getContentPane().setLayout(new MigLayout("", "[][][grow,fill][25%]", "[grow,fill][][][]"));
      this.chart = ChartFactory.createXYLineChart("", "Distance", (String)null, (XYDataset)null, PlotOrientation.VERTICAL, true, false, false);
      this.lblImage = new ChartPanel(this.chart, true);
      this.lblImage.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.lblImage.setIgnoreRepaint(true);
      this.lblImage.setPreferredSize(new Dimension(640, 480));
      this.lblImage.setLayout(new FlowLayout(1, 5, 5));
      this.getContentPane().add(this.lblImage, "span 3");
      this.tblPeaks = new VNAFFTPeakTable();
      JScrollPane scrollPane = new JScrollPane(this.tblPeaks);
      this.getContentPane().add(scrollPane, "wrap");
      this.getContentPane().add(new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text")), "");
      this.txtVelocityFactor = new JTextField();
      this.txtVelocityFactor.setFocusTraversalKeysEnabled(false);
      this.txtVelocityFactor.setHorizontalAlignment(4);
      this.txtVelocityFactor.setColumns(6);
      this.txtVelocityFactor.setText(VNAFormatFactory.getVelocityFormat().format(this.config.getDouble("VNAFFTDataDetailsDialog.vf", 0.66D)));
      this.getContentPane().add(this.txtVelocityFactor, "wrap");
      this.getContentPane().add(new JLabel(VNAMessages.getString("VNACableLengthDialog.lblPeakLimit.text")), "");
      this.txtPeakLimit = new JTextField();
      this.txtPeakLimit.setFocusTraversalKeysEnabled(false);
      this.txtPeakLimit.setHorizontalAlignment(4);
      this.txtPeakLimit.setColumns(6);
      this.txtPeakLimit.setText(VNAFormatFactory.getVelocityFormat().format(this.config.getDouble("VNAFFTDataDetailsDialog.peakLimit", 0.5D)));
      this.getContentPane().add(this.txtPeakLimit, "wrap");
      this.getContentPane().add(new JLabel(VNAMessages.getString("FFT.SampleLen")), "");
      this.txtTickLen = new JTextField(10);
      this.txtTickLen.setEditable(false);
      this.txtTickLen.setHorizontalAlignment(4);
      this.getContentPane().add(this.txtTickLen, "wrap");
      this.getContentPane().add(new JLabel(VNAMessages.getString("FFT.PeaktAt")), "");
      this.txtPeakAt = new JTextField(10);
      this.txtPeakAt.setEditable(false);
      this.txtPeakAt.setHorizontalAlignment(4);
      this.getContentPane().add(this.txtPeakAt, "wrap");
      this.btCancel = SwingUtil.createJButton("Button.Cancel", (e) -> {
         this.doDialogCancel();
      });
      this.btCancel.setActionCommand("Cancel");
      this.getContentPane().add(this.btCancel, "center");
      this.lblStatus = new JLabel("Ready ...");
      this.getContentPane().add(this.lblStatus, "span 2,grow");
      this.btScan = SwingUtil.createJButton("Button.START", (e) -> {
         this.doSTART();
      });
      this.getContentPane().add(this.btScan, "right");
      this.getRootPane().setDefaultButton(this.btCancel);
      this.doDialogInit();
      TraceHelper.exit(this, "VNAFFTDataDetailsDialog");
   }

   protected void doSTART() {
      TraceHelper.entry(this, "doSTART");
      this.setCursor(Cursor.getPredefinedCursor(3));
      this.btCancel.setEnabled(false);
      this.btScan.setEnabled(false);
      VNABackgroundJob job = new VNABackgroundJob();
      job.setNumberOfSamples(1024);
      job.setSpeedup(1);
      job.setAverage(0);
      job.setOverScan(1);
      job.setFrequencyRange(new VNAFrequencyRange(this.dib.getMinFrequency(), this.dib.getMaxFrequency()));
      job.setScanMode(VNAScanMode.MODE_REFLECTION);
      VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
      backgroundTask.addJob(job);
      backgroundTask.addDataConsumer(this);
      backgroundTask.setStatusLabel(this.lblStatus);
      backgroundTask.execute();
      TraceHelper.exit(this, "doSTART");
   }

   private JFreeChart createChart(Complex[] input, double oneTickLen) {
      String methodName = "createChart";
      TraceHelper.entry(this, "createChart");
      int len = input.length;
      JFSeries series1 = new JFSeries();
      XYSeries xySeries1 = new XYSeries("Abs()");

      for(int i = 0; i < len; ++i) {
         xySeries1.add((double)i * oneTickLen, input[i].abs());
      }

      series1.setSeries(xySeries1);
      this.chart = ChartFactory.createXYLineChart((String)null, "length (m)", (String)null, (XYDataset)null, PlotOrientation.VERTICAL, false, true, false);
      this.chart.setAntiAlias(false);
      NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
      rangeAxis1.setLabelFont(LABEL_FONT);
      rangeAxis1.setTickLabelFont(TICK_FONT);
      rangeAxis1.setLabelPaint(Color.RED);
      XYPlot plot = this.chart.getXYPlot();
      plot.setRangeAxis(0, rangeAxis1);
      plot.setDataset(0, series1.getDataset());
      plot.mapDatasetToRangeAxis(0, 0);
      plot.setBackgroundPaint(Color.white);
      plot.setDomainGridlinePaint(Color.darkGray);
      plot.setRangeGridlinePaint(Color.darkGray);
      plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
      plot.getRenderer(0).setSeriesPaint(0, Color.RED);
      plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1.0F));
      plot.getDomainAxis().setLabelFont(LABEL_FONT);
      plot.getDomainAxis().setTickLabelFont(TICK_FONT);
      this.chart.setBackgroundPaint(Color.white);
      TraceHelper.exit(this, "createChart");
      return this.chart;
   }

   protected void doDialogCancel() {
      String methodName = "doDialogCancel";
      TraceHelper.entry(this, "doDialogCancel");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doDialogCancel");
   }

   protected void doDialogInit() {
      String methodName = "doDialogInit";
      TraceHelper.entry(this, "doDialogInit");
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doDialogInit");
   }

   private void calculate(VNACalibratedSample[] samples) {
      String methodName = "calculate";
      TraceHelper.entry(this, "calculate");
      int len = samples.length;
      Complex[] values = new Complex[len];

      for(int i = 0; i < len; ++i) {
         values[i] = samples[i].getRHO();
      }

      FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
      Complex[] complexFFTData = transformer.transform(values, TransformType.INVERSE);

      try {
         long startFreq = samples[0].getFrequency();
         long stopFreq = samples[samples.length - 1].getFrequency();
         double oneTickTime = 1.0D / (double)(stopFreq - startFreq) / 2.0D;
         double vf = VNAFormatFactory.getVelocityFormat().parse(this.txtVelocityFactor.getText()).doubleValue();
         this.config.putDouble("VNAFFTDataDetailsDialog.vf", vf);
         double oneTickLen = 2.99792458E8D * oneTickTime * vf;
         double lowerPeakLimit = VNAFormatFactory.getVelocityFormat().parse(this.txtPeakLimit.getText()).doubleValue();
         this.config.putDouble("VNAFFTDataDetailsDialog.peakLimit", lowerPeakLimit);
         this.txtTickLen.setText(VNAFormatFactory.getLengthFormat().format(oneTickLen));
         this.tblPeaks.getModel().getValues().clear();
         double maxVal = lowerPeakLimit;
         int maxIndex = 0;

         for(int i = 0; i < len; ++i) {
            double val = complexFFTData[i].abs();
            if (val > maxVal) {
               maxVal = val;
               maxIndex = i;
            }
         }

         double upperLimit = maxVal;

         for(int x = 0; x < 10; ++x) {
            if (maxIndex != -1) {
               this.tblPeaks.getModel().getValues().add(new VNAFFTPeakTableEntry(maxIndex, maxVal, oneTickLen * (double)maxIndex));
               upperLimit = maxVal * 0.95D;
               TraceHelper.text(this, "calculate", "New max val %f", upperLimit);
            }

            maxVal = lowerPeakLimit;
            maxIndex = -1;

            for(int i = 0; i < len; ++i) {
               double val = complexFFTData[i].abs();
               if (val < upperLimit && val > maxVal) {
                  maxVal = val;
                  maxIndex = i;
               }
            }
         }

         this.tblPeaks.repaint();
         this.tblPeaks.updateUI();
         this.chart = this.createChart(complexFFTData, oneTickLen);
         this.lblImage.setChart(this.chart);
      } catch (ParseException var28) {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("FFT.Err.1"), VNAMessages.getString("FFT.title"), 0);
      }

      TraceHelper.exit(this, "calculate");
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      String methodName = "consumeDataBlock";
      TraceHelper.entry(this, "consumeDataBlock");
      VNASampleBlock rawData = ((VNABackgroundJob)jobs.get(0)).getResult();
      if (rawData != null) {
         IVNADriverMathHelper mathHelper = rawData.getMathHelper();
         if (mathHelper != null) {
            VNACalibrationBlock mainCalibrationBlock = this.datapool.getMainCalibrationBlock();
            if (mainCalibrationBlock != null) {
               VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mainCalibrationBlock, this.dib.getMinFrequency(), this.dib.getMaxFrequency(), rawData.getNumberOfSteps());
               VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
               context.setConversionTemperature(rawData.getDeviceTemperature());
               if (rawData.getScanMode().isReflectionMode()) {
                  VNACalibratedSampleBlock samples = mathHelper.createCalibratedSamples(context, rawData);
                  this.calculate(samples.getCalibratedSamples());
               }
            }
         }
      }

      this.setCursor(Cursor.getPredefinedCursor(0));
      this.btCancel.setEnabled(true);
      this.btScan.setEnabled(true);
      TraceHelper.exit(this, "consumeDataBlock");
   }
}
