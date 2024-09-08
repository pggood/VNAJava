package krause.vna.gui.scheduler;

import it.sauronsoftware.cron4j.Scheduler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.export.CSVExporter;
import krause.vna.export.JpegExporter;
import krause.vna.export.PDFExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.XMLExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNASchedulerDialog extends KrauseDialog implements IVNADataConsumer {
   private static VNAConfig config = VNAConfig.getSingleton();
   private JTextField txtCron;
   private Scheduler scheduler = new Scheduler();
   private String taskID;
   private VNAMainFrame mainFrame;
   private JButton btnStart;
   private JButton btnStop;
   private JButton btnOK;
   private JCheckBox rdbtnXls;
   private JCheckBox rdbtnCsv;
   private JCheckBox rdbtnPdf;
   private JCheckBox rdbtnJpg;
   private JCheckBox rdbtnXml;
   private JList lstTasks;
   private JCheckBox rdbtnZPlot;
   private JCheckBox rdbtnSParm;

   public VNASchedulerDialog(Frame aFrame, VNAMainFrame pMainFrame) {
      super((Window)aFrame, true);
      this.mainFrame = pMainFrame;
      this.setTitle(VNAMessages.getString("VNASchedulerDialog.title"));
      this.setResizable(false);
      this.setDefaultCloseOperation(2);
      JPanel pnlButton = new JPanel();
      this.getContentPane().add(pnlButton, "South");
      pnlButton.setLayout(new BorderLayout(0, 0));
      JPanel panel = new JPanel();
      pnlButton.add(panel, "East");
      this.btnOK = new JButton(VNAMessages.getString("Button.Close"));
      this.btnOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNASchedulerDialog.this.doDialogCancel();
         }
      });
      panel.add(this.btnOK);
      JPanel pnlControl = new JPanel();
      pnlButton.add(pnlControl, "West");
      this.btnStart = new JButton(VNAMessages.getString("Button.START"));
      this.btnStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNASchedulerDialog.this.doStart();
         }
      });
      pnlControl.add(this.btnStart);
      this.btnStop = new JButton(VNAMessages.getString("Button.STOP"));
      this.btnStop.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNASchedulerDialog.this.doStop();
         }
      });
      pnlControl.add(this.btnStop);
      JPanel pnlMain = new JPanel();
      this.getContentPane().add(pnlMain, "Center");
      pnlMain.setLayout(new BorderLayout(0, 0));
      JPanel pnlOutput = new JPanel();
      FlowLayout flowLayout_1 = (FlowLayout)pnlOutput.getLayout();
      flowLayout_1.setAlignment(0);
      pnlOutput.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASchedulerDialog.format.1"), 4, 2, (Font)null, (Color)null));
      pnlMain.add(pnlOutput, "North");
      pnlOutput.add(new JLabel(VNAMessages.getString("VNASchedulerDialog.format.2")));
      this.rdbtnXls = SwingUtil.createJCheckbox("Menu.Export.XLS", (ActionListener)null);
      pnlOutput.add(this.rdbtnXls);
      this.rdbtnCsv = SwingUtil.createJCheckbox("Menu.Export.CSV", (ActionListener)null);
      pnlOutput.add(this.rdbtnCsv);
      this.rdbtnPdf = SwingUtil.createJCheckbox("Menu.Export.PDF", (ActionListener)null);
      pnlOutput.add(this.rdbtnPdf);
      this.rdbtnJpg = SwingUtil.createJCheckbox("Menu.Export.JPG", (ActionListener)null);
      pnlOutput.add(this.rdbtnJpg);
      this.rdbtnXml = SwingUtil.createJCheckbox("Menu.Export.XML", (ActionListener)null);
      pnlOutput.add(this.rdbtnXml);
      this.rdbtnZPlot = SwingUtil.createJCheckbox("Menu.Export.ZPlot", (ActionListener)null);
      pnlOutput.add(this.rdbtnZPlot);
      this.rdbtnSParm = SwingUtil.createJCheckbox("Menu.Export.S2P", (ActionListener)null);
      pnlOutput.add(this.rdbtnSParm);
      JPanel pnlSchedule = new JPanel();
      pnlSchedule.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASchedulerDialog.title"), 4, 2, (Font)null, (Color)null));
      pnlMain.add(pnlSchedule, "Center");
      pnlSchedule.setLayout(new BorderLayout(0, 0));
      JPanel pnlList = new JPanel();
      pnlList.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASchedulerDialog.actions"), 4, 2, (Font)null, (Color)null));
      pnlSchedule.add(pnlList, "Center");
      pnlList.setLayout(new BorderLayout(0, 0));
      this.lstTasks = new JList(new DefaultListModel());
      this.lstTasks.setVisibleRowCount(-1);
      this.lstTasks.setSelectionMode(0);
      JScrollPane listScroller = new JScrollPane(this.lstTasks);
      listScroller.setPreferredSize(new Dimension(600, 300));
      pnlList.add(listScroller);
      JPanel pnlCron = new JPanel();
      FlowLayout flowLayout = (FlowLayout)pnlCron.getLayout();
      flowLayout.setAlignment(0);
      pnlSchedule.add(pnlCron, "North");
      JLabel lblCronstring = new JLabel(VNAMessages.getString("VNASchedulerDialog.cron"));
      pnlCron.add(lblCronstring);
      this.txtCron = new JTextField();
      this.txtCron.setText("* * * * *");
      pnlCron.add(this.txtCron);
      this.txtCron.setColumns(20);
      this.getRootPane().setDefaultButton(this.btnOK);
      this.doDialogInit();
   }

   protected void doStop() {
      TraceHelper.entry(this, "doStop");
      if (this.taskID != null) {
         this.scheduler.deschedule(this.taskID);
         this.taskID = null;
      }

      this.btnStart.setEnabled(true);
      this.btnStop.setEnabled(false);
      this.btnOK.setEnabled(true);
      this.txtCron.setEnabled(true);
      TraceHelper.exit(this, "doStop");
   }

   protected void doStart() {
      TraceHelper.entry(this, "doStart");
      this.btnStart.setEnabled(false);
      this.btnStop.setEnabled(true);
      this.btnOK.setEnabled(false);
      this.txtCron.setEnabled(false);
      this.taskID = this.scheduler.schedule(this.txtCron.getText(), new VNAScheduledScan(this.mainFrame, this));
      TraceHelper.exit(this, "doStart");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      if (this.taskID != null) {
         this.scheduler.deschedule(this.taskID);
         this.taskID = null;
      }

      if (this.scheduler.isStarted()) {
         this.scheduler.stop();
      }

      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.scheduler.start();
      this.btnStop.setEnabled(false);
      this.addEscapeKey();
      this.showCentered(this.getOwner());
      TraceHelper.exit(this, "doInit");
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      TraceHelper.entry(this, "consumeDataBlock");
      String filename = "";
      DefaultListModel model = (DefaultListModel)this.lstTasks.getModel();
      this.mainFrame.getDataPanel().consumeDataBlock(jobs);
      this.mainFrame.getDiagramPanel().consumeDataBlock(jobs);

      try {
         VNAExporter exp = null;
         String fnp = config.getAutoExportDirectory() + System.getProperty("file.separator") + config.getAutoExportFilename();
         Date now = new Date(System.currentTimeMillis());
         String nowString = DateFormat.getDateTimeInstance().format(now);
         if (this.rdbtnXml.isSelected()) {
            exp = new XMLExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         if (this.rdbtnJpg.isSelected()) {
            exp = new JpegExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         if (this.rdbtnCsv.isSelected()) {
            exp = new CSVExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         if (this.rdbtnPdf.isSelected()) {
            exp = new PDFExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         if (this.rdbtnXls.isSelected()) {
            exp = new XLSExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         if (this.rdbtnSParm.isSelected()) {
            exp = new SnPExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         if (this.rdbtnZPlot.isSelected()) {
            exp = new ZPlotsExporter(this.mainFrame);
            filename = exp.export(fnp, config.isExportOverwrite());
            model.add(0, nowString + " " + filename);
         }

         this.lstTasks.ensureIndexIsVisible(0);
      } catch (Exception var8) {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.5"), var8.getMessage()), VNAMessages.getString("Message.Export.6"), 0);
         this.doStop();
      }

      TraceHelper.exit(this, "consumeDataBlock");
   }
}
