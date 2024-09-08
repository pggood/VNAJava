package krause.vna.background;

import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;

public class VNABackgroundJob {
   private int average = 1;
   private VNAFrequencyRange frequencyRange;
   private int numberOfSamples;
   private VNASampleBlock result = null;
   private VNAScanMode scanMode;
   private int speedup = 1;
   private int overScan = 1;

   public int getAverage() {
      return this.average;
   }

   public VNAFrequencyRange getFrequencyRange() {
      return this.frequencyRange;
   }

   public int getNumberOfSamples() {
      return this.numberOfSamples;
   }

   public VNASampleBlock getResult() {
      return this.result;
   }

   public VNAScanMode getScanMode() {
      return this.scanMode;
   }

   public int getSpeedup() {
      return this.speedup;
   }

   public void setAverage(int average) {
      this.average = average;
   }

   public void setFrequencyRange(VNADeviceInfoBlock dib) {
      this.frequencyRange = new VNAFrequencyRange(dib.getMinFrequency(), dib.getMaxFrequency());
   }

   public void setFrequencyRange(VNAFrequencyRange frequencyRange) {
      this.frequencyRange = frequencyRange;
   }

   public void setNumberOfSamples(int numberOfSamples) {
      this.numberOfSamples = numberOfSamples;
   }

   public void setNumberOfSamples(VNADeviceInfoBlock dib) {
      this.numberOfSamples = dib.getNumberOfSamples4Calibration();
   }

   public void setResult(VNASampleBlock result) {
      this.result = result;
   }

   public void setScanMode(VNAScanMode scanMode) {
      this.scanMode = scanMode;
   }

   public void setSpeedup(int speedup) {
      this.speedup = speedup;
   }

   public int getOverScan() {
      return this.overScan;
   }

   public void setOverScan(int overScan) {
      this.overScan = overScan;
   }
}
