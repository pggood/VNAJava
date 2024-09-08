package krause.vna.device;

import krause.vna.data.VNAFrequencyRange;

public class VNAScanRange extends VNAFrequencyRange {
   private int numScanPoints;

   public VNAScanRange(long pStart, long pStop, int pSamples) {
      super(pStart, pStop);
      this.numScanPoints = pSamples;
   }

   public VNAScanRange(VNAFrequencyRange pRange, int pSamples) {
      super(pRange);
      this.numScanPoints = pSamples;
   }

   public int getNumScanPoints() {
      return this.numScanPoints;
   }

   public void setNumScanPoints(int numScanPoints) {
      this.numScanPoints = numScanPoints;
   }

   public String toString() {
      return String.format("VNAScanRange [numScanPoints=%d, %s]", this.numScanPoints, super.toString());
   }
}
