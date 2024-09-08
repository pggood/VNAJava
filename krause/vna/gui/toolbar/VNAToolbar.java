package krause.vna.gui.toolbar;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMenuAndToolbarHandler;
import krause.vna.gui.util.SwingUtil;

public class VNAToolbar extends JToolBar implements VNAApplicationStateObserver {
   private transient VNAMenuAndToolbarHandler handler = null;
   private JButton tbLEN;
   private JButton tbGEN;
   private JButton tbXLS;
   private JButton tbCSV;
   private JButton tbPDF;
   private JButton tbJPG;
   private JButton tbCal;
   private JButton tbCalLoad;
   private JButton tbDriverInfo;
   private JButton tbScheduler;
   private JButton tbAnalysis;
   private JButton tbS2P;
   private JButton tbZPlots;
   private JButton tbMultitune;
   private JButton tbPadcalc;
   private JButton tbXML;

   public VNAToolbar(VNAMenuAndToolbarHandler pHandler) {
      this.setBorder((Border)null);
      this.setOpaque(false);
      this.setRollover(true);
      this.setFloatable(false);
      this.handler = pHandler;
      this.handler.setToolbar(this);
      this.add(this.tbLEN = SwingUtil.createToolbarButton("Menu.Tools.Cablelength", this.handler));
      this.add(this.tbGEN = SwingUtil.createToolbarButton("Menu.Tools.Generator", this.handler));
      this.add(this.tbScheduler = SwingUtil.createToolbarButton("Menu.Schedule.Execute", this.handler));
      this.add(this.tbAnalysis = SwingUtil.createToolbarButton("Menu.Analysis", this.handler));
      this.add(this.tbMultitune = SwingUtil.createToolbarButton("Menu.Multitune", this.handler));
      this.add(this.tbPadcalc = SwingUtil.createToolbarButton("Menu.Tools.Padcalc", this.handler));
      this.addSeparator();
      this.add(this.tbCal = SwingUtil.createToolbarButton("Menu.Calibration.Calibrate", this.handler));
      this.add(this.tbCalLoad = SwingUtil.createToolbarButton("Menu.Calibration.Load", this.handler));
      this.addSeparator();
      this.add(this.tbCSV = SwingUtil.createToolbarButton("Menu.Export.CSV", this.handler));
      this.add(this.tbJPG = SwingUtil.createToolbarButton("Menu.Export.JPG", this.handler));
      this.add(this.tbPDF = SwingUtil.createToolbarButton("Menu.Export.PDF", this.handler));
      this.add(this.tbS2P = SwingUtil.createToolbarButton("Menu.Export.S2P", this.handler));
      this.add(this.tbXLS = SwingUtil.createToolbarButton("Menu.Export.XLS", this.handler));
      this.add(this.tbXML = SwingUtil.createToolbarButton("Menu.Export.XML", this.handler));
      this.add(this.tbZPlots = SwingUtil.createToolbarButton("Menu.Export.ZPlot", this.handler));
      this.addSeparator();
      this.add(this.tbDriverInfo = SwingUtil.createToolbarButton("Menu.Analyser.Info", this.handler));
      this.add(Box.createHorizontalGlue());
      this.add(SwingUtil.createToolbarButton("Menu.File.SettingsScales", this.handler));
      this.add(SwingUtil.createToolbarButton("Menu.File.Settings", this.handler));
      this.add(SwingUtil.createToolbarButton("Menu.File.Color", this.handler));
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      this.tbPadcalc.setEnabled(true);
      this.tbAnalysis.setEnabled(true);
      if (newState == VNAApplicationState.INNERSTATE.DRIVERLOADED) {
         this.tbCSV.setEnabled(false);
         this.tbJPG.setEnabled(false);
         this.tbPDF.setEnabled(false);
         this.tbXLS.setEnabled(false);
         this.tbXML.setEnabled(false);
         this.tbS2P.setEnabled(false);
         this.tbZPlots.setEnabled(false);
         this.tbLEN.setEnabled(false);
         this.tbGEN.setEnabled(false);
         this.tbMultitune.setEnabled(false);
         this.tbScheduler.setEnabled(false);
         this.tbCal.setEnabled(true);
         this.tbCalLoad.setEnabled(true);
         this.tbDriverInfo.setEnabled(true);
      } else if (newState == VNAApplicationState.INNERSTATE.CALIBRATED) {
         this.tbCSV.setEnabled(true);
         this.tbJPG.setEnabled(true);
         this.tbPDF.setEnabled(true);
         this.tbXLS.setEnabled(true);
         this.tbXML.setEnabled(true);
         this.tbS2P.setEnabled(true);
         this.tbZPlots.setEnabled(true);
         this.tbLEN.setEnabled(true);
         this.tbGEN.setEnabled(true);
         this.tbMultitune.setEnabled(true);
         this.tbScheduler.setEnabled(true);
         this.tbCal.setEnabled(true);
         this.tbCalLoad.setEnabled(true);
         this.tbDriverInfo.setEnabled(true);
      } else {
         this.tbCSV.setEnabled(false);
         this.tbJPG.setEnabled(false);
         this.tbPDF.setEnabled(false);
         this.tbXLS.setEnabled(false);
         this.tbXML.setEnabled(false);
         this.tbS2P.setEnabled(false);
         this.tbZPlots.setEnabled(false);
         this.tbLEN.setEnabled(false);
         this.tbGEN.setEnabled(false);
         this.tbMultitune.setEnabled(false);
         this.tbCal.setEnabled(false);
         this.tbCalLoad.setEnabled(false);
         this.tbScheduler.setEnabled(false);
         this.tbDriverInfo.setEnabled(false);
      }

   }
}
