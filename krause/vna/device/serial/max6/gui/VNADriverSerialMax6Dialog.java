package krause.vna.device.serial.max6.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAScanMode;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.device.serial.max6.VNADriverSerialMax6Messages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialMax6Dialog extends VNADriverDialog {
   private VNADriverSerialMax6DIB dib;
   private JButton btOK;
   private JPanel panel;
   private JLabel lblPhaseMin;
   private JLabel lblMax;
   private JLabel lblOpenTimeout;
   private JLabel lblReadTimeout;
   private JLabel lblCommandDelay;
   private JLabel lblBaudrate;
   private JLabel lblReference;
   private JTextField txtLossMin;
   private JTextField txtLossMax;
   private JTextField txtLevelMin;
   private JTextField txtLevelMax;
   private JTextField txtPhaseMin;
   private JTextField txtPhaseMax;
   private JTextField txtFreqMin;
   private JTextField txtFreqMax;
   private JTextField txtSteps;
   private JTextField txtTicks;
   private IVNADriver driver;
   private JTextField txtOpenTimeout;
   private JTextField txtReadTimeout;
   private JTextField txtCommandDelay;
   private JTextField txtBaudrate;
   private ComplexInputField referenceValue;
   private JTextField txtRss1Scale;
   private JTextField txtRss2Scale;
   private JTextField txtRss3Scale;
   private JTextField txtReflectionOffset;
   private JTextField txtReflectionScale;
   private JTextField txtRss1Offset;
   private JTextField txtRss2Offset;
   private JTextField txtRss3Offset;
   private JTextArea txtFirmware;
   private JLabel lblFirmware;
   private JButton button_1;
   private JButton button_2;

   public VNADriverSerialMax6Dialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNADriverSerialMax6Dialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNADriverSerialMax6Dialog");
      this.driver = pDriver;
      this.dib = (VNADriverSerialMax6DIB)this.driver.getDeviceInfoBlock();
      this.setTitle(VNADriverSerialMax6Messages.getString("Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.setMinimumSize(new Dimension(500, 500));
      this.setPreferredSize(new Dimension(650, 550));
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout(new MigLayout("", "[grow][][][][]", "[]"));
      this.panel.add(new JLabel(), "cell 0 0");
      JLabel lblMin = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblLossMax.text"));
      this.panel.add(lblMin, "cell 1 0");
      this.lblMax = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblPhaseMax.text"));
      this.panel.add(this.lblMax, "cell 2 0");
      JLabel lblLoss = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblLossMin.text"));
      this.panel.add(lblLoss, "cell 0 1");
      this.txtLossMin = new JTextField();
      this.txtLossMin.setEditable(false);
      this.txtLossMin.setHorizontalAlignment(4);
      this.txtLossMin.setColumns(10);
      this.panel.add(this.txtLossMin, "cell 1 1");
      this.txtLossMax = new JTextField();
      this.txtLossMax.setEditable(false);
      this.txtLossMax.setHorizontalAlignment(4);
      this.txtLossMax.setColumns(10);
      this.panel.add(this.txtLossMax, "cell 2 1");
      JLabel lblLevel = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblLevel.text"));
      this.panel.add(lblLevel, "cell 0 2");
      this.txtLevelMax = new JTextField();
      this.txtLevelMax.setEditable(false);
      this.txtLevelMax.setHorizontalAlignment(4);
      this.txtLevelMax.setColumns(10);
      this.panel.add(this.txtLevelMax, "cell 1 2");
      this.txtLevelMin = new JTextField();
      this.txtLevelMin.setEditable(false);
      this.txtLevelMin.setHorizontalAlignment(4);
      this.txtLevelMin.setColumns(10);
      this.panel.add(this.txtLevelMin, "cell 2 2");
      this.lblPhaseMin = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblPhaseMin.text"));
      this.panel.add(this.lblPhaseMin, "cell 0 3");
      this.txtPhaseMin = new JTextField();
      this.txtPhaseMin.setEditable(false);
      this.txtPhaseMin.setHorizontalAlignment(4);
      this.txtPhaseMin.setColumns(10);
      this.panel.add(this.txtPhaseMin, "cell 1 3");
      this.txtPhaseMax = new JTextField();
      this.txtPhaseMax.setEditable(false);
      this.txtPhaseMax.setHorizontalAlignment(4);
      this.txtPhaseMax.setColumns(10);
      this.panel.add(this.txtPhaseMax, "cell 2 3");
      JLabel lblFreqMin = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblFreqMin.text"));
      this.panel.add(lblFreqMin, "cell 0 4");
      this.txtFreqMin = new JTextField();
      this.txtFreqMin.setEditable(false);
      this.txtFreqMin.setHorizontalAlignment(4);
      this.panel.add(this.txtFreqMin, "cell 1 4");
      this.txtFreqMin.setColumns(10);
      this.txtFreqMax = new JTextField();
      this.txtFreqMax.setEditable(false);
      this.txtFreqMax.setHorizontalAlignment(4);
      this.panel.add(this.txtFreqMax, "cell 2 4");
      this.txtFreqMax.setColumns(10);
      JLabel lblNoOfSteps = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblNoOfSteps.text"));
      this.panel.add(lblNoOfSteps, "cell 0 5");
      this.txtSteps = new JTextField();
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setColumns(10);
      this.panel.add(this.txtSteps, "cell 1 5");
      JLabel lblDDSTicks = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblDDSTicks.text"));
      this.panel.add(lblDDSTicks, "cell 0 6");
      this.txtTicks = new JTextField();
      this.txtTicks.setHorizontalAlignment(4);
      this.txtTicks.setColumns(10);
      this.panel.add(this.txtTicks, "cell 1 6");
      this.panel.add(new JLabel(" "), "cell 0 7");
      this.panel.add(new JLabel(), "cell 0 8");
      this.panel.add(new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblReflection")), "cell 1 8");
      JLabel label = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblRss1"));
      this.panel.add(label, "cell 2 8");
      JLabel label_1 = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblRss2"));
      this.panel.add(label_1, "cell 3 8");
      JLabel label_2 = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblRss3"));
      this.panel.add(label_2, "cell 4 8");
      this.panel.add(new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblScale")), "cell 0 9");
      this.txtReflectionScale = new JTextField();
      this.txtReflectionScale.setHorizontalAlignment(4);
      this.txtReflectionScale.setColumns(10);
      this.panel.add(this.txtReflectionScale, "cell 1 9");
      this.txtRss1Scale = new JTextField();
      this.txtRss1Scale.setHorizontalAlignment(4);
      this.txtRss1Scale.setColumns(10);
      this.panel.add(this.txtRss1Scale, "cell 2 9");
      this.txtRss2Scale = new JTextField();
      this.txtRss2Scale.setEnabled(false);
      this.txtRss2Scale.setHorizontalAlignment(4);
      this.txtRss2Scale.setColumns(10);
      this.panel.add(this.txtRss2Scale, "cell 3 9");
      this.txtRss3Scale = new JTextField();
      this.txtRss3Scale.setEnabled(false);
      this.txtRss3Scale.setHorizontalAlignment(4);
      this.txtRss3Scale.setColumns(10);
      this.panel.add(this.txtRss3Scale, "cell 4 9");
      this.panel.add(new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblOffset")), "cell 0 10");
      this.txtReflectionOffset = new JTextField();
      this.txtReflectionOffset.setHorizontalAlignment(4);
      this.txtReflectionOffset.setColumns(10);
      this.panel.add(this.txtReflectionOffset, "cell 1 10");
      this.txtRss1Offset = new JTextField();
      this.txtRss1Offset.setHorizontalAlignment(4);
      this.txtRss1Offset.setColumns(10);
      this.panel.add(this.txtRss1Offset, "cell 2 10");
      this.txtRss2Offset = new JTextField();
      this.txtRss2Offset.setEnabled(false);
      this.txtRss2Offset.setHorizontalAlignment(4);
      this.txtRss2Offset.setColumns(10);
      this.panel.add(this.txtRss2Offset, "cell 3 10");
      this.txtRss3Offset = new JTextField();
      this.txtRss3Offset.setEnabled(false);
      this.txtRss3Offset.setHorizontalAlignment(4);
      this.txtRss3Offset.setColumns(10);
      this.panel.add(this.txtRss3Offset, "cell 4 10");
      this.panel.add(new JLabel(" "), "cell 0 11");
      JButton button = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btCalRSS1.text"));
      button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new VNATLCalibrationDialog(VNADriverSerialMax6Dialog.this.mainFrame, VNADriverSerialMax6Dialog.this.driver, VNAScanMode.MODE_RSS1);
            VNADriverSerialMax6Dialog.this.updateFieldsFromDIB(VNADriverSerialMax6Dialog.this.dib);
         }
      });
      this.panel.add(button, "cell 2 11,grow");
      this.button_1 = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btCalRSS1.text"));
      this.button_1.setEnabled(false);
      this.button_1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new VNATLCalibrationDialog(VNADriverSerialMax6Dialog.this.mainFrame, VNADriverSerialMax6Dialog.this.driver, VNAScanMode.MODE_RSS2);
            VNADriverSerialMax6Dialog.this.updateFieldsFromDIB(VNADriverSerialMax6Dialog.this.dib);
         }
      });
      this.panel.add(this.button_1, "cell 3 11,grow");
      this.button_2 = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btCalRSS1.text"));
      this.button_2.setEnabled(false);
      this.button_2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new VNATLCalibrationDialog(VNADriverSerialMax6Dialog.this.mainFrame, VNADriverSerialMax6Dialog.this.driver, VNAScanMode.MODE_RSS3);
            VNADriverSerialMax6Dialog.this.updateFieldsFromDIB(VNADriverSerialMax6Dialog.this.dib);
         }
      });
      this.panel.add(this.button_2, "cell 4 11,grow");
      this.lblOpenTimeout = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblOpenTimeout.text"));
      this.panel.add(this.lblOpenTimeout, "cell 0 12");
      this.txtOpenTimeout = new JTextField();
      this.txtOpenTimeout.setText("0");
      this.txtOpenTimeout.setHorizontalAlignment(4);
      this.txtOpenTimeout.setColumns(10);
      this.panel.add(this.txtOpenTimeout, "cell 1 12");
      this.lblReadTimeout = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblReadTimeout.text"));
      this.panel.add(this.lblReadTimeout, "cell 0 13");
      this.txtReadTimeout = new JTextField();
      this.txtReadTimeout.setText("0");
      this.txtReadTimeout.setHorizontalAlignment(4);
      this.txtReadTimeout.setColumns(10);
      this.panel.add(this.txtReadTimeout, "cell 1 13");
      this.lblCommandDelay = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblCommandDelay.text"));
      this.panel.add(this.lblCommandDelay, "cell 0 14");
      this.txtCommandDelay = new JTextField();
      this.txtCommandDelay.setText("0");
      this.txtCommandDelay.setHorizontalAlignment(4);
      this.txtCommandDelay.setColumns(10);
      this.panel.add(this.txtCommandDelay, "cell 1 14");
      this.lblBaudrate = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblBaudrate.text"));
      this.panel.add(this.lblBaudrate, "cell 0 15");
      this.txtBaudrate = new JTextField();
      this.txtBaudrate.setText("0");
      this.txtBaudrate.setHorizontalAlignment(4);
      this.txtBaudrate.setColumns(10);
      this.panel.add(this.txtBaudrate, "cell 1 15");
      this.lblReference = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblReference.text"));
      this.panel.add(this.lblReference, "cell 0 16");
      this.referenceValue = new ComplexInputField((Complex)null);
      this.referenceValue.setMaximum(new Complex(5000.0D, 5000.0D));
      this.referenceValue.setMinimum(new Complex(-5000.0D, -5000.0D));
      FlowLayout flowLayout = (FlowLayout)this.referenceValue.getLayout();
      flowLayout.setAlignment(0);
      this.panel.add(this.referenceValue, "cell 1 16 5 1");
      this.lblFirmware = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblFirmware.text"));
      this.panel.add(this.lblFirmware, "cell 0 17");
      this.txtFirmware = new JTextArea();
      this.txtFirmware.setEditable(false);
      this.txtFirmware.setRows(5);
      this.panel.add(this.txtFirmware, "cell 1 17 5 1,grow");
      JPanel pnlButtons = new JPanel();
      pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 4, 2, (Font)null, new Color(0, 0, 0)));
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.setLayout(new FlowLayout(2, 5, 5));
      pnlButtons.add(new HelpButton(this, "VNADriverSerialMax6Dialog"));
      JButton btnReset = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btnReset.text"));
      pnlButtons.add(btnReset);
      btnReset.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialMax6Dialog.this.doReset();
         }
      });
      btnReset.setToolTipText(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btnReset.toolTipText"));
      this.btOK = new JButton(VNADriverSerialMax6Messages.getString("Button.OK"));
      this.btOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverSerialMax6Dialog.this.doOK();
         }
      });
      JButton btCancel = new JButton(VNADriverSerialMax6Messages.getString("Button.Cancel"));
      btCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialMax6Dialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(btCancel);
      pnlButtons.add(this.btOK);
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNADriverSerialMax6Dialog");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.updateFieldsFromDIB(this.dib);
      this.txtFirmware.setText(this.driver.getDeviceFirmwareInfo());
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      ValidationResults results = new ValidationResults();
      int frq = IntegerValidator.parse(this.txtTicks.getText(), 999999, 999999999, VNADriverSerialMax6Messages.getString("Dialog.lblDDSTicks.text"), results);
      int openTimeout = IntegerValidator.parse(this.txtOpenTimeout.getText(), 500, 99000, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblOpenTimeout.text"), results);
      int readTimeout = IntegerValidator.parse(this.txtReadTimeout.getText(), 500, 99000, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblReadTimeout.text"), results);
      int commandDelay = IntegerValidator.parse(this.txtCommandDelay.getText(), 50, 99000, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblCommandDelay.text"), results);
      int baudrate = IntegerValidator.parse(this.txtBaudrate.getText(), 1200, 115200, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblBaudrate.text"), results);
      int steps = IntegerValidator.parse(this.txtSteps.getText(), 2000, 25000, VNADriverSerialMax6Messages.getString("Dialog.lblNoOfSteps.text"), results);
      double rss1Scale = DoubleValidator.parse(this.txtRss1Scale.getText(), 1.0E-4D, 1.0D, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
      double rss1Offset = DoubleValidator.parse(this.txtRss1Offset.getText(), -10.0D, 100.0D, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);
      double rss2Scale = DoubleValidator.parse(this.txtRss2Scale.getText(), 1.0E-4D, 1.0D, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
      double rss2Offset = DoubleValidator.parse(this.txtRss2Offset.getText(), -10.0D, 100.0D, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);
      double rss3Scale = DoubleValidator.parse(this.txtRss3Scale.getText(), 1.0E-4D, 1.0D, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
      double rss3Offset = DoubleValidator.parse(this.txtRss3Offset.getText(), -10.0D, 100.0D, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);
      double reflectionScale = DoubleValidator.parse(this.txtReflectionScale.getText(), 1.0E-4D, 1.0D, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
      double reflectionOffset = DoubleValidator.parse(this.txtReflectionOffset.getText(), -100.0D, 100.0D, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);
      if (results.isEmpty()) {
         this.dib.setDdsTicksPerMHz((long)frq);
         this.dib.setAfterCommandDelay(commandDelay);
         this.dib.setReadTimeout(readTimeout);
         this.dib.setOpenTimeout(openTimeout);
         this.dib.setBaudrate(baudrate);
         this.dib.setNumberOfSamples4Calibration(steps);
         this.dib.setRss1Scale(rss1Scale);
         this.dib.setRss1Offset(rss1Offset);
         this.dib.setRss2Scale(rss2Scale);
         this.dib.setRss2Offset(rss2Offset);
         this.dib.setRss3Scale(rss3Scale);
         this.dib.setRss3Offset(rss3Offset);
         this.dib.setReflectionScale(reflectionScale);
         this.dib.setReflectionOffset(reflectionOffset);
         this.dib.store(this.config, this.driver.getDriverConfigPrefix());
         this.setVisible(false);
         this.dispose();
      } else {
         new ValidationResultsDialog(this.getOwner(), results, VNAMessages.getString("VNANetworkDialog.ErrorDialogHeader"));
      }

      TraceHelper.exit(this, "doOK");
   }

   private void doReset() {
      TraceHelper.entry(this, "doReset");
      this.dib.reset();
      this.updateFieldsFromDIB(this.dib);
      TraceHelper.exit(this, "doReset");
   }

   private void updateFieldsFromDIB(VNADriverSerialMax6DIB pDIB) {
      this.txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
      this.txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));
      this.txtLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxLoss()));
      this.txtLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));
      this.txtLevelMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getLevelMax()));
      this.txtLevelMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getLevelMin()));
      this.txtPhaseMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxPhase()));
      this.txtPhaseMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinPhase()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getNumberOfSamples4Calibration()));
      this.txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));
      this.txtOpenTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getOpenTimeout()));
      this.txtReadTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getReadTimeout()));
      this.txtCommandDelay.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getAfterCommandDelay()));
      this.txtBaudrate.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getBaudrate()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getNumberOfSamples4Calibration()));
      this.txtReflectionScale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getReflectionScale()));
      this.txtReflectionOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getReflectionOffset()));
      this.txtRss1Scale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getRss1Scale()));
      this.txtRss1Offset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getRss1Offset()));
      this.txtRss2Scale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getRss2Scale()));
      this.txtRss2Offset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getRss2Offset()));
      this.txtRss3Scale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getRss3Scale()));
      this.txtRss3Offset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getRss3Offset()));
      this.referenceValue.setComplexValue(pDIB.getReferenceResistance());
   }
}
