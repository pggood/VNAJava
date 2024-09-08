package krause.vna.device;

import org.apache.commons.math3.complex.Complex;

public class VNATLHelper {
   public static final double SL = 983.571056430446D;
   public static final double SLfps = 9.83571056430446E8D;
   public static final double NEPER = 20.0D / Math.log(10.0D);
   public static final double DEG2RAD = 0.017453292519943295D;
   public static final double RAD2DEG = 57.29577951308232D;
   public static final double PI_HALF = 1.5707963267948966D;
   public static final double TWO_PI = 6.283185307179586D;

   public static VNATLParms getTLParms(double freq, double lenFt, double Zohf, double VFhf, double K1, double K2, double K0) {
      VNATLParms results = new VNATLParms();
      double Rdc = 2.0D * (K0 / 100.0D / NEPER) * Zohf;
      double Rhf = 2.0D * K1 / 100.0D / NEPER * Math.sqrt(freq) * Zohf;
      double Lhf = Zohf / (9.83571056430446E8D * VFhf);
      double Ghf = 2.0D * K2 / 100.0D / NEPER * freq / Zohf;
      double Chf = 1.0D / (Zohf * 9.83571056430446E8D * VFhf);
      double w = 6.283185307179586D * freq * 1000000.0D;
      Complex Zint = (new Complex(Rhf, Rhf)).multiply(new Complex(Rhf, Rhf)).add(new Complex(Rdc * Rdc, 0.0D)).sqrt();
      Complex RjwL = (new Complex(0.0D, w * Lhf)).add(Zint);
      Complex GjwC = new Complex(Ghf, w * Chf);
      Complex Zo = RjwL.divide(GjwC).sqrt();
      Complex Gamma = RjwL.multiply(GjwC).sqrt();
      results.setZ0(Zo);
      results.setCorrectedVf(w / (9.83571056430446E8D * Gamma.getImaginary()));
      results.setLoss(Gamma.getReal() * NEPER * lenFt);
      return results;
   }

   public static Complex ZIZL(Complex Zload, double Freq, double lenFt, double Zohf, double VFhf, double K1, double K2, double K0) {
      Freq /= 1000000.0D;
      VNATLParms vTLParms = getTLParms(Freq, lenFt, Zohf, VFhf, K1, K2, K0);
      Complex Zo = vTLParms.getZ0();
      double VF = vTLParms.getCorrectedVf();
      double Loss = vTLParms.getLoss();
      double AlphaL = Loss / NEPER;
      double BetaL = 6.283185307179586D * Freq / (983.571056430446D * VF) * lenFt;
      Complex Sinh_gL = new Complex(Math.cos(BetaL) * Math.sinh(AlphaL), Math.sin(BetaL) * Math.cosh(AlphaL));
      Complex Cosh_gL = new Complex(Math.cos(BetaL) * Math.cosh(AlphaL), Math.sin(BetaL) * Math.sinh(AlphaL));
      Complex rc = Zo.multiply(Zload.multiply(Cosh_gL).add(Zo.multiply(Sinh_gL)).divide(Zo.multiply(Cosh_gL).add(Zload.multiply(Sinh_gL))));
      return rc;
   }
}
