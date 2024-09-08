package krause.vna.device.serial.pro.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import krause.common.TypedProperties;
import krause.common.exception.ProcessingException;
import krause.common.gui.ILocationAwareDialog;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.IVNADriver;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.pro.VNADriverSerialProMessages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import net.miginfocom.swing.MigLayout;

public class VNAGeneratorProDialog extends KrauseDialog implements ChangeListener, ActionListener, ClipboardOwner, AdjustmentListener, ILocationAwareDialog {
   private static final String PROPERTIES_PREFIX = "VNAGeneratorProDialog";
   public static final int FONT_SIZE = 30;
   public static final int MIN_PHASE = 0;
   public static final int MAX_PHASE = 36000;
   private static Font symbolFont = new Font("Tahoma", 0, 30);
   private String groupSeparator = new String("" + DecimalFormatSymbols.getInstance().getGroupingSeparator());
   private JButton btOK;
   private JPanel contentPanel;
   private VNADigitTextFieldHandler handlerFrequencyQ = null;
   private VNADigitTextFieldHandler handlerFrequencyI = null;
   private VNADigitTextFieldHandler handlerAttenuationI = null;
   private VNADigitTextFieldHandler handlerAttenuationQ = null;
   private VNADigitTextFieldHandler handlerPhase = null;
   private JPanel panelFRQQ;
   private TypedProperties cfg = VNAConfig.getSingleton();
   private VNADriverSerialProDIB dib;
   private JLabel lblOnAir;
   private boolean onAir = false;
   private IVNADriver driver = null;
   private JTextField txtVALUE;
   JScrollBar sbPhase;
   private JToggleButton btLinkFrq;
   private JToggleButton btLinkAtt;

   public VNAGeneratorProDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
      super((Window)pMainFrame.getJFrame(), true);
      this.driver = pDriver;
      this.dib = (VNADriverSerialProDIB)this.driver.getDeviceInfoBlock();
      this.setTitle(VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(900, 500));
      this.addWindowListener(this);
      this.getContentPane().setLayout(new BorderLayout(5, 5));
      this.createDigitHandlers();
      this.contentPanel = new JPanel(new MigLayout("", "[left]0[grow, fill]0[right]", "0[]0[]0[]0[fill]0"));
      this.getContentPane().add(this.contentPanel, "Center");
      this.contentPanel.add(this.createFREQPanel(), "span 3, center, grow, wrap");
      this.contentPanel.add(this.createATTPanel(), "span 3, center, grow, wrap");
      this.contentPanel.add(this.createPHASEPanel(), "growy");
      this.contentPanel.add(this.createHELPPanel(), "growy");
      this.contentPanel.add(this.createNUMPanel(), "growy,wrap");
      this.btOK = new JButton(VNADriverSerialProMessages.getString("Button.Close"));
      this.btOK.addActionListener(this);
      this.contentPanel.add(this.btOK, "span 3, right");
      this.doDialogInit();
   }

   private JPanel createNUMPanel() {
      JPanel panelNUM = new JPanel(new MigLayout("", "", ""));
      panelNUM.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.control"), 4, 2, (Font)null, new Color(0, 0, 0)));
      panelNUM.setLayout(new GridLayout(0, 1, 0, 0));
      JPanel panel_1 = new JPanel();
      FlowLayout flowLayout = (FlowLayout)panel_1.getLayout();
      flowLayout.setAlignment(0);
      panel_1.setAlignmentX(0.0F);
      panelNUM.add(panel_1);
      JLabel lblValue = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblValue.text"));
      panel_1.add(lblValue);
      this.txtVALUE = new JTextField();
      this.txtVALUE.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() >= 115 && e.getKeyCode() <= 120) {
               VNAGeneratorProDialog.this.doProcessFKey(e);
            }

         }
      });
      panel_1.add(this.txtVALUE);
      this.txtVALUE.setColumns(10);
      JPanel panel_3 = new JPanel();
      panelNUM.add(panel_3);
      JLabel lblEndWithFor = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblEndWithFor.text"));
      panel_3.add(lblEndWithFor);
      JPanel panel_2 = new JPanel();
      panelNUM.add(panel_2);
      JLabel lblTuneTheFrequency = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblTuneTheFrequency.text"));
      panel_2.add(lblTuneTheFrequency);
      this.lblOnAir = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblOnAir.text"));
      panel_2.add(this.lblOnAir);
      this.lblOnAir.setCursor(Cursor.getPredefinedCursor(12));
      this.lblOnAir.setOpaque(true);
      this.lblOnAir.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNAGeneratorProDialog.this.doClickOnAirField(e);
         }
      });
      this.lblOnAir.setAlignmentX(0.5F);
      this.lblOnAir.setFont(new Font("Courier New", 0, 17));
      this.lblOnAir.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      return panelNUM;
   }

   private JPanel createHELPPanel() {
      JPanel panelHLP = new JPanel(new MigLayout("", "[grow,center]", "[]"));
      panelHLP.setBorder(new TitledBorder((Border)null, VNADriverSerialProMessages.getString("VNAGeneratorDialog.help"), 4, 2, (Font)null, (Color)null));
      JTextArea txtrFEnterI = new JTextArea();
      txtrFEnterI.setTabSize(3);
      txtrFEnterI.setEditable(false);
      txtrFEnterI.setBackground(UIManager.getColor("Label.background"));
      txtrFEnterI.setFont(UIManager.getFont("Label.font"));
      txtrFEnterI.setText(VNADriverSerialProMessages.getString("VNAGeneratorDialog.txtrFEnterI.text"));
      panelHLP.add(txtrFEnterI, "");
      return panelHLP;
   }

   private JPanel createPHASEPanel() {
      JPanel panelPhase = new JPanel(new MigLayout("", "[][][][][][][]", "[][]"));
      panelPhase.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.phaseDiff"), 4, 2, (Font)null, new Color(0, 0, 0)));
      panelPhase.add(this.handlerPhase.registerField(new VNADigitTextField(10000, 0L, 30)), "");
      panelPhase.add(this.handlerPhase.registerField(new VNADigitTextField(1000, 0L, 30)), "");
      panelPhase.add(this.handlerPhase.registerField(new VNADigitTextField(100, 0L, 30)), "");
      panelPhase.add(this.createNewLabel(this.groupSeparator), "");
      panelPhase.add(this.handlerPhase.registerField(new VNADigitTextField(10, 0L, 30)), "");
      panelPhase.add(this.handlerPhase.registerField(new VNADigitTextField(1, 0L, 30)), "");
      panelPhase.add(this.createNewLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.label_7.text")), "wrap");
      this.sbPhase = new JScrollBar(0, 0, 1, 0, 36000);
      this.sbPhase.setBlockIncrement(50);
      this.sbPhase.addAdjustmentListener(this);
      panelPhase.add(this.sbPhase, "grow, span 7");
      return panelPhase;
   }

   private JPanel createFREQPanel() {
      JPanel panelFRQ = new JPanel(new MigLayout("", "0[left]0[center,fill,grow]0[right]0", "0[]0"));
      panelFRQ.setBorder(new TitledBorder((Border)null, VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.Frequency"), 4, 2, (Font)null, (Color)null));
      JPanel panelFRQI = new JPanel(new MigLayout("", "0", "0"));
      panelFRQ.add(panelFRQI, "");
      panelFRQI.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.frq.i"), 4, 2, (Font)null, new Color(0, 0, 0)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1000000000, 0L, 30)));
      panelFRQI.add(this.createNewLabel(this.groupSeparator));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(100000000, 0L, 30)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(10000000, 0L, 30)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1000000, 0L, 30)));
      panelFRQI.add(this.createNewLabel(this.groupSeparator));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(100000, 0L, 30)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(10000, 0L, 30)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1000, 0L, 30)));
      panelFRQI.add(this.createNewLabel(this.groupSeparator));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(100, 0L, 30)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(10, 0L, 30)));
      panelFRQI.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1, 0L, 30)));
      panelFRQI.add(this.createNewLabel("Hz"));
      JPanel panelIQLink = new JPanel(new MigLayout("", "", ""));
      panelFRQ.add(panelIQLink, "");
      JButton btFrqRight2Left = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button.text"));
      btFrqRight2Left.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAGeneratorProDialog.this.handlerFrequencyI.setValue(VNAGeneratorProDialog.this.handlerFrequencyQ.getValue());
            VNAGeneratorProDialog.this.startGenerator();
         }
      });
      panelIQLink.add(btFrqRight2Left, "wrap");
      this.btLinkFrq = new JToggleButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.toggleButton.text"));
      this.btLinkFrq.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      panelIQLink.add(this.btLinkFrq, "wrap");
      JButton btFrqLeft2Right = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button_1.text"));
      btFrqLeft2Right.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAGeneratorProDialog.this.handlerFrequencyQ.setValue(VNAGeneratorProDialog.this.handlerFrequencyI.getValue());
            VNAGeneratorProDialog.this.startGenerator();
         }
      });
      panelIQLink.add(btFrqLeft2Right, "");
      this.panelFRQQ = new JPanel(new MigLayout("", "", ""));
      panelFRQ.add(this.panelFRQQ);
      this.panelFRQQ.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.frq.q"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(1000000000, 0L, 30)));
      this.panelFRQQ.add(this.createNewLabel(this.groupSeparator));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(100000000, 0L, 30)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(10000000, 0L, 30)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(1000000, 0L, 30)));
      this.panelFRQQ.add(this.createNewLabel(this.groupSeparator));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(100000, 0L, 30)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(10000, 0L, 30)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(1000, 0L, 30)));
      this.panelFRQQ.add(this.createNewLabel(this.groupSeparator));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(100, 0L, 30)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(10, 0L, 30)));
      this.panelFRQQ.add(this.handlerFrequencyQ.registerField(new VNADigitTextField(1, 0L, 30)));
      this.panelFRQQ.add(this.createNewLabel("Hz"));
      return panelFRQ;
   }

   private JPanel createATTPanel() {
      JPanel panelATT = new JPanel(new MigLayout("", "0[left]0[center,fill,grow]0[right]0", "0[]0"));
      panelATT.setBorder(new TitledBorder((Border)null, VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.att"), 4, 2, (Font)null, (Color)null));
      JPanel panelATTI = new JPanel(new MigLayout("", "", ""));
      panelATTI.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.att.i"), 4, 2, (Font)null, new Color(0, 0, 0)));
      panelATT.add(panelATTI, "");
      panelATTI.add(this.createNewLabel("-"), "");
      panelATTI.add(this.handlerAttenuationI.registerField(new VNADigitTextField(1000, 0L, 30)), "");
      panelATTI.add(this.handlerAttenuationI.registerField(new VNADigitTextField(100, 0L, 30)), "");
      panelATTI.add(this.createNewLabel(this.groupSeparator), "");
      panelATTI.add(this.handlerAttenuationI.registerField(new VNADigitTextField(10, 0L, 30)), "");
      panelATTI.add(this.handlerAttenuationI.registerField(new VNADigitTextField(1, 0L, 30)), "");
      panelATTI.add(this.createNewLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblDb.text")), "");
      JPanel panelATTCenter = new JPanel(new MigLayout("", "", ""));
      panelATT.add(panelATTCenter, "");
      JButton btAttRight2Left = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button_2.text"));
      btAttRight2Left.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAGeneratorProDialog.this.handlerAttenuationI.setValue(VNAGeneratorProDialog.this.handlerAttenuationQ.getValue());
            VNAGeneratorProDialog.this.startGenerator();
         }
      });
      panelATTCenter.add(btAttRight2Left, "");
      this.btLinkAtt = new JToggleButton("∞");
      this.btLinkAtt.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         }
      });
      panelATTCenter.add(this.btLinkAtt, "");
      JButton btAttLeft2Right = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button_3.text"));
      btAttLeft2Right.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAGeneratorProDialog.this.handlerAttenuationQ.setValue(VNAGeneratorProDialog.this.handlerAttenuationI.getValue());
            VNAGeneratorProDialog.this.startGenerator();
         }
      });
      panelATTCenter.add(btAttLeft2Right, "");
      JPanel panelATTQ = new JPanel(new MigLayout("", "", ""));
      panelATTQ.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.att.q"), 4, 2, (Font)null, new Color(0, 0, 0)));
      panelATT.add(panelATTQ, "");
      panelATTQ.add(this.createNewLabel("-"), "");
      panelATTQ.add(this.handlerAttenuationQ.registerField(new VNADigitTextField(1000, 0L, 30)), "");
      panelATTQ.add(this.handlerAttenuationQ.registerField(new VNADigitTextField(100, 0L, 30)), "");
      panelATTQ.add(this.createNewLabel(this.groupSeparator), "");
      panelATTQ.add(this.handlerAttenuationQ.registerField(new VNADigitTextField(10, 0L, 30)), "");
      panelATTQ.add(this.handlerAttenuationQ.registerField(new VNADigitTextField(1, 0L, 30)), "");
      panelATTQ.add(this.createNewLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblDb.text")), "");
      return panelATT;
   }

   private JLabel createNewLabel(String str) {
      JLabel rc = new JLabel(str);
      rc.setFont(symbolFont);
      return rc;
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      TraceHelper.text(this, "actionPerformed", e.toString());
      if (e.getSource() == this.btOK) {
         this.doDialogCancel();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void adjustmentValueChanged(AdjustmentEvent e) {
      this.handlerPhase.setValue((long)this.sbPhase.getValue());
      if (!e.getValueIsAdjusting()) {
         this.startGenerator();
      }

   }

   private void createDigitHandlers() {
      TraceHelper.entry(this, "createDigitHandlers");
      this.handlerFrequencyI = new VNADigitTextFieldHandler(this.dib.getMinFrequency(), this.dib.getMaxFrequency());
      this.handlerFrequencyQ = new VNADigitTextFieldHandler(this.dib.getMinFrequency(), this.dib.getMaxFrequency());
      this.handlerAttenuationI = new VNADigitTextFieldHandler(0L, 6020L);
      this.handlerAttenuationQ = new VNADigitTextFieldHandler(0L, 6020L);
      this.handlerPhase = new VNADigitTextFieldHandler(0L, 36000L);
      this.handlerAttenuationI.addChangeListener(this);
      this.handlerAttenuationQ.addChangeListener(this);
      this.handlerFrequencyI.addChangeListener(this);
      this.handlerFrequencyQ.addChangeListener(this);
      this.handlerPhase.addChangeListener(this);
      TraceHelper.exit(this, "createDigitHandlers");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.storeWindowPosition();
      this.storeWindowSize();
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.cfg.putLong("VNAGeneratorProDialog.handlerFrequencyQ", this.handlerFrequencyQ.getValue());
      this.cfg.putLong("VNAGeneratorProDialog.handlerFrequencyI", this.handlerFrequencyI.getValue());
      this.cfg.putInteger("VNAGeneratorProDialog.handlerAttenuationI", (int)this.handlerAttenuationI.getValue());
      this.cfg.putInteger("VNAGeneratorProDialog.handlerAttenuationQ", (int)this.handlerAttenuationQ.getValue());
      this.cfg.putInteger("VNAGeneratorProDialog.handlerPhase", (int)this.handlerPhase.getValue());
      this.cfg.putBoolean("VNAGeneratorProDialog.btLinkAtt", this.btLinkAtt.isSelected());
      this.cfg.putBoolean("VNAGeneratorProDialog.btLinkFrq", this.btLinkFrq.isSelected());
      this.stopGenerator();
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   private void doClickOnAirField(MouseEvent e) {
      TraceHelper.entry(this, "doMouseClickedOnFrequency");
      if (e.getButton() == 1) {
         if (this.onAir) {
            this.stopGenerator();
            this.onAir = false;
         } else {
            this.onAir = true;
            this.startGenerator();
         }

         this.updateOnAirField();
      }

      TraceHelper.exit(this, "doMouseClickedOnFrequency");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.handlerFrequencyI.setValue(this.cfg.getInteger("VNAGeneratorProDialog.handlerFrequencyI", this.dib.getMinFrequency()));
      this.handlerFrequencyQ.setValue(this.cfg.getInteger("VNAGeneratorProDialog.handlerFrequencyQ", this.dib.getMinFrequency()));
      this.handlerAttenuationI.setValue((long)this.cfg.getInteger("VNAGeneratorProDialog.handlerAttenuationI", 0));
      this.handlerAttenuationQ.setValue((long)this.cfg.getInteger("VNAGeneratorProDialog.handlerAttenuationQ", 0));
      this.handlerPhase.setValue((long)this.cfg.getInteger("VNAGeneratorProDialog.handlerPhase", 0));
      this.sbPhase.setValue((int)this.handlerPhase.getValue());
      this.btLinkAtt.setSelected(this.cfg.getBoolean("VNAGeneratorProDialog.btLinkAtt", false));
      this.btLinkFrq.setSelected(this.cfg.getBoolean("VNAGeneratorProDialog.btLinkFrq", false));
      this.updateOnAirField();
      this.addEscapeKey();
      TraceHelper.exit(this, "doInit");
   }

   protected void doProcessFKey(KeyEvent e) {
      TraceHelper.entry(this, "doProcessFKey");
      boolean ok = false;
      if (e.getKeyCode() == 115) {
         if (this.onAir) {
            this.stopGenerator();
            this.onAir = false;
         } else {
            this.onAir = true;
            this.startGenerator();
         }

         this.updateOnAirField();
         ok = true;
      } else {
         String sVal = this.txtVALUE.getText();
         if (sVal != null) {
            sVal = sVal.trim();
            if (sVal.length() > 0) {
               sVal = sVal.toUpperCase();
               int factor;
               if (sVal.endsWith("K")) {
                  factor = 1000;
                  sVal = sVal.substring(0, sVal.length() - 1);
               } else if (sVal.endsWith("M")) {
                  factor = 1000000;
                  sVal = sVal.substring(0, sVal.length() - 1);
               } else {
                  factor = 1;
               }

               try {
                  double dVal = NumberFormat.getInstance().parse(sVal).doubleValue();
                  dVal *= (double)factor;
                  switch(e.getKeyCode()) {
                  case 116:
                     this.handlerFrequencyI.setValue((long)((int)dVal));
                     ok = true;
                     break;
                  case 117:
                     this.handlerFrequencyQ.setValue((long)((int)dVal));
                     ok = true;
                     break;
                  case 118:
                     this.handlerAttenuationI.setValue((long)((int)(dVal * 100.0D)));
                     ok = true;
                     break;
                  case 119:
                     this.handlerAttenuationQ.setValue((long)((int)(dVal * 100.0D)));
                     ok = true;
                     break;
                  case 120:
                     int val = (int)(dVal * 100.0D);
                     this.handlerPhase.setValue((long)val);
                     this.sbPhase.setValue(val);
                     ok = true;
                  }
               } catch (Exception var8) {
               }
            }
         }

         if (ok) {
            this.startGenerator();
            this.txtVALUE.select(0, 999);
         } else {
            Toolkit.getDefaultToolkit().beep();
         }
      }

      TraceHelper.exit(this, "doProcessFKey");
   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      TraceHelper.entry(this, "lostOwnership");
      TraceHelper.exit(this, "lostOwnership");
   }

   public void restoreWindowPosition() {
      TraceHelper.entry(this, "restoreWindowPosition");
      this.cfg.restoreWindowPosition("VNAGeneratorProDialog", this, new Point(10, 10));
      TraceHelper.exit(this, "restoreWindowPosition");
   }

   public void restoreWindowSize() {
      TraceHelper.entry(this, "restoreWindowSize");
      this.cfg.restoreWindowSize("VNAGeneratorProDialog", this, new Dimension(880, 450));
      TraceHelper.exit(this, "restoreWindowSize");
   }

   public void showInPlace() {
      TraceHelper.entry(this, "showInPlace");
      this.restoreWindowPosition();
      this.pack();
      this.restoreWindowSize();
      this.setVisible(true);
      TraceHelper.exit(this, "showInPlace");
   }

   private void startGenerator() {
      TraceHelper.entry(this, "startGenerator");
      if (this.onAir) {
         this.btOK.setEnabled(false);
         long frequencyI = this.handlerFrequencyI.getValue();
         long frequencyQ = this.handlerFrequencyQ.getValue();
         int attentuationI = (int)this.handlerAttenuationI.getValue();
         int attenuationQ = (int)this.handlerAttenuationQ.getValue();
         int phase = (int)this.handlerPhase.getValue();
         byte mainAttenuation = 0;

         try {
            this.driver.startGenerator(frequencyI, frequencyQ, attentuationI, attenuationQ, phase, mainAttenuation);
         } catch (ProcessingException var10) {
            ErrorLogHelper.exception(this, "startGenerator", var10);
         }

         this.btOK.setEnabled(true);
      }

      TraceHelper.exit(this, "startGenerator");
   }

   public void stateChanged(ChangeEvent e) {
      TraceHelper.entry(this, "stateChanged", "" + e);
      VNADigitTextFieldHandler handler = (VNADigitTextFieldHandler)e.getSource();
      long delta = handler.getValue() - handler.getOldValue();
      if (handler == this.handlerFrequencyQ) {
         if (this.btLinkFrq.isSelected()) {
            this.handlerFrequencyI.setValue(this.handlerFrequencyI.getValue() + delta);
         }

         this.startGenerator();
      } else if (handler == this.handlerFrequencyI) {
         if (this.btLinkFrq.isSelected()) {
            this.handlerFrequencyQ.setValue(this.handlerFrequencyQ.getValue() + delta);
         }

         this.startGenerator();
      } else if (handler == this.handlerAttenuationI) {
         if (this.btLinkAtt.isSelected()) {
            this.handlerAttenuationQ.setValue(this.handlerAttenuationQ.getValue() + delta);
         }

         this.startGenerator();
      } else if (handler == this.handlerAttenuationQ) {
         if (this.btLinkAtt.isSelected()) {
            this.handlerAttenuationI.setValue(this.handlerAttenuationI.getValue() + delta);
         }

         this.startGenerator();
      } else if (handler == this.handlerPhase) {
         this.startGenerator();
      }

      TraceHelper.exit(this, "stateChanged");
   }

   private void stopGenerator() {
      TraceHelper.entry(this, "stopGenerator");
      this.btOK.setEnabled(false);

      try {
         this.driver.stopGenerator();
      } catch (ProcessingException var2) {
         ErrorLogHelper.exception(this, "stopGenerator", var2);
      }

      this.btOK.setEnabled(true);
      TraceHelper.exit(this, "stopGenerator");
   }

   public void storeWindowPosition() {
      TraceHelper.entry(this, "storeWindowPosition");
      this.cfg.storeWindowPosition("VNAGeneratorProDialog", this);
      TraceHelper.exit(this, "storeWindowPosition");
   }

   public void storeWindowSize() {
      TraceHelper.entry(this, "storeWindowSize");
      this.cfg.storeWindowSize("VNAGeneratorProDialog", this);
      TraceHelper.exit(this, "storeWindowSize");
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

   public void windowOpened(WindowEvent e) {
      TraceHelper.entry(this, "windowOpened");
      super.windowOpened(e);
      this.txtVALUE.requestFocus();
      TraceHelper.exit(this, "windowOpened");
   }
}
