package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNATimeFormat extends NumberFormat {
   private NumberFormat iFormat = null;

   public VNATimeFormat() {
      this.iFormat = NumberFormat.getNumberInstance();
      this.iFormat.setGroupingUsed(false);
      this.iFormat.setMaximumFractionDigits(2);
      this.iFormat.setMinimumFractionDigits(2);
      this.iFormat.setMaximumIntegerDigits(4);
      this.iFormat.setMinimumIntegerDigits(1);
   }

   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
      if (number < 1.0E-9D) {
         return toAppendTo.append(this.iFormat.format(number * 1.0E12D)).append(" ps");
      } else if (number < 1.0E-6D) {
         return toAppendTo.append(this.iFormat.format(number * 1.0E9D)).append(" ns");
      } else if (number < 0.001D) {
         return toAppendTo.append(this.iFormat.format(number * 1000000.0D)).append(" us");
      } else {
         return number < 1.0D ? toAppendTo.append(this.iFormat.format(number * 1000.0D)).append(" ms") : toAppendTo.append(this.iFormat.format(number)).append(" s");
      }
   }

   public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      return null;
   }

   public Number parse(String source, ParsePosition parsePosition) {
      return null;
   }
}
