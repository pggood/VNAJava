package krause.vna.gui.util;

import java.text.NumberFormat;
import krause.vna.gui.format.VNAFormatFactory;

public class VNAFrequencyPair {
   private long startFrequency = 0L;
   private long stopFrequency = 0L;

   public VNAFrequencyPair() {
   }

   public VNAFrequencyPair(long start, long stop) {
      this.setStartFrequency(start);
      this.setStopFrequency(stop);
   }

   public boolean equals(Object obj) {
      if (obj instanceof VNAFrequencyPair) {
         VNAFrequencyPair p = (VNAFrequencyPair)obj;
         return p.getStartFrequency() == this.getStartFrequency() && p.getStopFrequency() == this.getStopFrequency();
      } else {
         return super.equals(obj);
      }
   }

   public boolean isWithinPair(long frq) {
      return frq >= this.startFrequency && frq <= this.stopFrequency;
   }

   public long getStartFrequency() {
      return this.startFrequency;
   }

   public long getStopFrequency() {
      return this.stopFrequency;
   }

   public void setStartFrequency(long startFrequency) {
      this.startFrequency = startFrequency;
   }

   public void setStopFrequency(long stopFrequency) {
      this.stopFrequency = stopFrequency;
   }

   public String toString() {
      NumberFormat nf = VNAFormatFactory.getFrequencyFormat();
      return nf.format(this.getStartFrequency()) + "-" + nf.format(this.getStopFrequency());
   }
}
