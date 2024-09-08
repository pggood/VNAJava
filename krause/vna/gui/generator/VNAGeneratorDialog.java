package krause.vna.gui.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.gui.generator.table.VNAEditableFrequencyTable;
import krause.vna.resources.VNAMessages;

public class VNAGeneratorDialog extends KrauseDialog implements ChangeListener, ActionListener, ClipboardOwner {
   public static final String GENERATOR_LIST_FILENAME = "vna.generator.xml";
   private VNAConfig cfg = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private JButton btOK;
   private JPanel contentPanel;
   private VNADigitTextFieldHandler mouseHandler = null;
   private JPanel panel_1;
   private JLabel lblOnAir;
   private boolean onAir = false;
   private IVNADriver driver = null;
   private VNAEditableFrequencyTable tblFrequencies = null;

   public VNAGeneratorDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super((Window)pMainFrame.getJFrame(), true);
      this.driver = pDriver;
      this.setResizable(false);
      this.setTitle(VNAMessages.getString("VNAGeneratorDialog.title"));
      this.setDefaultCloseOperation(0);
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      this.mouseHandler = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());
      this.mouseHandler.addChangeListener(this);
      this.getContentPane().setLayout(new BorderLayout(0, 0));
      this.contentPanel = new JPanel();
      this.contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
      this.getContentPane().add(this.contentPanel, "Center");
      FlowLayout fl_contentPanel = new FlowLayout(1, 5, 5);
      this.contentPanel.setLayout(fl_contentPanel);
      this.panel_1 = new JPanel();
      this.panel_1.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      this.contentPanel.add(this.panel_1);
      this.panel_1.setLayout(new FlowLayout(1, 5, 5));
      VNADigitTextField textField_9 = new VNADigitTextField(1000000000L, 0L);
      this.mouseHandler.registerField(textField_9);
      this.panel_1.add(textField_9);
      JLabel label_1 = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
      this.panel_1.add(label_1);
      label_1.setFont(new Font("Tahoma", 0, 54));
      VNADigitTextField textField = new VNADigitTextField(100000000L, 0L);
      this.mouseHandler.registerField(textField);
      this.panel_1.add(textField);
      VNADigitTextField textField_7 = new VNADigitTextField(10000000L, 0L);
      this.mouseHandler.registerField(textField_7);
      this.panel_1.add(textField_7);
      VNADigitTextField textField_6 = new VNADigitTextField(1000000L, 0L);
      this.mouseHandler.registerField(textField_6);
      this.panel_1.add(textField_6);
      label_1 = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
      this.panel_1.add(label_1);
      label_1.setFont(new Font("Tahoma", 0, 54));
      VNADigitTextField textField_5 = new VNADigitTextField(100000L, 0L);
      this.mouseHandler.registerField(textField_5);
      this.panel_1.add(textField_5);
      VNADigitTextField textField_3 = new VNADigitTextField(10000L, 0L);
      this.mouseHandler.registerField(textField_3);
      this.panel_1.add(textField_3);
      VNADigitTextField textField_4 = new VNADigitTextField(1000L, 0L);
      this.mouseHandler.registerField(textField_4);
      this.panel_1.add(textField_4);
      JLabel label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
      this.panel_1.add(label);
      label.setFont(new Font("Tahoma", 0, 54));
      VNADigitTextField textField_2 = new VNADigitTextField(100L, 0L);
      this.mouseHandler.registerField(textField_2);
      this.panel_1.add(textField_2);
      VNADigitTextField textField_1 = new VNADigitTextField(10L, 0L);
      this.mouseHandler.registerField(textField_1);
      this.panel_1.add(textField_1);
      VNADigitTextField textField_8 = new VNADigitTextField(1L, 0L);
      this.mouseHandler.registerField(textField_8);
      this.panel_1.add(textField_8);
      JLabel lblHz = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblHz.text"));
      lblHz.setFont(new Font("Tahoma", 0, 54));
      this.panel_1.add(lblHz);
      this.tblFrequencies = new VNAEditableFrequencyTable();
      this.contentPanel.add(this.tblFrequencies);
      this.tblFrequencies.addActionListener(this);
      JPanel buttonPane = new JPanel();
      this.getContentPane().add(buttonPane, "South");
      this.lblOnAir = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.text"));
      this.lblOnAir.setToolTipText(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.toolTipText"));
      this.lblOnAir.setCursor(Cursor.getPredefinedCursor(12));
      this.lblOnAir.setOpaque(true);
      this.lblOnAir.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNAGeneratorDialog.this.doMouseClickedOnOnAirField(e);
         }
      });
      buttonPane.setLayout(new FlowLayout(1, 5, 5));
      this.lblOnAir.setAlignmentX(0.5F);
      this.lblOnAir.setFont(new Font("Courier New", 0, 17));
      this.lblOnAir.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      buttonPane.add(this.lblOnAir);
      JLabel lblTuneTheFrequency = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblTuneTheFrequency.text"));
      buttonPane.add(lblTuneTheFrequency);
      this.btOK = new JButton(VNAMessages.getString("Button.Close"));
      this.btOK.addActionListener(this);
      buttonPane.add(this.btOK);
      this.doDialogInit();
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.cfg.setGeneratorFrequency(this.mouseHandler.getValue());
      this.tblFrequencies.save(this.cfg.getVNAConfigDirectory() + "/" + "vna.generator.xml");
      this.stopGenerator();
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.mouseHandler.setValue(this.cfg.getGeneratorFrequency());
      this.tblFrequencies.load(this.cfg.getVNAConfigDirectory() + "/" + "vna.generator.xml");
      this.updateOnAirField();
      this.addEscapeKey();
      this.showCentered(this.getOwner());
      TraceHelper.exit(this, "doInit");
   }

   private void updateOnAirField() {
      if (this.onAir) {
         this.lblOnAir.setForeground(Color.BLACK);
         this.lblOnAir.setBackground(Color.RED);
      } else {
         this.lblOnAir.setForeground(Color.RED);
         this.lblOnAir.setBackground(Color.BLACK);
      }

   }

   public void stateChanged(ChangeEvent e) {
      if (e.getSource() == this.mouseHandler && this.onAir) {
         try {
            this.driver.startGenerator(this.mouseHandler.getValue(), 0L, 0, 0, 0, 0);
         } catch (ProcessingException var3) {
            ErrorLogHelper.exception(this, "stateChanged", var3);
         }
      }

   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      TraceHelper.text(this, "actionPerformed", e.toString());
      if (e.getSource() == this.btOK) {
         this.doDialogCancel();
      } else if (e.getSource() == this.tblFrequencies) {
         if ("ADD".equals(e.getActionCommand())) {
            this.tblFrequencies.addFrequency(this.mouseHandler.getValue());
         } else if (!"DEL".equals(e.getActionCommand()) && ("USE".equals(e.getActionCommand()) || "FRQ".equals(e.getActionCommand()))) {
            long freq = e.getWhen();
            this.mouseHandler.setValue(freq);
            if (this.onAir) {
               this.startGenerator(freq);
            }
         }
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      TraceHelper.entry(this, "lostOwnership");
      TraceHelper.exit(this, "lostOwnership");
   }

   private void doMouseClickedOnOnAirField(MouseEvent e) {
      TraceHelper.entry(this, "doMouseClickedOnOnAirField");
      if (e.getButton() == 1) {
         if (this.onAir) {
            this.stopGenerator();
            this.onAir = false;
         } else {
            this.startGenerator(this.mouseHandler.getValue());
            this.onAir = true;
         }

         this.updateOnAirField();
      }

      TraceHelper.exit(this, "doMouseClickedOnOnAirField");
   }

   private void startGenerator(long frequency) {
      TraceHelper.entry(this, "startGenerator");
      this.btOK.setEnabled(false);

      try {
         this.driver.startGenerator(frequency, 0L, 0, 0, 0, 0);
      } catch (ProcessingException var4) {
         ErrorLogHelper.exception(this, "startGenerator", var4);
      }

      this.btOK.setEnabled(true);
      TraceHelper.exit(this, "startGenerator");
   }

   private void stopGenerator() {
      TraceHelper.entry(this, "stopGenerator");
      this.btOK.setEnabled(false);

      try {
         this.driver.stopGenerator();
      } catch (ProcessingException var2) {
         ErrorLogHelper.exception(this, "startGenerator", var2);
      }

      this.btOK.setEnabled(true);
      TraceHelper.exit(this, "stopGenerator");
   }
}
