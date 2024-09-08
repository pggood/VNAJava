package krause.vna.data.fft;

import java.util.ArrayList;
import java.util.List;
import krause.common.math.FFT;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import org.apache.commons.math3.complex.Complex;

public class VNAFFTHelper {
   public static Complex[] createFFTDataPoints(VNACalibratedSample[] input) {
      int inputLen = input.length;
      int nx = (int)(Math.log((double)inputLen) / Math.log(2.0D));
      if (1 << nx != inputLen) {
         ++nx;
      }

      int newLen = 1 << nx;
      Complex[] rc = new Complex[newLen];

      int i;
      for(i = 0; i < inputLen; ++i) {
         rc[i] = input[i].getRHO();
      }

      for(i = inputLen; i < newLen; ++i) {
         rc[i] = Complex.ZERO;
      }

      return rc;
   }

   public static Complex[] doIFFT(VNACalibratedSample[] calibratedSamples) {
      Complex[] fftIn = createFFTDataPoints(calibratedSamples);
      Complex[] rc = FFT.ifft(fftIn);
      return rc;
   }

   public static Complex[] doFFT(VNACalibratedSample[] calibratedSamples) {
      Complex[] fftIn = createFFTDataPoints(calibratedSamples);
      Complex[] rc = FFT.fft(fftIn);
      return rc;
   }

   public static double[] getABS(VNACalibratedSample[] inp) {
      double[] rc = new double[inp.length];

      for(int i = 0; i < inp.length; ++i) {
         double radian = inp[i].getReflectionPhase() * 3.141592653589793D / 180.0D;
         double amplitude = Math.pow(10.0D, inp[i].getReflectionLoss() / 20.0D);
         double real = amplitude * Math.cos(radian);
         double imag = amplitude * Math.sin(radian);
         rc[i] = (new Complex(real, imag)).abs();
      }

      return rc;
   }

   public static double[] getABS(Complex[] inp) {
      double[] rc = new double[inp.length];

      for(int i = 0; i < inp.length; ++i) {
         rc[i] = inp[i].abs();
      }

      return rc;
   }

   public static int[] findPeaks(double[] input) {
      List<Integer> peakList = new ArrayList();
      int inputLen = input.length;
      double[] gradient = new double[inputLen - 1];
      double lastY = input[0];
      double currGradient;
      for(int i = 1; i < inputLen; ++i) {
         double currY = input[i];
         currGradient = currY - lastY;
         if (Math.abs(currGradient) < 0.005D) {
            currGradient = 0.0D;
         }

         gradient[i - 1] = currGradient;
         lastY = currY;
      }

      int state = 0;
      int max = gradient.length;

      for(int i = 0; i < max; ++i) {
         currGradient = gradient[i];
         switch(state) {
         case 0:
            if (currGradient > 0.0D) {
               state = 1;
            }
            break;
         case 1:
            if (currGradient < 0.0D) {
               peakList.add(i);
               state = 0;
            }
         }
      }

      int j = peakList.size();
      int[] peaks = new int[j];

      for(int i = 0; i < j; ++i) {
         peaks[i] = (Integer)peakList.get(i);
      }

      TraceHelper.exitWithRC(Class.class, "findPeaks", peakList);
      return peaks;
   }

   public static int[] findPeaks2(double[] fftAbs) {
      int[] rc = null;
      double min = 2.2250738585072014E-308D;
      int minIdx = -1;

      for(int i = 0; i < fftAbs.length; ++i) {
         if (fftAbs[i] > min) {
            min = fftAbs[i];
            minIdx = i;
         }
      }

      if (minIdx != -1) {
         rc = new int[]{minIdx};
      }

      return rc;
   }

   public static Complex[] createFFTDataPoints2(VNACalibratedSample[] input) {
      int inputLen = input.length;
      int nx = (int)(Math.log((double)inputLen) / Math.log(2.0D));
      if (1 << nx != inputLen) {
         ++nx;
      }

      int newLen = 1 << nx;
      Complex[] rc = new Complex[newLen];

      int i;
      for(i = 0; i < inputLen; ++i) {
         VNACalibratedSample orgPt = input[i];
         double radian = orgPt.getReflectionPhase() * 3.141592653589793D / 180.0D;
         double amplitude = Math.pow(10.0D, orgPt.getReflectionLoss() / 20.0D);
         double real = amplitude * Math.cos(radian);
         double imag = amplitude * Math.sin(radian);
         rc[i] = new Complex(real, imag);
      }

      for(i = inputLen; i < newLen; ++i) {
         rc[i] = Complex.ZERO;
      }

      return rc;
   }
}
