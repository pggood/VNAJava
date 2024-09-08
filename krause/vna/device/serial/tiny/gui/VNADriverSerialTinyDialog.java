package krause.vna.device.serial.tiny.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.tiny.VNADriverSerialTiny;
import krause.vna.device.serial.tiny.VNADriverSerialTinyDIB;
import krause.vna.device.serial.tiny.VNADriverSerialTinyMessages;
import krause.vna.device.serial.tiny.calibration.PhaseCalibrationHelper;
import krause.vna.gui.HelpButton;
import krause.vna.gui.StatusBarLabel;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialTinyDialog extends VNADriverDialog {
   private final VNAConfig config = VNAConfig.getSingleton();
   private final VNADataPool datapool = VNADataPool.getSingleton();
   private final VNADriverSerialTinyDIB dib;
   private final VNADriverSerialTiny driver;
   private JButton btCal;
   private JButton btCancel;
   private JButton btOK;
   private JButton btDefault;
   private JLabel lblPhaseMax;
   private JLabel lblPhaseMin;
   private JLabel lblReference;
   private StatusBarLabel lblStatusbar;
   private JPanel panel;
   private ComplexInputField referenceValue;
   private JTextField txtBootloaderBaudRate;
   private JTextField txtFirmware;
   private JTextField txtFreqMax;
   private JTextField txtFreqMin;
   private JTextField txtGainCorrection;
   private JTextField txtIFPhaseCorrection;
   private JTextField txtLossMax;
   private JTextField txtLossMin;
   private JTextField txtPhaseCorrection;
   private JTextField txtPhaseMax;
   private JTextField txtPhaseMin;
   private JTextField txtPower;
   private JTextField txtSteps;
   private JTextField txtTempCorrection;
   private JTextField txtTemperature;
   private JCheckBox cbPeakSuppression;

   public VNADriverSerialTinyDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNADriverSerialTinyDialog");
      this.driver = (VNADriverSerialTiny)pDriver;
      this.dib = (VNADriverSerialTinyDIB)this.driver.getDeviceInfoBlock();
      this.setTitle(VNADriverSerialTinyMessages.getString("drvTitle"));
      this.setDefaultCloseOperation(0);
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNADriverSerialTinyDialog");
      this.setPreferredSize(new Dimension(540, 540));
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout(new MigLayout("", "[grow][][]", ""));
      this.panel.add(new JLabel(), "");
      this.lblPhaseMax = new JLabel(VNADriverSerialTinyMessages.getString("lblMin"));
      this.panel.add(this.lblPhaseMax, "");
      JLabel lblLossMax = new JLabel(VNADriverSerialTinyMessages.getString("lblMax"));
      this.panel.add(lblLossMax, "wrap");
      this.panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblLoss")), "");
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
      this.lblPhaseMin = new JLabel(VNADriverSerialTinyMessages.getString("lblPhase"));
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
      this.panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblFreq")), "");
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
      this.panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblNoOfSteps")), "");
      this.txtSteps = new JTextField();
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setColumns(10);
      this.panel.add(this.txtSteps, "wrap");
      this.panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblFirmware")), "");
      this.txtFirmware = new JTextField();
      this.txtFirmware.setEditable(false);
      this.panel.add(this.txtFirmware, "grow,span 3,wrap");
      this.panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblPower")), "");
      this.txtPower = new JTextField();
      this.txtPower.setEditable(false);
      this.panel.add(this.txtPower, "grow,span 3,wrap");
      this.panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblTemperature")), "");
      this.txtTemperature = new JTextField();
      this.txtTemperature.setEditable(false);
      this.panel.add(this.txtTemperature, "grow,span 3,wrap");
      this.lblReference = new JLabel(VNADriverSerialTinyMessages.getString("lblReference"));
      this.lblReference.setBounds(10, 330, 141, 30);
      this.panel.add(this.lblReference, "");
      this.referenceValue = new ComplexInputField((Complex)null);
      this.referenceValue.setMaximum(new Complex(5000.0D, 5000.0D));
      this.referenceValue.setMinimum(new Complex(-5000.0D, -5000.0D));
      FlowLayout flowLayout = (FlowLayout)this.referenceValue.getLayout();
      flowLayout.setAlignment(0);
      this.panel.add(this.referenceValue, "grow,span 3,wrap");
      this.panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblPhaseCorr"), -20.0D, 20.0D)));
      this.txtPhaseCorrection = new JTextField();
      this.txtPhaseCorrection.setText("0");
      this.txtPhaseCorrection.setHorizontalAlignment(4);
      this.txtPhaseCorrection.setColumns(10);
      this.panel.add(this.txtPhaseCorrection, "wrap");
      this.panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblIFPhaseCorr"), -20.0D, 20.0D)));
      this.txtIFPhaseCorrection = new JTextField();
      this.txtIFPhaseCorrection.setText("0");
      this.txtIFPhaseCorrection.setHorizontalAlignment(4);
      this.txtIFPhaseCorrection.setColumns(10);
      this.panel.add(this.txtIFPhaseCorrection, "wrap");
      this.panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblGainCorr"), 0.5D, 2.0D)));
      this.txtGainCorrection = new JTextField();
      this.txtGainCorrection.setText("0");
      this.txtGainCorrection.setHorizontalAlignment(4);
      this.txtGainCorrection.setColumns(10);
      this.panel.add(this.txtGainCorrection, "wrap");
      this.panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblTempCorr"), -0.5D, 0.5D)));
      this.txtTempCorrection = new JTextField();
      this.txtTempCorrection.setText("0");
      this.txtTempCorrection.setHorizontalAlignment(4);
      this.txtTempCorrection.setColumns(10);
      this.panel.add(this.txtTempCorrection, "wrap");
      this.panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblBootloaderBaudRate"), 19200, 921600)));
      this.txtBootloaderBaudRate = new JTextField();
      this.txtBootloaderBaudRate.setText("0");
      this.txtBootloaderBaudRate.setHorizontalAlignment(4);
      this.txtBootloaderBaudRate.setColumns(10);
      this.panel.add(this.txtBootloaderBaudRate, "wrap");
      this.panel.add(new JLabel(""));
      this.cbPeakSuppression = new JCheckBox(VNADriverSerialTinyMessages.getString("lblPeakSuppression"));
      this.panel.add(this.cbPeakSuppression, "grow, span2, wrap");
      this.btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialTinyDialog.this.doDialogCancel();
         }
      });
      this.btCal = SwingUtil.createJButton("Button.AutoCalibrate", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialTinyDialog.this.doCalibrate();
         }
      });
      this.btDefault = SwingUtil.createJButton("Button.Default", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSerialTinyDialog.this.doReset();
         }
      });
      this.btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverSerialTinyDialog.this.doOK();
         }
      });
      this.panel.add(this.btCal, "wmin 100px");
      this.panel.add(this.btDefault, "wmin 100px, wrap");
      this.panel.add(new HelpButton(this, "VNADriverSerialTinyDialog"), "wmin 100px");
      this.panel.add(this.btCancel, "wmin 100px");
      this.panel.add(this.btOK, "wmin 100px,wrap");
      this.lblStatusbar = new StatusBarLabel("Ready", 1000);
      this.lblStatusbar.setBackground(Color.GREEN);
      this.panel.add(this.lblStatusbar, "span 3,grow,wrap");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNADriverSerialTinyDialog");
   }

   protected void doCalibrate() {
      this.btCancel.setEnabled(false);
      this.btDefault.setEnabled(false);
      this.btOK.setEnabled(false);
      this.btCal.setEnabled(false);
      this.setCursor(Cursor.getPredefinedCursor(3));
      (new PhaseCalibrationHelper(this, this.dib)).doCalibrate();
      this.setCursor(Cursor.getPredefinedCursor(0));
      this.btCancel.setEnabled(true);
      this.btDefault.setEnabled(true);
      this.btCal.setEnabled(true);
      this.btOK.setEnabled(true);
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.updateFieldsFromDIB();
      this.txtTemperature.setText(VNAFormatFactory.getResistanceBaseFormat().format(this.driver.getDeviceTemperature()));
      this.txtFirmware.setText(this.driver.getDeviceFirmwareInfo());
      this.txtPower.setText(VNAFormatFactory.getResistanceBaseFormat().format(this.driver.getDeviceSupply()));
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      ValidationResults results = new ValidationResults();
      int steps = IntegerValidator.parse(this.txtSteps.getText(), 200, 25000, VNADriverSerialTinyMessages.getString("lblNoOfSteps"), results);
      long minFreq = LongValidator.parse(this.txtFreqMin.getText(), 1L, 999999999999L, VNADriverSerialTinyMessages.getString("lblFreq"), results);
      long maxFreq = LongValidator.parse(this.txtFreqMax.getText(), 1L, 999999999999L, VNADriverSerialTinyMessages.getString("lblFreq"), results);
      double minLoss = DoubleValidator.parse(this.txtLossMin.getText(), -200.0D, 200.0D, VNADriverSerialTinyMessages.getString("lblLoss"), results);
      double maxLoss = DoubleValidator.parse(this.txtLossMax.getText(), -200.0D, 200.0D, VNADriverSerialTinyMessages.getString("lblLoss"), results);
      double phaseCorrection = DoubleValidator.parse(this.txtPhaseCorrection.getText(), -20.0D, 20.0D, VNADriverSerialTinyMessages.getString("lblPhaseCorr"), results);
      double ifPhaseCorrection = DoubleValidator.parse(this.txtIFPhaseCorrection.getText(), -20.0D, 20.0D, VNADriverSerialTinyMessages.getString("lblIFPhaseCorr"), results);
      double gainCorrection = DoubleValidator.parse(this.txtGainCorrection.getText(), 0.5D, 2.0D, VNADriverSerialTinyMessages.getString("lblGainCorr"), results);
      double tempCorrection = DoubleValidator.parse(this.txtTempCorrection.getText(), -0.5D, 0.5D, VNADriverSerialTinyMessages.getString("lblTempCorr"), results);
      int blBaud = IntegerValidator.parse(this.txtBootloaderBaudRate.getText(), 19200, 921600, VNADriverSerialTinyMessages.getString("lblBootloaderBaudRate"), results);
      boolean peakSupp = this.cbPeakSuppression.isSelected();
      if (results.isEmpty()) {
         steps = steps / 100 * 100;
         this.dib.setNumberOfSamples4Calibration(steps);
         this.dib.setReferenceResistance(this.referenceValue.getComplexValue());
         this.dib.setMinFrequency(minFreq);
         this.dib.setMaxFrequency(maxFreq);
         this.dib.setPhaseCorrection(phaseCorrection);
         this.dib.setIfPhaseCorrection(ifPhaseCorrection);
         this.dib.setGainCorrection(gainCorrection);
         this.dib.setTempCorrection(tempCorrection);
         this.dib.setBootloaderBaudrate(blBaud);
         this.dib.setPeakSuppression(peakSupp);
         this.dib.setMinLoss(minLoss);
         this.dib.setMaxLoss(maxLoss);
         this.dib.store(this.config, this.driver.getDriverConfigPrefix());
         IVNADriverMathHelper mathHelper = this.datapool.getDriver().getMathHelper();
         VNACalibrationBlock mcb = this.datapool.getMainCalibrationBlock();
         VNACalibrationKit kit = this.datapool.getCalibrationKit();
         mathHelper.createCalibrationPoints(mathHelper.createCalibrationContextForCalibrationPoints(mcb, kit), mcb);
         this.datapool.clearResizedCalibrationBlock();
         this.datapool.clearCalibratedData();
         this.setVisible(false);
      } else {
         new ValidationResultsDialog(this.getOwner(), results, this.getTitle());
      }

      TraceHelper.exit(this, "doOK");
   }

   private void doReset() {
      TraceHelper.entry(this, "doReset");
      this.dib.reset();
      this.updateFieldsFromDIB();
      TraceHelper.exit(this, "doReset");
   }

   private void updateFieldsFromDIB() {
      this.txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(this.dib.getMaxFrequency()));
      this.txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(this.dib.getMinFrequency()));
      this.txtLossMax.setText(VNAFormatFactory.getReflectionLossFormat().format(this.dib.getMaxLoss()));
      this.txtLossMin.setText(VNAFormatFactory.getReflectionLossFormat().format(this.dib.getMinLoss()));
      this.txtPhaseMax.setText(VNAFormatFactory.getPhaseFormat().format(this.dib.getMaxPhase()));
      this.txtPhaseMin.setText(VNAFormatFactory.getPhaseFormat().format(this.dib.getMinPhase()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)this.dib.getNumberOfSamples4Calibration()));
      this.referenceValue.setComplexValue(this.dib.getReferenceResistance());
      this.txtPhaseCorrection.setText(VNAFormatFactory.getGainFormat().format(this.dib.getPhaseCorrection()));
      this.txtIFPhaseCorrection.setText(VNAFormatFactory.getGainFormat().format(this.dib.getIfPhaseCorrection()));
      this.txtGainCorrection.setText(VNAFormatFactory.getGainFormat().format(this.dib.getGainCorrection()));
      this.txtTempCorrection.setText(VNAFormatFactory.getGainFormat().format(this.dib.getTempCorrection()));
      this.txtBootloaderBaudRate.setText(VNAFormatFactory.getFrequencyFormat4Export().format((long)this.dib.getBootloaderBaudrate()));
      this.cbPeakSuppression.setSelected(this.dib.isPeakSuppression());
   }
}
