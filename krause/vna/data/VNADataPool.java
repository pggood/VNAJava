package krause.vna.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalSetHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.device.IVNADriver;

public class VNADataPool {
   public static final String KEY_DEVICETYPE = "deviceType";
   public static final String KEY_FREQUENCYRANGE = "frequencyRange";
   public static final String KEY_MAIN_CALBLK = "mainCalibrationBlock";
   public static final String KEY_MAIN_REFLECTION_CALBLK = "mainCalibrationBlockReflection";
   public static final String KEY_RESIZED_CALBLK = "resizedCalibrationBlock";
   public static final String KEY_SCANMODE = "scanMode";
   public static final String PROPERTIES_PREFIX = "VNADeviceConfig.";
   public static final String PROPERTIES_START_FREQUENCY = "VNADeviceConfig.StartFrequency";
   public static final String PROPERTIES_STOP_FREQUENCY = "VNADeviceConfig.StopFrequency";
   public static final String PROPERTIES_TRANSMISSION_MODE = "VNADeviceConfig.TransmissionMode";
   private static VNADataPool singleton = null;
   private VNACalibratedSampleBlock calibratedData = null;
   private String deviceType = null;
   private IVNADriver driver = null;
   private VNAFrequencyRange frequencyRange = null;
   private VNACalibrationBlock mainCalibrationBlock = null;
   private Map<String, VNACalibrationBlock> mainCalibrationBlocks = new HashMap();
   private VNASampleBlock rawData = null;
   private List<VNASampleBlock> rawDataBlocks = new ArrayList();
   private VNAReferenceDataBlock referenceData = null;
   private VNACalibrationBlock resizedCalibrationBlock = null;
   private VNAScanMode scanMode = null;
   private VNACalibrationKit currentCalSet = null;

   public static synchronized VNADataPool getSingleton() {
      if (singleton == null) {
         singleton = new VNADataPool();
      }

      return singleton;
   }

   public static synchronized VNADataPool init(VNAConfig pConfig) {
      if (singleton == null) {
         singleton = new VNADataPool();
         singleton.load(pConfig);
      }

      return singleton;
   }

   protected VNADataPool() {
   }

   public void clearCalibratedData() {
      this.calibratedData = null;
   }

   public void clearCalibrationBlocks() {
      TraceHelper.entry(this, "clearCalibrationBlocks");
      if (this.mainCalibrationBlock != null) {
         this.mainCalibrationBlock = null;
      }

      if (this.mainCalibrationBlocks != null) {
         this.mainCalibrationBlocks.clear();
      }

      TraceHelper.exit(this, "clearCalibrationBlocks");
   }

   public void clearResizedCalibrationBlock() {
      TraceHelper.entry(this, "clearResizedCalibrationBlock");
      this.resizedCalibrationBlock = null;
      TraceHelper.exit(this, "clearResizedCalibrationBlock");
   }

   public VNACalibratedSampleBlock getCalibratedData() {
      return this.calibratedData;
   }

   public String getDeviceType() {
      return this.deviceType;
   }

   public IVNADriver getDriver() {
      return this.driver;
   }

   public VNAFrequencyRange getFrequencyRange() {
      return this.frequencyRange;
   }

   public VNACalibrationBlock getMainCalibrationBlock() {
      return this.mainCalibrationBlock;
   }

   public VNACalibrationBlock getMainCalibrationBlockForMode(VNAScanMode mode) {
      return (VNACalibrationBlock)this.mainCalibrationBlocks.get(mode.key());
   }

   public Map<String, VNACalibrationBlock> getMainCalibrationBlocks() {
      return this.mainCalibrationBlocks;
   }

   public VNASampleBlock getRawData() {
      return this.rawData;
   }

   public List<VNASampleBlock> getRawDataBlocks() {
      return this.rawDataBlocks;
   }

   public VNAReferenceDataBlock getReferenceData() {
      return this.referenceData;
   }

   public VNACalibrationBlock getResizedCalibrationBlock() {
      return this.resizedCalibrationBlock;
   }

   public VNAScanMode getScanMode() {
      return this.scanMode;
   }

   private void load(VNAConfig pConfig) {
      TraceHelper.entry(this, "load");
      this.frequencyRange = new VNAFrequencyRange(pConfig.getLong("VNADeviceConfig.StartFrequency", 1000000L), pConfig.getLong("VNADeviceConfig.StopFrequency", 180000000L));
      int sm = pConfig.getInteger("VNADeviceConfig.TransmissionMode", -1);
      if (sm != -1) {
         this.scanMode = new VNAScanMode(sm);
      }

      this.deviceType = pConfig.getVNADriverType();
      String csn = pConfig.getCurrentCalSetID();
      this.currentCalSet = new VNACalibrationKit();
      Iterator var5 = (new VNACalSetHelper()).load(pConfig.getCalibrationKitFilename()).iterator();

      while(var5.hasNext()) {
         VNACalibrationKit aCalSet = (VNACalibrationKit)var5.next();
         if (aCalSet.getId().equals(csn)) {
            TraceHelper.text(this, "load", "Using calibration set [" + aCalSet.getName() + "]");
            this.currentCalSet = aCalSet;
            break;
         }
      }

      TraceHelper.exit(this, "load");
   }

   public void save(VNAConfig pConfig) {
      TraceHelper.entry(this, "save");
      pConfig.putLong("VNADeviceConfig.StartFrequency", this.frequencyRange.getStart());
      pConfig.putLong("VNADeviceConfig.StopFrequency", this.frequencyRange.getStop());
      if (this.scanMode != null) {
         pConfig.putInteger("VNADeviceConfig.TransmissionMode", this.scanMode.getMode());
      }

      pConfig.setVNADriverType(this.deviceType);
      TraceHelper.exit(this, "save");
   }

   public void setCalibratedData(VNACalibratedSampleBlock calibratedData) {
      this.calibratedData = calibratedData;
   }

   public void setDeviceType(String newDeviceType) {
      this.deviceType = newDeviceType;
   }

   public void setDriver(IVNADriver driver) {
      this.driver = driver;
   }

   public void setFrequencyRange(long start, long stop) {
      this.setFrequencyRange(new VNAFrequencyRange(start, stop));
   }

   public void setFrequencyRange(VNAFrequencyRange pNewFrequencyRange) {
      this.frequencyRange = pNewFrequencyRange;
   }

   public void setMainCalibrationBlock(VNACalibrationBlock newMainCalibrationBlock) {
      TraceHelper.entry(this, "setMainCalibrationBlock");
      this.mainCalibrationBlock = newMainCalibrationBlock;
      TraceHelper.exit(this, "setMainCalibrationBlock");
   }

   public void setMainCalibrationBlockForMode(VNACalibrationBlock mcb) {
      TraceHelper.entry(this, "setMainCalibrationBlockForMode", mcb.getScanMode().key());
      this.mainCalibrationBlocks.put(mcb.getScanMode().key(), mcb);
      TraceHelper.exit(this, "setMainCalibrationBlockForMode");
   }

   public void setRawData(VNASampleBlock rawData) {
      this.rawData = rawData;
   }

   public void setRawDataBlocks(List<VNASampleBlock> rawDataBlocks) {
      this.rawDataBlocks = rawDataBlocks;
   }

   public void setReferenceData(VNAReferenceDataBlock referenceData) {
      this.referenceData = referenceData;
   }

   public void setResizedCalibrationBlock(VNACalibrationBlock newResizedCalibrationBlock) {
      this.resizedCalibrationBlock = newResizedCalibrationBlock;
   }

   public void setScanMode(VNAScanMode newMode) {
      this.scanMode = newMode;
   }

   public VNACalibrationKit getCalibrationKit() {
      return this.currentCalSet;
   }

   public void setCalibrationKit(VNACalibrationKit calSet) {
      this.currentCalSet = calSet;
   }
}
