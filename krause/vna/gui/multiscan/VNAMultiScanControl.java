package krause.vna.gui.multiscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.VNAScanRange;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.gui.panels.data.table.VNAEditableFrequencyPairTable;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.resources.VNAMessages;

public class VNAMultiScanControl extends JInternalFrame implements IVNADataConsumer, ActionListener {
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNAMultiScanWindow mainWindow;
   private List<VNAMultiScanResult> results = new ArrayList();
   private JLabel lblStatus;
   private FrequencyInputField txtStart;
   private FrequencyInputField txtStop;
   private VNAEditableFrequencyPairTable tblFrequencies = null;
   private JCheckBox cbFreeRun;
   private JButton btnScan;

   public VNAMultiScanControl(VNAMultiScanWindow pMainWindow) {
      super("Control", true, false, false, true);
      this.mainWindow = pMainWindow;
      if (this.datapool.getResizedCalibrationBlock() == null && this.datapool.getMainCalibrationBlock() != null) {
         VNACalibrationBlock oldBlock = this.datapool.getResizedCalibrationBlock();
         if (oldBlock != null) {
            TraceHelper.text(this, "recalcCalibrationBlock", "OLD id=" + oldBlock.hashCode());
            TraceHelper.text(this, "recalcCalibrationBlock", " start  =" + oldBlock.getStartFrequency());
            TraceHelper.text(this, "recalcCalibrationBlock", " end    =" + oldBlock.getStopFrequency());
            TraceHelper.text(this, "recalcCalibrationBlock", " samples=" + oldBlock.getNumberOfSteps());
         }

         VNACalibrationBlock newBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(this.datapool.getMainCalibrationBlock(), this.datapool.getFrequencyRange().getStart(), this.datapool.getFrequencyRange().getStop(), this.config.getNumberOfSamples());
         TraceHelper.text(this, "recalcCalibrationBlock", "NEW id=" + newBlock.hashCode());
         TraceHelper.text(this, "recalcCalibrationBlock", " start  =" + newBlock.getStartFrequency());
         TraceHelper.text(this, "recalcCalibrationBlock", " end    =" + newBlock.getStopFrequency());
         TraceHelper.text(this, "recalcCalibrationBlock", " samples=" + newBlock.getNumberOfSteps());
         this.datapool.setResizedCalibrationBlock(newBlock);
      }

      long minFrq = this.datapool.getResizedCalibrationBlock().getStartFrequency();
      long maxFrq = this.datapool.getResizedCalibrationBlock().getStopFrequency();
      this.setLocation(0, 0);
      this.setSize(350, 300);
      this.setBackground(Color.GREEN);
      this.lblStatus = new JLabel("");
      this.getContentPane().add(this.lblStatus, "South");
      JPanel pnlAction = new JPanel();
      this.getContentPane().add(pnlAction, "North");
      this.btnScan = SwingUtil.createJButton("Panel.Data.ButtonScan", this);
      pnlAction.add(this.btnScan);
      this.cbFreeRun = SwingUtil.createJCheckbox("Panel.Data.ButtonFree", this);
      pnlAction.add(this.cbFreeRun);
      JPanel pnlRESULT = new JPanel();
      this.getContentPane().add(pnlRESULT, "Center");
      pnlRESULT.setLayout(new BorderLayout(0, 0));
      JPanel pnlBTRES = new JPanel();
      pnlRESULT.add(pnlBTRES, "South");
      JPanel pnlADD = new JPanel();
      pnlBTRES.add(pnlADD);
      JLabel lblStart = new JLabel("Start");
      pnlADD.add(lblStart);
      this.txtStart = new FrequencyInputField("start", this.datapool.getDriver().getDeviceInfoBlock().getMinFrequency(), minFrq, maxFrq);
      pnlADD.add(this.txtStart);
      this.txtStart.setColumns(10);
      JLabel lblStop = new JLabel("Stop");
      pnlADD.add(lblStop);
      this.txtStop = new FrequencyInputField("stop", this.datapool.getDriver().getDeviceInfoBlock().getMaxFrequency(), minFrq, maxFrq);
      pnlADD.add(this.txtStop);
      this.txtStop.setColumns(10);
      pnlRESULT.add(this.createListbox(), "Center");
      this.loadListbox();
      this.moveToFront();
      this.setVisible(true);
      this.doInit();
   }

   private void doInit() {
      TraceHelper.entry(this, "doInit");
      this.txtStart.setFrequency((long)this.config.getInteger("MultiTune.Control.Start", 1000000));
      this.txtStop.setFrequency((long)this.config.getInteger("MultiTune.Control.Stop", 30000000));
      this.config.restoreWindowPosition("MultiTune.Control", this, new Point(0, 0));
      this.config.restoreWindowSize("MultiTune.Control", this, new Dimension(300, 300));
      TraceHelper.exit(this, "doInit");
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      TraceHelper.entry(this, "actionPerformed", cmd);
      if (e.getSource() == this.cbFreeRun) {
         if (this.cbFreeRun.isSelected()) {
            this.btnScan.setEnabled(false);
            this.doSingleScan();
         } else {
            this.cbFreeRun.setSelected(false);
            this.btnScan.setEnabled(true);
         }
      } else if (e.getSource() == this.btnScan) {
         this.doSingleScan();
      } else if (e.getSource() == this.tblFrequencies) {
         if (cmd.equals("ADD")) {
            this.doAddRange();
         } else if (cmd.startsWith("DEL")) {
            int i1 = cmd.indexOf(59);
            int i2 = cmd.indexOf(59, i1 + 1);
            long start = Long.parseLong(cmd.substring(i1 + 1, i2));
            long stop = Long.parseLong(cmd.substring(i2 + 1));
            this.doRemoveRange(start, stop);
         }
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      TraceHelper.entry(this, "consumeDataBlock");
      Iterator var3 = jobs.iterator();

      while(var3.hasNext()) {
         VNABackgroundJob job = (VNABackgroundJob)var3.next();
         if (job instanceof VNAMultiScanBackgroundJob) {
            VNAMultiScanBackgroundJob msJob = (VNAMultiScanBackgroundJob)job;
            msJob.getResultWindow().consumeSampleBlock(job.getResult());
         }
      }

      if (this.cbFreeRun.isSelected()) {
         this.doSingleScan();
      }

      TraceHelper.exit(this, "consumeDataBlock");
   }

   private JComponent createListbox() {
      JScrollPane rc = null;
      TraceHelper.entry(this, "createListbox");
      this.tblFrequencies = new VNAEditableFrequencyPairTable();
      this.tblFrequencies.getButtonUse().setVisible(false);
      this.tblFrequencies.addActionListener(this);
      rc = new JScrollPane(this.tblFrequencies);
      rc.setPreferredSize(new Dimension(200, 300));
      rc.setMinimumSize(rc.getPreferredSize());
      rc.setAlignmentX(0.0F);
      TraceHelper.exit(this, "createListbox");
      return rc;
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.cbFreeRun.setSelected(false);
      this.config.storeWindowPosition("MultiTune.Control", this);
      this.config.storeWindowSize("MultiTune.Control", this);
      this.saveListbox();
      Iterator var2 = this.results.iterator();

      while(var2.hasNext()) {
         VNAMultiScanResult result = (VNAMultiScanResult)var2.next();
         result.dispose();
      }

      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doAddRange() {
      TraceHelper.entry(this, "doAddRange");
      long start = this.txtStart.getFrequency();
      long stop = this.txtStop.getFrequency();
      VNAScanRange range = new VNAScanRange(start, stop, this.config.getNumberOfSamples());
      ValidationResults valRes = this.datapool.getDriver().validateScanRange(range);
      if (valRes.isEmpty()) {
         VNAMultiScanResult result = new VNAMultiScanResult(this, this.txtStart.getFrequency(), this.txtStop.getFrequency(), this.mainWindow.getScale());
         this.mainWindow.getDesktop().add(result);
         this.results.add(result);
         this.tblFrequencies.addFrequency(new VNAFrequencyPair(result.getStartFrequency(), result.getStopFrequency()));
      } else {
         new ValidationResultsDialog((Window)null, valRes, VNAMessages.getString("VNAMultiScanControl.Value.1"));
      }

      TraceHelper.exit(this, "doAddRange");
   }

   private void doRemoveRange(long start, long stop) {
      TraceHelper.entry(this, "doRemoveRange");
      Iterator var6 = this.results.iterator();

      while(var6.hasNext()) {
         VNAMultiScanResult result = (VNAMultiScanResult)var6.next();
         if (result.getStartFrequency() == start && result.getStopFrequency() == stop) {
            this.results.remove(result);
            result.dispose();
            break;
         }
      }

      TraceHelper.exit(this, "doRemoveRange");
   }

   private void doSingleScan() {
      TraceHelper.entry(this, "doSingleScan");
      VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
      Iterator var3 = this.results.iterator();

      while(var3.hasNext()) {
         VNAMultiScanResult result = (VNAMultiScanResult)var3.next();
         VNABackgroundJob job = new VNAMultiScanBackgroundJob(result);
         backgroundTask.addJob(job);
      }

      backgroundTask.setStatusLabel(this.lblStatus);
      backgroundTask.addDataConsumer(this);
      backgroundTask.execute();
      TraceHelper.exit(this, "doSingleScan");
   }

   public VNAMultiScanWindow getMainWindow() {
      return this.mainWindow;
   }

   private void loadListbox() {
      TraceHelper.entry(this, "loadListbox");
      this.tblFrequencies.load(this.config.getVNAConfigDirectory() + "/Multiscan.xml");
      int x = 20;
      int y = 20;

      for(Iterator var4 = this.tblFrequencies.getFrequencyPairs().iterator(); var4.hasNext(); y += 30) {
         VNAFrequencyPair pair = (VNAFrequencyPair)var4.next();
         VNAMultiScanResult result = new VNAMultiScanResult(this, pair.getStartFrequency(), pair.getStopFrequency(), this.mainWindow.getScale());
         this.mainWindow.getDesktop().add(result);
         this.results.add(result);
         result.setLocation(x, y);
         result.moveToFront();
         x += 30;
      }

      TraceHelper.exit(this, "loadListbox");
   }

   private void saveListbox() {
      TraceHelper.entry(this, "saveListbox");
      this.tblFrequencies.save(this.config.getVNAConfigDirectory() + "/Multiscan.xml");
      TraceHelper.exit(this, "saveListbox");
   }
}
