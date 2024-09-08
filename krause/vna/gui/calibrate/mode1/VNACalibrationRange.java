package krause.vna.gui.calibrate.mode1;

import krause.vna.device.VNAScanRange;

public class VNACalibrationRange extends VNAScanRange {
   private int numOverScans;

   public VNACalibrationRange(long pStart, long pStop, int pSamples, int pOverscans) {
      super(pStart, pStop, pSamples);
      this.numOverScans = pOverscans;
   }

   public int getNumOverScans() {
      return this.numOverScans;
   }

   public void setNumOverScans(int numOverScans) {
      this.numOverScans = numOverScans;
   }
}
