package krause.common.math;

public class DFFT {
   public static double[] execute(double[] v) {
      int N = v.length;
      double twoPiOnN = 6.283185307179586D / (double)N;
      double[] r_data = new double[N];
      double[] i_data = new double[N];
      double[] psd = new double[N];

      for(int k = 0; k < N; ++k) {
         double twoPikOnN = twoPiOnN * (double)k;

         for(int j = 0; j < N; ++j) {
            double twoPijkOnN = twoPikOnN * (double)j;
            r_data[k] += v[j] * Math.cos(twoPijkOnN);
            i_data[k] -= v[j] * Math.sin(twoPijkOnN);
         }

         r_data[k] /= (double)N;
         i_data[k] /= (double)N;
         psd[k] = r_data[k] * r_data[k] + i_data[k] * i_data[k];
      }

      return psd;
   }
}
