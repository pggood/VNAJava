package krause.vna.gui.cable;

public class VNAVelocityFactor {
   private String name;
   private double z0;
   private String f1;
   private String attenF1;
   private String f2;
   private String attenF2;
   private double vf;

   public VNAVelocityFactor(String name, double z0, double vf, String attenF1, String f1, String attenF2, String f2) {
      this.name = name;
      this.z0 = z0;
      this.f1 = f1;
      this.attenF1 = attenF1;
      this.f2 = f2;
      this.attenF2 = attenF2;
      this.vf = vf;
   }

   public VNAVelocityFactor() {
   }

   public VNAVelocityFactor(String name, double vf) {
      this.name = name;
      this.z0 = 50.0D;
      this.vf = vf;
   }

   public VNAVelocityFactor(String name, double z0, double vf) {
      this(name, vf);
      this.z0 = z0;
   }

   public String toString() {
      return "VNAVelocityFactor [name=" + this.name + ", z0=" + this.z0 + ", f1=" + this.f1 + ", attenF1=" + this.attenF1 + ", f2=" + this.f2 + ", attenF2=" + this.attenF2 + ", vf=" + this.vf + "]";
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public double getZ0() {
      return this.z0;
   }

   public void setZ0(double z0) {
      this.z0 = z0;
   }

   public String getF1() {
      return this.f1;
   }

   public void setF1(String f1) {
      this.f1 = f1;
   }

   public String getAttenF1() {
      return this.attenF1;
   }

   public void setAttenF1(String attenF1) {
      this.attenF1 = attenF1;
   }

   public String getF2() {
      return this.f2;
   }

   public void setF2(String f2) {
      this.f2 = f2;
   }

   public String getAttenF2() {
      return this.attenF2;
   }

   public void setAttenF2(String attenF2) {
      this.attenF2 = attenF2;
   }

   public double getVf() {
      return this.vf;
   }

   public void setVf(double vf) {
      this.vf = vf;
   }
}
