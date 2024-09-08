package krause.vna.device.serial.metro.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.metro.VNADriverSerialMetroDIB;
import krause.vna.device.serial.metro.VNADriverSerialMetroMessages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialMetroDialog extends VNADriverDialog {
   private JButton btOK;
   private JPanel panel;
   private JTextField txtReturnLossMin;
   private JTextField txtReturnLossMax;
   private JTextField txtTransmissionLossMin;
   private JTextField txtTransmissionLossMax;
   private JLabel lblPhaseMin;
   private JTextField txtPhaseMin;
   private JTextField txtPhaseMax;
   private JTextField txtFreqMin;
   private JTextField txtFreqMax;
   private JTextField txtSteps;
   private JTextField txtTicks;
   private IVNADriver driver;
   private JLabel lblOpenTimeout;
   private JTextField txtOpenTimeout;
   private VNADriverSerialMetroDIB dib;
   private JLabel lblReadTimeout;
   private JTextField txtReadTimeout;
   private JLabel lblCommandDelay;
   private JTextField txtCommandDelay;
   private JLabel lblBaudrate;
   private JTextField txtBaudrate;
   private ComplexInputField referenceValue;
   private JLabel lblReference;
   private JLabel lblNoOfSteps;

   public VNADriverSerialMetroDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNADriverSerialMetroDialog");
      this.driver = pDriver;
      this.dib = (VNADriverSerialMetroDIB)this.driver.getDeviceInfoBlock();
      this.setTitle(VNADriverSerialMetroMessages.getString("Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNADriverSerialMetroDialog");
      this.setPreferredSize(new Dimension(390, 390));
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout(new MigLayout("", "", ""));
      this.panel.add(new JLabel(), "");
      this.panel.add(new JLabel(VNADriverSerialMetroMessages.getString("minimum")), "");
      this.panel.add(new JLabel(VNADriverSerialMetroMessages.getString("maximum")), "wrap");
      this.panel.add(new JLabel(VNADriverSerialMetroMessages.getString("lblReturnLoss")), "");
      this.txtReturnLossMax = new JTextField();
      this.txtReturnLossMax.setHorizontalAlignment(4);
      this.txtReturnLossMax.setColumns(10);
      this.panel.add(this.txtReturnLossMax, "");
      this.txtReturnLossMin = new JTextField();
      this.txtReturnLossMin.setHorizontalAlignment(4);
      this.txtReturnLossMin.setColumns(10);
      this.txtReturnLossMin.setEditable(true);
      this.panel.add(this.txtReturnLossMin, "wrap");
      this.panel.add(new JLabel(VNADriverSerialMetroMessages.getString("lblTransmissionLoss")));
      this.txtTransmissionLossMax = new JTextField();
      this.txtTransmissionLossMax.setHorizontalAlignment(4);
      this.txtTransmissionLossMax.setColumns(10);
      this.panel.add(this.txtTransmissionLossMax, "");
      this.txtTransmissionLossMin = new JTextField();
      this.txtTransmissionLossMin.setHorizontalAlignment(4);
      this.txtTransmissionLossMin.setColumns(10);
      this.txtTransmissionLossMin.setEditable(false);
      this.panel.add(this.txtTransmissionLossMin, "wrap");
      this.lblPhaseMin = new JLabel(VNADriverSerialMetroMessages.getString("Dialog.lblPhaseMin.text"));
      this.panel.add(this.lblPhaseMin, "");
      this.txtPhaseMin = new JTextField();
      this.txtPhaseMin.setEditable(false);
      this.txtPhaseMin.setHorizontalAlignment(4);
      this.txtPhaseMin.setColumns(10);
      this.panel.add(this.txtPhaseMin, "");
      this.txtPhaseMax = new JTextField();
      this.txtPhaseMax.setEditable(false);
      this.txtPhaseMax.setHorizontalAlignment(4);
      this.txtPhaseMax.setColumns(10);
      this.panel.add(this.txtPhaseMax, "wrap");
      JLabel lblFreqMin = new JLabel(VNADriverSerialMetroMessages.getString("lblFreq"));
      this.panel.add(lblFreqMin, "");
      this.txtFreqMin = new JTextField();
      this.txtFreqMin.setHorizontalAlignment(4);
      this.panel.add(this.txtFreqMin, "");
      this.txtFreqMin.setColumns(10);
      this.txtFreqMax = new JTextField();
      this.txtFreqMax.setHorizontalAlignment(4);
      this.panel.add(this.txtFreqMax, "wrap");
      this.txtFreqMax.setColumns(10);
      this.lblNoOfSteps = new JLabel(VNADriverSerialMetroMessages.getString("Dialog.lblNoOfSteps.text"));
      this.panel.add(this.lblNoOfSteps, "");
      this.txtSteps = new JTextField();
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setColumns(10);
      this.panel.add(this.txtSteps, "wrap");
      JLabel lblDDSTicks = new JLabel(VNADriverSerialMetroMessages.getString("Dialog.lblDDSTicks.text"));
      this.panel.add(lblDDSTicks, "");
      this.txtTicks = new JTextField();
      this.txtTicks.setHorizontalAlignment(4);
      this.txtTicks.setColumns(10);
      this.panel.add(this.txtTicks, "wrap");
      this.lblOpenTimeout = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblOpenTimeout.text"));
      this.panel.add(this.lblOpenTimeout, "");
      this.txtOpenTimeout = new JTextField();
      this.txtOpenTimeout.setText("0");
      this.txtOpenTimeout.setHorizontalAlignment(4);
      this.txtOpenTimeout.setColumns(10);
      this.panel.add(this.txtOpenTimeout, "wrap");
      this.lblReadTimeout = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblReadTimeout.text"));
      this.panel.add(this.lblReadTimeout, "");
      this.txtReadTimeout = new JTextField();
      this.txtReadTimeout.setText("0");
      this.txtReadTimeout.setHorizontalAlignment(4);
      this.txtReadTimeout.setColumns(10);
      this.panel.add(this.txtReadTimeout, "wrap");
      this.lblCommandDelay = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblCommandDelay.text"));
      this.panel.add(this.lblCommandDelay, "");
      this.txtCommandDelay = new JTextField();
      this.txtCommandDelay.setText("0");
      this.txtCommandDelay.setHorizontalAlignment(4);
      this.txtCommandDelay.setColumns(10);
      this.panel.add(this.txtCommandDelay, "wrap");
      this.lblBaudrate = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblBaudrate.text"));
      this.panel.add(this.lblBaudrate, "");
      this.txtBaudrate = new JTextField();
      this.txtBaudrate.setText("0");
      this.txtBaudrate.setHorizontalAlignment(4);
      this.txtBaudrate.setColumns(10);
      this.panel.add(this.txtBaudrate, "wrap");
      this.lblReference = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblReference.text"));
      this.lblReference.setBounds(10, 310, 200, 30);
      this.panel.add(this.lblReference, "");
      this.referenceValue = new ComplexInputField((Complex)null);
      this.referenceValue.setMaximum(new Complex(5000.0D, 5000.0D));
      this.referenceValue.setMinimum(new Complex(-5000.0D, -5000.0D));
      FlowLayout flowLayout = (FlowLayout)this.referenceValue.getLayout();
      flowLayout.setAlignment(0);
      this.panel.add(this.referenceValue, "span 2");
      JPanel pnlButtons = new JPanel();
      pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 4, 2, (Font)null, new Color(0, 0, 0)));
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.setLayout(new FlowLayout(2, 5, 5));
      JButton btnReset = new JButton(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.btnReset.text"));
      pnlButtons.add(btnReset);
      btnReset.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialMetroDialog.this.doReset();
         }
      });
      btnReset.setToolTipText(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.btnReset.toolTipText"));
      this.btOK = new JButton(VNADriverSerialMetroMessages.getString("Button.OK"));
      this.btOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverSerialMetroDialog.this.doOK();
         }
      });
      JButton btCancel = new JButton(VNADriverSerialMetroMessages.getString("Button.Cancel"));
      btCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialMetroDialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(btCancel);
      pnlButtons.add(this.btOK);
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNADriverSerialMetroDialog");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      ValidationResults results = new ValidationResults();
      int frq = IntegerValidator.parse(this.txtTicks.getText(), 999999, 999999999, VNADriverSerialMetroMessages.getString("Dialog.lblDDSTicks.text"), results);
      int openTimeout = IntegerValidator.parse(this.txtOpenTimeout.getText(), 500, 99000, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblOpenTimeout.text"), results);
      int readTimeout = IntegerValidator.parse(this.txtReadTimeout.getText(), 500, 99000, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblReadTimeout.text"), results);
      int commandDelay = IntegerValidator.parse(this.txtCommandDelay.getText(), 50, 99000, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblCommandDelay.text"), results);
      int baudrate = IntegerValidator.parse(this.txtBaudrate.getText(), 1200, 115200, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblBaudrate.text"), results);
      int steps = IntegerValidator.parse(this.txtSteps.getText(), 2000, 25000, VNADriverSerialMetroMessages.getString("Dialog.lblNoOfSteps.text"), results);
      long freqMin = LongValidator.parse(this.txtFreqMin.getText(), 100000L, 4400000000L, VNADriverSerialMetroMessages.getString("lblFreq"), results);
      long freqMax = LongValidator.parse(this.txtFreqMax.getText(), 100000L, 4400000000L, VNADriverSerialMetroMessages.getString("lblFreq"), results);
      double maxTransmissionLoss = DoubleValidator.parse(this.txtTransmissionLossMax.getText(), -76.0D, 5.0D, VNADriverSerialMetroMessages.getString("lblTransmissionLoss"), results);
      double minReturnLoss = DoubleValidator.parse(this.txtReturnLossMin.getText(), -76.0D, 5.0D, VNADriverSerialMetroMessages.getString("lblReturnLoss"), results);
      double maxReturnLoss = DoubleValidator.parse(this.txtReturnLossMax.getText(), -76.0D, 5.0D, VNADriverSerialMetroMessages.getString("lblReturnLoss"), results);
      if (results.isEmpty()) {
         this.dib.setDdsTicksPerMHz((long)frq);
         this.dib.setAfterCommandDelay(commandDelay);
         this.dib.setReadTimeout(readTimeout);
         this.dib.setOpenTimeout(openTimeout);
         this.dib.setBaudrate(baudrate);
         this.dib.setReferenceResistance(this.referenceValue.getComplexValue());
         this.dib.setNumberOfSamples4Calibration(steps);
         this.dib.setMaxReflectionLoss(maxReturnLoss);
         this.dib.setMaxTransmissionLoss(maxTransmissionLoss);
         this.dib.setMinLoss(minReturnLoss);
         this.dib.setMinFrequency(freqMin);
         this.dib.setMaxFrequency(freqMax);
         this.dib.store(this.config, this.driver.getDriverConfigPrefix());
         this.setVisible(false);
      } else {
         new ValidationResultsDialog(this.getOwner(), results, VNAMessages.getString("VNANetworkDialog.ErrorDialogHeader"));
      }

      TraceHelper.exit(this, "doOK");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   private void updateFieldsFromDIB(VNADriverSerialMetroDIB pDIB) {
      this.txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
      this.txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));
      this.txtReturnLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxReflectionLoss()));
      this.txtReturnLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));
      this.txtTransmissionLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxTransmissionLoss()));
      this.txtTransmissionLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));
      this.txtPhaseMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxPhase()));
      this.txtPhaseMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinPhase()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getNumberOfSamples4Calibration()));
      this.txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));
      this.txtOpenTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getOpenTimeout()));
      this.txtReadTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getReadTimeout()));
      this.txtCommandDelay.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getAfterCommandDelay()));
      this.txtBaudrate.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getBaudrate()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getNumberOfSamples4Calibration()));
      this.referenceValue.setComplexValue(pDIB.getReferenceResistance());
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.updateFieldsFromDIB(this.dib);
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void doReset() {
      TraceHelper.entry(this, "doReset");
      this.dib.reset();
      this.updateFieldsFromDIB(this.dib);
      TraceHelper.exit(this, "doReset");
   }
}
