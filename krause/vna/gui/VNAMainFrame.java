package krause.vna.gui;

import com.apple.eawt.AppEventListener;
import com.l2fprod.common.swing.StatusBar;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import krause.common.exception.ProcessingException;
import krause.util.PropertiesHelper;
import krause.util.ResourceLoader;
import krause.util.ras.logging.ApplicationLogHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverFactory;
import krause.vna.gui.mac.MacApplicationHandler;
import krause.vna.gui.menu.VNAMenuBar;
import krause.vna.gui.panels.VNADiagramPanel;
import krause.vna.gui.panels.data.VNADataPanel;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.toolbar.VNAToolbar;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAMainFrame implements ClipboardOwner, AppEventListener {
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNAApplicationState applicationState = new VNAApplicationState(this);
   private VNADataPanel dataPanel = null;
   private JFrame mainFrame = null;
   private VNAMenuAndToolbarHandler menuAndToolbarHandler = null;
   private VNADiagramPanel diagramPanel = null;
   private VNAMarkerPanel markerPanel = null;
   private StatusBar statusBar = null;
   private VNAToolbar toolbar;
   private VNAMenuBar menubar;

   protected VNAMainFrame() {
      ApplicationLogHelper.text(this, "VNAMainFrame", "Setting up instance...");
      this.createAndShowGUI();
      this.applicationState.evtGUIInitialzed();
      ApplicationLogHelper.text(this, "VNAMainFrame", "Instance setup done!");
   }

   private void addHotKey(String key, Action action) {
      JRootPane rp = this.getJFrame().getRootPane();
      InputMap inputMap = rp.getInputMap(2);
      inputMap.put(KeyStroke.getKeyStroke(key), key);
      rp.getActionMap().put(key, action);
   }

   private void addHotKeys() {
      TraceHelper.entry(this, "addHotKeys");
      this.addHotKey("F2", new AbstractAction() {
         public void actionPerformed(ActionEvent arg0) {
            VNAMainFrame.this.getDataPanel().getTxtStartFreq().requestFocus();
         }
      });
      this.addHotKey("F3", new AbstractAction() {
         public void actionPerformed(ActionEvent arg0) {
            VNAMainFrame.this.getDataPanel().getTxtStopFreq().requestFocus();
         }
      });
      this.addHotKey("F4", new AbstractAction() {
         public void actionPerformed(ActionEvent arg0) {
            VNAMainFrame.this.getDataPanel().getCbMode().requestFocus();
         }
      });
      TraceHelper.exit(this, "addHotKeys");
   }

   public void changedMode() {
      TraceHelper.entry(this, "changedMode");
      if (this.datapool.getScanMode() != null) {
         this.datapool.setMainCalibrationBlock(this.datapool.getMainCalibrationBlockForMode(this.datapool.getScanMode()));
         if (this.datapool.getMainCalibrationBlock() == null) {
            JOptionPane.showMessageDialog(this.getJFrame(), VNAMessages.getString("Message.ModeChange.1"), VNAMessages.getString("Message.ModeChange.2"), 1);
            this.applicationState.evtCalibrationUnloaded();
         } else {
            this.datapool.clearResizedCalibrationBlock();
            this.applicationState.evtCalibrationLoaded();
         }
      }

      TraceHelper.exit(this, "changedMode");
   }

   private void createAndShowGUI() {
      this.menuAndToolbarHandler = new VNAMenuAndToolbarHandler(this);
      this.createGUIComponents();
      this.verifyTTL();
      this.addHotKeys();
      this.getJFrame().pack();
      this.config.restoreWindowPosition("MainWindow", this.getJFrame(), new Point(10, 10));
      this.config.restoreWindowSize("MainWindow", this.getJFrame(), new Dimension(1000, 600));
      this.getJFrame().setVisible(true);
      this.getStatusBarStatus().setText(MessageFormat.format(VNAMessages.getString("Application.welcome"), VNAMessages.getString("Application.version"), VNAMessages.getString("Application.date"), VNAMessages.getString("Application.copyright")));
   }

   private void verifyTTL() {
      if (VNAMessages.getString("Application.version").endsWith("pre")) {
         GregorianCalendar current = new GregorianCalendar();
         GregorianCalendar expiration = new GregorianCalendar(2021, 5, 30);
         if (current.after(expiration)) {
            Object[] options = new Object[]{VNAMessages.getString("Button.Terminate")};
            JOptionPane.showOptionDialog(this.getJFrame(), "Please use an official version", "vna/J - License for preview version expired", 0, 0, (Icon)null, options, options[0]);
            System.exit(1);
         }
      }

   }

   private VNADataPanel createDataPanel() {
      TraceHelper.entry(this, "createDataPanel");
      VNADataPanel rc = new VNADataPanel(this);
      rc.load();
      this.applicationState.addStateListener(rc);
      TraceHelper.exit(this, "createDataPanel");
      return rc;
   }

   private VNADiagramPanel createDiagramPanel() {
      TraceHelper.entry(this, "createDiagramPanel");
      VNADiagramPanel rc = new VNADiagramPanel(this);
      this.applicationState.addStateListener(rc);
      TraceHelper.exit(this, "createDiagramPanel");
      return rc;
   }

   private void createGUIComponents() {
      String methodName = "createGUIComponents";
      TraceHelper.entry(this, "createGUIComponents");
      String title = MessageFormat.format(VNAMessages.getString("Application.header"), VNAMessages.getString("Application.version"), System.getProperty("java.version"));
      JFrame frame = new JFrame(title);
      frame.setDefaultCloseOperation(0);
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            VNAMainFrame.this.doShutdown();
         }

         public void windowDeiconified(WindowEvent e) {
         }

         public void windowIconified(WindowEvent e) {
         }
      });
      if (this.config.isMac()) {
         TraceHelper.text(this, "createGUIComponents", "running on OS X");
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", title);
         new MacApplicationHandler(this);
      }

      frame.setResizable(!this.config.isResizeLocked());

      try {
         byte[] iconBytes = ResourceLoader.getResourceAsByteArray("images/logo.gif");
         Image img = Toolkit.getDefaultToolkit().createImage(iconBytes);
         frame.setIconImage(img);
      } catch (IOException var6) {
         ErrorLogHelper.exception(SwingUtil.class, "createGUIComponents", var6);
      }

      this.setJFrame(frame);
      if (this.config.isMac()) {
         frame.setLayout(new MigLayout("", "[grow,fill][300px]", "[][grow,fill][][]"));
      } else {
         frame.setLayout(new MigLayout("", "[grow,fill][250px]", "[][grow,fill][][]"));
      }

      this.toolbar = new VNAToolbar(this.menuAndToolbarHandler);
      this.applicationState.addStateListener(this.toolbar);
      frame.add(this.toolbar, "span 2,wrap,grow");
      this.diagramPanel = this.createDiagramPanel();
      this.dataPanel = this.createDataPanel();
      this.markerPanel = this.createMarkerPanel();
      this.statusBar = createStatusPanel();
      this.menubar = new VNAMenuBar(this.menuAndToolbarHandler, this.getStatusBarStatus());
      frame.setJMenuBar(this.menubar);
      this.applicationState.addStateListener(this.menubar);
      frame.add(this.diagramPanel, "");
      frame.add(this.dataPanel, "span 1 2,top,wrap,growy");
      frame.add(this.markerPanel, "wrap");
      frame.add(this.statusBar, "span 2,grow");
      TraceHelper.exit(this, "createGUIComponents");
   }

   private VNAMarkerPanel createMarkerPanel() {
      TraceHelper.entry(this, "createMarkerPanel");
      VNAMarkerPanel rc = new VNAMarkerPanel(this);
      this.applicationState.addStateListener(rc);
      rc.setVisible(true);
      TraceHelper.exit(this, "createMarkerPanel");
      return rc;
   }

   private static StatusBar createStatusPanel() {
      StatusBar rc = new StatusBar();
      rc.addZone("status", new StatusBarLabel(VNAMessages.getString("Message.Ready"), 50), "30%");
      rc.addZone("driver", new StatusBarLabel("???", 20), "15%");
      rc.addZone("calibStatus", new StatusBarLabel("???", 20), "10%");
      rc.addZone("calibFile", new StatusBarLabel("???", 80), "*");
      return rc;
   }

   public void doShutdown() {
      TraceHelper.entry(this, "doShutdown");
      this.config.storeWindowPosition("MainWindow", this.getJFrame());
      this.config.storeWindowSize("MainWindow", this.getJFrame());
      if (this.config.isAskOnExit()) {
         Object[] options = new Object[]{VNAMessages.getString("Button.Terminate"), VNAMessages.getString("Button.Cancel")};
         int n = JOptionPane.showOptionDialog(this.getJFrame(), VNAMessages.getString("Message.Exit.1"), VNAMessages.getString("Message.Exit.2"), 0, 3, (Icon)null, options, options[0]);
         if (n != 0) {
            return;
         }
      }

      this.getDataPanel().save();
      this.datapool.save(this.config);
      this.config.save();
      if (this.datapool.getDriver() != null) {
         this.datapool.getDriver().destroy();
      }

      TraceHelper.exit(this, "doShutdown");
      System.exit(0);
   }

   public VNAApplicationState getApplicationState() {
      return this.applicationState;
   }

   public VNADataPanel getDataPanel() {
      return this.dataPanel;
   }

   public VNADiagramPanel getDiagramPanel() {
      return this.diagramPanel;
   }

   public JFrame getJFrame() {
      return this.mainFrame;
   }

   public VNAMarkerPanel getMarkerPanel() {
      return this.markerPanel;
   }

   public VNAMenuAndToolbarHandler getMenuAndToolbarHandler() {
      return this.menuAndToolbarHandler;
   }

   public VNAMenuBar getMenubar() {
      return this.menubar;
   }

   public JLabel getStatusBarCalibrationFilename() {
      return (JLabel)this.statusBar.getZone("calibFile");
   }

   public JLabel getStatusBarCalibrationStatus() {
      return (JLabel)this.statusBar.getZone("calibStatus");
   }

   public JLabel getStatusBarDriverType() {
      return (JLabel)this.statusBar.getZone("driver");
   }

   public JLabel getStatusBarStatus() {
      return (JLabel)this.statusBar.getZone("status");
   }

   public VNAToolbar getToolbar() {
      return this.toolbar;
   }

   public boolean loadDriver() {
      TraceHelper.entry(this, "loadDriver");
      boolean rc = false;

      try {
         IVNADriver drv = VNADriverFactory.getSingleton().getDriverForType(this.datapool.getDeviceType());
         this.datapool.setDriver(drv);
         this.datapool.setScanMode(drv.getDefaultMode());
         drv.init();
         this.applicationState.evtDriverLoaded();
         rc = true;
      } catch (ProcessingException var3) {
         ErrorLogHelper.exception(this, "setDeviceType", var3);
         OptionDialogHelper.showExceptionDialog(this.getJFrame(), "VNAMainFrame.Error.loadDriver.1", "VNAMainFrame.Error.loadDriver.2", var3);
      }

      TraceHelper.exit(this, "loadDriver");
      return rc;
   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
   }

   public void preloadCalibrationBlocks() {
      String methodName = "preloadCalibrationBlocks";
      TraceHelper.entry(this, "preloadCalibrationBlocks");

      try {
         VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
         VNACalibrationKit kit = this.datapool.getCalibrationKit();
         String configPath = "CalibrationBlocks." + this.datapool.getDriver().getDeviceInfoBlock().getShortName() + ".";
         Properties props = PropertiesHelper.createProperties(this.config, configPath, true);
         Enumeration enu = props.keys();

         while(enu.hasMoreElements()) {
            String key = (String)enu.nextElement();
            String filename = props.getProperty(key);
            String pathname = this.config.getVNACalibrationDirectory() + System.getProperty("file.separator") + filename;
            TraceHelper.text(this, "preloadCalibrationBlocks", "Try to load[" + pathname + "]");
            File file = new File(pathname);
            VNACalibrationBlock block = VNACalibrationBlockHelper.load(file, this.datapool.getDriver(), kit);
            if (block.blockMatches(dib)) {
               this.datapool.setMainCalibrationBlockForMode(block);
            }
         }
      } catch (ProcessingException var12) {
         ErrorLogHelper.exception(this, "preloadCalibrationBlocks", var12);
      }

      TraceHelper.exit(this, "preloadCalibrationBlocks");
   }

   public void setJFrame(JFrame mainFrame) {
      this.mainFrame = mainFrame;
   }

   public void setMainCalibrationBlock(VNACalibrationBlock pMainCalibrationBlock) {
      TraceHelper.entry(this, "setMainCalibrationBlock");
      if (this.datapool.getMainCalibrationBlock() != null) {
         this.datapool.clearResizedCalibrationBlock();
         this.applicationState.evtCalibrationUnloaded();
      }

      this.datapool.setMainCalibrationBlock(pMainCalibrationBlock);
      if (this.datapool.getMainCalibrationBlock() != null) {
         this.datapool.setMainCalibrationBlockForMode(pMainCalibrationBlock);
         this.datapool.clearResizedCalibrationBlock();
         this.storePreloadCalibrationBlocks();
         this.applicationState.evtCalibrationLoaded();
      }

      TraceHelper.exit(this, "setMainCalibrationBlock");
   }

   private void storePreloadCalibrationBlocks() {
      TraceHelper.entry(this, "storePreloadCalibrationBlocks");
      Map<String, VNACalibrationBlock> calBlocks = this.datapool.getMainCalibrationBlocks();
      String configPath = "CalibrationBlocks." + this.datapool.getDriver().getDeviceInfoBlock().getShortName() + ".";
      Iterator var4 = calBlocks.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<String, VNACalibrationBlock> blockEntry = (Entry)var4.next();
         VNACalibrationBlock block = (VNACalibrationBlock)blockEntry.getValue();
         String configKey = configPath + block.getScanMode().key();
         if (block.getFile() != null) {
            this.config.put(configKey, block.getFile().getName());
         } else {
            this.config.remove(configKey);
         }
      }

      TraceHelper.exit(this, "storePreloadCalibrationBlocks");
   }

   public void unloadDriver() {
      TraceHelper.entry(this, "unloadDriver");
      if (this.datapool.getMainCalibrationBlock() != null) {
         this.datapool.clearCalibrationBlocks();
      }

      if (this.datapool.getDriver() != null) {
         this.datapool.getDriver().destroy();
         this.datapool.setDriver((IVNADriver)null);
      }

      this.applicationState.evtDriverUnloaded();
      TraceHelper.exit(this, "unloadDriver");
   }
}
