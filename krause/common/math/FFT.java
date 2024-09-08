package krause.common.math;

import org.apache.commons.math3.complex.Complex;

public class FFT {
   public static Complex[] fft(Complex[] x) {
      int N = x.length;
      if (N == 1) {
         return new Complex[]{x[0]};
      } else if (N % 2 != 0) {
         throw new RuntimeException("N is not a power of 2");
      } else {
         Complex[] even = new Complex[N / 2];

         for(int k = 0; k < N / 2; ++k) {
            even[k] = x[2 * k];
         }

         Complex[] q = fft(even);
         Complex[] odd = even;

         for(int k = 0; k < N / 2; ++k) {
            odd[k] = x[2 * k + 1];
         }

         Complex[] r = fft(odd);
         Complex[] y = new Complex[N];

         for(int k = 0; k < N / 2; ++k) {
            double kth = (double)(-2 * k) * 3.141592653589793D / (double)N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].add(wk.multiply(r[k]));
            y[k + N / 2] = q[k].subtract(wk.multiply(r[k]));
         }

         return y;
      }
   }

   public static Complex[] ifft(Complex[] x) {
      int N = x.length;
      Complex[] y = new Complex[N];

      int i;
      for(i = 0; i < N; ++i) {
         y[i] = x[i].conjugate();
      }

      y = fft(y);

      for(i = 0; i < N; ++i) {
         y[i] = y[i].conjugate();
      }

      for(i = 0; i < N; ++i) {
         y[i] = y[i].multiply(1.0D / (double)N);
      }

      return y;
   }

   public static Complex[] cconvolve(Complex[] x, Complex[] y) {
      if (x.length != y.length) {
         throw new RuntimeException("Dimensions don't agree");
      } else {
         int N = x.length;
         Complex[] a = fft(x);
         Complex[] b = fft(y);
         Complex[] c = new Complex[N];

         for(int i = 0; i < N; ++i) {
            c[i] = a[i].multiply(b[i]);
         }

         return ifft(c);
      }
   }

   public static Complex[] convolve(Complex[] x, Complex[] y) {
      Complex[] a = new Complex[2 * x.length];

      for(int i = 0; i < x.length; ++i) {
         a[i] = x[i];
      }

      for(int i = x.length; i < 2 * x.length; ++i) {
         a[i] = Complex.ZERO;
      }

      Complex[] b = new Complex[2 * y.length];

      for(int i = 0; i < y.length; ++i) {
         b[i] = y[i];
      }

      for(int i = y.length; i < 2 * y.length; ++i) {
         b[i] = Complex.ZERO;
      }

      return cconvolve(a, b);
   }

   public static void show(Complex[] x, String title) {
      System.out.println(title);
      System.out.println("-------------------");

      for(int i = 0; i < x.length; ++i) {
         System.out.println(x[i].getReal() + "+" + x[i].getImaginary() + "i");
      }

      System.out.println();
   }

   public static void main(String[] args) {
      int N = 4;
      Complex[] x = new Complex[N];

      for(int i = 0; i < N; ++i) {
         x[i] = new Complex((double)i, 0.0D);
         x[i] = new Complex(-2.0D * Math.random() + 1.0D, 0.0D);
      }

      show(x, "x");
      Complex[] y = fft(x);
      show(y, "y = fft(x)");
      Complex[] z = ifft(y);
      show(z, "z = ifft(y)");
      Complex[] c = cconvolve(x, x);
      show(c, "c = cconvolve(x, x)");
      Complex[] d = convolve(x, x);
      show(d, "d = convolve(x, x)");
   }
}
