package krause.vna.gui.beacon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormatSymbols;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.common.validation.IntegerValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.firmware.IVNABackgroundFlashBurnerConsumer;
import krause.vna.firmware.SimpleStringListbox;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNABeaconDialog extends KrauseDialog implements ActionListener, IVNABackgroundFlashBurnerConsumer {
   private static final String PROPERTIES_PREFIX = "VNABeaconDialog";
   public static final int FONT_SIZE = 30;
   private static Font symbolFont = new Font("Tahoma", 0, 30);
   private String groupSeparator = new String("" + DecimalFormatSymbols.getInstance().getGroupingSeparator());
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private IVNADriver driver;
   private VNADeviceInfoBlock dib;
   private JButton btOK;
   private JPanel contentPanel;
   private JLabel lblOnAir;
   private boolean onAir;
   private VNADigitTextFieldHandler handlerFrequencyI;
   private JTextArea txtText;
   private JTextField txtInterval;
   private JTextField txtBPM;
   private SimpleStringListbox messageList;
   private VnaBackgroundBeacon backgroundBeacon;

   public VNABeaconDialog(Window wnd) {
      super(wnd, true);
      this.driver = this.datapool.getDriver();
      this.dib = this.driver.getDeviceInfoBlock();
      this.onAir = false;
      this.setConfigurationPrefix("VNABeaconDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNABeaconDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(900, 500));
      this.addWindowListener(this);
      this.handlerFrequencyI = new VNADigitTextFieldHandler(this.dib.getMinFrequency(), this.dib.getMaxFrequency());
      this.getContentPane().setLayout(new BorderLayout(5, 5));
      this.contentPanel = new JPanel(new MigLayout("", "[left]0[grow, fill]0[right]", "[][grow][][][]"));
      this.getContentPane().add(this.contentPanel, "Center");
      this.contentPanel.add(this.createFREQPanel(), "span 3, center, grow, wrap");
      this.contentPanel.add(this.createTEXTPanel(), "span 3, center, grow, wrap");
      this.lblOnAir = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.text"));
      this.lblOnAir.setCursor(Cursor.getPredefinedCursor(12));
      this.lblOnAir.setOpaque(true);
      this.lblOnAir.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNABeaconDialog.this.doClickOnAirField(e);
         }
      });
      this.lblOnAir.setAlignmentX(0.5F);
      this.lblOnAir.setFont(new Font("Courier New", 0, 17));
      this.lblOnAir.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      this.lblOnAir.setToolTipText(VNAMessages.getString("VNABeaconDialog.OnAir"));
      this.contentPanel.add(this.lblOnAir, "span 3, center, grow, wrap");
      this.messageList = new SimpleStringListbox(VNAMessages.getString("VNABeaconDialog.Nachrichten"));
      JScrollPane listScroller = new JScrollPane(this.messageList);
      listScroller.setPreferredSize(new Dimension(600, 100));
      this.contentPanel.add(listScroller, "span 3, center, grow, wrap");
      this.btOK = new JButton(VNAMessages.getString("Button.Close"));
      this.btOK.addActionListener(this);
      this.contentPanel.add(this.btOK, "span 3, right");
      this.doDialogInit();
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      TraceHelper.text(this, "actionPerformed", e.toString());
      if (e.getSource() == this.btOK) {
         this.doDialogCancel();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.config.putInteger("VNABeaconDialog.Interval", Integer.parseInt(this.txtInterval.getText()));
      this.config.putInteger("VNABeaconDialog.BPM", Integer.parseInt(this.txtBPM.getText()));
      this.config.putLong("VNABeaconDialog.handlerFrequencyI", this.handlerFrequencyI.getValue());
      this.config.setProperty("VNABeaconDialog.text", this.txtText.getText());
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      if (this.backgroundBeacon != null) {
         this.backgroundBeacon.cancel(false);

         while(!this.backgroundBeacon.isDone()) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }
         }

         this.backgroundBeacon = null;
      }

      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   private void doClickOnAirField(MouseEvent e) {
      TraceHelper.entry(this, "doMouseClickedOnFrequency");
      if (e.getButton() == 1) {
         if (this.onAir) {
            this.beaconOff();
         } else {
            this.beaconOn();
         }

         this.updateOnAirField();
      }

      TraceHelper.exit(this, "doMouseClickedOnFrequency");
   }

   private void beaconOn() {
      TraceHelper.entry(this, "beaconOn");
      ValidationResults results = new ValidationResults();
      int interval = IntegerValidator.parse(this.txtInterval.getText(), 1, 999999999, VNAMessages.getString("VNABeaconDialog.Interval"), results);
      int bpm = IntegerValidator.parse(this.txtBPM.getText(), 1, 80, VNAMessages.getString("VNABeaconDialog.BPM"), results);
      if (results.isEmpty()) {
         this.txtBPM.setEnabled(false);
         this.txtInterval.setEnabled(false);
         this.txtText.setEnabled(false);
         this.btOK.setEnabled(false);
         this.backgroundBeacon = new VnaBackgroundBeacon();
         this.backgroundBeacon.setDriver(this.driver);
         this.backgroundBeacon.setDataConsumer(this);
         this.backgroundBeacon.setListbox(this.messageList);
         this.backgroundBeacon.setFrequency(this.handlerFrequencyI.getValue());
         this.backgroundBeacon.setPause(interval);
         this.backgroundBeacon.setBpm(bpm);
         this.backgroundBeacon.setMessage(this.txtText.getText());
         this.backgroundBeacon.execute();
         this.onAir = true;
      } else {
         new ValidationResultsDialog(this.getOwner(), results, this.getTitle());
      }

      TraceHelper.exit(this, "beaconOn");
   }

   private void beaconOff() {
      TraceHelper.entry(this, "beaconOff");
      this.backgroundBeacon.cancel(false);
      this.backgroundBeacon = null;
      this.onAir = false;
      TraceHelper.exit(this, "beaconOff");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.handlerFrequencyI.setValue(this.config.getInteger("VNABeaconDialog.handlerFrequencyI", this.dib.getMinFrequency()));
      this.txtInterval.setText("" + this.config.getInteger("VNABeaconDialog.Interval", 60));
      this.txtBPM.setText("" + this.config.getInteger("VNABeaconDialog.BPM", 60));
      this.txtText.setText(this.config.getProperty("VNABeaconDialog.text", "Test"));
      this.updateOnAirField();
      this.addEscapeKey();
      this.doDialogShow();
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

   private JPanel createTEXTPanel() {
      JPanel panelTEXT = new JPanel(new MigLayout("", "[center,fill,grow]", "[]"));
      panelTEXT.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNABeaconDialog.Panel"), 4, 2, (Font)null, (Color)null));
      panelTEXT.add(new JLabel(VNAMessages.getString("VNABeaconDialog.Text")), "");
      this.txtText = new JTextArea("", 3, 40);
      this.txtText.setFont(new Font("Courier New", 0, 12));
      this.txtText.setLineWrap(true);
      this.txtText.setWrapStyleWord(true);
      JScrollPane sp = new JScrollPane(this.txtText);
      panelTEXT.add(sp, "wrap");
      panelTEXT.add(new JLabel(VNAMessages.getString("VNABeaconDialog.Interval")), "");
      this.txtInterval = new JTextField(10);
      panelTEXT.add(this.txtInterval, "wrap");
      panelTEXT.add(new JLabel(VNAMessages.getString("VNABeaconDialog.BPM")), "");
      this.txtBPM = new JTextField(10);
      panelTEXT.add(this.txtBPM, "wrap");
      return panelTEXT;
   }

   private JPanel createFREQPanel() {
      JPanel panelFRQ = new JPanel(new MigLayout("", "[center,fill,grow]", ""));
      panelFRQ.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNABeaconDialog.Frequenz"), 4, 2, (Font)null, (Color)null));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1000000000, 0L, 30)));
      panelFRQ.add(this.createNewLabel(this.groupSeparator));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(100000000, 0L, 30)));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(10000000, 0L, 30)));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1000000, 0L, 30)));
      panelFRQ.add(this.createNewLabel(this.groupSeparator));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(100000, 0L, 30)));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(10000, 0L, 30)));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1000, 0L, 30)));
      panelFRQ.add(this.createNewLabel(this.groupSeparator));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(100, 0L, 30)));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(10, 0L, 30)));
      panelFRQ.add(this.handlerFrequencyI.registerField(new VNADigitTextField(1, 0L, 30)));
      panelFRQ.add(this.createNewLabel("Hz"));
      return panelFRQ;
   }

   private JLabel createNewLabel(String str) {
      JLabel rc = new JLabel(str);
      rc.setFont(symbolFont);
      return rc;
   }

   public void consumeReturnCode(Integer rc) {
      TraceHelper.entry(this, "consumeReturnCode");
      this.messageList.addMessage("... beacon mode ended");
      this.txtBPM.setEnabled(true);
      this.txtInterval.setEnabled(true);
      this.txtText.setEnabled(true);
      this.btOK.setEnabled(true);
      TraceHelper.exit(this, "consumeReturnCode");
   }
}
