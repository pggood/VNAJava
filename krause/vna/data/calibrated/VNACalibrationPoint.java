package krause.vna.data.calibrated;

import java.io.Serializable;
import org.apache.commons.math3.complex.Complex;

public class VNACalibrationPoint implements Serializable {
   private long frequency = 0L;
   private Complex deltaE = null;
   private Complex e11 = null;
   private Complex e00 = null;
   private double loss = 0.0D;
   private double phase = 0.0D;
   private int rss1 = 0;
   private int rss2 = 0;
   private int rss3 = 0;
   private Complex edf = null;
   private Complex esf = null;
   private Complex erf = null;

   public void copy(VNACalibrationPoint pSource) {
      this.setDeltaE(pSource.getDeltaE());
      this.setE00(pSource.getE00());
      this.setE11(pSource.getE11());
      this.setLoss(pSource.getLoss());
      this.setPhase(pSource.getPhase());
      this.setRss1(pSource.getRss1());
      this.setRss2(pSource.getRss2());
      this.setRss3(pSource.getRss3());
      this.setEdf(pSource.getEdf());
      this.setErf(pSource.getErf());
      this.setEsf(pSource.getEsf());
   }

   public Complex getDeltaE() {
      return this.deltaE;
   }

   public Complex getE00() {
      return this.e00;
   }

   public Complex getE11() {
      return this.e11;
   }

   public Complex getEdf() {
      return this.edf;
   }

   public Complex getErf() {
      return this.erf;
   }

   public Complex getEsf() {
      return this.esf;
   }

   public long getFrequency() {
      return this.frequency;
   }

   public double getLoss() {
      return this.loss;
   }

   public int getRss1() {
      return this.rss1;
   }

   public int getRss2() {
      return this.rss2;
   }

   public int getRss3() {
      return this.rss3;
   }

   public void setDeltaE(Complex deltaE) {
      this.deltaE = deltaE;
   }

   public void setE00(Complex e00) {
      this.e00 = e00;
   }

   public void setE11(Complex e11) {
      this.e11 = e11;
   }

   public void setEdf(Complex edf) {
      this.edf = edf;
   }

   public void setErf(Complex erf) {
      this.erf = erf;
   }

   public void setEsf(Complex esf) {
      this.esf = esf;
   }

   public void setFrequency(long frequency) {
      this.frequency = frequency;
   }

   public void setLoss(double loss) {
      this.loss = loss;
   }

   public void setRss1(int rss1) {
      this.rss1 = rss1;
   }

   public void setRss2(int rss2) {
      this.rss2 = rss2;
   }

   public void setRss3(int rss3) {
      this.rss3 = rss3;
   }

   public double getPhase() {
      return this.phase;
   }

   public void setPhase(double phase) {
      this.phase = phase;
   }
}
