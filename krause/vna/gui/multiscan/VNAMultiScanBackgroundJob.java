package krause.vna.gui.multiscan;

import krause.vna.background.VNABackgroundJob;
import krause.vna.data.VNAFrequencyRange;

public class VNAMultiScanBackgroundJob extends VNABackgroundJob {
   private VNAMultiScanResult resultWindow;

   public VNAMultiScanBackgroundJob(VNAMultiScanResult pResultWindow) {
      this.resultWindow = pResultWindow;
      this.setNumberOfSamples(500);
      this.setSpeedup(1);
      this.setFrequencyRange(new VNAFrequencyRange(pResultWindow.getStartFrequency(), pResultWindow.getStopFrequency()));
      this.setScanMode(pResultWindow.getScanMode());
   }

   public VNAMultiScanResult getResultWindow() {
      return this.resultWindow;
   }
}
