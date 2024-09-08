package krause.util;

public abstract class ArrayHelper {
   public static String intArrayToString(int[] array) {
      String rc = new String();

      for(int i = 0; i < array.length; ++i) {
         rc = rc + array[i] + " ";
      }

      return rc;
   }

   public static String byteArrayToString(byte[] array) {
      String rc = new String();

      for(int i = 0; i < array.length; ++i) {
         rc = rc + Integer.toHexString(array[i]) + " ";
      }

      return rc;
   }

   public static byte[] intArray2ByteArray(int[] inbuf) {
      byte[] tbuf = new byte[inbuf.length];

      for(int i = 0; i < inbuf.length; ++i) {
         tbuf[i] = (byte)(inbuf[i] % 256);
      }

      return tbuf;
   }

   public static String doubleArrayToString(double[] inp) {
      String rc = new String();
      if (inp != null) {
         for(int i = 0; i < inp.length; ++i) {
            rc = rc + Double.toString(inp[i]) + " ";
         }
      }

      return rc;
   }
}
