package krause.vna.data.calibrated;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;

public class VNACalibrationBlock implements Serializable {
   public static final String CALIBRATION_FILETYPE_2 = "__V2";
   public static final String CALIBRATION_FILETYPE_3 = "__V3";
   public static final String CALIBRATION_FILETYPE_4 = "__V4";
   private static final long serialVersionUID = -2310064516699988988L;
   public static final String CALIBRATION_FILETYPE_5 = "__V5";
   private String analyserType = "99";
   private VNASampleBlock calibrationData4Load = null;
   private VNASampleBlock calibrationData4Loop = null;
   private VNASampleBlock calibrationData4Open = null;
   private VNASampleBlock calibrationData4Short = null;
   private transient VNACalibrationPoint[] calibrationPoints = null;
   private String comment = null;
   private transient File file = null;
   private transient IVNADriverMathHelper mathHelper = null;
   private int numberOfOverscans = 1;
   private int numberOfSteps = 100;
   private VNAScanMode scanMode;
   private long startFrequency;
   private long stopFrequency;
   private transient Double temperature;

   public VNACalibrationBlock() {
      this.scanMode = VNAScanMode.MODE_TRANSMISSION;
      this.startFrequency = 1000000L;
      this.stopFrequency = 100000000L;
      this.temperature = null;
   }

   public VNACalibrationBlock(VNASampleBlock block) {
      this.scanMode = VNAScanMode.MODE_TRANSMISSION;
      this.startFrequency = 1000000L;
      this.stopFrequency = 100000000L;
      this.temperature = null;
      this.setAnalyserType(block.getAnalyserType());
      this.setMathHelper(block.getMathHelper());
      this.setNumberOfSteps(block.getNumberOfSteps());
      this.setStartFrequency(block.getStartFrequency());
      this.setStopFrequency(block.getStopFrequency());
      this.setScanMode(block.getScanMode());
      this.setNumberOfOverscans(block.getNumberOfOverscans());
   }

   public boolean blockMatches(String pType, int pNoS, long pStart, long pStop, VNAScanMode pMode) {
      boolean rc = true;
      rc &= this.getAnalyserType().equals(pType);
      rc &= this.getStartFrequency() == pStart;
      rc &= this.getStopFrequency() == pStop;
      rc &= this.getScanMode().equals(pMode);
      return rc;
   }

   public boolean blockMatches(VNADeviceInfoBlock dib) {
      boolean rc = true;
      rc &= this.getAnalyserType().equals(dib.getType());
      rc &= this.getStartFrequency() == dib.getMinFrequency();
      rc &= this.getStopFrequency() == dib.getMaxFrequency();
      return rc;
   }

   public boolean blockMatches(VNADeviceInfoBlock dib, VNAScanMode pScanMode) {
      return this.blockMatches(dib.getType(), dib.getNumberOfSamples4Calibration(), dib.getMinFrequency(), dib.getMaxFrequency(), pScanMode);
   }

   public void calculateCalibrationTemperature() {
      String methodName = "calculateCalibrationTemperature";
      TraceHelper.entry(this, "calculateCalibrationTemperature");
      int i = 0;
      double temp = 0.0D;
      if (this.getCalibrationData4Load() != null && this.getCalibrationData4Load().getDeviceTemperature() != null) {
         ++i;
         temp += this.getCalibrationData4Load().getDeviceTemperature();
      }

      if (this.getCalibrationData4Loop() != null && this.getCalibrationData4Loop().getDeviceTemperature() != null) {
         ++i;
         temp += this.getCalibrationData4Loop().getDeviceTemperature();
      }

      if (this.getCalibrationData4Open() != null && this.getCalibrationData4Open().getDeviceTemperature() != null) {
         ++i;
         temp += this.getCalibrationData4Open().getDeviceTemperature();
      }

      if (this.getCalibrationData4Short() != null && this.getCalibrationData4Short().getDeviceTemperature() != null) {
         ++i;
         temp += this.getCalibrationData4Short().getDeviceTemperature();
      }

      if (i > 0) {
         temp /= (double)i;
         TraceHelper.exitWithRC(this, "calculateCalibrationTemperature", temp);
         this.temperature = temp;
      } else {
         TraceHelper.exit(this, "calculateCalibrationTemperature");
         this.temperature = null;
      }

   }

   public String getAnalyserType() {
      return this.analyserType;
   }

   public VNASampleBlock getCalibrationData4Load() {
      return this.calibrationData4Load;
   }

   public VNASampleBlock getCalibrationData4Loop() {
      return this.calibrationData4Loop;
   }

   public VNASampleBlock getCalibrationData4Open() {
      return this.calibrationData4Open;
   }

   public VNASampleBlock getCalibrationData4Short() {
      return this.calibrationData4Short;
   }

   public VNACalibrationPoint[] getCalibrationPoints() {
      return this.calibrationPoints;
   }

   public String getComment() {
      return this.comment;
   }

   public File getFile() {
      return this.file;
   }

   public IVNADriverMathHelper getMathHelper() {
      return this.mathHelper;
   }

   public int getNumberOfOverscans() {
      return this.numberOfOverscans;
   }

   public int getNumberOfSteps() {
      return this.numberOfSteps;
   }

   public VNAScanMode getScanMode() {
      return this.scanMode;
   }

   public long getStartFrequency() {
      return this.startFrequency;
   }

   public long getStopFrequency() {
      return this.stopFrequency;
   }

   public Double getTemperature() {
      return this.temperature;
   }

   public boolean satisfiedDeviceInfoBlock(VNADeviceInfoBlock dib) {
      TraceHelper.entry(this, "satisfiedDeviceInfoBlock");
      boolean rc = true;
      VNAScanModeParameter smr = dib.getScanModeParameterForMode(this.getScanMode());
      if (smr != null) {
         rc &= smr.isRequiresOpen() ? this.getCalibrationData4Open() != null : true;
         rc &= smr.isRequiresShort() ? this.getCalibrationData4Short() != null : true;
         rc &= smr.isRequiresLoad() ? this.getCalibrationData4Load() != null : true;
         rc &= smr.isRequiresLoop() ? this.getCalibrationData4Loop() != null : true;
      }

      TraceHelper.exitWithRC(this, "satisfiedDeviceInfoBlock", rc);
      return rc;
   }

   public void setAnalyserType(String analyserType) {
      this.analyserType = analyserType;
   }

   public void setCalibrationData4Load(VNASampleBlock calibrationData4Load) {
      this.calibrationData4Load = calibrationData4Load;
   }

   public void setCalibrationData4Loop(VNASampleBlock calibrationData4Loop) {
      this.calibrationData4Loop = calibrationData4Loop;
   }

   public void setCalibrationData4Open(VNASampleBlock calibrationData4Open) {
      this.calibrationData4Open = calibrationData4Open;
   }

   public void setCalibrationData4Short(VNASampleBlock calibrationData4Short) {
      this.calibrationData4Short = calibrationData4Short;
   }

   public void setCalibrationPoints(VNACalibrationPoint[] calibratedSamples) {
      this.calibrationPoints = calibratedSamples;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setFile(File file) {
      this.file = file;
   }

   public void setMathHelper(IVNADriverMathHelper mathHelper) {
      this.mathHelper = mathHelper;
   }

   public void setNumberOfOverscans(int numberOfOverscans) {
      this.numberOfOverscans = numberOfOverscans;
   }

   public void setNumberOfSteps(int numberOfSteps) {
      this.numberOfSteps = numberOfSteps;
   }

   public void setScanMode(VNAScanMode scanMode) {
      this.scanMode = scanMode;
   }

   public void setStartFrequency(long startFrequency) {
      this.startFrequency = startFrequency;
   }

   public void setStopFrequency(long stopFrequency) {
      this.stopFrequency = stopFrequency;
   }

   public void setTemperature(Double temperature) {
      this.temperature = temperature;
   }

   public String toString() {
      return "VNACalibrationBlock [analyserType=" + this.analyserType + ", calibrationData4Load=" + this.calibrationData4Load + ", calibrationData4Loop=" + this.calibrationData4Loop + ", calibrationData4Open=" + this.calibrationData4Open + ", calibrationData4Short=" + this.calibrationData4Short + ", calibrationPoints=" + Arrays.toString(this.calibrationPoints) + ", numberOfOverscans=" + this.numberOfOverscans + ", numberOfSteps=" + this.numberOfSteps + ", scanMode=" + this.scanMode + ", startFrequency=" + this.startFrequency + ", stopFrequency=" + this.stopFrequency + "]";
   }
}
