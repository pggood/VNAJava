package krause.vna.data.calibrationkit;

import java.util.UUID;

public class VNACalibrationKit {
   private String id;
   private String name;
   private double openOffset;
   private double openLoss;
   private double shortOffset;
   private double shortLoss;
   private boolean female;
   private double openCapCoeffC0;
   private double openCapCoeffC1;
   private double openCapCoeffC2;
   private double openCapCoeffC3;
   private double shortInductance;
   private double thruLength;

   public VNACalibrationKit(String newName) {
      this.initFields(newName);
   }

   public VNACalibrationKit() {
      this.initFields("DEFAULT");
   }

   private void initFields(String name) {
      this.name = name;
      this.setId(UUID.randomUUID().toString());
      this.female = false;
      this.openCapCoeffC0 = 0.0D;
      this.openCapCoeffC1 = 0.0D;
      this.openCapCoeffC2 = 0.0D;
      this.openCapCoeffC3 = 0.01D;
      this.openLoss = 0.0D;
      this.openOffset = 0.0D;
      this.shortInductance = 0.0D;
      this.shortOffset = 0.0D;
      this.thruLength = 0.0D;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public double getOpenOffset() {
      return this.openOffset;
   }

   public void setOpenOffset(double openOffset) {
      this.openOffset = openOffset;
   }

   public double getOpenLoss() {
      return this.openLoss;
   }

   public void setOpenLoss(double openLoss) {
      this.openLoss = openLoss;
   }

   public double getShortOffset() {
      return this.shortOffset;
   }

   public void setShortOffset(double shortOffset) {
      this.shortOffset = shortOffset;
   }

   public double getShortLoss() {
      return this.shortLoss;
   }

   public void setShortLoss(double shortLoss) {
      this.shortLoss = shortLoss;
   }

   public boolean isFemale() {
      return this.female;
   }

   public void setFemale(boolean female) {
      this.female = female;
   }

   public double getOpenCapCoeffC0() {
      return this.openCapCoeffC0;
   }

   public void setOpenCapCoeffC0(double openCapCoeffC0) {
      this.openCapCoeffC0 = openCapCoeffC0;
   }

   public double getOpenCapCoeffC1() {
      return this.openCapCoeffC1;
   }

   public void setOpenCapCoeffC1(double openCapCoeffC1) {
      this.openCapCoeffC1 = openCapCoeffC1;
   }

   public double getOpenCapCoeffC2() {
      return this.openCapCoeffC2;
   }

   public void setOpenCapCoeffC2(double openCapCoeffC2) {
      this.openCapCoeffC2 = openCapCoeffC2;
   }

   public double getOpenCapCoeffC3() {
      return this.openCapCoeffC3;
   }

   public void setOpenCapCoeffC3(double openCapCoeffC3) {
      this.openCapCoeffC3 = openCapCoeffC3;
   }

   public double getShortInductance() {
      return this.shortInductance;
   }

   public void setShortInductance(double shortInductance) {
      this.shortInductance = shortInductance;
   }

   public double getThruLength() {
      return this.thruLength;
   }

   public void setThruLength(double thruLength) {
      this.thruLength = thruLength;
   }

   public String toString() {
      return this.name;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }
}
