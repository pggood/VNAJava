package krause.vna.device.serial.proext.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.proext.VNADriverSerialProExtMessages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialProExtDialog extends VNADriverDialog {
   private JButton btOK;
   private JPanel panel;
   private JTextField txtLossMin;
   private JTextField txtLossMax;
   private JLabel lblPhaseMin;
   private JTextField txtPhaseMin;
   private JLabel lblPhaseMax;
   private JTextField txtPhaseMax;
   private JTextField txtFreqMin;
   private JTextField txtFreqMax;
   private JTextField txtSteps;
   private JTextField txtTicks;
   private JTextField txtFirmware;
   private IVNADriver driver;
   private JLabel lblOpenTimeOut;
   private JTextField txtOpenTimeout;
   private JTextField txtCommandDelay;
   private JLabel lblCommandDelay;
   private JLabel lblReadTimeout;
   private JTextField txtReadTimeout;
   private JButton btnReset;
   private ComplexInputField referenceValue;
   private JLabel lblReference;
   private JTextField txtPower;
   private VNADriverSerialProDIB dib;

   public VNADriverSerialProExtDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNADriverSerialProDialog");
      this.driver = pDriver;
      this.dib = (VNADriverSerialProDIB)this.driver.getDeviceInfoBlock();
      this.setTitle(VNADriverSerialProExtMessages.getString("Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNADriverSerialProExtDialog");
      this.setPreferredSize(new Dimension(490, 490));
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout(new MigLayout("", "[grow][][][]", ""));
      this.panel.add(new JLabel(), "");
      this.lblPhaseMax = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblMin"));
      this.panel.add(this.lblPhaseMax, "");
      JLabel lblLossMax = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblMax"));
      this.panel.add(lblLossMax, "wrap");
      JLabel lblLossMin = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblLoss"));
      this.panel.add(lblLossMin, "");
      this.txtLossMin = new JTextField();
      this.txtLossMin.setEditable(false);
      this.txtLossMin.setHorizontalAlignment(4);
      this.txtLossMin.setColumns(10);
      this.panel.add(this.txtLossMin, "");
      this.txtLossMax = new JTextField();
      this.txtLossMax.setEditable(false);
      this.txtLossMax.setHorizontalAlignment(4);
      this.txtLossMax.setColumns(10);
      this.panel.add(this.txtLossMax, "wrap");
      this.lblPhaseMin = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblPhase"));
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
      JLabel lblFreqMin = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblFreq"));
      lblFreqMin.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNADriverSerialProExtDialog.this.txtFreqMin.setEditable(true);
            VNADriverSerialProExtDialog.this.txtFreqMax.setEditable(true);
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
      JLabel lblNoOfSteps = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblNoOfSteps.text"));
      this.panel.add(lblNoOfSteps, "");
      this.txtSteps = new JTextField();
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setColumns(10);
      this.panel.add(this.txtSteps, "wrap");
      JLabel lblDDSTicks = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblDDSTicks.text"));
      this.panel.add(lblDDSTicks, "");
      this.txtTicks = new JTextField(10);
      this.txtTicks.setHorizontalAlignment(4);
      this.panel.add(this.txtTicks, "wrap");
      JLabel lblFirmware = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblFirmware.text"));
      this.panel.add(lblFirmware, "");
      this.txtFirmware = new JTextField();
      this.txtFirmware.setEditable(false);
      this.panel.add(this.txtFirmware, "grow,span 3,wrap");
      JLabel lblPower = new JLabel(VNADriverSerialProExtMessages.getString("Dialog.lblPower"));
      this.panel.add(lblPower, "");
      this.txtPower = new JTextField();
      this.txtPower.setEditable(false);
      this.panel.add(this.txtPower, "grow,span 3,wrap");
      this.lblOpenTimeOut = new JLabel(VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblOpenTimeout.text"));
      this.panel.add(this.lblOpenTimeOut, "");
      this.txtOpenTimeout = new JTextField();
      this.txtOpenTimeout.setText("0");
      this.txtOpenTimeout.setHorizontalAlignment(4);
      this.txtOpenTimeout.setColumns(6);
      this.panel.add(this.txtOpenTimeout, "wrap");
      this.lblCommandDelay = new JLabel(VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblCommandDelay.text"));
      this.lblCommandDelay.setBounds(10, 270, 141, 18);
      this.panel.add(this.lblCommandDelay, "");
      this.txtCommandDelay = new JTextField();
      this.txtCommandDelay.setText("0");
      this.txtCommandDelay.setHorizontalAlignment(4);
      this.txtCommandDelay.setColumns(6);
      this.panel.add(this.txtCommandDelay, "wrap");
      this.lblReadTimeout = new JLabel(VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblReadTimeout.text"));
      this.panel.add(this.lblReadTimeout);
      this.txtReadTimeout = new JTextField();
      this.txtReadTimeout.setText("0");
      this.txtReadTimeout.setHorizontalAlignment(4);
      this.txtReadTimeout.setColumns(6);
      this.panel.add(this.txtReadTimeout, "wrap");
      this.lblReference = new JLabel(VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblReference.text"));
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
            VNADriverSerialProExtDialog.this.doDialogCancel();
         }
      });
      this.btnReset = new JButton(VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.btnDefaults.text"));
      this.btnReset.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialProExtDialog.this.doReset();
         }
      });
      pnlButtons.add(this.btnReset);
      pnlButtons.add(btCancel);
      this.btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverSerialProExtDialog.this.doOK();
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
      int frqCalVal = IntegerValidator.parse(this.txtTicks.getText(), 900000, 1100000, VNADriverSerialProExtMessages.getString("Dialog.lblDDSTicks.text"), results);
      int openTimeout = IntegerValidator.parse(this.txtOpenTimeout.getText(), 500, 99000, VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblOpenTimeout.text"), results);
      int readTimeout = IntegerValidator.parse(this.txtReadTimeout.getText(), 500, 99000, VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblReadTimeout.text"), results);
      int commandDelay = IntegerValidator.parse(this.txtCommandDelay.getText(), 50, 99000, VNADriverSerialProExtMessages.getString("VNADriverSerialProExtDialog.lblCommandDelay.text"), results);
      int steps = IntegerValidator.parse(this.txtSteps.getText(), 200, 25000, VNADriverSerialProExtMessages.getString("Dialog.lblNoOfSteps.text"), results);
      long min = LongValidator.parse(this.txtFreqMin.getText(), 1L, 999999999999L, VNADriverSerialProExtMessages.getString("Dialog.lblFreq"), results);
      long max = LongValidator.parse(this.txtFreqMax.getText(), 1L, 999999999999L, VNADriverSerialProExtMessages.getString("Dialog.lblFreq"), results);
      if (results.isEmpty()) {
         steps = steps / 100 * 100;
         this.dib.setNumberOfSamples4Calibration(steps);
         this.dib.setDdsTicksPerMHz((long)frqCalVal);
         this.dib.setAfterCommandDelay(commandDelay);
         this.dib.setReadTimeout(readTimeout);
         this.dib.setOpenTimeout(openTimeout);
         this.dib.setReferenceResistance(this.referenceValue.getComplexValue());
         this.dib.setMinFrequency(min);
         this.dib.setMaxFrequency(max);
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
   }
}
