package krause.vna.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class VNAApplicationState {
   private VNAApplicationState.INNERSTATE innerState;
   private List<VNAApplicationStateObserver> listeners = new ArrayList();
   private VNAMainFrame mainframe;
   private VNADataPool datapool = VNADataPool.getSingleton();

   public VNAApplicationState(VNAMainFrame pMF) {
      this.mainframe = pMF;
      this.innerState = VNAApplicationState.INNERSTATE.UNINITIALIZED;
   }

   public void addStateListener(VNAApplicationStateObserver pListener) {
      TraceHelper.entry(this, "addStateListener", pListener.getClass().getName());
      this.listeners.add(pListener);
      TraceHelper.exit(this, "addStateListener");
   }

   public void datapoolLoaded() {
      TraceHelper.entry(this, "datapoolLoaded");
      this.setState(VNAApplicationState.INNERSTATE.NEW);
      TraceHelper.exit(this, "datapoolLoaded");
   }

   public void evtCalibrationLoaded() {
      TraceHelper.entry(this, "evtCalibrationLoaded", "state=" + this.innerState);
      String drvString = this.datapool.getDriver().getDeviceInfoBlock().getShortName() + "/" + this.datapool.getDriver().getPortname();
      this.mainframe.getStatusBarDriverType().setText(drvString);
      VNACalibrationBlock mcb = this.datapool.getMainCalibrationBlock();
      if (mcb != null) {
         this.mainframe.getStatusBarCalibrationStatus().setText(mcb.getNumberOfSteps() + "/" + mcb.getNumberOfOverscans());
         String calFilename = "";
         if (mcb.getFile() != null) {
            if (mcb.getComment() != null && !"".equals(mcb.getComment())) {
               calFilename = calFilename + mcb.getFile().getName() + " (" + mcb.getComment() + ")";
            } else {
               calFilename = calFilename + mcb.getFile().getName();
            }
         }

         Double calTemp = mcb.getTemperature();
         if (calTemp != null) {
            calFilename = calFilename + "   CalTemp: [" + VNAFormatFactory.getTemperatureFormat().format(calTemp) + "°C]";
         }

         if (this.datapool.getCalibrationKit() != null) {
            calFilename = calFilename + "   CalKit: [" + this.datapool.getCalibrationKit().getName() + "]";
         }

         this.mainframe.getStatusBarCalibrationFilename().setText(calFilename);
      }

      this.datapool.setCalibratedData(new VNACalibratedSampleBlock(0));
      this.setState(VNAApplicationState.INNERSTATE.CALIBRATED);
      TraceHelper.exit(this, "evtCalibrationLoaded");
   }

   public void evtCalibrationUnloaded() {
      TraceHelper.entry(this, "evtCalibrationUnloaded", "state=" + this.innerState);
      this.setState(VNAApplicationState.INNERSTATE.DRIVERLOADED);
      this.mainframe.getStatusBarDriverType().setText(this.datapool.getDriver().getDeviceInfoBlock().getShortName());
      this.mainframe.getStatusBarCalibrationStatus().setText(VNAMessages.getString("VNAMainFrame.Cal.UNCAL"));
      this.mainframe.getStatusBarCalibrationFilename().setText(VNAMessages.getString("VNAMainFrame.Cal.NOFILE"));
      TraceHelper.exit(this, "evtCalibrationUnloaded");
   }

   public void evtDriverLoaded() {
      TraceHelper.entry(this, "evtDriverLoaded", "state=" + this.innerState);
      this.setState(VNAApplicationState.INNERSTATE.DRIVERLOADED);
      String drvString = this.datapool.getDriver().getDeviceInfoBlock().getShortName() + "/" + this.datapool.getDriver().getPortname();
      this.mainframe.getStatusBarDriverType().setText(drvString);
      this.mainframe.getStatusBarCalibrationStatus().setText(VNAMessages.getString("VNAMainFrame.Cal.UNCAL"));
      this.mainframe.getStatusBarCalibrationFilename().setText(VNAMessages.getString("VNAMainFrame.Cal.NOFILE"));
      this.mainframe.preloadCalibrationBlocks();
      this.mainframe.changedMode();
      if (this.datapool.getMainCalibrationBlock() != null) {
         this.evtCalibrationLoaded();
      }

      TraceHelper.exit(this, "evtDriverLoaded");
   }

   public void evtDriverUnloaded() {
      TraceHelper.entry(this, "evtDriverUnloaded", "state=" + this.innerState);
      this.setState(VNAApplicationState.INNERSTATE.GUIINITIALIZED);
      TraceHelper.exit(this, "evtDriverUnloaded");
   }

   public void evtGUIInitialzed() {
      TraceHelper.entry(this, "evtGUIInitialzed");
      this.mainframe.getStatusBarDriverType().setText("---");
      this.mainframe.getStatusBarCalibrationStatus().setText(VNAMessages.getString("VNAMainFrame.Cal.UNCAL"));
      this.mainframe.getStatusBarCalibrationFilename().setText(VNAMessages.getString("VNAMainFrame.Cal.NOFILE"));
      this.setState(VNAApplicationState.INNERSTATE.GUIINITIALIZED);
      this.mainframe.loadDriver();
      TraceHelper.exit(this, "evtGUIInitialzed");
   }

   public void evtMeasureEnded() {
      TraceHelper.entry(this, "evtMeasureEnded", "state=" + this.innerState);
      this.setState(VNAApplicationState.INNERSTATE.CALIBRATED);
      TraceHelper.exit(this, "evtMeasureEnded");
   }

   public void evtMeasureStarted() {
      TraceHelper.entry(this, "evtMeasureStarted", "state=" + this.innerState);
      this.setState(VNAApplicationState.INNERSTATE.RUNNING);
      TraceHelper.exit(this, "evtMeasureStarted");
   }

   public void evtScanModeChanged() {
      TraceHelper.entry(this, "evtScanModeChanged");
      this.mainframe.changedMode();
      TraceHelper.exit(this, "evtScanModeChanged");
   }

   public VNAApplicationState.INNERSTATE getState() {
      return this.innerState;
   }

   private void publishState(VNAApplicationState.INNERSTATE pOld, VNAApplicationState.INNERSTATE pNew) {
      TraceHelper.entry(this, "publishState", "state=" + this.innerState);
      Iterator var4 = this.listeners.iterator();

      while(var4.hasNext()) {
         VNAApplicationStateObserver listener = (VNAApplicationStateObserver)var4.next();
         listener.changeState(pOld, pNew);
      }

      TraceHelper.exit(this, "publishState");
   }

   public void republishState() {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         VNAApplicationStateObserver listener = (VNAApplicationStateObserver)var2.next();
         listener.changeState(this.innerState, this.innerState);
      }

   }

   protected void setState(VNAApplicationState.INNERSTATE pNew) {
      VNAApplicationState.INNERSTATE old = this.innerState;
      this.innerState = pNew;
      this.publishState(old, pNew);
   }

   public static enum INNERSTATE {
      NEW,
      UNINITIALIZED,
      GUIINITIALIZED,
      DRIVERLOADED,
      CALIBRATED,
      READY,
      RUNNING,
      TERMINATING;
   }
}
