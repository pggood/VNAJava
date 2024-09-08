package krause.vna.gui.padcalc;

import java.util.ArrayList;
import java.util.List;
import krause.util.ras.logging.TraceHelper;

public class VNAPadCalculator {
   private VNAGenericPad pad;

   public void calculatePad(double atten) {
      TraceHelper.entry(this, "calculatePad");
      if (this.pad instanceof VNAPiPad) {
         this.calculatePiPad(atten);
      } else {
         this.calculateTPad(atten);
      }

      TraceHelper.exit(this, "calculatePad");
   }

   private void calculateTPad(double atten) {
      TraceHelper.entry(this, "calculateTPad");
      this.calculatePiPad(atten);
      double r3 = this.pad.getR3() * this.pad.getR4() / (this.pad.getR3() + this.pad.getR4() + this.pad.getR5());
      double r4 = this.pad.getR3() * this.pad.getR5() / (this.pad.getR3() + this.pad.getR4() + this.pad.getR5());
      double r5 = this.pad.getR4() * this.pad.getR5() / (this.pad.getR3() + this.pad.getR4() + this.pad.getR5());
      this.pad.setR3(r3);
      this.pad.setR4(r4);
      this.pad.setR5(r5);
      TraceHelper.exit(this, "calculateTPad");
   }

   private void calculatePiPad(double atten) {
      TraceHelper.entry(this, "calculatePiPad");
      double a = Math.log(10.0D) / 20.0D;
      double z = Math.exp(a * atten);
      double m = this.pad.getR1() / this.pad.getR2();
      double r3 = this.pad.getR1() * (z * z - 1.0D) / (z * z - 2.0D * z * Math.sqrt(m) + 1.0D);
      double r4 = this.pad.getR1() * (z * z - 1.0D) / (2.0D * z * Math.sqrt(m));
      double r5 = this.pad.getR2() * (z * z - 1.0D) / (z * z - 2.0D * z / Math.sqrt(m) + 1.0D);
      this.pad.setR3(r3);
      this.pad.setR4(r4);
      this.pad.setR5(r5);
      TraceHelper.exit(this, "calculatePiPad");
   }

   public List<Double> calculateSeriesCircuit(double[] fullSeries, double resistanceX, int maxParts, double percentPrecision) {
      List<Double> rc = new ArrayList();
      double minVal = resistanceX * (1.0D - percentPrecision);
      double maxVal = resistanceX * (1.0D + percentPrecision);
      int indexFound = -1;

      int minIdx;
      double firstRes;
      for(minIdx = 0; minIdx < fullSeries.length; ++minIdx) {
         firstRes = fullSeries[minIdx];
         if (minVal <= firstRes && firstRes <= maxVal) {
            indexFound = minIdx;
            break;
         }
      }

      if (indexFound != -1) {
         rc.add(fullSeries[indexFound]);
      } else {
         minIdx = -1;

         for(int i = 0; i < fullSeries.length; ++i) {
            double val = fullSeries[i];
            if (val >= minVal) {
               minIdx = i - 1;
               break;
            }
         }

         if (minIdx != -1) {
            firstRes = fullSeries[minIdx];
            rc.add(firstRes);
            double newRes = resistanceX - firstRes;
            if (maxParts > 1) {
               List<Double> newSub = this.calculateSeriesCircuit(fullSeries, newRes, maxParts - 1, percentPrecision);
               rc.addAll(newSub);
            }
         }
      }

      return rc;
   }

   public double[] createFullSeries(double[] orgSeries, int decades) {
      int orgLen = orgSeries.length;
      int newLen = orgLen * decades;
      double[] rc = new double[newLen];
      int mult = 1;

      for(int i = 0; i < decades; ++i) {
         int offset = i * orgLen;

         for(int j = 0; j < orgLen; ++j) {
            int idx = j + offset;
            double d = orgSeries[j] * (double)mult;
            rc[idx] = d;
         }

         mult *= 10;
      }

      return rc;
   }

   public VNAGenericPad getPad() {
      return this.pad;
   }

   public void setPad(VNAGenericPad pad) {
      this.pad = pad;
   }

   public void reverseCalcPad(double atten) {
      TraceHelper.entry(this, "reverseCalcPad");
      if (this.pad instanceof VNAPiPad) {
         this.reverseCalculatePiPad(atten);
      } else {
         this.reverseCalculateTPad(atten);
      }

      TraceHelper.exit(this, "reverseCalcPad");
   }

   private void reverseCalculateTPad(double atten) {
      TraceHelper.entry(this, "reverseCalculateTPad");
      TraceHelper.exit(this, "reverseCalculateTPad");
   }

   private void reverseCalculatePiPad(double atten) {
      TraceHelper.entry(this, "reverseCalculatePiPad");
      VNAPiPad pp = (VNAPiPad)this.getPad();
      double a = Math.log(10.0D) / 20.0D;
      double z = Math.exp(a * atten);
      double m = pp.getR1() / pp.getR2();
      double r1 = pp.getR3() * (z * z - 2.0D * z * Math.sqrt(m) + 1.0D) / (z * z - 1.0D);
      double r2 = pp.getR5() * (z * z - 2.0D * z / Math.sqrt(m) + 1.0D) / (z * z - 1.0D);
      pp.setR1(r1);
      pp.setR2(r2);
      TraceHelper.exit(this, "reverseCalculatePiPad");
   }
}
