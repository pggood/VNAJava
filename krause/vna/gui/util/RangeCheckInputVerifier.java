package krause.vna.gui.util;

import java.awt.Toolkit;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import krause.vna.resources.VNAMessages;

public class RangeCheckInputVerifier extends InputVerifier {
   private String message = "";
   private NumberFormat formatToUse;
   private boolean emptyValid = false;

   public RangeCheckInputVerifier(NumberFormat pFormatToUse, boolean emptyValid) {
      this.formatToUse = pFormatToUse;
      this.emptyValid = emptyValid;
   }

   public boolean shouldYieldFocus(JComponent input) {
      boolean inputOK = this.verify(input);
      if (inputOK) {
         return true;
      } else {
         input.setInputVerifier((InputVerifier)null);
         this.message = this.message + VNAMessages.getString("Input.Frq.3");
         JOptionPane.showMessageDialog(input.getParent().getParent(), this.message, VNAMessages.getString("Input.Frq.4"), 2);
         input.setInputVerifier(this);
         Toolkit.getDefaultToolkit().beep();
         return false;
      }
   }

   public boolean verify(JComponent input) {
      boolean rc = true;
      int multi = 1;
      double val = 0.0D;
      RangeCheckedTextField source = (RangeCheckedTextField)input;
      String text = source.getText().toUpperCase().trim();
      if (text.length() == 0 && this.emptyValid) {
         rc = true;
      } else {
         try {
            val = NumberFormat.getNumberInstance().parse(text).doubleValue() * (double)multi;
            source.setText(this.formatToUse.format(val));
         } catch (ParseException var9) {
            this.message = VNAMessages.getString("Input.Frq.6");
            rc = false;
         }

         if (rc && val < source.getLowerLimit()) {
            this.message = MessageFormat.format(VNAMessages.getString("Input.Frq.7"), this.formatToUse.format(source.getLowerLimit()));
            rc = false;
         }

         if (rc && val > source.getUpperLimit()) {
            this.message = MessageFormat.format(VNAMessages.getString("Input.Frq.8"), this.formatToUse.format(source.getUpperLimit()));
            rc = false;
         }
      }

      source.setValidData(rc);
      return rc;
   }
}
