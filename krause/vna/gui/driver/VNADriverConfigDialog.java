package krause.vna.gui.driver;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverFactory;
import krause.vna.device.VNADriverNameComparator;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADriverConfigDialog extends KrauseDialog implements IVNABackgroundTaskStatusListener {
   private static VNAConfig config = VNAConfig.getSingleton();
   private static VNADataPool datapool = VNADataPool.getSingleton();
   private JButton btOK;
   private JButton btTest;
   private JList driverList;
   private JList portList;
   private IVNADriver selectedDriver = null;
   private String selectedPort = null;
   private JLabel statusBar;
   private VNAMainFrame mainFrame;
   private IVNADriver currentlyLoadedDriver = null;
   private JCheckBox cbNoFilter;
   private JCheckBox cbWireless;

   public VNADriverConfigDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      this.mainFrame = pMainFrame;
      TraceHelper.entry(this, "VNADriverConfigDialog");
      this.setResizable(true);
      this.setConfigurationPrefix("VNADriverConfigDialog");
      this.setProperties(config);
      this.setTitle(VNAMessages.getString("VNADriverConfigDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setLayout(new MigLayout("", "[][grow,fill]", "[grow,fill][][]"));
      this.setPreferredSize(new Dimension(720, 370));
      this.add(this.createDriverPanel(), "");
      this.add(this.createPortPanel(), "wrap");
      this.add(this.createStatusPanel(), "span 2,grow,wrap");
      this.add(this.createButtonPanel(), "span 2,grow,wrap");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNADriverConfigDialog");
   }

   private Component createStatusPanel() {
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", "[grow,fill]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNADriverConfigDialog.status"), 4, 2, (Font)null, (Color)null));
      this.statusBar = new JLabel();
      rc.add(this.statusBar, "");
      return rc;
   }

   private Component createButtonPanel() {
      JPanel rc = new JPanel(new MigLayout("", "[grow][grow][grow][grow][grow]", "[]"));
      JButton btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverConfigDialog.this.doDialogCancel();
         }
      });
      rc.add(btCancel, "left");
      this.cbNoFilter = SwingUtil.createJCheckbox("Button.Filter", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverConfigDialog.this.doFilter();
         }
      });
      rc.add(this.cbNoFilter, "left");
      this.cbWireless = SwingUtil.createJCheckbox("Button.Wireless", (ActionListener)null);
      rc.add(this.cbWireless, "left");
      this.btTest = SwingUtil.createJButton("Button.Test", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverConfigDialog.this.doTest();
         }
      });
      this.btTest.setEnabled(false);
      rc.add(this.btTest, "center");
      rc.add(new HelpButton(this, "VNADriverConfigDialog"), "center");
      this.btOK = SwingUtil.createJButton("Button.Update", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNADriverConfigDialog.this.doOK();
         }
      });
      this.btOK.setEnabled(false);
      rc.add(this.btOK, "right");
      return rc;
   }

   private void fillDriverList() {
      TraceHelper.entry(this, "fillDriverList");
      List<IVNADriver> availableDrivers = VNADriverFactory.getSingleton().getDriverList();
      Collections.sort(availableDrivers, new VNADriverNameComparator());
      Vector<String> drvVector = new Vector(availableDrivers.size());
      Iterator var4 = availableDrivers.iterator();

      while(var4.hasNext()) {
         IVNADriver driver = (IVNADriver)var4.next();
         drvVector.add(driver.getDeviceInfoBlock().getShortName());
      }

      this.driverList.setListData(drvVector);
      TraceHelper.exit(this, "fillDriverList");
   }

   private Component createDriverPanel() {
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", "[grow,fill][]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNADriverConfigDialog.drvList"), 4, 2, (Font)null, (Color)null));
      this.driverList = new JList();
      this.driverList.setVisibleRowCount(6);
      this.driverList.setSelectionMode(0);
      this.driverList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            VNADriverConfigDialog.this.handleDriverListSelection(e);
         }
      });
      JScrollPane sp = new JScrollPane(this.driverList);
      rc.add(sp, "wrap");
      rc.add(new JLabel(VNAMessages.getString("VNADriverConfigDialog.drvUsage")), "");
      return rc;
   }

   private Component createPortPanel() {
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", "[grow,fill][]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNADriverConfigDialog.portLIst"), 4, 2, (Font)null, (Color)null));
      this.portList = new JList();
      this.portList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            VNADriverConfigDialog.this.handlePortListSelection(e);
         }
      });
      JScrollPane sp = new JScrollPane(this.portList);
      rc.add(sp, "wrap");
      rc.add(new JLabel(VNAMessages.getString("VNADriverConfigDialog.portUsage")), "");
      return rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      if (this.currentlyLoadedDriver != null) {
         this.mainFrame.loadDriver();
      }

      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.fillDriverList();
      this.currentlyLoadedDriver = this.selectedDriver = datapool.getDriver();
      if (this.currentlyLoadedDriver != null) {
         this.mainFrame.unloadDriver();
         this.driverList.setSelectedValue(this.currentlyLoadedDriver.getDeviceInfoBlock().getShortName(), true);
      }

      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      config.setPortName(this.selectedDriver, this.selectedPort);
      datapool.setDeviceType(this.selectedDriver.getDeviceInfoBlock().getType());
      this.mainFrame.loadDriver();
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doOK");
   }

   protected void doFilter() {
      this.handleDriverListSelection((ListSelectionEvent)null);
   }

   private void fillPortList() throws ProcessingException {
      TraceHelper.entry(this, "fillPortList");
      List<String> ports = this.selectedDriver.getPortList();
      Vector<String> portVector = new Vector(ports.size());
      boolean filter = !this.cbNoFilter.isSelected();
      if (filter) {
         TraceHelper.text(this, "fillPortList", "Filtering serial ports ...");
      }

      Iterator var5 = ports.iterator();

      while(var5.hasNext()) {
         String port = (String)var5.next();
         if (filter) {
            boolean useIt = false;
            if (config.isMac()) {
               if (port.startsWith("cu")) {
                  useIt = true;
               }
            } else if (config.isWindows()) {
               useIt = true;
            } else if (!"tty".equals(port)) {
               if (port.startsWith("tty")) {
                  String sName = port.substring(3);

                  try {
                     Integer.parseInt(sName);
                  } catch (NumberFormatException var9) {
                     useIt = true;
                  }
               } else {
                  useIt = true;
               }
            }

            if (useIt) {
               portVector.add(port);
            } else {
               TraceHelper.text(this, "fillPortList", "port [" + port + " filtered");
            }
         } else {
            portVector.add(port);
         }
      }

      Collections.sort(portVector);
      this.portList.setListData(portVector);
      TraceHelper.exit(this, "fillPortList");
   }

   protected void handleDriverListSelection(ListSelectionEvent e) {
      TraceHelper.entry(this, "handleDriverListSelection");
      if (e == null || !e.getValueIsAdjusting()) {
         String selDrv = (String)this.driverList.getSelectedValue();
         TraceHelper.text(this, "handleDriverListSelection", "drv=" + selDrv);

         try {
            this.btOK.setEnabled(false);
            this.selectedDriver = VNADriverFactory.getSingleton().getDriverForShortName(selDrv);
            if (this.selectedDriver != null) {
               this.fillPortList();
               this.selectedPort = config.getPortName(this.selectedDriver);
               if (this.selectedPort != null) {
                  this.portList.setSelectedValue(this.selectedPort, true);
               }
            }
         } catch (ProcessingException var4) {
         }

         this.setStatusbar(Color.RED, "");
         TraceHelper.exit(this, "handleDriverListSelection");
      }
   }

   protected void handlePortListSelection(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
         TraceHelper.entry(this, "handlePortListSelection");
         this.selectedPort = (String)this.portList.getSelectedValue();
         TraceHelper.text(this, "handlePortListSelection", "drv=" + this.selectedPort);
         this.btTest.setEnabled(this.selectedPort != null);
         this.setStatusbar(Color.RED, "");
         TraceHelper.exit(this, "handlePortListSelection");
      }
   }

   private void setStatusbar(Color col, String text) {
      this.statusBar.setBackground(col);
      this.statusBar.setOpaque(true);
      this.statusBar.setText(text);
   }

   private void doTest() {
      TraceHelper.entry(this, "doTest");
      String oldPortName = null;
      this.btOK.setEnabled(false);
      this.btTest.setEnabled(false);
      oldPortName = config.getPortName(this.selectedDriver);
      config.setPortName(this.selectedDriver, this.selectedPort);
      if (this.selectedDriver.checkForDevicePresence(this.cbWireless.isSelected())) {
         this.setStatusbar(Color.GREEN, VNAMessages.getString("VNADriverConfigDialog.statusOK"));
         this.btOK.setEnabled(true);
      } else {
         this.setStatusbar(Color.RED, VNAMessages.getString("VNADriverConfigDialog.statusFAIL1"));
      }

      this.btTest.setEnabled(true);
      if (oldPortName != null) {
         config.setPortName(this.selectedDriver, oldPortName);
      }

      TraceHelper.exit(this, "doTest");
   }

   public void publishProgress(int percentage) {
   }
}
