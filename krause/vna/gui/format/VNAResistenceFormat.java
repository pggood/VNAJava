package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNAResistenceFormat extends NumberFormat {
   public static final char OMEGA = '\u03A9';  // Unicode for Ω
   private NumberFormat baseFormat = new VNAResistenceBaseFormat();

   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
      if (number < 1.0E-9D) {
         return toAppendTo.append(this.baseFormat.format(number * 1.0E12D)).append("p").append("Ω");
      } else if (number < 1.0E-6D) {
         return toAppendTo.append(this.baseFormat.format(number * 1.0E9D)).append("n").append("Ω");
      } else if (number < 0.001D) {
         return toAppendTo.append(this.baseFormat.format(number * 1000000.0D)).append("u").append("Ω");
      } else if (number < 1.0D) {
         return toAppendTo.append(this.baseFormat.format(number * 1000.0D)).append("m").append("Ω");
      } else if (number < 1000.0D) {
         return toAppendTo.append(this.baseFormat.format(number / 1.0D)).append("").append("Ω");
      } else if (number < 1000000.0D) {
         return toAppendTo.append(this.baseFormat.format(number / 1000.0D)).append("k").append("Ω");
      } else {
         return number < 1.0E9D ? toAppendTo.append(this.baseFormat.format(number / 1000000.0D)).append("M").append("Ω") : toAppendTo;
      }
   }

   public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      return null;
   }

   public Number parse(String source, ParsePosition parsePosition) {
      return this.baseFormat.parse(source, parsePosition);
   }
}
