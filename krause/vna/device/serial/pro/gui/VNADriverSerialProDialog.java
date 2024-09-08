package krause.vna.device.serial.pro.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.pro.VNADriverSerialProMessages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialProDialog extends VNADriverDialog {
   private JButton btOK;
   private JPanel panel;
   private JTextField txtLossMin;
   private JTextField txtLossMax;
   private JTextField txtPhaseMin;
   private JTextField txtPhaseMax;
   private JTextField txtFreqMin;
   private JTextField txtFreqMax;
   private JTextField txtSteps;
   private JTextField txtTicks;
   private JTextField txtFirmware;
   private IVNADriver driver;
   private JTextField txtOpenTimeout;
   private JTextField txtCommandDelay;
   private JLabel lblCommandDelay;
   private JTextField txtReadTimeout;
   private JButton btnReset;
   private ComplexInputField referenceValue;
   private JLabel lblReference;
   private JTextField txtPower;
   private VNADriverSerialProDIB dib;
   private JCheckBox cbFirmware;
   private JCheckBox cb6dB;
   private JTextField txtAttenIOffset;
   private JTextField txtAttenQOffset;

   public VNADriverSerialProDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNADriverSerialProDialog");
      this.driver = pDriver;
      this.dib = (VNADriverSerialProDIB)this.driver.getDeviceInfoBlock();
      this.setTitle(VNADriverSerialProMessages.getString("Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNADriverSerialProDialog");
      this.setPreferredSize(new Dimension(500, 550));
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout(new MigLayout("", "[grow][][][]", ""));
      this.panel.add(new JLabel(), "");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblPhaseMax.text")), "");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblLossMax.text")), "wrap");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblLossMin.text")), "");
      this.txtLossMin = new JTextField();
      this.txtLossMin.setEditable(true);
      this.txtLossMin.setHorizontalAlignment(4);
      this.txtLossMin.setColumns(10);
      this.panel.add(this.txtLossMin, "");
      this.txtLossMax = new JTextField();
      this.txtLossMax.setEditable(true);
      this.txtLossMax.setHorizontalAlignment(4);
      this.txtLossMax.setColumns(10);
      this.panel.add(this.txtLossMax, "wrap");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblPhaseMin.text")), "");
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
      JLabel lblFreqMin = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblFreqMin.text"));
      lblFreqMin.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNADriverSerialProDialog.this.txtFreqMin.setEditable(true);
            VNADriverSerialProDialog.this.txtFreqMax.setEditable(true);
         }
      });
      this.panel.add(lblFreqMin, "");
      this.txtFreqMin = new JTextField();
      this.txtFreqMin.setEditable(false);
      this.txtFreqMin.setHorizontalAlignment(4);
      this.txtFreqMin.setColumns(10);
      this.panel.add(this.txtFreqMin, "");
      this.txtFreqMax = new JTextField();
      this.txtFreqMax.setEditable(false);
      this.txtFreqMax.setHorizontalAlignment(4);
      this.txtFreqMax.setColumns(10);
      this.panel.add(this.txtFreqMax, "wrap");
      JLabel lblNoOfSteps = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblNoOfSteps.text"));
      this.panel.add(lblNoOfSteps, "");
      this.txtSteps = new JTextField();
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setColumns(10);
      this.txtSteps.setEditable(false);
      this.panel.add(this.txtSteps, "wrap");
      JLabel lblDDSTicks = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblDDSTicks.text"));
      this.panel.add(lblDDSTicks, "");
      this.txtTicks = new JTextField(10);
      this.txtTicks.setHorizontalAlignment(4);
      this.panel.add(this.txtTicks, "wrap");
      JLabel lblFirmware = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblFirmware.text"));
      this.panel.add(lblFirmware, "");
      this.txtFirmware = new JTextField();
      this.txtFirmware.setEditable(false);
      this.panel.add(this.txtFirmware, "grow,span 3,wrap");
      JLabel lbl = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblFirmwareRevision"));
      this.panel.add(lbl, "");
      this.cbFirmware = new JCheckBox(VNADriverSerialProMessages.getString("Dialog.cbFirmwareRevision"));
      this.cbFirmware.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (VNADriverSerialProDialog.this.cbFirmware.isSelected()) {
               VNADriverSerialProDialog.this.cb6dB.setEnabled(true);
            } else {
               VNADriverSerialProDialog.this.cb6dB.setEnabled(false);
               VNADriverSerialProDialog.this.cb6dB.setSelected(false);
            }

         }
      });
      this.panel.add(this.cbFirmware, "grow,span 1");
      this.cb6dB = new JCheckBox(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.cbFixed6dB"));
      this.panel.add(this.cb6dB, "grow,span 1,wrap");
      JLabel lblPower = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblPower.text"));
      this.panel.add(lblPower, "");
      this.txtPower = new JTextField();
      this.txtPower.setEditable(false);
      this.panel.add(this.txtPower, "grow,span 3,wrap");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblOpenTimeout.text")), "");
      this.txtOpenTimeout = new JTextField();
      this.txtOpenTimeout.setText("0");
      this.txtOpenTimeout.setHorizontalAlignment(4);
      this.txtOpenTimeout.setColumns(6);
      this.panel.add(this.txtOpenTimeout, "wrap");
      this.lblCommandDelay = new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblCommandDelay.text"));
      this.lblCommandDelay.setBounds(10, 270, 141, 18);
      this.panel.add(this.lblCommandDelay, "");
      this.txtCommandDelay = new JTextField();
      this.txtCommandDelay.setText("0");
      this.txtCommandDelay.setHorizontalAlignment(4);
      this.txtCommandDelay.setColumns(6);
      this.panel.add(this.txtCommandDelay, "wrap");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblReadTimeout.text")));
      this.txtReadTimeout = new JTextField();
      this.txtReadTimeout.setText("0");
      this.txtReadTimeout.setHorizontalAlignment(4);
      this.txtReadTimeout.setColumns(6);
      this.panel.add(this.txtReadTimeout, "wrap");
      this.panel.add(new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblAttenOffset.text")), "");
      this.txtAttenIOffset = new JTextField();
      this.txtAttenIOffset.setText("0");
      this.txtAttenIOffset.setHorizontalAlignment(4);
      this.txtAttenIOffset.setColumns(6);
      this.panel.add(this.txtAttenIOffset, "");
      this.txtAttenQOffset = new JTextField();
      this.txtAttenQOffset.setText("0");
      this.txtAttenQOffset.setHorizontalAlignment(4);
      this.txtAttenQOffset.setColumns(6);
      this.panel.add(this.txtAttenQOffset, "wrap");
      this.lblReference = new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblReference.text"));
      this.lblReference.setBounds(10, 330, 141, 30);
      this.panel.add(this.lblReference, "");
      this.referenceValue = new ComplexInputField((Complex)null);
      this.referenceValue.setMaximum(new Complex(5000.0D, 5000.0D));
      this.referenceValue.setMinimum(new Complex(-5000.0D, -5000.0D));
      FlowLayout flowLayout = (FlowLayout)this.referenceValue.getLayout();
      flowLayout.setAlignment(0);
      this.panel.add(this.referenceValue, "grow,span 3,wrap");
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      pnlButtons.setLayout(new FlowLayout(2, 5, 5));
      pnlButtons.add(new HelpButton(this, "VNADriverSerialProDialog"));
      JButton btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialProDialog.this.doDialogCancel();
         }
      });
      this.btnReset = new JButton(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.btnReset.text"));
      this.btnReset.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialProDialog.this.doReset();
         }
      });
      pnlButtons.add(this.btnReset);
      pnlButtons.add(btCancel);
      this.btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverSerialProDialog.this.doOK();
         }
      });
      pnlButtons.add(this.btOK);
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNADriverSerialProDialog");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      ValidationResults results = new ValidationResults();
      int frq = IntegerValidator.parse(this.txtTicks.getText(), 999999, 999999999, VNADriverSerialProMessages.getString("Dialog.lblDDSTicks.text"), results);
      int openTimeout = IntegerValidator.parse(this.txtOpenTimeout.getText(), 500, 99000, VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblOpenTimeout.text"), results);
      int readTimeout = IntegerValidator.parse(this.txtReadTimeout.getText(), 500, 99000, VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblReadTimeout.text"), results);
      int commandDelay = IntegerValidator.parse(this.txtCommandDelay.getText(), 50, 99000, VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblCommandDelay.text"), results);
      int steps = IntegerValidator.parse(this.txtSteps.getText(), 200, 25000, VNADriverSerialProMessages.getString("Dialog.lblNoOfSteps.text"), results);
      int firmware = this.cbFirmware.isSelected() ? 1 : 0;
      double attenI = DoubleValidator.parse(this.txtAttenIOffset.getText(), -100.0D, 100.0D, VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblAttenuationOffset.text"), results);
      double attenQ = DoubleValidator.parse(this.txtAttenQOffset.getText(), -100.0D, 100.0D, VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblAttenuationOffset.text"), results);
      double lossMin = DoubleValidator.parse(this.txtLossMin.getText(), -200.0D, 200.0D, VNADriverSerialProMessages.getString("Dialog.lblLossMin.text"), results);
      double lossMax = DoubleValidator.parse(this.txtLossMax.getText(), -200.0D, 200.0D, VNADriverSerialProMessages.getString("Dialog.lblLossMin.text"), results);
      long frqMin = LongValidator.parse(this.txtFreqMin.getText(), 1L, 999999999999L, VNADriverSerialProMessages.getString("Dialog.lblFreqMin.text"), results);
      long frqMax = LongValidator.parse(this.txtFreqMax.getText(), 1L, 999999999999L, VNADriverSerialProMessages.getString("Dialog.lblFreqMin.text"), results);
      if (results.isEmpty()) {
         steps = steps / 100 * 100;
         this.dib.setNumberOfSamples4Calibration(steps);
         this.dib.setDdsTicksPerMHz((long)frq);
         this.dib.setAfterCommandDelay(commandDelay);
         this.dib.setReadTimeout(readTimeout);
         this.dib.setOpenTimeout(openTimeout);
         this.dib.setReferenceResistance(this.referenceValue.getComplexValue());
         this.dib.setFirmwareVersion(firmware);
         this.dib.setFixed6dBOnThru(this.cb6dB.isSelected());
         this.dib.setAttenOffsetI(attenI);
         this.dib.setAttenOffsetQ(attenQ);
         this.dib.setMinFrequency(frqMin);
         this.dib.setMaxFrequency(frqMax);
         this.dib.setMinLoss(lossMin);
         this.dib.setMaxLoss(lossMax);
         this.dib.store(this.config, this.driver.getDriverConfigPrefix());
         this.setVisible(false);
      } else {
         new ValidationResultsDialog(this.getOwner(), results, this.getTitle());
      }

      TraceHelper.exit(this, "doOK");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.updateFieldsFromDIB(this.dib);
      this.txtFirmware.setText(this.driver.getDeviceFirmwareInfo());
      this.txtPower.setText(VNAFormatFactory.getResistanceBaseFormat().format(this.driver.getDeviceSupply()));
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

   private void updateFieldsFromDIB(VNADriverSerialProDIB pDIB) {
      this.txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
      this.txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));
      this.txtLossMax.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getMaxLoss()));
      this.txtLossMin.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getMinLoss()));
      this.txtPhaseMax.setText(VNAFormatFactory.getPhaseFormat().format(pDIB.getMaxPhase()));
      this.txtPhaseMin.setText(VNAFormatFactory.getPhaseFormat().format(pDIB.getMinPhase()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getNumberOfSamples4Calibration()));
      this.txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));
      this.txtOpenTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getOpenTimeout()));
      this.txtReadTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getReadTimeout()));
      this.txtCommandDelay.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getAfterCommandDelay()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)pDIB.getNumberOfSamples4Calibration()));
      this.referenceValue.setComplexValue(pDIB.getReferenceResistance());
      this.txtAttenIOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(this.dib.getAttenOffsetI()));
      this.txtAttenQOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(this.dib.getAttenOffsetQ()));
      this.cbFirmware.setSelected(pDIB.getFirmwareVersion() >= 1);
      if (this.cbFirmware.isSelected()) {
         this.cb6dB.setSelected(pDIB.isFixed6dBOnThru());
         this.cb6dB.setEnabled(true);
      } else {
         this.cb6dB.setSelected(false);
         this.cb6dB.setEnabled(false);
      }

   }
}
