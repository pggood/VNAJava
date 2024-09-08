package krause.vna.gui.fft;

public class VNAFFTPeakTableEntry {
   private int bin;
   private double value;
   private double length;

   public VNAFFTPeakTableEntry(int bin, double value, double length) {
      this.bin = bin;
      this.value = value;
      this.length = length;
   }

   public int getBin() {
      return this.bin;
   }

   public void setBin(int bin) {
      this.bin = bin;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = value;
   }

   public double getLength() {
      return this.length;
   }

   public void setLength(double length) {
      this.length = length;
   }
}
