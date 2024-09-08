package krause.vna.gui.calibrate;

import com.l2fprod.common.swing.StatusBar;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.common.validation.IntegerValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.export.JFSeries;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.calibrate.mode1.VNACalibrationRangeTable;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.util.ComponentTitledBorder;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

public class VNACalibrationDialog extends KrauseDialog implements IVNADataConsumer {
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private static final float STROKE_WIDTH = 1.0F;
   private final Font LABEL_FONT = new Font("SansSerif", 0, 10);
   private final Font TICK_FONT = new Font("SansSerif", 0, 10);
   private boolean dataValid = false;
   private long startTime;
   private VNACalibrationBlock calibrationBlock = null;
   private JButton btOK;
   private JButton btReadOpen;
   private JButton btReadShort;
   private JButton btReadLoad;
   private JButton btCancel;
   private JButton btLOAD;
   private JButton btReadLoop;
   private JButton btSAVE;
   private VNACalibrationDialog.MeasurementMode currentMode = null;
   private ChartPanel chartOPEN;
   private ChartPanel chartSHORT;
   private ChartPanel chartLOAD;
   private ChartPanel chartLOOP;
   private VNAMainFrame mainFrame;
   private JPanel pnlShort;
   private JPanel pnlLoad;
   private JPanel pnlOpen;
   private JPanel pnlLoop;
   private JTextField txtOverscan;
   private JTextField txtNumSamples;
   private StatusBar statusBar;
   private JRadioButton rdbtnMode1;
   private JRadioButton rdbtnMode2;
   private JTextArea txtLOAD;
   private JTextArea txtLOOP;
   private JTextArea txtOPEN;
   private JTextArea txtSHORT;

   public VNACalibrationDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      TraceHelper.entry(this, "VNACalibrationDialog");
      this.mainFrame = pMainFrame;
      this.setConfigurationPrefix("VNACalibrationDialog");
      this.setProperties(this.config);
      this.calibrationBlock = new VNACalibrationBlock();
      this.calibrationBlock.setScanMode(this.datapool.getScanMode());
      String tit = VNAMessages.getString("VNACalibrationDialog.title");
      this.setTitle(MessageFormat.format(tit, this.datapool.getScanMode().toString()));
      this.setDefaultCloseOperation(0);
      this.setMinimumSize(new Dimension(850, 500));
      this.setPreferredSize(this.getMinimumSize());
      this.getContentPane().setLayout(new MigLayout("", "[200px]0[200px]0[200px]0[200px]", "[grow,fill]0[]0[]0[]"));
      JFreeChart chart = this.createChart(new VNASampleBlock(), true);
      this.getContentPane().add(this.pnlOpen = this.createPanelOpen(chart), "");
      this.getContentPane().add(this.pnlShort = this.createPanelShort(chart), "");
      this.getContentPane().add(this.pnlLoad = this.createPanelLoad(chart), "");
      this.getContentPane().add(this.pnlLoop = this.createPanelLoop(chart), "wrap");
      this.getContentPane().add(this.createBottomPanel(), "span 4, grow, left, wrap");
      this.getContentPane().add(this.createButtonPanel(), "span 4, right,wrap");
      this.getContentPane().add(this.statusBar = this.createStatusPanel(), "span 4, grow,left");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalibrationDialog");
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      TraceHelper.entry(this, "consumeDataBlock");
      VNASampleBlock data = null;
      VNABackgroundJob job;
      Iterator var5;
      if (this.rdbtnMode2.isSelected()) {
         if (jobs.size() > 1) {
            List<VNASampleBlock> blocks = new ArrayList();
            var5 = jobs.iterator();

            while(var5.hasNext()) {
               job = (VNABackgroundJob)var5.next();
               blocks.add(job.getResult());
            }

            data = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);
         } else if (jobs.size() == 1) {
            data = ((VNABackgroundJob)jobs.get(0)).getResult();
         }
      } else {
         int blkSize = 0;

         for(var5 = jobs.iterator(); var5.hasNext(); blkSize += job.getResult().getSamples().length) {
            job = (VNABackgroundJob)var5.next();
         }

         VNABaseSample[] samples = new VNABaseSample[blkSize];
         int idx = 0;
         long minFreq = Long.MAX_VALUE;
         long maxFreq = Long.MIN_VALUE;
         Iterator var11 = jobs.iterator();

         while(var11.hasNext()) {
            job = (VNABackgroundJob)var11.next();
            if (job.getFrequencyRange().getStart() < minFreq) {
               minFreq = job.getFrequencyRange().getStart();
            }

            if (job.getFrequencyRange().getStop() > maxFreq) {
               maxFreq = job.getFrequencyRange().getStop();
            }

            VNASampleBlock asb = job.getResult();
            int l = asb.getSamples().length;

            for(int i = 0; i < l; ++i) {
               VNABaseSample s = asb.getSamples()[i];
               samples[idx++] = s;
            }
         }

         VNASampleBlock frstResult = ((VNABackgroundJob)jobs.get(0)).getResult();
         data = new VNASampleBlock();
         data.setAnalyserType(frstResult.getAnalyserType());
         data.setMathHelper(frstResult.getMathHelper());
         data.setScanMode(frstResult.getScanMode());
         data.setDeviceTemperature(frstResult.getDeviceTemperature());
         data.setNumberOfSteps(blkSize);
         data.setSamples(samples);
         data.setStartFrequency(minFreq);
         data.setStopFrequency(maxFreq);
         data.setNumberOfOverscans(frstResult.getNumberOfOverscans());
         TraceHelper.text(this, "consumeDataBlock", "block=" + data);
      }

      if (data != null) {
         data.getMathHelper().applyFilter(data.getSamples());
         if (this.currentMode == VNACalibrationDialog.MeasurementMode.LOAD) {
            this.calibrationBlock.setCalibrationData4Load(data);
            this.chartLOAD.setChart(this.createChart(data, true));
         } else if (this.currentMode == VNACalibrationDialog.MeasurementMode.OPEN) {
            this.calibrationBlock.setCalibrationData4Open(data);
            this.chartOPEN.setChart(this.createChart(data, true));
         } else if (this.currentMode == VNACalibrationDialog.MeasurementMode.LOOP) {
            boolean iqMode = !"4".equals(data.getAnalyserType());
            this.calibrationBlock.setCalibrationData4Loop(data);
            this.chartLOOP.setChart(this.createChart(data, iqMode));
         } else {
            this.calibrationBlock.setCalibrationData4Short(data);
            this.chartSHORT.setChart(this.createChart(data, true));
         }

         this.calibrationBlock.setMathHelper(data.getMathHelper());
      }

      long endTime = System.currentTimeMillis();
      long diff = (endTime - this.startTime) / 1000L;
      this.mainFrame.getStatusBarStatus().setText(MessageFormat.format(VNAMessages.getString("VNACalibrationDialog.scanTime"), data.getNumberOfSteps() * this.config.getNumberOfOversample(), diff));
      this.controlButtonsBasedOnDIB(true);
      this.processCalibrationsBlock();
      TraceHelper.exit(this, "consumeDataBlock");
   }

   void controlButtonsBasedOnDIB(boolean val) {
      TraceHelper.entry(this, "controlButtonsBasedOnDIB");
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      VNAScanModeParameter smp = dib.getScanModeParameterForMode(this.datapool.getScanMode());
      boolean x = val & smp.isRequiresLoop();
      this.pnlLoop.setEnabled(x);
      this.btReadLoop.setEnabled(x);
      this.chartLOOP.setEnabled(x);
      this.txtLOOP.setEnabled(x);
      x = val & smp.isRequiresLoad();
      this.pnlLoad.setEnabled(x);
      this.btReadLoad.setEnabled(x);
      this.chartLOAD.setEnabled(x);
      this.txtLOAD.setEnabled(x);
      x = val & smp.isRequiresOpen();
      this.pnlOpen.setEnabled(x);
      this.btReadOpen.setEnabled(x);
      this.chartOPEN.setEnabled(x);
      this.txtOPEN.setEnabled(x);
      x = val & smp.isRequiresShort();
      this.pnlShort.setEnabled(x);
      this.btReadShort.setEnabled(x);
      this.chartSHORT.setEnabled(x);
      this.txtSHORT.setEnabled(x);
   }

   private JPanel createBottomPanel() {
      TraceHelper.entry(this, "createBottomPanel");
      ButtonGroup bg = new ButtonGroup();
      JPanel rc = new JPanel();
      rc.setLayout(new MigLayout("", "[50%,left][grow,fill]", "[top,fill]"));
      JPanel pnlMode1 = new JPanel();
      this.rdbtnMode1 = new JRadioButton(VNAMessages.getString("VNACalibrationDialog.rdbtnMode1"));
      bg.add(this.rdbtnMode1);
      JPanel pnlMode2 = new JPanel();
      this.rdbtnMode2 = new JRadioButton(VNAMessages.getString("VNACalibrationDialog.rdbtnMode2"));
      bg.add(this.rdbtnMode2);
      this.rdbtnMode1.addActionListener((e) -> {
         pnlMode1.repaint();
         pnlMode2.repaint();
      });
      this.rdbtnMode2.addActionListener((e) -> {
         pnlMode1.repaint();
         pnlMode2.repaint();
      });
      this.rdbtnMode1.setSelected(false);
      this.rdbtnMode1.setFocusPainted(false);
      pnlMode1.setBorder(new ComponentTitledBorder(this.rdbtnMode1, pnlMode1, BorderFactory.createEtchedBorder()));
      pnlMode1.setLayout(new MigLayout("", "[fill]", "[top,100px]"));
      rc.add(pnlMode1, "");
      VNACalibrationRange[] calRanges = this.datapool.getDriver().getCalibrationRanges();
      VNACalibrationRangeTable jlist = new VNACalibrationRangeTable(calRanges);
      JScrollPane scrollPane = new JScrollPane(jlist);
      scrollPane.setViewportBorder((Border)null);
      scrollPane.setBackground(pnlMode1.getBackground());
      pnlMode1.add(scrollPane);
      this.rdbtnMode2.setSelected(true);
      this.rdbtnMode2.setFocusPainted(false);
      pnlMode2.setBorder(new ComponentTitledBorder(this.rdbtnMode2, pnlMode2, BorderFactory.createEtchedBorder()));
      pnlMode2.setLayout(new MigLayout("", "[left][]", "[top]"));
      rc.add(pnlMode2, "");
      JLabel lblOverscan = new JLabel(VNAMessages.getString("VNACalibrationDialog.lblOverscan.text"));
      this.txtOverscan = new JTextField();
      this.txtOverscan.setColumns(3);
      pnlMode2.add(lblOverscan, "");
      pnlMode2.add(this.txtOverscan, "wrap");
      JLabel lblNumSamples = new JLabel(VNAMessages.getString("VNACalibrationDialog.lblNumSamples.text"));
      this.txtNumSamples = new JTextField();
      this.txtNumSamples.setColumns(6);
      pnlMode2.add(lblNumSamples, "");
      pnlMode2.add(this.txtNumSamples, "");
      TraceHelper.exit(this, "createBottomPanel");
      return rc;
   }

   private JPanel createButtonPanel() {
      TraceHelper.entry(this, "createButtonPanel");
      JPanel rc = new JPanel();
      rc.setLayout(new MigLayout("", "[fill][fill][fill][fill][fill]", "[center]"));
      rc.add(new HelpButton(this, "VNACalibrationDialog"), "wmin 100px");
      this.btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
      this.btCancel.addActionListener((e) -> {
         this.dataValid = false;
         this.setVisible(false);
      });
      this.btCancel.setActionCommand("Cancel");
      rc.add(this.btCancel, "wmin 100px");
      this.btLOAD = new JButton(VNAMessages.getString("Button.Load"));
      this.btLOAD.addActionListener((e) -> {
         this.doLOAD();
      });
      rc.add(this.btLOAD, "wmin 100px");
      this.btSAVE = new JButton(VNAMessages.getString("Button.Save"));
      this.btSAVE.addActionListener((e) -> {
         this.doSAVE();
      });
      this.btSAVE.setEnabled(false);
      rc.add(this.btSAVE, "wmin 100px");
      this.btOK = new JButton(VNAMessages.getString("Button.Update"));
      this.btOK.addActionListener((e) -> {
         this.dataValid = true;
         this.setVisible(false);
      });
      this.btOK.setActionCommand("OK");
      rc.add(this.btOK, "wmin 100px");
      TraceHelper.exit(this, "createButtonPanel");
      return rc;
   }

   private JFreeChart createChart(VNASampleBlock pBlock, boolean iqMode) {
      TraceHelper.entry(this, "createChart");
      JFSeries series1 = new JFSeries((VNAMeasurementScale)null);
      JFSeries series2 = new JFSeries((VNAMeasurementScale)null);
      XYSeries xySeries1 = null;
      XYSeries xySeries2 = null;
      if (iqMode) {
         xySeries1 = new XYSeries("I");
         xySeries2 = new XYSeries("Q");
      } else {
         xySeries1 = new XYSeries("I");
         xySeries2 = new XYSeries("RSS");
      }

      if (pBlock != null && pBlock.getSamples() != null) {
         VNABaseSample[] pDataList = pBlock.getSamples();
         int i;
         VNABaseSample data;
         if (iqMode) {
            for(i = 0; i < pDataList.length; ++i) {
               data = pDataList[i];
               xySeries1.add((double)data.getFrequency(), data.getLoss());
               xySeries2.add((double)data.getFrequency(), data.getAngle());
            }
         } else {
            for(i = 0; i < pDataList.length; ++i) {
               data = pDataList[i];
               xySeries1.add((double)data.getFrequency(), data.getLoss());
               xySeries2.add((double)data.getFrequency(), (double)data.getRss1());
            }
         }
      }

      series1.setSeries(xySeries1);
      series2.setSeries(xySeries2);
      JFreeChart chart = ChartFactory.createXYLineChart((String)null, (String)null, (String)null, (XYDataset)null, PlotOrientation.VERTICAL, false, false, false);
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

   private JPanel createPanelLoad(JFreeChart chart) {
      TraceHelper.entry(this, "createPanelLoad");
      JPanel rc = new JPanel();
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalibrationDialog.grpLoad.text"), 4, 2, (Font)null, (Color)null));
      rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));
      this.chartLOAD = new ChartPanel(chart, true);
      this.chartLOAD.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Load() != null) {
               VNASampleBlock blk = new VNASampleBlock(VNACalibrationDialog.this.calibrationBlock);
               blk.setSamples(VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Load().getSamples());
               new VNACalibrationDataDetailsDialog(VNACalibrationDialog.this.getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Load");
            }

         }
      });
      this.chartLOAD.setIgnoreRepaint(true);
      this.txtLOAD = new JTextArea();
      this.txtLOAD.setEditable(false);
      this.txtLOAD.setBackground(UIManager.getColor("Viewport.background"));
      this.txtLOAD.setFont(UIManager.getFont("TextField.font"));
      if (this.datapool.getDeviceType().equals("3")) {
         if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
            this.txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.tranExtender"));
         } else {
            this.txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.reflExtender"));
         }
      } else if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
         this.txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.tran"));
      } else {
         this.txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.refl"));
      }

      this.txtLOAD.setWrapStyleWord(true);
      this.txtLOAD.setLineWrap(true);
      this.btReadLoad = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadLoad.text"));
      this.btReadLoad.addActionListener((e) -> {
         this.currentMode = VNACalibrationDialog.MeasurementMode.LOAD;
         this.doMeasure();
      });
      rc.add(this.chartLOAD, "wrap");
      rc.add(this.btReadLoad, "wrap");
      rc.add(this.txtLOAD, "");
      TraceHelper.exit(this, "createPanelLoad");
      return rc;
   }

   private JPanel createPanelLoop(JFreeChart chart) {
      TraceHelper.entry(this, "cratePanelLoop");
      JPanel rc = new JPanel();
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalibrationDialog.grpLoop.text"), 4, 2, (Font)null, (Color)null));
      rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));
      this.chartLOOP = new ChartPanel(chart, true);
      this.chartLOOP.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Loop() != null) {
               VNASampleBlock blk = new VNASampleBlock(VNACalibrationDialog.this.calibrationBlock);
               blk.setSamples(VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Loop().getSamples());
               new VNACalibrationDataDetailsDialog(VNACalibrationDialog.this.getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Loop");
            }

         }
      });
      this.chartLOOP.setIgnoreRepaint(true);
      this.txtLOOP = new JTextArea();
      this.txtLOOP.setEditable(false);
      this.txtLOOP.setBackground(UIManager.getColor("Viewport.background"));
      this.txtLOOP.setFont(UIManager.getFont("TextField.font"));
      if (this.datapool.getDeviceType().equals("3")) {
         if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
            this.txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.tranExtender"));
         } else {
            this.txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.reflExtender"));
         }
      } else if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
         this.txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.tran"));
      } else {
         this.txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.refl"));
      }

      this.txtLOOP.setWrapStyleWord(true);
      this.txtLOOP.setLineWrap(true);
      this.btReadLoop = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadLoop.text"));
      this.btReadLoop.addActionListener((e) -> {
         this.currentMode = VNACalibrationDialog.MeasurementMode.LOOP;
         this.doMeasure();
      });
      rc.add(this.chartLOOP, "wrap");
      rc.add(this.btReadLoop, "wrap");
      rc.add(this.txtLOOP, "");
      TraceHelper.exit(this, "cratePanelLoop");
      return rc;
   }

   private JPanel createPanelOpen(JFreeChart chart) {
      TraceHelper.entry(this, "createPanelOpen");
      JPanel rc = new JPanel();
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalibrationDialog.grpOpen.text"), 4, 2, (Font)null, (Color)null));
      rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));
      this.chartOPEN = new ChartPanel(chart, true);
      this.chartOPEN.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Open() != null) {
               VNASampleBlock blk = new VNASampleBlock(VNACalibrationDialog.this.calibrationBlock);
               blk.setSamples(VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Open().getSamples());
               new VNACalibrationDataDetailsDialog(VNACalibrationDialog.this.getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Open");
            }

         }
      });
      this.chartOPEN.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.chartOPEN.setIgnoreRepaint(true);
      this.txtOPEN = new JTextArea();
      this.txtOPEN.setEditable(false);
      this.txtOPEN.setBackground(UIManager.getColor("Viewport.background"));
      this.txtOPEN.setFont(UIManager.getFont("TextField.font"));
      this.txtOPEN.setLineWrap(true);
      this.txtOPEN.setWrapStyleWord(true);
      if (this.datapool.getDeviceType().equals("3")) {
         if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
            this.txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.tranExtender"));
         } else {
            this.txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.reflExtender"));
         }
      } else if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
         this.txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.tran"));
      } else {
         this.txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.refl"));
      }

      this.btReadOpen = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadOpen.text"));
      this.btReadOpen.addActionListener((e) -> {
         this.currentMode = VNACalibrationDialog.MeasurementMode.OPEN;
         this.doMeasure();
      });
      rc.add(this.chartOPEN, "wrap");
      rc.add(this.btReadOpen, "wrap");
      rc.add(this.txtOPEN, "");
      TraceHelper.exit(this, "createPanelOpen");
      return rc;
   }

   private JPanel createPanelShort(JFreeChart chart) {
      TraceHelper.entry(this, "createPanelShort");
      JPanel rc = new JPanel();
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalibrationDialog.grpShort.text"), 4, 2, (Font)null, (Color)null));
      rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));
      this.chartSHORT = new ChartPanel(chart, true);
      this.chartSHORT.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Short() != null) {
               VNASampleBlock blk = new VNASampleBlock(VNACalibrationDialog.this.calibrationBlock);
               blk.setSamples(VNACalibrationDialog.this.calibrationBlock.getCalibrationData4Short().getSamples());
               new VNACalibrationDataDetailsDialog(VNACalibrationDialog.this.getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Short");
            }

         }
      });
      this.chartSHORT.setIgnoreRepaint(true);
      this.txtSHORT = new JTextArea();
      this.txtSHORT.setEditable(false);
      this.txtSHORT.setBackground(UIManager.getColor("Viewport.background"));
      this.txtSHORT.setFont(UIManager.getFont("TextField.font"));
      this.txtSHORT.setWrapStyleWord(true);
      this.txtSHORT.setLineWrap(true);
      if (this.datapool.getDeviceType().equals("3")) {
         if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
            this.txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.tranExtender"));
         } else {
            this.txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.reflExtender"));
         }
      } else if (this.calibrationBlock.getScanMode().isTransmissionMode()) {
         this.txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.tran"));
      } else {
         this.txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.refl"));
      }

      this.btReadShort = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadShort.text"));
      this.btReadShort.addActionListener((e) -> {
         this.currentMode = VNACalibrationDialog.MeasurementMode.SHORT;
         this.doMeasure();
      });
      rc.add(this.chartSHORT, "wrap");
      rc.add(this.btReadShort, "wrap");
      rc.add(this.txtSHORT, "");
      TraceHelper.exit(this, "createPanelShort");
      return rc;
   }

   private StatusBar createStatusPanel() {
      StatusBar rc = new StatusBar();
      JLabel lbl = new JLabel(VNAMessages.getString("Message.Ready"));
      lbl.setOpaque(true);
      rc.addZone("status", lbl, "*");
      return rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doExit");
      this.dataValid = false;
      this.setVisible(false);
      TraceHelper.exit(this, "doExit");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      this.controlButtonsBasedOnDIB(true);
      this.btOK.setEnabled(false);
      this.txtNumSamples.setText(VNAFormatFactory.getFrequencyFormat().format((long)dib.getNumberOfSamples4Calibration()));
      this.txtOverscan.setText(VNAFormatFactory.getFrequencyFormat().format((long)dib.getNumberOfOverscans4Calibration()));
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doLOAD() {
      TraceHelper.entry(this, "doLOAD");
      VNACalibrationLoadDialog dlg = new VNACalibrationLoadDialog(this.getOwner());
      VNACalibrationBlock cal = dlg.getSelectedCalibrationBlock();
      if (cal != null) {
         this.calibrationBlock = cal;
         boolean iqMode = !"4".equals(cal.getAnalyserType());
         this.chartOPEN.setChart(this.createChart(cal.getCalibrationData4Open(), true));
         this.chartSHORT.setChart(this.createChart(cal.getCalibrationData4Short(), true));
         this.chartLOAD.setChart(this.createChart(cal.getCalibrationData4Load(), true));
         this.chartLOOP.setChart(this.createChart(cal.getCalibrationData4Loop(), iqMode));
         this.btSAVE.setEnabled(true);
         this.btOK.setBackground(Color.GREEN);
         this.btOK.setEnabled(true);
         this.dataValid = true;
      }

      dlg.dispose();
      TraceHelper.exit(this, "doLOAD");
   }

   private void doMeasure() {
      TraceHelper.entry(this, "doMeasure");
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      if (this.rdbtnMode2.isSelected()) {
         ValidationResults results = new ValidationResults();
         int overScans = IntegerValidator.parse(this.txtOverscan.getText(), 1, 10, VNAMessages.getString("VNACalibrationDialog.lblOverscan.text"), results);
         int numSamples = IntegerValidator.parse(this.txtNumSamples.getText(), 1000, 30000, VNAMessages.getString("VNACalibrationDialog.lblNumSamples.text"), results);
         if (results.isEmpty()) {
            this.config.setNumberOfOversample(overScans);
            this.btOK.setEnabled(false);
            this.btSAVE.setEnabled(false);
            this.btLOAD.setEnabled(false);
            this.controlButtonsBasedOnDIB(false);
            VNABackgroundJob job = new VNABackgroundJob();
            job.setNumberOfSamples(numSamples);
            job.setFrequencyRange(dib);
            job.setSpeedup(1);
            job.setOverScan(overScans);
            job.setAverage(0);
            job.setScanMode(this.datapool.getScanMode());
            VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
            backgroundTask.addJob(job);
            backgroundTask.setStatusLabel((JLabel)this.statusBar.getZone("status"));
            backgroundTask.addDataConsumer(this);
            backgroundTask.execute();
            this.startTime = System.currentTimeMillis();
         } else {
            new ValidationResultsDialog(this.getOwner(), results, this.getTitle());
         }
      } else {
         this.btOK.setEnabled(false);
         this.btSAVE.setEnabled(false);
         this.btLOAD.setEnabled(false);
         this.btReadLoad.setEnabled(false);
         this.btReadOpen.setEnabled(false);
         this.btReadShort.setEnabled(false);
         this.btReadLoop.setEnabled(false);
         VNACalibrationRange[] calRanges = this.datapool.getDriver().getCalibrationRanges();
         VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
         VNACalibrationRange[] var7 = calRanges;
         int var13 = calRanges.length;

         for(int var12 = 0; var12 < var13; ++var12) {
            VNACalibrationRange calRange = var7[var12];
            VNABackgroundJob jobLF = new VNABackgroundJob();
            jobLF.setNumberOfSamples(calRange.getNumScanPoints());
            jobLF.setFrequencyRange((VNAFrequencyRange)calRange);
            jobLF.setSpeedup(1);
            jobLF.setScanMode(this.datapool.getScanMode());
            jobLF.setAverage(calRange.getNumOverScans());
            backgroundTask.addJob(jobLF);
         }

         backgroundTask.setStatusLabel((JLabel)this.statusBar.getZone("status"));
         backgroundTask.addDataConsumer(this);
         backgroundTask.execute();
         this.startTime = System.currentTimeMillis();
      }

      TraceHelper.exit(this, "doMeasure");
   }

   private void doSAVE() {
      TraceHelper.entry(this, "doSAVE()");
      VNACalibrationSaveDialog dlg = new VNACalibrationSaveDialog(this.getOwner(), this.calibrationBlock);
      dlg.dispose();
      TraceHelper.exit(this, "doSAVE()");
   }

   public VNACalibrationBlock getCalibration() {
      return this.calibrationBlock;
   }

   public boolean isDataValid() {
      return this.dataValid;
   }

   private void processCalibrationsBlock() {
      String methodName = "processCalibrationsBlock";
      TraceHelper.entry(this, "processCalibrationsBlock");

      try {
         if (this.calibrationBlock.getMathHelper() != null && this.calibrationBlock.satisfiedDeviceInfoBlock(this.datapool.getDriver().getDeviceInfoBlock())) {
            VNACalibrationContext ct = this.calibrationBlock.getMathHelper().createCalibrationContextForCalibrationPoints(this.calibrationBlock, this.datapool.getCalibrationKit());
            this.calibrationBlock = this.calibrationBlock.getMathHelper().createCalibrationBlockFromRaw(ct, this.calibrationBlock.getCalibrationData4Open(), this.calibrationBlock.getCalibrationData4Short(), this.calibrationBlock.getCalibrationData4Load(), this.calibrationBlock.getCalibrationData4Loop());
            this.calibrationBlock.setFile((File)null);
            this.btOK.setBackground(Color.GREEN);
            this.btOK.setEnabled(true);
            this.btSAVE.setEnabled(true);
            this.dataValid = true;
         }
      } catch (ProcessingException var3) {
         ErrorLogHelper.exception(this, "processCalibrationsBlock", var3);
      }

      TraceHelper.exit(this, "processCalibrationsBlock");
   }

   public void setCalibration(VNACalibrationBlock calibration) {
      this.calibrationBlock = calibration;
   }

   private static enum MeasurementMode {
      LOAD,
      OPEN,
      SHORT,
      LOOP;
   }
}
