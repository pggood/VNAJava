package krause.vna.data.filter;

import krause.util.ras.logging.ErrorLogHelper;

public class Gaussian {
   private double sigma = 1.0D;
   private double sqrSigma = 1.0D;

   public Gaussian(double sigma) {
      this.setSigma(sigma);
   }

   public double getSigma() {
      return this.sigma;
   }

   public void setSigma(double sigma) {
      this.sigma = Math.max(1.0E-8D, sigma);
      this.sqrSigma = sigma * sigma;
   }

   public double function1D(double x) {
      return Math.exp(x * x / (-2.0D * this.sqrSigma)) / (Math.sqrt(6.283185307179586D) * this.sigma);
   }

   public double function2D(double x, double y) {
      return Math.exp(-(x * x + y * y) / (2.0D * this.sqrSigma));
   }

   public double[] kernel1D(int size) {
      if (size % 2 == 0 || size < 3 || size > 101) {
         ErrorLogHelper.text(this, "kernel1D", "Wrong size");
      }

      int r = size / 2;
      double[] kernel = new double[size];
      int x = -r;

      for(int i = 0; i < size; ++i) {
         kernel[i] = this.function1D((double)x);
         ++x;
      }

      return kernel;
   }

   public double[][] kernel2D(int size) {
      if (size % 2 == 0 || size < 3 || size > 101) {
         ErrorLogHelper.text(this, "kernel2D", "Wrong size");
      }

      int r = size / 2;
      double[][] kernel = new double[size][size];
      double sum = 0.0D;
      int i = -r;

      int j;
      for(j = 0; j < size; ++j) {
         int x = -r;

         for(int k = 0; k < size; ++k) {
            kernel[j][k] = this.function2D((double)x, (double)i);
            sum += kernel[j][k];
            ++x;
         }

         ++i;
      }

      for(i = 0; i < kernel.length; ++i) {
         for(j = 0; j < kernel[0].length; ++j) {
            kernel[i][j] /= sum;
         }
      }

      return kernel;
   }
}
