package krause.vna.importers;

import java.util.Arrays;

public class SnPRecord {
   private long frequency;
   private double[] loss = new double[4];
   private double[] phase = new double[4];

   public long getFrequency() {
      return this.frequency;
   }

   public void setFrequency(long frequency) {
      this.frequency = frequency;
   }

   public double[] getLoss() {
      return this.loss;
   }

   public void setLoss(int i, double loss) {
      this.loss[i] = loss;
   }

   public double[] getPhase() {
      return this.phase;
   }

   public void setPhase(int i, double phase) {
      this.phase[i] = phase;
   }

   public String toString() {
      return "SnPInputRecord [frequency=" + this.frequency + ", loss=" + Arrays.toString(this.loss) + ", phase=" + Arrays.toString(this.phase) + "]";
   }
}
