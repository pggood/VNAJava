package krause.vna.gui.calibrate.frequency;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormatSymbols;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAFrequencyCalibrationDialog extends KrauseDialog implements ActionListener, ChangeListener, FocusListener {
   private JButton btCancel;
   private JPanel contentPanel;
   private VNADigitTextFieldHandler handlerTicks = null;
   private FrequencyInputField frequencyField;
   private VNADeviceInfoBlock dib;
   private IVNADriver driver = null;
   private long orgTicks;
   private JButton btOK;

   public VNAFrequencyCalibrationDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super((Window)pMainFrame.getJFrame(), true);
      this.setConfigurationPrefix("VNAFrequencyCalibrationDialog");
      this.setProperties(VNAConfig.getSingleton());
      String comma = new String("" + DecimalFormatSymbols.getInstance().getGroupingSeparator());
      this.driver = pDriver;
      this.dib = this.driver.getDeviceInfoBlock();
      this.orgTicks = this.dib.getDdsTicksPerMHz();
      this.setTitle(VNAMessages.getString("VNAFrequencyCalibrationDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(320, 340));
      this.addWindowListener(this);
      this.getContentPane().setLayout(new BorderLayout());
      this.contentPanel = new JPanel();
      this.contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
      this.getContentPane().add(this.contentPanel, "Center");
      this.contentPanel.setLayout(new MigLayout("", "fill", "[][fill]"));
      JTextArea ta = new JTextArea(VNAMessages.getString("VNAFrequencyCalibrationDialog.helptext"));
      ta.setForeground(new Color(0, 0, 255));
      ta.setWrapStyleWord(true);
      ta.setLineWrap(true);
      ta.setBackground(this.contentPanel.getBackground());
      ta.setEditable(false);
      this.contentPanel.add(ta, "wrap");
      JPanel panelFRQ = new JPanel();
      panelFRQ.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNAFrequencyCalibrationDialog.Frequency"), 4, 2, (Font)null, (Color)null));
      this.contentPanel.add(panelFRQ, "wrap");
      panelFRQ.setLayout(new FlowLayout(0, 5, 5));
      panelFRQ.add(this.frequencyField = new FrequencyInputField("", 1L, this.dib.getMinFrequency(), this.dib.getMaxFrequency()));
      panelFRQ.add(new JLabel("Hz"));
      this.frequencyField.addFocusListener(this);
      JPanel panelTicks = new JPanel();
      panelTicks.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNAFrequencyCalibrationDialog.Ticks"), 4, 2, (Font)null, (Color)null));
      this.contentPanel.add(panelTicks, "wrap");
      panelTicks.setLayout(new FlowLayout(1, 5, 5));
      this.handlerTicks = new VNADigitTextFieldHandler(0L, 99999999L);
      this.handlerTicks.addChangeListener(this);
      JLabel label = new JLabel(comma);
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(10000000, 0L, 40)));
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(1000000, 0L, 40)));
      label.setFont(new Font("Tahoma", 0, 40));
      panelTicks.add(label);
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(100000, 0L, 40)));
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(10000, 0L, 40)));
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(1000, 0L, 40)));
      label = new JLabel(comma);
      label.setFont(new Font("Tahoma", 0, 40));
      panelTicks.add(label);
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(100, 0L, 40)));
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(10, 0L, 40)));
      panelTicks.add(this.handlerTicks.registerField(new VNADigitTextField(1, 0L, 40)));
      JPanel buttonPane = new JPanel();
      this.getContentPane().add(buttonPane, "South");
      buttonPane.setLayout(new FlowLayout(1, 5, 5));
      buttonPane.add(new HelpButton(this, "VNAFrequencyCalibrationDialog"));
      this.btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
      this.btCancel.addActionListener(this);
      buttonPane.add(this.btCancel);
      this.btOK = new JButton(VNAMessages.getString("Button.Save"));
      this.btOK.addActionListener(this);
      buttonPane.add(this.btOK);
      this.doDialogInit();
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      if (e.getSource() == this.btCancel) {
         this.doDialogCancel();
      } else if (e.getSource() == this.btOK) {
         this.doSAVE();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doSAVE() {
      TraceHelper.entry(this, "doSAVE");
      this.dib.setDdsTicksPerMHz(this.handlerTicks.getValue());
      this.dib.store(this.getProperties(), this.driver.getDriverConfigPrefix());
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doSAVE");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.stopGenerator();
      this.dib.store(this.getProperties(), this.driver.getDriverConfigPrefix());
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.dib.setDdsTicksPerMHz(this.orgTicks);
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.handlerTicks.setValue(this.dib.getDdsTicksPerMHz());
      long frq = this.dib.getMaxFrequency() / 2L / 1000000L * 1000000L;
      this.frequencyField.setFrequency(frq);
      this.startGenerator();
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      TraceHelper.entry(this, "lostOwnership");
      TraceHelper.exit(this, "lostOwnership");
   }

   private void startGenerator() {
      TraceHelper.entry(this, "startGenerator");
      this.dib.setDdsTicksPerMHz(this.handlerTicks.getValue());
      long frequencyI = this.frequencyField.getFrequency();
      long frequencyQ = 0L;
      int attentuationI = 0;
      int attenuationQ = 0;
      int phase = 0;
      byte mainAttenuation = 0;

      try {
         this.driver.startGenerator(frequencyI, frequencyQ, attentuationI, attenuationQ, phase, mainAttenuation);
      } catch (ProcessingException var10) {
         ErrorLogHelper.exception(this, "startGenerator", var10);
      }

      TraceHelper.exit(this, "startGenerator");
   }

   private void stopGenerator() {
      TraceHelper.entry(this, "stopGenerator");
      this.btCancel.setEnabled(false);

      try {
         this.driver.stopGenerator();
      } catch (ProcessingException var2) {
         ErrorLogHelper.exception(this, "stopGenerator", var2);
      }

      this.btCancel.setEnabled(true);
      TraceHelper.exit(this, "stopGenerator");
   }

   public void stateChanged(ChangeEvent arg0) {
      TraceHelper.entry(this, "stateChanged");
      this.startGenerator();
      TraceHelper.exit(this, "stateChanged");
   }

   public void focusGained(FocusEvent e) {
      TraceHelper.entry(this, "focusGained");
      this.startGenerator();
      TraceHelper.exit(this, "focusGained");
   }

   public void focusLost(FocusEvent e) {
      TraceHelper.entry(this, "focusLost");
      this.startGenerator();
      TraceHelper.exit(this, "focusLost");
   }
}
