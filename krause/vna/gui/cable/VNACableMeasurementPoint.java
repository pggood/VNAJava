package krause.vna.gui.cable;

import krause.vna.data.calibrated.VNACalibratedSample;

public class VNACableMeasurementPoint {
   private VNACalibratedSample start;
   private VNACalibratedSample stop;
   private long delta;
   private double length;
   private double velocityFactor;
   private boolean meterMode = true;
   private boolean scale360 = false;
   public static final double SOL = 2.99792458E8D;
   public static final double METER2FEET = 0.3048D;

   public double getVelocityFactor() {
      return this.velocityFactor;
   }

   public VNACableMeasurementPoint(boolean pMeterMode, boolean pScale360) {
      this.meterMode = pMeterMode;
      this.scale360 = pScale360;
   }

   public long getDelta() {
      return this.delta;
   }

   public void setDelta(long delta) {
      this.delta = delta;
   }

   public double getLength() {
      return this.length;
   }

   public VNACalibratedSample getStart() {
      return this.start;
   }

   public void setStart(VNACalibratedSample start) {
      this.start = start;
   }

   public VNACalibratedSample getStop() {
      return this.stop;
   }

   public void setStop(VNACalibratedSample stop) {
      this.stop = stop;
   }

   public void calculateLength(double velocityFactor) {
      this.setDelta(this.getStop().getFrequency() - this.getStart().getFrequency());
      double l = 2.99792458E8D * velocityFactor / ((double)(2L * this.getDelta()) + 1.0E-7D);
      if (!this.meterMode) {
         l /= 0.3048D;
      }

      this.length = l;
   }

   public void calculateVelocityFactor(double pCableLength) {
      this.setDelta(this.getStop().getFrequency() - this.getStart().getFrequency());
      if (!this.meterMode) {
         pCableLength *= 0.3048D;
      }

      double vf = pCableLength * 2.0D * (double)this.getDelta() / 2.99792458E8D;
      this.velocityFactor = vf;
   }

   public String toString() {
      return "VNACableMeasurementPoint [delta=" + this.delta + ", length=" + this.length + ", start=" + this.start.getFrequency() + ", stop=" + this.stop.getFrequency() + "]";
   }
}
