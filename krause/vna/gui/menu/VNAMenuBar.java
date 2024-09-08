package krause.vna.gui.menu;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMenuAndToolbarHandler;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNAMenuBar extends JMenuBar implements VNAApplicationStateObserver {
   private transient VNAMenuAndToolbarHandler handler = null;
   private JLabel statusBar = null;
   private VNAConfig config = VNAConfig.getSingleton();
   private JMenu menuAnalyser = null;
   private JMenu menuExport = null;
   private JMenu menuTools = null;
   private JMenu menuCalibrate = null;
   private JMenu menuFile = null;
   private JMenu menuHelp = null;
   private JMenu menuPreset = null;

   public VNAMenuBar(VNAMenuAndToolbarHandler pHandler, JLabel pStatusBar) {
      this.handler = pHandler;
      this.statusBar = pStatusBar;
      this.handler.setMenubar(this);
      this.add(this.menuFile = this.createFileMenu());
      this.add(this.menuTools = this.createToolsMenu());
      this.add(this.menuCalibrate = this.createCalibrationMenu());
      this.add(this.menuExport = this.createExportMenu());
      this.add(this.menuAnalyser = this.createAnalyserMenu());
      this.add(this.menuPreset = this.createPresetsMenu());
      this.add(Box.createHorizontalGlue());
      this.add(this.menuHelp = this.createHelpMenu());
   }

   private JMenu createPresetsMenu() {
      TraceHelper.entry(this, "createPresetsMenu");
      JMenu rc = SwingUtil.createJMenu("Menu.Presets", this.statusBar);
      rc.add(SwingUtil.createJMenuItem("Menu.Presets.Load", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Presets.Save", this.handler, this.statusBar));
      TraceHelper.exit(this, "createPresetsMenu");
      return rc;
   }

   public JMenu createExperimentalMenu() {
      String methodName = "createExperimentalMenu";
      TraceHelper.entry(this, "createExperimentalMenu");
      JMenu menu1 = SwingUtil.createJMenu("Menu.Experimental", this.statusBar);
      menu1.add(SwingUtil.createJMenuItem("Menu.Experimental.A", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Experimental.B", this.handler, this.statusBar));
      TraceHelper.exit(this, "createExperimentalMenu");
      return menu1;
   }

   private JMenu createExportMenu() {
      JMenu rc = SwingUtil.createJMenu("Menu.Export", this.statusBar);
      rc.add(SwingUtil.createJMenuItem("Menu.Export.CSV", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.JPG", this.handler, this.statusBar, KeyStroke.getKeyStroke(118, 0)));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.PDF", this.handler, this.statusBar, KeyStroke.getKeyStroke(119, 0)));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.S2P", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.S2PCollector", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.XLS", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.XML", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.ZPlot", this.handler, this.statusBar));
      rc.addSeparator();
      rc.add(SwingUtil.createJMenuItem("Menu.Export.Setting", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Export.AutoSetting", this.handler, this.statusBar));
      return rc;
   }

   private JMenu createToolsMenu() {
      JMenu menu1 = SwingUtil.createJMenu("Menu.Tools", this.statusBar);
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Beacon", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Cablelength", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.CableLoss", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Gaussian", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Generator", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Schedule.Execute", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Multitune", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.Padcalc", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Tools.FFT", this.handler, this.statusBar));
      return menu1;
   }

   private JMenu createCalibrationMenu() {
      JMenu menu1 = SwingUtil.createJMenu("Menu.Calibration", this.statusBar);
      menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Frequency", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Calibrate", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Load", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Import", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.Export", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Calibration.CalibrationSet", this.handler, this.statusBar));
      return menu1;
   }

   private JMenu createHelpMenu() {
      JMenu menu1 = SwingUtil.createJMenu("Menu.Help", this.statusBar);
      menu1.setMnemonic(VNAMessages.getString("MMenu.Help.Key").charAt(0));
      menu1.add(SwingUtil.createJMenuItem("Menu.Help.Readme", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Help.Support", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.Help.License", this.handler, this.statusBar));
      if (!this.config.isMac()) {
         menu1.addSeparator();
         menu1.add(SwingUtil.createJMenuItem("Menu.Help.About", this.handler, this.statusBar));
      }

      return menu1;
   }

   private JMenu createFileMenu() {
      JMenu menu1 = SwingUtil.createJMenu("Menu.File", this.statusBar);
      menu1.add(SwingUtil.createJMenuItem("Menu.Analysis", this.handler, this.statusBar));
      if (!this.config.isMac()) {
         menu1.add(SwingUtil.createJMenuItem("Menu.File.Settings", this.handler, this.statusBar));
      }

      menu1.add(SwingUtil.createJMenuItem("Menu.File.SettingsScales", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.File.Color", this.handler, this.statusBar));
      menu1.add(SwingUtil.createJMenuItem("Menu.File.Language", this.handler, this.statusBar));
      if (!this.config.isMac()) {
         menu1.addSeparator();
         menu1.add(SwingUtil.createJMenuItem("Menu.File.Exit", this.handler, this.statusBar));
      }

      return menu1;
   }

   private JMenu createAnalyserMenu() {
      TraceHelper.entry(this, "createAnalyserMenu");
      JMenu rc = SwingUtil.createJMenu("Menu.Analyser", this.statusBar);
      rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Setup", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Info", this.handler, this.statusBar));
      rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Reconnect", this.handler, this.statusBar, KeyStroke.getKeyStroke(116, 64)));
      rc.addSeparator();
      rc.add(SwingUtil.createJMenuItem("Menu.Tools.Firmware", this.handler, this.statusBar));
      rc.addSeparator();
      if (this.config.isMac()) {
         rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Single", this.handler, this.statusBar, KeyStroke.getKeyStroke(116, 0)));
         rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Free", this.handler, this.statusBar, KeyStroke.getKeyStroke(117, 0)));
      } else {
         rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Single", this.handler, this.statusBar, KeyStroke.getKeyStroke(123, 0)));
         rc.add(SwingUtil.createJMenuItem("Menu.Analyser.Free", this.handler, this.statusBar, KeyStroke.getKeyStroke(122, 0)));
      }

      TraceHelper.exit(this, "createAnalyserMenu");
      return rc;
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      if (newState == VNAApplicationState.INNERSTATE.DRIVERLOADED) {
         this.menuExport.setEnabled(false);
         this.menuTools.setEnabled(false);
         this.menuCalibrate.setEnabled(true);
         this.menuAnalyser.setEnabled(true);
         this.menuPreset.setEnabled(false);
      } else if (newState == VNAApplicationState.INNERSTATE.CALIBRATED) {
         this.menuExport.setEnabled(true);
         this.menuTools.setEnabled(true);
         this.menuCalibrate.setEnabled(true);
         this.menuAnalyser.setEnabled(true);
         this.menuPreset.setEnabled(true);
      } else if (newState == VNAApplicationState.INNERSTATE.RUNNING) {
         this.menuExport.setEnabled(false);
         this.menuTools.setEnabled(false);
         this.menuCalibrate.setEnabled(false);
         this.menuAnalyser.setEnabled(false);
         this.menuPreset.setEnabled(false);
      } else {
         this.menuExport.setEnabled(false);
         this.menuTools.setEnabled(false);
         this.menuCalibrate.setEnabled(false);
         this.menuAnalyser.setEnabled(true);
         this.menuPreset.setEnabled(false);
      }

   }
}
