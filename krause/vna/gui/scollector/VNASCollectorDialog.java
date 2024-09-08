package krause.vna.gui.scollector;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.importers.SnPRecord;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNASCollectorDialog extends KrauseDialog implements ActionListener {
   public static final int S11 = 0;
   public static final int S21 = 1;
   public static final int S12 = 2;
   public static final int S22 = 3;
   private JTextField txtStart;
   private JTextField txtStop;
   private JTextField txtSteps;
   private JButton buttonAddS11;
   private JButton buttonAddS12;
   private JButton buttonAddS21;
   private JButton buttonAddS22;
   private JButton buttonDeleteS11;
   private JButton buttonDeleteS12;
   private JButton buttonDeleteS21;
   private JButton buttonDeleteS22;
   private JButton buttonSave;
   private JButton buttonClose;
   private VNACalibratedSampleBlock s11;
   private VNACalibratedSampleBlock s21;
   private VNACalibratedSampleBlock s12;
   private VNACalibratedSampleBlock s22;
   private long startFreq = -1L;
   private long stopFreq = -1L;
   private int numSamples = -1;
   private VNADataPool datapool = VNADataPool.getSingleton();

   public VNASCollectorDialog() {
      super((Dialog)null, false);
      this.setDefaultCloseOperation(2);
      TraceHelper.entry(this, "VNASCollectorDialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix(this.getClass().getSimpleName());
      this.setTitle(VNAMessages.getString("VNASCollectorDialog.title"));
      JPanel panel = new JPanel();
      panel.setBorder((Border)null);
      this.getContentPane().add(panel, "Center");
      panel.setLayout(new MigLayout("", "[][grow,center][]", ""));
      JPanel pnlContent = new JPanel();
      pnlContent.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASCollectorDialog.lblScanParameters"), 4, 2, (Font)null, (Color)null));
      pnlContent.setLayout(new MigLayout("", "[][grow]", "[][][]"));
      panel.add(pnlContent, "span 3, grow, wrap");
      JLabel lblStartFrequency = new JLabel(VNAMessages.getString("VNASCollectorDialog.lblStartFrequency.text"));
      pnlContent.add(lblStartFrequency, "cell 0 0,alignx trailing,aligny top");
      this.txtStart = new JTextField();
      this.txtStart.setHorizontalAlignment(4);
      this.txtStart.setEditable(false);
      pnlContent.add(this.txtStart, "cell 1 0,growx");
      this.txtStart.setColumns(10);
      JLabel lblStopFrequency = new JLabel(VNAMessages.getString("VNASCollectorDialog.lblStopFrequency.text"));
      pnlContent.add(lblStopFrequency, "cell 0 1,alignx trailing");
      this.txtStop = new JTextField();
      this.txtStop.setHorizontalAlignment(4);
      this.txtStop.setEditable(false);
      pnlContent.add(this.txtStop, "cell 1 1,growx");
      this.txtStop.setColumns(10);
      JLabel lblOfSteps = new JLabel(VNAMessages.getString("VNASCollectorDialog.lblOfSteps.text"));
      pnlContent.add(lblOfSteps, "cell 0 2,alignx trailing");
      this.txtSteps = new JTextField();
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setEditable(false);
      pnlContent.add(this.txtSteps, "cell 1 2,growx");
      this.txtSteps.setColumns(10);
      panel.add(new JLabel(), "");
      JPanel panel_3 = new JPanel();
      panel_3.setLayout(new MigLayout("", "[]", "[]"));
      panel_3.add(this.buttonAddS21 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "");
      panel_3.add(this.buttonDeleteS21 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
      panel.add(panel_3, "");
      panel.add(new JLabel(), "wrap");
      JPanel panel_1 = new JPanel();
      panel_1.setLayout(new MigLayout("", "[]", "[]"));
      panel_1.add(this.buttonAddS11 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "wrap");
      panel_1.add(this.buttonDeleteS11 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
      panel.add(panel_1, "");
      URL url = this.getClass().getResource("/images/s-parameters.jpg");
      panel.add(new JLabel(new ImageIcon(url)), "");
      JPanel panel_4 = new JPanel();
      panel_4.setLayout(new MigLayout("", "[]", "[]"));
      panel_4.add(this.buttonAddS22 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "wrap");
      panel_4.add(this.buttonDeleteS22 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
      panel.add(panel_4, "wrap");
      panel.add(new JLabel(), "");
      JPanel panel_2 = new JPanel();
      panel_2.setLayout(new MigLayout("", "[]", "[]"));
      panel_2.add(this.buttonAddS12 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "");
      panel_2.add(this.buttonDeleteS12 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
      panel.add(panel_2, "");
      panel.add(new JLabel(), "wrap");
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "South");
      this.buttonSave = SwingUtil.createJButton("Button.Save", this);
      pnlButtons.add(this.buttonSave);
      pnlButtons.add(new HelpButton(this, this.getClass().getSimpleName()));
      this.buttonClose = SwingUtil.createJButton("Button.Close", this);
      pnlButtons.add(this.buttonClose);
      this.doDialogInit();
      TraceHelper.exit(this, "VNASCollectorDialog");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.setPreferredSize(new Dimension(400, 400));
      this.updateInfo();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      Object o = e.getSource();
      if (o == this.buttonClose) {
         this.doDialogCancel();
      } else if (o == this.buttonSave) {
         this.doSave();
      } else if (o == this.buttonAddS11) {
         this.doAddS11();
      } else if (o == this.buttonAddS12) {
         this.doAddS12();
      } else if (o == this.buttonAddS21) {
         this.doAddS21();
      } else if (o == this.buttonAddS22) {
         this.doAddS22();
      } else if (o == this.buttonDeleteS11) {
         this.doDeleteS11();
      } else if (o == this.buttonDeleteS12) {
         this.doDeleteS12();
      } else if (o == this.buttonDeleteS21) {
         this.doDeleteS21();
      } else if (o == this.buttonDeleteS22) {
         this.doDeleteS22();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doSave() {
      TraceHelper.entry(this, "doSave");
      SnPRecord[] sRecords = new SnPRecord[this.numSamples];

      for(int i = 0; i < this.numSamples; ++i) {
         sRecords[i] = new SnPRecord();
      }

      int i;
      SnPRecord rec;
      VNACalibratedSample sample;
      VNACalibratedSample[] samples;
      if (this.s11 != null) {
         samples = this.s11.getCalibratedSamples();

         for(i = 0; i < this.numSamples; ++i) {
            rec = sRecords[i];
            sample = samples[i];
            rec.setFrequency(sample.getFrequency());
            rec.setLoss(0, sample.getReflectionLoss());
            rec.setPhase(0, sample.getReflectionPhase());
         }
      }

      if (this.s21 != null) {
         samples = this.s21.getCalibratedSamples();

         for(i = 0; i < this.numSamples; ++i) {
            rec = sRecords[i];
            sample = samples[i];
            rec.setFrequency(sample.getFrequency());
            rec.setLoss(1, sample.getTransmissionLoss());
            rec.setPhase(1, sample.getTransmissionPhase());
         }
      }

      if (this.s12 != null) {
         samples = this.s12.getCalibratedSamples();

         for(i = 0; i < this.numSamples; ++i) {
            rec = sRecords[i];
            sample = samples[i];
            rec.setFrequency(sample.getFrequency());
            rec.setLoss(2, sample.getTransmissionLoss());
            rec.setPhase(2, sample.getTransmissionPhase());
         }
      }

      if (this.s22 != null) {
         samples = this.s22.getCalibratedSamples();

         for(i = 0; i < this.numSamples; ++i) {
            rec = sRecords[i];
            sample = samples[i];
            rec.setFrequency(sample.getFrequency());
            rec.setLoss(3, sample.getReflectionLoss());
            rec.setPhase(3, sample.getReflectionPhase());
         }
      }

      new VNASnPExportDialog(this, sRecords);
      TraceHelper.exit(this, "doSave");
   }

   private void doDeleteS22() {
      TraceHelper.entry(this, "doDeleteS22");
      this.s22 = null;
      this.updateInfo();
      TraceHelper.exit(this, "doDeleteS22");
   }

   private void doDeleteS21() {
      TraceHelper.entry(this, "doDeleteS21");
      this.s21 = null;
      this.updateInfo();
      TraceHelper.exit(this, "doDeleteS21");
   }

   private void doDeleteS12() {
      TraceHelper.entry(this, "doDeleteS12");
      this.s12 = null;
      this.updateInfo();
      TraceHelper.exit(this, "doDeleteS12");
   }

   private void doDeleteS11() {
      TraceHelper.entry(this, "doDeleteS11");
      this.s11 = null;
      this.updateInfo();
      TraceHelper.exit(this, "doDeleteS11");
   }

   private void doAddS22() {
      TraceHelper.entry(this, "doAddS22");
      VNACalibratedSampleBlock data = this.datapool.getCalibratedData();
      if (data != null) {
         VNACalibratedSample[] samples = data.getCalibratedSamples();
         if (this.matchesSamples(samples)) {
            this.s22 = data;
         } else {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), this.getTitle(), 0);
         }
      } else {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), this.getTitle(), 0);
      }

      this.updateInfo();
      TraceHelper.exit(this, "doAddS22");
   }

   private void doAddS21() {
      TraceHelper.entry(this, "doAddS21");
      VNACalibratedSampleBlock data = this.datapool.getCalibratedData();
      if (data != null) {
         VNACalibratedSample[] samples = data.getCalibratedSamples();
         if (this.matchesSamples(samples)) {
            this.s21 = data;
         } else {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), this.getTitle(), 0);
         }
      } else {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), this.getTitle(), 0);
      }

      this.updateInfo();
      TraceHelper.exit(this, "doAddS21");
   }

   private void doAddS12() {
      TraceHelper.entry(this, "doAddS12");
      VNACalibratedSampleBlock data = this.datapool.getCalibratedData();
      if (data != null) {
         VNACalibratedSample[] samples = data.getCalibratedSamples();
         if (this.matchesSamples(samples)) {
            this.s12 = data;
         } else {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), this.getTitle(), 0);
         }
      } else {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), this.getTitle(), 0);
      }

      this.updateInfo();
      TraceHelper.exit(this, "doAddS12");
   }

   private void doAddS11() {
      TraceHelper.entry(this, "doAddS11");
      VNACalibratedSampleBlock data = this.datapool.getCalibratedData();
      if (data != null) {
         VNACalibratedSample[] samples = data.getCalibratedSamples();
         if (this.matchesSamples(samples)) {
            this.s11 = data;
         } else {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), this.getTitle(), 0);
         }
      } else {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), this.getTitle(), 0);
      }

      this.updateInfo();
      TraceHelper.exit(this, "doAddS11");
   }

   private void updateInfo() {
      TraceHelper.entry(this, "updateInfo");
      this.buttonDeleteS11.setEnabled(this.s11 != null);
      this.buttonDeleteS12.setEnabled(this.s12 != null);
      this.buttonDeleteS21.setEnabled(this.s21 != null);
      this.buttonDeleteS22.setEnabled(this.s22 != null);
      if (this.s11 == null && this.s12 == null && this.s21 == null && this.s22 == null) {
         this.startFreq = -1L;
         this.stopFreq = -1L;
         this.numSamples = -1;
         this.txtStart.setText("");
         this.txtStop.setText("");
         this.txtSteps.setText("");
         this.buttonSave.setEnabled(false);
      } else {
         this.txtStart.setText(VNAFormatFactory.getFrequencyFormat().format(this.startFreq));
         this.txtStop.setText(VNAFormatFactory.getFrequencyFormat().format(this.stopFreq));
         this.txtSteps.setText(VNAFormatFactory.getFrequencyFormat().format((long)this.numSamples));
         this.buttonSave.setEnabled(true);
      }

      TraceHelper.exit(this, "updateInfo");
   }

   private boolean matchesSamples(VNACalibratedSample[] samples) {
      boolean rc = false;
      TraceHelper.entry(this, "matchesSamples");
      if (this.startFreq == -1L && this.stopFreq == -1L && this.numSamples == -1) {
         this.numSamples = samples.length;
         this.startFreq = samples[0].getFrequency();
         this.stopFreq = samples[this.numSamples - 1].getFrequency();
         rc = true;
      } else {
         rc = this.numSamples == samples.length && this.startFreq == samples[0].getFrequency();
      }

      TraceHelper.exit(this, "matchesSamples");
      return rc;
   }
}
