package krause.vna.gui.panels.data;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNAScanRange;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.gui.panels.VNADiagramPanel;
import krause.vna.gui.panels.VNAImagePanel;
import krause.vna.gui.panels.VNAScaleSelectPanel;
import krause.vna.gui.panels.data.table.VNAEditableFrequencyPairTable;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADataPanel extends JPanel implements FocusListener, ActionListener, IVNADataConsumer, VNAApplicationStateObserver {
   public static final String SCAN_LIST_FILENAME = "scanlist.xml";
   public static final int MIN_SCAN_RANGE = 5000;
   private final VNAConfig config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();
   private FrequencyInputField txtStartFreq = null;
   private FrequencyInputField txtStopFreq = null;
   private JButton buttonScan = null;
   private JButton buttonZoom = null;
   private VNAScanModeComboBox cbMode = null;
   private JCheckBox cbFreeRun = null;
   private VNAEditableFrequencyPairTable tblFrequencies = null;
   private transient VNAMainFrame mainFrame;
   private JPanel gbFreqEnt;
   private JSlider sldSpeed;
   private JSlider sldAverage;
   private JLabel lblAverage;
   private JLabel lblSpeed;
   private JCheckBox cbGaussianFilter = null;
   private JCheckBox cbPhosphor;
   private JButton btnSetMaxFrequency;
   private JButton btnSetMinFrequency;

   public VNADataPanel(VNAMainFrame pMainFrame) {
      TraceHelper.entry(this, "VNADataPanel");
      this.mainFrame = pMainFrame;
      this.setLayout(new MigLayout("", "[grow,fill]", "[][grow,fill][]"));
      this.gbFreqEnt = new JPanel(new MigLayout("", "[][grow,fill][20px]", "[][]"));
      this.gbFreqEnt.setBorder(BorderFactory.createTitledBorder(VNAMessages.getString("Panel.Data.1")));
      this.gbFreqEnt.add(new JLabel(VNAMessages.getString("Panel.Data.2")), "");
      this.txtStartFreq = new FrequencyInputField("fromFreq", this.datapool.getFrequencyRange().getStart());
      this.txtStartFreq.setColumns(10);
      this.gbFreqEnt.add(this.txtStartFreq, "right");
      this.btnSetMinFrequency = SwingUtil.createImageButton("Panel.Data.min", this);
      this.gbFreqEnt.add(this.btnSetMinFrequency, "gap 0, wmax 20px, left, wrap");
      this.gbFreqEnt.add(new JLabel(VNAMessages.getString("Panel.Data.3")), "");
      this.txtStopFreq = new FrequencyInputField("toFreq", this.datapool.getFrequencyRange().getStop());
      this.txtStopFreq.setColumns(10);
      this.gbFreqEnt.add(this.txtStopFreq, "right");
      this.btnSetMaxFrequency = SwingUtil.createImageButton("Panel.Data.max", this);
      this.gbFreqEnt.add(this.btnSetMaxFrequency, "gap 0, wmax 20px, left, wrap");
      this.txtStopFreq.addFocusListener(this);
      this.txtStartFreq.addFocusListener(this);
      this.txtStopFreq.addActionListener(this);
      this.txtStartFreq.addActionListener(this);
      this.add(this.gbFreqEnt, "top,wrap");
      JPanel gbFreq = new JPanel(new MigLayout("", "[left,grow,fill]", "[top,grow,fill]"));
      gbFreq.setBorder(BorderFactory.createTitledBorder(VNAMessages.getString("Panel.Data.4")));
      JScrollPane spFrequencies = this.createListbox();
      gbFreq.add(spFrequencies, "");
      this.add(gbFreq, "wrap");
      JPanel pnlButtons = new JPanel(new MigLayout("", "[left,fill][grow,right]", ""));
      pnlButtons.setBorder(BorderFactory.createTitledBorder(VNAMessages.getString("Panel.Data.5")));
      pnlButtons.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (LogManager.getSingleton().isTracingEnabled()) {
               LogManager.getSingleton().setTracingEnabled(false);
               VNADataPanel.this.mainFrame.getStatusBarStatus().setText("Tracing disabled");
            } else {
               LogManager.getSingleton().setTracingEnabled(true);
               VNADataPanel.this.mainFrame.getStatusBarStatus().setText("Tracing enabled");
            }

            Toolkit.getDefaultToolkit().beep();
         }
      });
      this.cbMode = new VNAScanModeComboBox();
      this.cbMode.addActionListener(this);
      pnlButtons.add(this.cbMode, "span 2,wrap");
      pnlButtons.add(new JLabel(), "");
      this.buttonZoom = SwingUtil.createJButton("Panel.Data.ButtonZoom", this);
      pnlButtons.add(this.buttonZoom, "grow, wrap");
      this.cbFreeRun = SwingUtil.createJCheckBox("Panel.Data.ButtonFree", this);
      pnlButtons.add(this.cbFreeRun, "");
      this.buttonScan = SwingUtil.createJButton("Panel.Data.ButtonScan", this);
      this.buttonScan.setBackground(Color.GREEN);
      this.buttonScan.registerKeyboardAction(this.buttonScan.getActionForKeyStroke(KeyStroke.getKeyStroke(32, 0, false)), KeyStroke.getKeyStroke(10, 0, false), 0);
      this.buttonScan.registerKeyboardAction(this.buttonScan.getActionForKeyStroke(KeyStroke.getKeyStroke(32, 0, true)), KeyStroke.getKeyStroke(10, 0, true), 0);
      pnlButtons.add(this.buttonScan, "grow,wrap");
      this.lblSpeed = new JLabel(MessageFormat.format(VNAMessages.getString("Panel.Data.SpeedLabel"), this.config.getScanSpeed()));
      pnlButtons.add(this.lblSpeed, "");
      this.sldSpeed = new JSlider(1, 6, this.config.getScanSpeed());
      this.sldSpeed.setPaintLabels(false);
      this.sldSpeed.setPaintTicks(false);
      this.sldSpeed.setMajorTickSpacing(1);
      this.sldSpeed.setMinorTickSpacing(1);
      this.sldSpeed.setSnapToTicks(true);
      this.sldSpeed.setToolTipText(VNAMessages.getString("Panel.Data.Speed"));
      this.sldSpeed.putClientProperty("JComponent.sizeVariant", "small");
      this.sldSpeed.addChangeListener((e) -> {
         int val = this.sldSpeed.getValue();
         this.lblSpeed.setText(MessageFormat.format(VNAMessages.getString("Panel.Data.SpeedLabel"), val));
         this.config.setScanSpeed(val);
      });
      pnlButtons.add(this.sldSpeed, "grow, wrap");
      this.lblAverage = new JLabel(MessageFormat.format(VNAMessages.getString("Panel.Data.AverageLabel"), this.config.getAverage()));
      pnlButtons.add(this.lblAverage, "");
      this.sldAverage = new JSlider(1, 9, this.config.getAverage());
      this.sldAverage.setPaintLabels(false);
      this.sldAverage.setPaintTicks(false);
      this.sldAverage.setMajorTickSpacing(3);
      this.sldAverage.setMinorTickSpacing(1);
      this.sldAverage.setSnapToTicks(true);
      this.sldAverage.setToolTipText(VNAMessages.getString("Panel.Data.Average"));
      this.sldAverage.putClientProperty("JComponent.sizeVariant", "small");
      this.sldAverage.addChangeListener((e) -> {
         int val = this.sldAverage.getValue();
         this.lblAverage.setText(MessageFormat.format(VNAMessages.getString("Panel.Data.AverageLabel"), val));
         this.config.setAverage(val);
      });
      pnlButtons.add(this.sldAverage, "grow, wrap");
      this.sldAverage.setValue(this.config.getAverage());
      this.cbPhosphor = SwingUtil.createJCheckBox("Panel.Data.Phosphor", (ActionListener)null);
      this.cbPhosphor.setSelected(this.config.isPhosphor());
      this.cbPhosphor.addActionListener((e) -> {
         this.config.setPhosphor(this.cbPhosphor.isSelected());
      });
      pnlButtons.add(this.cbPhosphor, "grow");
      this.cbGaussianFilter = SwingUtil.createJCheckBox("Panel.Data.GaussianFilter", (ActionListener)null);
      this.cbGaussianFilter.setSelected(this.config.isApplyGaussianFilter());
      this.cbGaussianFilter.addActionListener((e) -> {
         this.config.setApplyGaussianFilter(this.cbGaussianFilter.isSelected());
      });
      pnlButtons.add(this.cbGaussianFilter, "grow");
      this.add(pnlButtons, "");
      TraceHelper.exit(this, "createDataPanel");
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      Object src = e.getSource();
      TraceHelper.entry(this, "actionPerformed", cmd);
      if (src == this.cbMode) {
         this.doChangeMode();
      } else if (src == this.buttonScan) {
         this.doSingleScan();
      } else if (src == this.buttonZoom) {
         this.doZoom();
      } else if (src == this.cbFreeRun) {
         this.doFreeRun();
      } else if (src == this.txtStartFreq) {
         this.txtStopFreq.requestFocusInWindow();
      } else if (src == this.txtStopFreq) {
         this.buttonScan.requestFocusInWindow();
      } else if (e.getSource() == this.tblFrequencies) {
         this.handleFrequencyList(cmd);
      } else {
         IVNADriver drv;
         VNADeviceInfoBlock dib;
         if (e.getSource() == this.btnSetMaxFrequency) {
            drv = this.datapool.getDriver();
            dib = drv.getDeviceInfoBlock();
            this.txtStopFreq.setFrequency(dib.getMaxFrequency());
         } else if (e.getSource() == this.btnSetMinFrequency) {
            drv = this.datapool.getDriver();
            dib = drv.getDeviceInfoBlock();
            this.txtStartFreq.setFrequency(dib.getMinFrequency());
         }
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doChangeMode() {
      this.datapool.setScanMode(this.cbMode.getSelectedMode());
      this.mainFrame.getApplicationState().evtScanModeChanged();
      IVNADriver drv = this.datapool.getDriver();
      VNADeviceInfoBlock dib = drv.getDeviceInfoBlock();
      VNAScanModeParameter smp = dib.getScanModeParameterForMode(this.datapool.getScanMode());
      VNADiagramPanel dp = this.mainFrame.getDiagramPanel();
      VNAScaleSelectPanel ssp = dp.getScaleSelectPanel();
      if (smp != null) {
         ssp.getCbLeftScale().setSelectedItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(smp.getScaleLeft()));
         ssp.getCbRightScale().setSelectedItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(smp.getScaleRight()));
         dp.repaint();
      }

   }

   private void handleFrequencyList(String cmd) {
      String[] tokens = cmd.split("\\;", 10);
      if ("ADD".equals(tokens[0])) {
         VNAFrequencyPair fp = new VNAFrequencyPair(this.txtStartFreq.getFrequency(), this.txtStopFreq.getFrequency());
         this.tblFrequencies.addFrequency(fp);
      } else if ("USE".equals(tokens[0])) {
         long start = Long.parseLong(tokens[1]);
         long stop = Long.parseLong(tokens[2]);
         this.txtStartFreq.setFrequency(start);
         this.txtStopFreq.setFrequency(stop);
         this.changeFrequencyBasedOnFields();
         if (this.config.isScanAfterTableSelect() && this.buttonScan.isEnabled()) {
            this.doSingleScan();
         }
      }

   }

   private void changeFrequencyBasedOnFields() {
      TraceHelper.entry(this, "changeFrequencyBasedOnFields");
      this.datapool.setFrequencyRange(this.txtStartFreq.getFrequency(), this.txtStopFreq.getFrequency());
      this.datapool.clearResizedCalibrationBlock();
      this.datapool.setRawData((VNASampleBlock)null);
      this.datapool.getRawDataBlocks().clear();
      TraceHelper.exit(this, "changeFrequencyBasedOnFields");
   }

   public void changeFrequencyRange(VNAFrequencyRange range) {
      TraceHelper.entry(this, "changeFrequencyRange");
      this.txtStartFreq.setFrequency(range.getStart());
      this.txtStopFreq.setFrequency(range.getStop());
      this.changeFrequencyBasedOnFields();
      TraceHelper.exit(this, "changeFrequencyRange");
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      if (newState == VNAApplicationState.INNERSTATE.DRIVERLOADED) {
         this.buttonScan.setEnabled(false);
         this.buttonZoom.setEnabled(false);
         this.cbFreeRun.setEnabled(false);
         this.tblFrequencies.setEnabled(false);
         this.txtStartFreq.setEditable(false);
         this.txtStopFreq.setEditable(false);
         VNADeviceInfoBlock devInfo = this.datapool.getDriver().getDeviceInfoBlock();
         this.txtStartFreq.setLowerLimit(devInfo.getMinFrequency());
         this.txtStartFreq.setUpperLimit(devInfo.getMaxFrequency());
         this.txtStopFreq.setLowerLimit(devInfo.getMinFrequency());
         this.txtStopFreq.setUpperLimit(devInfo.getMaxFrequency());
         this.txtStartFreq.setFrequency(devInfo.getMinFrequency());
         this.txtStopFreq.setFrequency(devInfo.getMaxFrequency());
         if (oldState == VNAApplicationState.INNERSTATE.GUIINITIALIZED) {
            this.cbMode.setModes(devInfo.getScanModeParameters());
         }

         this.cbMode.setEnabled(true);
         this.datapool.setFrequencyRange(devInfo.getMinFrequency(), devInfo.getMaxFrequency());
      } else if (newState == VNAApplicationState.INNERSTATE.CALIBRATED) {
         this.buttonScan.setEnabled(true);
         this.buttonZoom.setEnabled(true);
         this.cbFreeRun.setEnabled(true);
         this.cbMode.setEnabled(true);
         if (oldState == VNAApplicationState.INNERSTATE.DRIVERLOADED) {
            this.cbMode.setSelectedMode(this.datapool.getScanMode());
         }

         this.txtStartFreq.setEditable(true);
         this.txtStopFreq.setEditable(true);
         this.tblFrequencies.setEnabled(true);
         this.sldSpeed.setEnabled(true);
         this.sldAverage.setEnabled(true);
         this.cbGaussianFilter.setEnabled(true);
         this.cbPhosphor.setEnabled(true);
      } else if (newState == VNAApplicationState.INNERSTATE.RUNNING) {
         this.buttonScan.setEnabled(false);
         this.buttonZoom.setEnabled(false);
         if (!this.cbFreeRun.isSelected()) {
            this.cbFreeRun.setEnabled(false);
         }

         this.cbMode.setEnabled(false);
         this.tblFrequencies.setEnabled(false);
         this.txtStartFreq.setEditable(false);
         this.txtStopFreq.setEditable(false);
         this.sldSpeed.setEnabled(false);
         this.sldAverage.setEnabled(false);
         this.cbGaussianFilter.setEnabled(false);
         this.cbPhosphor.setEnabled(false);
      } else {
         this.buttonScan.setEnabled(false);
         this.buttonZoom.setEnabled(false);
         this.cbFreeRun.setEnabled(false);
         this.cbMode.setEnabled(false);
         this.tblFrequencies.setEnabled(false);
         this.txtStartFreq.setEditable(false);
         this.txtStopFreq.setEditable(false);
         this.sldSpeed.setEnabled(false);
         this.sldAverage.setEnabled(false);
         this.cbGaussianFilter.setEnabled(false);
         this.cbPhosphor.setEnabled(false);
      }

   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      String methodName = "consumeDataBlock";
      TraceHelper.entry(this, "consumeDataBlock");
      this.mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(0));
      this.mainFrame.getApplicationState().evtMeasureEnded();
      if (this.cbFreeRun.isSelected()) {
         this.doSingleScan();
      }

      TraceHelper.exit(this, "consumeDataBlock");
   }

   private JScrollPane createListbox() {
      JScrollPane rc = null;
      TraceHelper.entry(this, "createListbox");
      this.tblFrequencies = new VNAEditableFrequencyPairTable();
      this.tblFrequencies.addActionListener(this);
      rc = new JScrollPane(this.tblFrequencies);
      rc.setViewportBorder((Border)null);
      TraceHelper.exit(this, "createListbox");
      return rc;
   }

   private void doFreeRun() {
      TraceHelper.entry(this, "doFreeRun");
      if (this.cbFreeRun.isSelected()) {
         this.buttonScan.setEnabled(false);
         this.doSingleScan();
      } else {
         this.buttonScan.setEnabled(true);
      }

      TraceHelper.exit(this, "doFreeRun");
   }

   private void doSingleScan() {
      String methodName = "doSingleScan";
      TraceHelper.entry(this, "doSingleScan");
      this.mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(3));
      VNAScanRange range = new VNAScanRange(this.txtStartFreq.getFrequency(), this.txtStopFreq.getFrequency(), this.config.getNumberOfSamples());
      ValidationResults valRes = this.datapool.getDriver().validateScanRange(range);
      if (valRes.isEmpty()) {
         this.txtStartFreq.setFrequency(range.getStart());
         this.txtStopFreq.setFrequency(range.getStop());
         this.datapool.setFrequencyRange(range);
         if (!this.datapool.getDriver().isScanSupported(this.config.getNumberOfSamples(), range, this.datapool.getScanMode())) {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Panel.Data.miniVNApro.1"), VNAMessages.getString("Panel.Data.miniVNApro.2"), 1);
            this.mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(0));
            return;
         }

         if (this.datapool.getResizedCalibrationBlock() == null && this.datapool.getMainCalibrationBlock() != null) {
            VNACalibrationBlock newBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(this.datapool.getMainCalibrationBlock(), this.datapool.getFrequencyRange().getStart(), this.datapool.getFrequencyRange().getStop(), this.config.getNumberOfSamples());
            this.datapool.setResizedCalibrationBlock(newBlock);
         }

         this.mainFrame.getApplicationState().evtMeasureStarted();
         VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
         backgroundTask.setStatusLabel(this.mainFrame.getStatusBarStatus());
         backgroundTask.addDataConsumer(this.mainFrame.getDiagramPanel());
         backgroundTask.addDataConsumer(this);
         VNABackgroundJob job = new VNABackgroundJob();
         job.setSpeedup(this.sldSpeed.getValue());
         job.setAverage(this.sldAverage.getValue());
         job.setNumberOfSamples(this.config.getNumberOfSamples());
         job.setFrequencyRange((VNAFrequencyRange)range);
         job.setScanMode(this.datapool.getScanMode());
         backgroundTask.addJob(job);
         backgroundTask.execute();
      } else {
         new ValidationResultsDialog((Window)null, valRes, VNAMessages.getString("VNANetworkDialog.ErrorDialogHeader"));
      }

      TraceHelper.exit(this, "doSingleScan");
   }

   private void doZoom() {
      TraceHelper.entry(this, "doZoom");
      VNAMarkerPanel mp = this.mainFrame.getMarkerPanel();
      if (!mp.getMarker(0).isVisible() && !mp.getMarker(1).isVisible()) {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Panel.Data.Zoom.Msg1"), VNAMessages.getString("Panel.Data.Zoom.Title"), 1);
      } else {
         VNAMarker marker = null;
         VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
         long fStart;
         long fStop;
         long oldSpan;
         if (mp.getMarker(0).isVisible() && mp.getMarker(1).isVisible()) {
            fStart = mp.getMarker(0).getFrequency();
            fStop = mp.getMarker(1).getFrequency();
            if (fStop < fStart) {
               oldSpan = fStart;
               fStart = fStop;
               fStop = oldSpan;
            }
         } else {
            long newSpan;
            if (mp.getMarker(0).isVisible()) {
               marker = mp.getMarker(0);
               oldSpan = this.txtStopFreq.getFrequency() - this.txtStartFreq.getFrequency();
               newSpan = oldSpan / 10L;
               fStart = Math.max(mp.getMarker(0).getFrequency() - newSpan, dib.getMinFrequency());
               fStop = Math.min(mp.getMarker(0).getFrequency() + newSpan, dib.getMaxFrequency());
            } else {
               marker = mp.getMarker(1);
               oldSpan = this.txtStopFreq.getFrequency() - this.txtStartFreq.getFrequency();
               newSpan = oldSpan / 10L;
               fStart = Math.max(mp.getMarker(1).getFrequency() - newSpan, dib.getMinFrequency());
               fStop = Math.min(mp.getMarker(1).getFrequency() + newSpan, dib.getMaxFrequency());
            }
         }

         if (fStart + 5000L > fStop) {
            String msg = MessageFormat.format(VNAMessages.getString("Panel.Data.Zoom.Msg2"), 5000);
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), msg, VNAMessages.getString("Panel.Data.Zoom.Title"), 1);
         } else {
            if (marker != null) {
               VNAImagePanel ip = this.mainFrame.getDiagramPanel().getImagePanel();
               int x = ip.getWidth() / 2;
               VNACalibratedSample sample = ip.getSampleAtMousePosition(x);
               mp.getMarker(0).update(sample);
            } else {
               mp.getMarker(0).clearFields();
               mp.getMarker(1).clearFields();
               mp.getDeltaMarker().clearFields();
            }

            this.txtStartFreq.setFrequency(fStart);
            this.txtStopFreq.setFrequency(fStop);
            this.changeFrequencyBasedOnFields();
            this.cbFreeRun.setSelected(false);
            if (this.config.isScanAfterZoom() && this.buttonScan.isEnabled()) {
               this.doSingleScan();
            } else {
               this.mainFrame.getDiagramPanel().repaint();
            }
         }
      }

      TraceHelper.exit(this, "doZoom");
   }

   public void focusGained(FocusEvent e) {
      TraceHelper.entry(this, "focusGained");
      TraceHelper.exit(this, "focusGained");
   }

   public void focusLost(FocusEvent e) {
      TraceHelper.entry(this, "focusLost");
      if (this.txtStartFreq.isValidData() && this.txtStopFreq.isValidData()) {
         long fStart = this.txtStartFreq.getFrequency();
         long fStop = this.txtStopFreq.getFrequency();
         if (fStop < fStart) {
            this.txtStartFreq.setFrequency(fStop);
            this.txtStopFreq.setFrequency(fStart);
         }

         this.changeFrequencyBasedOnFields();
      }

      TraceHelper.exit(this, "focusLost");
   }

   public JButton getButtonScan() {
      return this.buttonScan;
   }

   public VNAScanModeComboBox getCbMode() {
      return this.cbMode;
   }

   public FrequencyInputField getTxtStartFreq() {
      return this.txtStartFreq;
   }

   public FrequencyInputField getTxtStopFreq() {
      return this.txtStopFreq;
   }

   public boolean isIdleMode() {
      return this.buttonScan.isEnabled() && !this.cbFreeRun.isSelected();
   }

   public void load() {
      TraceHelper.entry(this, "load");
      this.txtStartFreq.setFrequency(this.datapool.getFrequencyRange().getStart());
      this.txtStopFreq.setFrequency(this.datapool.getFrequencyRange().getStop());
      this.tblFrequencies.load(this.config.getVNAConfigDirectory() + "/" + "scanlist.xml");
      TraceHelper.exit(this, "load");
   }

   public void save() {
      this.tblFrequencies.save(this.config.getVNAConfigDirectory() + "/" + "scanlist.xml");
   }

   public void setSingleScanMode() {
      TraceHelper.entry(this, "setSingleScanMode");
      this.buttonScan.setEnabled(false);
      this.cbFreeRun.setSelected(false);
      TraceHelper.exit(this, "setSingleScanMode");
   }

   public void startFreeRun() {
      TraceHelper.entry(this, "startFreeRun");
      if (!this.cbFreeRun.isSelected()) {
         this.cbFreeRun.setSelected(true);
         if (this.buttonScan.isEnabled()) {
            this.doFreeRun();
         }
      }

      TraceHelper.exit(this, "startFreeRun");
   }

   public void startSingleScan() {
      TraceHelper.entry(this, "startSingleScan");
      if (this.cbFreeRun.isSelected()) {
         this.cbFreeRun.setSelected(false);
      }

      if (this.buttonScan.isEnabled()) {
         this.doSingleScan();
      }

      TraceHelper.exit(this, "startSingleScan");
   }
}
