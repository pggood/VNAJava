package krause.vna.device.serial.max6.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.device.serial.max6.VNADriverSerialMax6Messages;
import krause.vna.device.serial.max6.VNARssPair;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNATLCalibrationDialog extends VNADriverDialog implements IVNADataConsumer {
   private VNADriverSerialMax6DIB dib;
   private IVNADriver driver;
   private JButton btOK;
   private JPanel panel;
   private FrequencyInputField frq;
   private VNAScanMode mode;
   private int currStep = 0;
   private JTextField txtMessUss;
   private VNARssPair currPair;
   private JTextField txtMessdBm;
   private double ONE_MW = 0.001D;
   private JButton btLoop;
   private JButton btAttn;
   private JTextField txtRawLoop;
   private JTextField txtRawAttn;
   private JTextField txtCalAttn;
   private JTextField txtCalLoop;
   private JTextField txtAttn;
   private JTextField txtCalRssOffset;
   private JTextField txtCalRssScale;
   private JLabel lblStatus;

   public VNATLCalibrationDialog(VNAMainFrame pMainFrame, IVNADriver pDriver, VNAScanMode pMode) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNATLCalibrationDialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNATLCalibrationDialog");
      this.driver = pDriver;
      this.dib = (VNADriverSerialMax6DIB)this.driver.getDeviceInfoBlock();
      this.mode = pMode;
      this.currPair = this.createInitialRssPair();
      this.setTitle(VNAMessages.getString("VNATLCalibrationDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setMinimumSize(new Dimension(300, 300));
      this.setPreferredSize(new Dimension(400, 340));
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout(new MigLayout("", "[][][][][]", "[]"));
      this.panel.add(new JLabel("Cal. freq. (Hz):"), "");
      this.frq = new FrequencyInputField("fromFreq", this.dib.getMinFrequency(), this.dib.getMinFrequency(), this.dib.getMaxFrequency());
      this.frq.setColumns(10);
      this.frq.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
         }

         public void focusLost(FocusEvent e) {
            VNATLCalibrationDialog.this.doStep0();
         }
      });
      this.panel.add(this.frq, "grow,wrap");
      this.panel.add(new JLabel("Measured Uss (V):"), "");
      this.txtMessUss = new JTextField();
      this.txtMessUss.setHorizontalAlignment(4);
      this.txtMessUss.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
            VNATLCalibrationDialog.this.txtMessUss.select(0, 999);
         }

         public void focusLost(FocusEvent e) {
            VNATLCalibrationDialog.this.calculatedBmFromUss();
         }
      });
      this.panel.add(this.txtMessUss, "grow");
      this.txtMessdBm = new JTextField();
      this.txtMessdBm.setHorizontalAlignment(4);
      this.txtMessdBm.setEditable(false);
      this.panel.add(this.txtMessdBm, "grow");
      this.panel.add(new JLabel("dBm"), "wrap");
      this.panel.add(new JLabel("Cal. Attenuator (dB):"), "");
      this.txtAttn = new JTextField();
      this.txtAttn.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
         }

         public void focusLost(FocusEvent e) {
            try {
               double v = VNAFormatFactory.getReflectionLossFormat().parse(VNATLCalibrationDialog.this.txtAttn.getText()).doubleValue();
               VNATLCalibrationDialog.this.txtAttn.setText(VNAFormatFactory.getReflectionLossFormat().format(v));
            } catch (ParseException var4) {
            }

         }
      });
      this.txtAttn.setHorizontalAlignment(4);
      this.panel.add(this.txtAttn, "grow,wrap");
      this.panel.add(new JLabel(), "");
      this.panel.add(new JLabel("Loop"), "");
      this.panel.add(new JLabel("Atten."), "wrap");
      this.panel.add(new JLabel("Read:"), "");
      this.btLoop = new JButton("Loop");
      this.btLoop.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNATLCalibrationDialog.this.doStep2();
         }
      });
      this.panel.add(this.btLoop, "grow");
      this.btAttn = new JButton("Atten.");
      this.btAttn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNATLCalibrationDialog.this.doStep3();
         }
      });
      this.panel.add(this.btAttn, "grow,wrap");
      this.panel.add(new JLabel("Raw:"), "");
      this.txtRawLoop = new JTextField();
      this.txtRawLoop.setHorizontalAlignment(4);
      this.txtRawLoop.setEditable(false);
      this.txtRawLoop.setColumns(10);
      this.panel.add(this.txtRawLoop, "");
      this.txtRawAttn = new JTextField();
      this.txtRawAttn.setHorizontalAlignment(4);
      this.txtRawAttn.setEditable(false);
      this.txtRawAttn.setColumns(10);
      this.panel.add(this.txtRawAttn, "wrap");
      this.panel.add(new JLabel("MAX6:"), "");
      this.txtCalLoop = new JTextField();
      this.txtCalLoop.setHorizontalAlignment(4);
      this.txtCalLoop.setEditable(false);
      this.txtCalLoop.setColumns(10);
      this.panel.add(this.txtCalLoop, "");
      this.txtCalAttn = new JTextField();
      this.txtCalAttn.setHorizontalAlignment(4);
      this.txtCalAttn.setEditable(false);
      this.txtCalAttn.setColumns(10);
      this.panel.add(this.txtCalAttn, "wrap");
      this.panel.add(new JLabel());
      this.panel.add(new JLabel("Offset (dB)"), "");
      this.panel.add(new JLabel("Scale"), "wrap");
      this.panel.add(new JLabel("Initial:"), "");
      JTextField tf = new JTextField(VNAFormatFactory.getReflectionLossFormat().format(this.currPair.getOffset()));
      tf.setEditable(false);
      tf.setHorizontalAlignment(4);
      tf.setColumns(10);
      this.panel.add(tf, "");
      tf = new JTextField(VNAFormatFactory.getTransmissionScaleFormat().format(this.currPair.getScale()));
      tf.setColumns(10);
      tf.setEditable(false);
      tf.setHorizontalAlignment(4);
      this.panel.add(tf, "grow,wrap");
      this.panel.add(new JLabel("Calibrated:"), "");
      this.txtCalRssOffset = new JTextField();
      this.txtCalRssOffset.setEditable(false);
      this.txtCalRssOffset.setHorizontalAlignment(4);
      this.txtCalRssOffset.setColumns(10);
      this.panel.add(this.txtCalRssOffset, "");
      this.txtCalRssScale = new JTextField();
      this.txtCalRssScale.setColumns(10);
      this.txtCalRssScale.setEditable(false);
      this.txtCalRssScale.setHorizontalAlignment(4);
      this.panel.add(this.txtCalRssScale, "grow,wrap");
      this.lblStatus = new JLabel();
      this.panel.add(this.lblStatus, "span 4,grow,wrap");
      JPanel pnlButtons = new JPanel();
      pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 4, 2, (Font)null, new Color(0, 0, 0)));
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.setLayout(new FlowLayout(2, 5, 5));
      this.btOK = new JButton(VNADriverSerialMax6Messages.getString("Button.OK"));
      this.btOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNATLCalibrationDialog.this.doOK();
         }
      });
      this.btOK.setEnabled(false);
      JButton btCancel = new JButton(VNADriverSerialMax6Messages.getString("Button.Cancel"));
      btCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNATLCalibrationDialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(new HelpButton(this, "VNATLCalibrationDialog"));
      pnlButtons.add(btCancel);
      pnlButtons.add(this.btOK);
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNATLCalibrationDialog");
   }

   protected void calculatedBmFromUss() {
      TraceHelper.entry(this, "calculatedBmFromUss");

      try {
         Number num = VNAFormatFactory.getRSSFormat().parse(this.txtMessUss.getText());
         double uss = num.doubleValue();
         double ueff = uss / (2.0D * Math.sqrt(2.0D));
         double peff = ueff * ueff / this.dib.getReferenceResistance().getReal();
         double dbm = 10.0D * Math.log10(peff / this.ONE_MW);
         this.txtMessdBm.setText(VNAFormatFactory.getReflectionLossFormat().format(dbm));
      } catch (ParseException var10) {
      }

      TraceHelper.exit(this, "calculatedBmFromUss");
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      TraceHelper.entry(this, "consumeDataBlock");
      if (jobs != null) {
         int rss1 = 0;
         Iterator var4 = jobs.iterator();

         while(var4.hasNext()) {
            VNABackgroundJob job = (VNABackgroundJob)var4.next();
            if (job.getNumberOfSamples() == 1) {
               VNASampleBlock res = job.getResult();
               VNABaseSample sample = res.getSamples()[0];
               rss1 += sample.getRss1();
            }
         }

         rss1 /= jobs.size();
         if (this.currStep == 2) {
            this.processRawStep2(rss1);
         } else if (this.currStep == 3) {
            this.processRawStep3(rss1);
         }
      }

      this.reenableAllFields();
      TraceHelper.exit(this, "consumeDataBlock");
   }

   private VNARssPair createInitialRssPair() {
      VNARssPair rc = null;
      TraceHelper.entry(this, "createInitialRssPair");
      if (this.mode.isRss1Mode()) {
         rc = new VNARssPair(this.dib.getRss1Offset(), this.dib.getRss1Scale());
      } else if (this.mode.isRss2Mode()) {
         rc = new VNARssPair(this.dib.getRss2Offset(), this.dib.getRss2Scale());
      } else if (this.mode.isRss3Mode()) {
         rc = new VNARssPair(this.dib.getRss3Offset(), this.dib.getRss3Scale());
      }

      TraceHelper.exit(this, "createInitialRssPair");
      return rc;
   }

   private void disableAllFields() {
      TraceHelper.entry(this, "disableAllFields");
      this.btLoop.setEnabled(true);
      this.btAttn.setEnabled(true);
      this.txtMessUss.setEnabled(false);
      this.txtAttn.setEnabled(false);
      TraceHelper.exit(this, "disableAllFields");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.frq.setFrequency(100000000L);
      this.frq.grabFocus();
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");

      try {
         this.dib.setRss1Offset(VNAFormatFactory.getReflectionLossFormat().parse(this.txtCalRssOffset.getText()).doubleValue());
         this.dib.setRss1Scale(VNAFormatFactory.getTransmissionScaleFormat().parse(this.txtCalRssScale.getText()).doubleValue());
         this.dispose();
      } catch (ParseException var2) {
      }

      TraceHelper.exit(this, "doOK");
   }

   protected void doStep0() {
      TraceHelper.entry(this, "doStep0");
      this.currStep = 0;
      this.txtRawAttn.setText("");
      this.txtRawLoop.setText("");
      this.txtCalAttn.setText("");
      this.txtCalLoop.setText("");
      this.txtCalRssOffset.setText("");
      this.txtCalRssScale.setText("");
      this.updateFrequency();
      TraceHelper.exit(this, "doStep0");
   }

   protected void doStep1() {
      TraceHelper.entry(this, "doStep1");
      this.currStep = 1;
      this.updateFrequency();
      TraceHelper.exit(this, "doStep1");
   }

   protected void doStep2() {
      TraceHelper.entry(this, "doStep2");
      this.currStep = 2;
      this.updateFrequency();
      TraceHelper.exit(this, "doStep2");
   }

   protected void doStep3() {
      TraceHelper.entry(this, "doStep3");
      this.currStep = 3;
      this.updateFrequency();
      TraceHelper.exit(this, "doStep3");
   }

   private void processNewCal() {
      TraceHelper.entry(this, "processNewCal");

      try {
         int rawLoop = Integer.parseInt(this.txtRawLoop.getText());
         int rawAttn = Integer.parseInt(this.txtRawAttn.getText());
         double attnRef = VNAFormatFactory.getReflectionLossFormat().parse(this.txtAttn.getText()).doubleValue();
         double refPwr = VNAFormatFactory.getReflectionLossFormat().parse(this.txtMessdBm.getText()).doubleValue();
         int rawDelta = rawLoop - rawAttn;
         double oneDB = attnRef / (double)rawDelta;
         double newLoop = (double)rawLoop * oneDB;
         double loopOffset = -(refPwr - newLoop);
         this.txtCalRssScale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(oneDB));
         this.txtCalRssOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(loopOffset));
         this.btOK.setEnabled(true);
      } catch (Exception var14) {
         this.btOK.setEnabled(false);
      }

      TraceHelper.exit(this, "processNewCal");
   }

   private void processRawStep2(int rss) {
      TraceHelper.entry(this, "processRawStep2");
      this.txtRawLoop.setText("" + rss);
      double l = (double)rss * this.currPair.getScale() - this.currPair.getOffset();
      this.txtCalLoop.setText(VNAFormatFactory.getReflectionLossFormat().format(l));
      this.processNewCal();
      TraceHelper.exit(this, "processRawStep2");
   }

   private void processRawStep3(int rss) {
      TraceHelper.entry(this, "processRawStep3");
      this.txtRawAttn.setText("" + rss);
      double l = (double)rss * this.currPair.getScale() - this.currPair.getOffset();
      this.txtCalAttn.setText(VNAFormatFactory.getReflectionLossFormat().format(l));
      this.processNewCal();
      TraceHelper.exit(this, "processRawStep3");
   }

   private void reenableAllFields() {
      TraceHelper.entry(this, "reenableAllFields");
      this.btLoop.setEnabled(true);
      this.btAttn.setEnabled(true);
      this.txtMessUss.setEnabled(true);
      this.txtAttn.setEnabled(true);
      TraceHelper.exit(this, "reenableAllFields");
   }

   protected void updateFrequency() {
      TraceHelper.entry(this, "updateFrequency", "step=" + this.currStep);
      this.disableAllFields();
      this.btLoop.setEnabled(false);
      this.btAttn.setEnabled(false);
      long f = this.frq.getFrequency();
      VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.driver);

      for(int i = 0; i < 10; ++i) {
         VNABackgroundJob job = new VNABackgroundJob();
         job.setNumberOfSamples(1);
         job.setFrequencyRange(new VNAFrequencyRange(f, f));
         job.setScanMode(this.mode);
         backgroundTask.addJob(job);
      }

      backgroundTask.addDataConsumer(this);
      backgroundTask.setStatusLabel(this.lblStatus);
      backgroundTask.execute();
      TraceHelper.exit(this, "updateFrequency");
   }
}
