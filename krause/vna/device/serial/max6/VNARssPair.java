package krause.vna.device.serial.max6;

public class VNARssPair {
   private double offset = 0.0D;
   private double scale = 1.0D;

   public VNARssPair(double offset, double scale) {
      this.offset = offset;
      this.scale = scale;
   }

   public double getOffset() {
      return this.offset;
   }

   public void setOffset(double offset) {
      this.offset = offset;
   }

   public double getScale() {
      return this.scale;
   }

   public void setScale(double scale) {
      this.scale = scale;
   }
}
