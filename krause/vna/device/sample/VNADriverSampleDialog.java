package krause.vna.device.sample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;

public class VNADriverSampleDialog extends VNADriverDialog {
   private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("krause.vna.device.sample.driver");
   private JPanel panel;
   private JTextField txtLossMin;
   private JTextField txtLossMax;
   private JLabel lblPhaseMin;
   private JTextField txtPhaseMin;
   private JLabel lblMax;
   private JTextField txtPhaseMax;
   private JTextField txtFreqMin;
   private JTextField txtFreqMax;
   private JTextField txtSteps;
   private IVNADriver driver;

   public VNADriverSampleDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super(pMainFrame.getJFrame(), pMainFrame);
      TraceHelper.entry(this, "VNADriverSampleDialog");
      this.driver = pDriver;
      this.setTitle(BUNDLE.getString("Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.setBounds(100, 100, 420, 237);
      this.panel = new JPanel();
      this.getContentPane().add(this.panel, "Center");
      this.panel.setLayout((LayoutManager)null);
      JLabel lblLossMin = new JLabel(BUNDLE.getString("Dialog.lblLossMin.text"));
      lblLossMin.setBounds(9, 40, 151, 18);
      this.panel.add(lblLossMin);
      this.txtLossMin = new JTextField();
      this.txtLossMin.setEditable(false);
      this.txtLossMin.setHorizontalAlignment(4);
      this.txtLossMin.setBounds(170, 40, 86, 20);
      this.panel.add(this.txtLossMin);
      this.txtLossMin.setColumns(10);
      JLabel lblMin = new JLabel(BUNDLE.getString("Dialog.lblLossMax.text"));
      lblMin.setBounds(170, 12, 90, 18);
      this.panel.add(lblMin);
      this.txtLossMax = new JTextField();
      this.txtLossMax.setEditable(false);
      this.txtLossMax.setHorizontalAlignment(4);
      this.txtLossMax.setBounds(280, 40, 86, 20);
      this.panel.add(this.txtLossMax);
      this.txtLossMax.setColumns(10);
      this.lblPhaseMin = new JLabel(BUNDLE.getString("Dialog.lblPhaseMin.text"));
      this.lblPhaseMin.setBounds(10, 70, 151, 18);
      this.panel.add(this.lblPhaseMin);
      this.txtPhaseMin = new JTextField();
      this.txtPhaseMin.setEditable(false);
      this.txtPhaseMin.setHorizontalAlignment(4);
      this.txtPhaseMin.setBounds(170, 70, 86, 20);
      this.panel.add(this.txtPhaseMin);
      this.txtPhaseMin.setColumns(10);
      this.lblMax = new JLabel(BUNDLE.getString("Dialog.lblPhaseMax.text"));
      this.lblMax.setBounds(279, 10, 90, 20);
      this.panel.add(this.lblMax);
      this.txtPhaseMax = new JTextField();
      this.txtPhaseMax.setEditable(false);
      this.txtPhaseMax.setHorizontalAlignment(4);
      this.txtPhaseMax.setBounds(280, 70, 86, 20);
      this.panel.add(this.txtPhaseMax);
      this.txtPhaseMax.setColumns(10);
      JLabel lblFreqMin = new JLabel(BUNDLE.getString("Dialog.lblFreqMin.text"));
      lblFreqMin.setBounds(9, 100, 151, 18);
      this.panel.add(lblFreqMin);
      this.txtFreqMin = new JTextField();
      this.txtFreqMin.setEditable(false);
      this.txtFreqMin.setHorizontalAlignment(4);
      this.txtFreqMin.setBounds(170, 100, 86, 20);
      this.panel.add(this.txtFreqMin);
      this.txtFreqMin.setColumns(10);
      this.txtFreqMax = new JTextField();
      this.txtFreqMax.setEditable(false);
      this.txtFreqMax.setHorizontalAlignment(4);
      this.txtFreqMax.setBounds(280, 100, 120, 20);
      this.panel.add(this.txtFreqMax);
      this.txtFreqMax.setColumns(12);
      JLabel lblNoOfSteps = new JLabel(BUNDLE.getString("Dialog.lblNoOfSteps.text"));
      lblNoOfSteps.setBounds(9, 132, 151, 18);
      this.panel.add(lblNoOfSteps);
      this.txtSteps = new JTextField();
      this.txtSteps.setEditable(false);
      this.txtSteps.setHorizontalAlignment(4);
      this.txtSteps.setBounds(170, 130, 86, 20);
      this.panel.add(this.txtSteps);
      this.txtSteps.setColumns(10);
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      pnlButtons.setLayout(new BorderLayout(0, 0));
      JButton btCancel = new JButton(BUNDLE.getString("Button.Cancel"));
      btCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNADriverSampleDialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(btCancel, "West");
      this.doDialogInit();
      TraceHelper.exit(this, "VNADriverSampleDialog");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      VNADeviceInfoBlock dib = this.driver.getDeviceInfoBlock();
      this.txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxFrequency()));
      this.txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinFrequency()));
      this.txtLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxLoss()));
      this.txtLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinLoss()));
      this.txtPhaseMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxPhase()));
      this.txtPhaseMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinPhase()));
      this.txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format((long)dib.getNumberOfSamples4Calibration()));
      this.addEscapeKey();
      this.showCentered(this.getWidth(), this.getHeight());
      TraceHelper.exit(this, "doInit");
   }
}
