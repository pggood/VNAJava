package krause.util;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;

public abstract class StringHelper {
   static final byte[] TBL_HEX = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

   public static String double2String(double val) {
      return NumberFormat.getInstance().format(val);
   }

   public static String int2String(int val) {
      return NumberFormat.getInstance().format((long)val);
   }

   public static String trimLength(String input, int maxlen) {
      return input != null && input.length() > maxlen ? input.substring(0, maxlen) : input;
   }

   public static String array2String(String[] strings, String delimiter) {
      StringBuffer sb = new StringBuffer();
      int max = strings.length - 1;

      for(int i = 0; i <= max; ++i) {
         sb.append(strings[i]);
         sb.append(delimiter);
      }

      return sb.toString();
   }

   public static String byteArrayToString1(byte[] input) throws UnsupportedEncodingException {
      byte[] hex = new byte[3 * input.length];
      int index = 0;
      byte[] var6 = input;
      int var5 = input.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         byte b = var6[var4];
         int v = b & 255;
         hex[index++] = TBL_HEX[v >>> 4];
         hex[index++] = TBL_HEX[v & 15];
         hex[index++] = 32;
      }

      return new String(hex, "ASCII");
   }

   public static String byteArrayToString2(byte[] input) throws UnsupportedEncodingException {
      byte[] hex = new byte[3 * input.length];
      int index = 0;
      byte[] var6 = input;
      int var5 = input.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         byte b = var6[var4];
         hex[index++] = b;
         hex[index++] = 32;
         hex[index++] = 32;
      }

      return new String(hex, "ASCII");
   }
}
