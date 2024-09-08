package krause.vna.gui.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class VNAResistenceBaseFormat extends NumberFormat {
   private NumberFormat iFormat = null;

   public VNAResistenceBaseFormat() {
      this.iFormat = NumberFormat.getNumberInstance();
      this.iFormat.setGroupingUsed(false);
      this.iFormat.setMaximumFractionDigits(2);
      this.iFormat.setMinimumFractionDigits(2);
      this.iFormat.setMaximumIntegerDigits(4);
      this.iFormat.setMinimumIntegerDigits(1);
   }

   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.iFormat.format(number, toAppendTo, pos);
   }

   public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.iFormat.format(number, toAppendTo, pos);
   }

   public Number parse(String source, ParsePosition parsePosition) {
      return this.iFormat.parse(source, parsePosition);
   }
}
