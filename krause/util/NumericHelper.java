package krause.util;

import java.math.BigDecimal;

public class NumericHelper {
   public static double getDoubleWithDefault(Double theVal, double theDef) {
      return theVal == null ? theDef : theVal;
   }

   public static double getDoubleWithDefault(Integer theVal, double theDef) {
      return theVal == null ? theDef : theVal.doubleValue();
   }

   public static int getIntegerWithDefault(Integer theVal, int theDef) {
      return theVal == null ? theDef : theVal;
   }

   public static Double convertBigDecimal2Double(BigDecimal val) {
      return val == null ? null : val.doubleValue();
   }
}
