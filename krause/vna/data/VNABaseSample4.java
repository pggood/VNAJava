package krause.vna.data;

public class VNABaseSample4 {
   private VNABaseSample min1 = new VNABaseSample(2.147483647E9D, 0.0D, 0L);
   private VNABaseSample min2 = new VNABaseSample(2.147483647E9D, 0.0D, 0L);
   private VNABaseSample max1 = new VNABaseSample(-2.147483648E9D, 0.0D, 0L);
   private VNABaseSample max2 = new VNABaseSample(-2.147483648E9D, 0.0D, 0L);

   public VNABaseSample getMin1() {
      return this.min1;
   }

   public void setMin1(VNABaseSample min1) {
      this.min1 = min1;
   }

   public VNABaseSample getMin2() {
      return this.min2;
   }

   public void setMin2(VNABaseSample min2) {
      this.min2 = min2;
   }

   public VNABaseSample getMax1() {
      return this.max1;
   }

   public void setMax1(VNABaseSample max1) {
      this.max1 = max1;
   }

   public VNABaseSample getMax2() {
      return this.max2;
   }

   public void setMax2(VNABaseSample max2) {
      this.max2 = max2;
   }

   public String toString() {
      return "VNABaseSample4 [max1=" + this.max1 + ", max2=" + this.max2 + ", min1=" + this.min1 + ", min2=" + this.min2 + "]";
   }
}
