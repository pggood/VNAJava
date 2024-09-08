package krause.vna.data;

import krause.common.TypedProperties;

public class VNAFrequencyRange {
   public static final long NO_FREQ = Long.MIN_VALUE;
   private long start;
   private long stop;

   public VNAFrequencyRange() {
      this.start = Long.MIN_VALUE;
      this.stop = Long.MIN_VALUE;
   }

   public VNAFrequencyRange(long start, long stop) {
      this.start = start;
      this.stop = stop;
   }

   public VNAFrequencyRange(VNAFrequencyRange range) {
      this.start = range.getStart();
      this.stop = range.getStop();
   }

   public long getStart() {
      return this.start;
   }

   public long getStop() {
      return this.stop;
   }

   public String toString() {
      return "VNAFrequencyRange [start=" + this.start + ", stop=" + this.stop + "]";
   }

   public void setStart(long start) {
      this.start = start;
   }

   public void setStop(long stop) {
      this.stop = stop;
   }

   public void saveToProperties(TypedProperties props) {
      props.putLong("Range.start", this.start);
      props.putLong("Range.stop", this.stop);
   }

   public void restoreFromProperties(TypedProperties props) {
      this.start = props.getInteger("Range.start", Long.MIN_VALUE);
      this.stop = props.getInteger("Range.stop", Long.MIN_VALUE);
   }

   public boolean isValid() {
      return this.start != Long.MIN_VALUE && this.stop != Long.MIN_VALUE;
   }
}
