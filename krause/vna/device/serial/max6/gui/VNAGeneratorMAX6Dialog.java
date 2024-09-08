package krause.vna.device.serial.max6.gui;

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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
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
import krause.vna.device.serial.max6.VNADriverSerialMax6Messages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.gui.generator.table.VNAEditableFrequencyTable;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAGeneratorMAX6Dialog extends KrauseDialog implements ChangeListener, ActionListener, ClipboardOwner, AdjustmentListener {
   public static final String GENERATOR_LIST_FILENAME = "vna.generator.xml";
   private VNAConfig cfg = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private JButton btOK;
   private JPanel contentPanel;
   private VNADigitTextFieldHandler mouseHandler = null;
   private JLabel lblOnAir;
   private boolean onAir = false;
   private IVNADriver driver = null;
   private VNAEditableFrequencyTable tblFrequencies = null;
   private JScrollBar sbLevel;
   private int outputLevel = 16383;

   public VNAGeneratorMAX6Dialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super((Window)pMainFrame.getJFrame(), true);
      this.driver = pDriver;
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNAGeneratorMAX6Dialog");
      this.setTitle(VNADriverSerialMax6Messages.getString("VNAGeneratorMAX6Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.getContentPane().setLayout(new BorderLayout(0, 0));
      this.contentPanel = new JPanel(new MigLayout("", "[][][][]", "[][]"));
      this.contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
      this.getContentPane().add(this.contentPanel, "Center");
      this.contentPanel.add(this.createFrequencyPanel(), "span 3,grow");
      this.tblFrequencies = new VNAEditableFrequencyTable();
      this.tblFrequencies.addActionListener(this);
      this.contentPanel.add(this.tblFrequencies, "span 1 2,wrap");
      this.contentPanel.add(new JLabel(VNADriverSerialMax6Messages.getString("VNAGeneratorMAX6Dialog.attn")), "");
      this.sbLevel = new JScrollBar(0, this.outputLevel, 1, 0, 16384);
      this.sbLevel.setBlockIncrement(1000);
      this.sbLevel.addAdjustmentListener(this);
      this.contentPanel.add(this.sbLevel, "span 2,grow x");
      JPanel buttonPane = new JPanel();
      this.getContentPane().add(buttonPane, "South");
      this.lblOnAir = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.text"));
      this.lblOnAir.setToolTipText(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.toolTipText"));
      this.lblOnAir.setCursor(Cursor.getPredefinedCursor(12));
      this.lblOnAir.setOpaque(true);
      this.lblOnAir.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNAGeneratorMAX6Dialog.this.doMouseClickedOnOnAirField(e);
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

   private JPanel createFrequencyPanel() {
      TraceHelper.entry(this, "createFrequencyPanel");
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      this.mouseHandler = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());
      this.mouseHandler.addChangeListener(this);
      JPanel rc = new JPanel();
      rc.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      rc.setLayout(new FlowLayout(1, 5, 5));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(1000000000L, 0L)));
      JLabel label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
      label.setFont(new Font("Tahoma", 0, 54));
      rc.add(label);
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(100000000L, 0L)));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(10000000L, 0L)));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(1000000L, 0L)));
      label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
      label.setFont(new Font("Tahoma", 0, 54));
      rc.add(label);
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(100000L, 0L)));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(10000L, 0L)));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(1000L, 0L)));
      label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
      label.setFont(new Font("Tahoma", 0, 54));
      rc.add(label);
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(100L, 0L)));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(10L, 0L)));
      rc.add(this.mouseHandler.registerField(new VNADigitTextField(1L, 0L)));
      JLabel lblHz = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblHz.text"));
      lblHz.setFont(new Font("Tahoma", 0, 54));
      rc.add(lblHz);
      TraceHelper.exit(this, "createFrequencyPanel");
      return rc;
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
      TraceHelper.entry(this, "stateChanged", "" + e);
      if (e.getSource() == this.mouseHandler && this.onAir) {
         this.startGenerator();
      }

      TraceHelper.exit(this, "stateChanged");
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
               this.startGenerator();
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
            this.startGenerator();
            this.onAir = true;
         }

         this.updateOnAirField();
      }

      TraceHelper.exit(this, "doMouseClickedOnOnAirField");
   }

   private void startGenerator() {
      TraceHelper.entry(this, "startGenerator");
      this.btOK.setEnabled(false);

      try {
         long frequency = this.mouseHandler.getValue();
         this.driver.startGenerator(frequency, 0L, 16383 - this.outputLevel, 0, 0, 0);
      } catch (ProcessingException var3) {
         ErrorLogHelper.exception(this, "startGenerator", var3);
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

   public void adjustmentValueChanged(AdjustmentEvent e) {
      if (!e.getValueIsAdjusting()) {
         this.outputLevel = e.getValue();
         this.sbLevel.setToolTipText("" + this.outputLevel);
         if (this.onAir) {
            this.startGenerator();
         }
      }

   }
}
