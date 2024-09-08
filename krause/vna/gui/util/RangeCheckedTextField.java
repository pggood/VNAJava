package krause.vna.gui.util;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JTextField;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class RangeCheckedTextField extends JTextField implements FocusListener {
   private static final Dimension DEFAULT_SIZE = new Dimension(100, 20);
   private double lowerLimit;
   private double upperLimit;
   private boolean validData;
   private boolean emptyValid;
   private NumberFormat formatToUse;

   public RangeCheckedTextField(NumberFormat fmt) {
      this(fmt, false);
   }

   public RangeCheckedTextField(NumberFormat fmt, boolean emptyValid) {
      this.lowerLimit = 1000000.0D;
      this.upperLimit = 1.0E8D;
      this.validData = false;
      this.emptyValid = false;
      this.formatToUse = null;
      this.setHorizontalAlignment(11);
      this.setMinimumSize(DEFAULT_SIZE);
      this.setMaximumSize(this.getMinimumSize());
      this.setPreferredSize(this.getMinimumSize());
      this.setEmptyValid(emptyValid);
      this.formatToUse = fmt;
      this.setInputVerifier(new RangeCheckInputVerifier(this.formatToUse, this.isEmptyValid()));
      this.addFocusListener(this);
   }

   public void setLowerLimit(double lowerLimit) {
      this.lowerLimit = lowerLimit;
   }

   public void setUpperLimit(double upperLimit) {
      this.upperLimit = upperLimit;
   }

   public double getLowerLimit() {
      return this.lowerLimit;
   }

   public double getUpperLimit() {
      return this.upperLimit;
   }

   public void setValue(Double f) {
      if (f != null) {
         this.setText(NumberFormat.getNumberInstance().format(f));
      } else {
         this.setText("");
      }

   }

   public Double getValue() {
      Double rc = null;
      String text = this.getText().trim();
      if (text.length() != 0 || !this.emptyValid) {
         try {
            rc = NumberFormat.getNumberInstance().parse(text).doubleValue();
         } catch (ParseException var4) {
            ErrorLogHelper.exception(this, "getValue", var4);
         }
      }

      return rc;
   }

   public void focusGained(FocusEvent e) {
      TraceHelper.entry(this, "focusGained");
      RangeCheckedTextField tf = (RangeCheckedTextField)e.getComponent();
      tf.select(0, 999);
      TraceHelper.exit(this, "focusGained");
   }

   public void focusLost(FocusEvent e) {
   }

   public boolean isValidData() {
      return this.validData;
   }

   public void setValidData(boolean validData) {
      this.validData = validData;
   }

   public boolean isEmptyValid() {
      return this.emptyValid;
   }

   public void setEmptyValid(boolean emptyValid) {
      this.emptyValid = emptyValid;
   }
}
