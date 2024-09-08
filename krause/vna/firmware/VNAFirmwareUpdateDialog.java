package krause.vna.firmware;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class VNAFirmwareUpdateDialog extends KrauseDialog implements ActionListener, IVNABackgroundFlashBurnerConsumer {
   private VNAConfig config = VNAConfig.getSingleton();
   private JTextField txtFilename;
   private JButton btFlash;
   private JButton btClose;
   private JButton btSearch;
   private FirmwareFileParser flashFileParser = null;
   private SimpleStringListbox messageList;
   private VnaBackgroundFlashBurner backgroundBurner;
   private JCheckBox cbAutoReset;
   private final IVNAFlashableDevice currentDriver = (IVNAFlashableDevice)VNADataPool.getSingleton().getDriver();
   private long startTime;

   public VNAFirmwareUpdateDialog(Frame owner) {
      super((Window)owner, true);
      TraceHelper.exit(this, "VNAFirmwareUpdateDialog");
      this.setResizable(true);
      this.setPreferredSize(new Dimension(600, 400));
      this.setDefaultCloseOperation(0);
      this.setConfigurationPrefix("VNAFirmwareUpdateDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNAFirmwareUpdateDialog.title"));
      this.setLayout(new MigLayout("", "[grow][][][]", ""));
      this.add(new JLabel(VNAMessages.getString("VNAFirmwareUpdateDialog.filename")), "left, span 4, wrap");
      this.add(this.txtFilename = new JTextField(128), "span 3");
      this.txtFilename.setEditable(false);
      this.add(this.btSearch = SwingUtil.createJButton("Button.Search", this), "right, wrap");
      this.messageList = new SimpleStringListbox(VNAMessages.getString("VNAFirmwareUpdateDialog.messages"));
      JScrollPane listScroller = new JScrollPane(this.messageList);
      listScroller.setPreferredSize(new Dimension(600, 800));
      this.add(listScroller, "span 4, wrap");
      this.add(this.btClose = SwingUtil.createJButton("Button.Close", this), "");
      this.add(this.cbAutoReset = SwingUtil.createJCheckbox("AutoReset", this), "left");
      this.add(new HelpButton(this, "VNAFirmwareUpdateDialog"), "");
      this.add(this.btFlash = SwingUtil.createJButton("Button.Install", this), "right,wrap");
      this.doDialogInit();
      TraceHelper.exit(this, "VNAFirmwareUpdateDialog");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.btFlash.setEnabled(false);
      this.cbAutoReset.setEnabled(this.currentDriver.hasResetButton());
      this.cbAutoReset.setSelected(this.currentDriver.supportsAutoReset());
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      String cmd = e.getActionCommand();
      if (VNAMessages.getString("Button.Close.Command").equals(cmd)) {
         this.doDialogCancel();
      } else if (VNAMessages.getString("Button.Install.Command").equals(cmd)) {
         this.doFlash();
      } else if (VNAMessages.getString("Button.Search.Command").equals(cmd)) {
         this.doSearch();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doFlash() {
      TraceHelper.entry(this, "doFlash");
      int rc = 1;
      if (this.cbAutoReset.isSelected()) {
         rc = JOptionPane.showConfirmDialog(this, VNAMessages.getString("VNAFirmwareUpdateDialog.warningNoReset"), VNAMessages.getString("VNAFirmwareUpdateDialog.title"), 2, 2);
      } else if (this.currentDriver.hasResetButton()) {
         rc = JOptionPane.showConfirmDialog(this, VNAMessages.getString("VNAFirmwareUpdateDialog.warning"), VNAMessages.getString("VNAFirmwareUpdateDialog.title"), 2, 2);
      } else {
         rc = JOptionPane.showConfirmDialog(this, VNAMessages.getString("VNAFirmwareUpdateDialog.warningNoReset"), VNAMessages.getString("VNAFirmwareUpdateDialog.title"), 2, 2);
      }

      if (rc == 0) {
         this.btClose.setEnabled(false);
         this.btFlash.setEnabled(false);
         this.btSearch.setEnabled(false);
         this.startTime = System.currentTimeMillis();
         this.backgroundBurner = new VnaBackgroundFlashBurner();
         this.backgroundBurner.setDataConsumer(this);
         this.backgroundBurner.setListbox(this.messageList);
         this.backgroundBurner.setFlashFile(this.flashFileParser);
         this.backgroundBurner.setAutoReset(this.cbAutoReset.isSelected());
         this.backgroundBurner.setDriver(VNADataPool.getSingleton().getDriver());
         this.backgroundBurner.execute();
      } else {
         this.messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.UploadCanceled"));
      }

      TraceHelper.exit(this, "doFlash");
   }

   public void consumeReturnCode(Integer rc) {
      TraceHelper.entry(this, "consumeReturnCode");
      this.btClose.setEnabled(true);
      this.btFlash.setEnabled(true);
      this.btSearch.setEnabled(true);
      if (rc == 0) {
         long duration = (System.currentTimeMillis() - this.startTime) / 1000L;
         this.messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.UploadOK") + duration + "s");
      } else {
         this.messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.UploadFailed"));
      }

      TraceHelper.exit(this, "consumeReturnCode");
   }

   private void doSearch() {
      TraceHelper.entry(this, "doSearch");
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new FileFilter() {
         public String getDescription() {
            VNADeviceInfoBlock dib = VNADataPool.getSingleton().getDriver().getDeviceInfoBlock();
            return "Firmware (" + dib.getFirmwareFileFilter() + ")";
         }

         public boolean accept(File aFile) {
            if (aFile.isDirectory()) {
               return true;
            } else {
               VNADeviceInfoBlock dib = VNADataPool.getSingleton().getDriver().getDeviceInfoBlock();
               WildcardFileFilter wcff = new WildcardFileFilter(dib.getFirmwareFileFilter(), IOCase.SYSTEM);
               return wcff.accept(aFile);
            }
         }
      });
      fc.setCurrentDirectory(new File(this.config.getFlashFilename()));
      int returnVal = fc.showOpenDialog((Component)null);
      if (returnVal == 0) {
         File selectedFile = fc.getSelectedFile();
         String sf = selectedFile.getAbsolutePath();
         this.config.setFlashFilename(sf);
         this.txtFilename.setText(sf);
         this.messageList.clear();

         try {
            this.flashFileParser = new FirmwareFileParser(selectedFile);
            this.flashFileParser.parseFile();
            this.messageList.addMessage(VNAMessages.getString("VNAFirmwareUpdateDialog.FileLoaded"));
            if (this.flashFileParser.isIntelHexFile()) {
               this.messageList.addMessage("Flash start address 0x" + Integer.toHexString(this.flashFileParser.getFlashMin()));
               this.messageList.addMessage("Flash end   address 0x" + Integer.toHexString(this.flashFileParser.getFlashMax()));
               this.messageList.addMessage("Flash memory offset 0x" + Integer.toHexString(this.flashFileParser.getMemOffset()));
               this.messageList.addMessage("Flash memory size   0x" + Integer.toHexString(this.flashFileParser.getMemUsage()));
            } else {
               this.messageList.addMessage(this.flashFileParser.getFlash().length + " bytes read from file");
            }

            this.btFlash.setEnabled(true);
         } catch (ProcessingException var6) {
            this.messageList.addMessage(var6.getMessage());
            this.btFlash.setEnabled(false);
         }
      }

      TraceHelper.exit(this, "doSearch");
   }
}
