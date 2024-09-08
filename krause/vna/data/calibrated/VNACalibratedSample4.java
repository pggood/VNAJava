package krause.vna.data.calibrated;

public class VNACalibratedSample4 {
   private VNACalibratedSample min1 = new VNACalibratedSample();
   private VNACalibratedSample min2 = new VNACalibratedSample();
   private VNACalibratedSample max1 = new VNACalibratedSample();
   private VNACalibratedSample max2 = new VNACalibratedSample();

   public VNACalibratedSample4(boolean scale360) {
      if (scale360) {
         this.max1.setReflectionPhase(-1.7976931348623157E308D);
         this.max2.setReflectionPhase(-1.7976931348623157E308D);
         this.min1.setReflectionPhase(Double.MAX_VALUE);
         this.min2.setReflectionPhase(Double.MAX_VALUE);
      } else {
         this.max1.setReflectionPhase(0.0D);
         this.max2.setReflectionPhase(0.0D);
         this.min1.setReflectionPhase(Double.MAX_VALUE);
         this.min2.setReflectionPhase(Double.MAX_VALUE);
      }

   }

   public VNACalibratedSample getMin1() {
      return this.min1;
   }

   public void setMin1(VNACalibratedSample min1) {
      this.min1 = min1;
   }

   public VNACalibratedSample getMin2() {
      return this.min2;
   }

   public void setMin2(VNACalibratedSample min2) {
      this.min2 = min2;
   }

   public VNACalibratedSample getMax1() {
      return this.max1;
   }

   public void setMax1(VNACalibratedSample max1) {
      this.max1 = max1;
   }

   public VNACalibratedSample getMax2() {
      return this.max2;
   }

   public void setMax2(VNACalibratedSample max2) {
      this.max2 = max2;
   }

   public String toString() {
      return "VNACalibratedSample4 [max1=" + this.max1 + ", max2=" + this.max2 + ", min1=" + this.min1 + ", min2=" + this.min2 + "]";
   }
}
