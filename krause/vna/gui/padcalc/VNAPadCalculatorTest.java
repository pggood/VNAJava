package krause.vna.gui.padcalc;

import junit.framework.TestCase;

public class VNAPadCalculatorTest extends TestCase {
   public void test0() {
      VNAPadCalculator pc = new VNAPadCalculator();
      double[] fullSeries = pc.createFullSeries(VNAPadConstants.E48Factors, 7);
      assertNotNull(fullSeries);
   }

   public void test1() {
      VNAPadCalculator pc = new VNAPadCalculator();
      double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);

      for(int i = 0; i < 10; ++i) {
         double resistanceX = (double)Math.round(Math.random() * 1000000.0D);
         System.out.println("target=" + resistanceX);
         System.out.println("   " + pc.calculateSeriesCircuit(fullSeries, resistanceX, 3, 0.001D));
         System.out.println("   " + pc.calculateSeriesCircuit(fullSeries, resistanceX, 2, 0.01D));
         System.out.println("   " + pc.calculateSeriesCircuit(fullSeries, resistanceX, 2, 0.1D));
      }

   }

   public void test2() {
      VNAPadCalculator pc = new VNAPadCalculator();
      double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
      System.out.println("207,4=" + pc.calculateSeriesCircuit(fullSeries, 207.4D, 4, 0.01D));
      System.out.println(" 87,1=" + pc.calculateSeriesCircuit(fullSeries, 87.1D, 4, 0.01D));
      System.out.println(" 77,1=" + pc.calculateSeriesCircuit(fullSeries, 77.1D, 4, 0.01D));
   }

   public void test3() {
      VNAPadCalculator pc = new VNAPadCalculator();
      double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
      System.out.println("103,30=" + pc.calculateSeriesCircuit(fullSeries, 103.3D, 4, 0.01D));
      System.out.println("246,30=" + pc.calculateSeriesCircuit(fullSeries, 246.3D, 4, 0.01D));
      System.out.println(" 60,43=" + pc.calculateSeriesCircuit(fullSeries, 60.43D, 4, 0.01D));
   }

   public void test4() {
      VNAPadCalculator pc = new VNAPadCalculator();
      double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
      System.out.println("207,4=" + pc.calculateSeriesCircuit(fullSeries, 207.4D, 4, 0.001D));
      System.out.println(" 87,1=" + pc.calculateSeriesCircuit(fullSeries, 87.1D, 4, 0.001D));
      System.out.println(" 77,1=" + pc.calculateSeriesCircuit(fullSeries, 77.1D, 4, 0.001D));
   }

   public void test5() {
      VNAPadCalculator pc = new VNAPadCalculator();
      double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
      System.out.println("103,30=" + pc.calculateSeriesCircuit(fullSeries, 103.3D, 4, 0.001D));
      System.out.println("246,30=" + pc.calculateSeriesCircuit(fullSeries, 246.3D, 4, 0.001D));
      System.out.println(" 60,43=" + pc.calculateSeriesCircuit(fullSeries, 60.43D, 4, 0.001D));
   }
}
