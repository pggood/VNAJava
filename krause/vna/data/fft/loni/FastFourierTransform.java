package krause.vna.data.fft.loni;

public final class FastFourierTransform {
   private FastFourierTransform() {
   }

   public static void fastFT(double[][][] realArray, double[][][] imagArray, boolean direction) {
      int numOfFrame = 0;
      int numOfRow = 0;
      int numOfCol = 0;
      if (realArray != null) {
         numOfFrame = realArray.length;
         numOfRow = realArray[0].length;
         numOfCol = realArray[0][0].length;
      }

      int numOfFrameImag = 0;
      int numOfRowImag = 0;
      int numOfColImag = 0;
      if (imagArray != null) {
         numOfFrameImag = imagArray.length;
         numOfRowImag = imagArray[0].length;
         numOfColImag = imagArray[0][0].length;
      }

      double[] realRow = new double[numOfRow];
      double[] imagRow = new double[numOfRow];
      double[] realFrame = new double[numOfFrame];
      double[] imagFrame = new double[numOfFrame];

      try {
         _checkSizeOfArray(numOfFrame, numOfRow, numOfCol, numOfFrameImag, numOfRowImag, numOfColImag);
         int numOfPowerInFrame = _checkPowerOf2(numOfFrame);
         int numOfPowerInRow = _checkPowerOf2(numOfRow);
         int numOfPowerInCol = _checkPowerOf2(numOfCol);

         int row;
         int col;
         for(row = 0; row < numOfFrame; ++row) {
            for(col = 0; col < numOfRow; ++col) {
               _fastFT1D(realArray[row][col], imagArray[row][col], numOfPowerInCol, direction);
            }
         }

         int frame;
         for(row = 0; row < numOfFrame; ++row) {
            for(col = 0; col < numOfCol; ++col) {
               for(frame = 0; frame < numOfRow; ++frame) {
                  realRow[frame] = realArray[row][frame][col];
                  imagRow[frame] = imagArray[row][frame][col];
               }

               _fastFT1D(realRow, imagRow, numOfPowerInRow, direction);

               for(frame = 0; frame < numOfRow; ++frame) {
                  realArray[row][frame][col] = realRow[frame];
                  imagArray[row][frame][col] = imagRow[frame];
               }
            }
         }

         for(row = 0; row < numOfRow; ++row) {
            for(col = 0; col < numOfCol; ++col) {
               for(frame = 0; frame < numOfFrame; ++frame) {
                  realFrame[frame] = realArray[frame][row][col];
                  imagFrame[frame] = imagArray[frame][row][col];
               }

               _fastFT1D(realFrame, imagFrame, numOfPowerInFrame, direction);

               for(frame = 0; frame < numOfFrame; ++frame) {
                  realArray[frame][row][col] = realFrame[frame];
                  imagArray[frame][row][col] = imagFrame[frame];
               }
            }
         }
      } catch (NotSameArraySizeException var19) {
         System.out.println("\n" + var19);
         System.out.println("Warning: Please assign real and imaginary arrays with the same dimension");
      } catch (NotPowerOf2Exception var20) {
         System.out.println("\n" + var20);
         System.out.println("Warning: Please assign an array with the length equal to an integer power of 2");
      }

   }

   public static void fastFT(double[][] realArray, double[][] imagArray, boolean direction) {
      int numOfRow = 0;
      int numOfCol = 0;
      if (realArray != null) {
         numOfRow = realArray.length;
         numOfCol = realArray[0].length;
      }

      int numOfRowImag = 0;
      int numOfColImag = 0;
      if (imagArray != null) {
         numOfRowImag = imagArray.length;
         numOfColImag = imagArray[0].length;
      }

      double[] realRow = new double[numOfRow];
      double[] imagRow = new double[numOfRow];

      try {
         _checkSizeOfArray(numOfRow, numOfCol, numOfRowImag, numOfColImag);
         int numOfPowerInRow = _checkPowerOf2(numOfRow);
         int numOfPowerInCol = _checkPowerOf2(numOfCol);

         int col;
         for(col = 0; col < numOfRow; ++col) {
            _fastFT1D(realArray[col], imagArray[col], numOfPowerInCol, direction);
         }

         for(col = 0; col < numOfCol; ++col) {
            int row;
            for(row = 0; row < numOfRow; ++row) {
               realRow[row] = realArray[row][col];
               imagRow[row] = imagArray[row][col];
            }

            _fastFT1D(realRow, imagRow, numOfPowerInRow, direction);

            for(row = 0; row < numOfRow; ++row) {
               realArray[row][col] = realRow[row];
               imagArray[row][col] = imagRow[row];
            }
         }
      } catch (NotSameArraySizeException var13) {
         System.out.println("\n" + var13);
         System.out.println("Warning: Please assign real and imaginary arrays with the same dimension");
      } catch (NotPowerOf2Exception var14) {
         System.out.println("\n" + var14);
         System.out.println("Warning: Please assign an array with the length equal to an integer power of 2");
      }

   }

   public static void fastFT(double[] realArray, double[] imagArray, boolean direction) {
      int numOfReal = 0;
      if (realArray != null) {
         numOfReal = realArray.length;
      }

      int numOfImag = 0;
      if (imagArray != null) {
         numOfImag = imagArray.length;
      }

      try {
         _checkSizeOfArray(numOfReal, numOfImag);
         int numOfPower = _checkPowerOf2(numOfReal);
         _fastFT1D(realArray, imagArray, numOfPower, direction);
      } catch (NotSameArraySizeException var6) {
         System.out.println("\n" + var6);
         System.out.println("Warning: Please assign real and imaginary arrays with the same dimension");
      } catch (NotPowerOf2Exception var7) {
         System.out.println("\n" + var7);
         System.out.println("Warning: Please assign an array with the length equal to an integer power of 2");
      }

   }

   private static void _checkSizeOfArray(int frameReal, int rowReal, int colReal, int frameImag, int rowImag, int colImag) throws NotSameArraySizeException {
      if (frameReal != frameImag || rowReal != rowImag || colReal != colImag) {
         throw new NotSameArraySizeException();
      }
   }

   private static void _checkSizeOfArray(int rowReal, int colReal, int rowImag, int colImag) throws NotSameArraySizeException {
      if (rowReal != rowImag || colReal != colImag) {
         throw new NotSameArraySizeException();
      }
   }

   private static void _checkSizeOfArray(int numOfReal, int numOfImag) throws NotSameArraySizeException {
      if (numOfReal != numOfImag) {
         throw new NotSameArraySizeException();
      }
   }

   private static int _checkPowerOf2(int index) throws NotPowerOf2Exception {
      if (index < 2) {
         throw new NotPowerOf2Exception(index);
      } else if ((index & index - 1) != 0) {
         throw new NotPowerOf2Exception(index);
      } else {
         int i;
         for(i = 0; (index & 1 << i) == 0; ++i) {
         }

         return i;
      }
   }

   private static void _fastFT1D(double[] real, double[] imag, int numOfPower, boolean direct) {
      int numOfPoint = real.length;
      int i2 = numOfPoint >> 1;
      int j2 = 0;

      int i;
      for(i = 0; i < numOfPoint - 1; ++i) {
         if (i < j2) {
            double tr = real[i];
            double ti = imag[i];
            real[i] = real[j2];
            imag[i] = imag[j2];
            real[j2] = tr;
            imag[j2] = ti;
         }

         int k2;
         for(k2 = i2; k2 <= j2; k2 >>= 1) {
            j2 -= k2;
         }

         j2 += k2;
      }

      double c1 = -1.0D;
      double c2 = 0.0D;
      int l2 = 1;

      for(i = 0; i < numOfPower; ++i) {
         int l1 = l2;
         l2 <<= 1;
         double u1 = 1.0D;
         double u2 = 0.0D;
         int i1;
         for(int j = 0; j < l1; ++j) {
            for(i = j; i < numOfPoint; i += l2) {
               i1 = i + l1;
               double t1 = u1 * real[i1] - u2 * imag[i1];
               double t2 = u1 * imag[i1] + u2 * real[i1];
               real[i1] = real[i] - t1;
               imag[i1] = imag[i] - t2;
               real[i] += t1;
               imag[i] += t2;
            }

            double z = u1 * c1 - u2 * c2;
            u2 = u1 * c2 + u2 * c1;
            u1 = z;
         }

         c2 = Math.sqrt((1.0D - c1) / 2.0D);
         if (direct) {
            c2 = -c2;
         }

         c1 = Math.sqrt((1.0D + c1) / 2.0D);
      }

      if (direct) {
         for(i = 0; i < numOfPoint; ++i) {
            real[i] /= (double)numOfPoint;
            imag[i] /= (double)numOfPoint;
         }
      }

   }
}
