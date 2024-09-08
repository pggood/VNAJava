package krause.vna.gui.input;

import java.awt.Toolkit;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class FrequencyInputVerifier extends InputVerifier {
   private String message = "";

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
      String methodName = "verify";
      TraceHelper.entry(this, "verify");
      boolean rc = true;
      int multi = 1;
      double val = 0.0D;
      FrequencyInputField source = (FrequencyInputField)input;
      TraceHelper.text(this, "verify", source.getName());
      String text = source.getText().toUpperCase();
      if (text.length() < 2) {
         this.message = VNAMessages.getString("Input.Frq.5");
         rc = false;
      }

      if (rc) {
         if (text.endsWith("G")) {
            multi = 1000000000;
            text = text.substring(0, text.length() - 1);
         }

         if (text.endsWith("M")) {
            multi = 1000000;
            text = text.substring(0, text.length() - 1);
         }

         if (text.endsWith("K")) {
            multi = 1000;
            text = text.substring(0, text.length() - 1);
         }

         if ("MIN".equals(text)) {
            val = (double)source.getLowerLimit();
            source.setText(VNAFormatFactory.getFrequencyFormat().format(val));
         } else if ("MAX".equals(text)) {
            val = (double)source.getUpperLimit();
            source.setText(VNAFormatFactory.getFrequencyFormat().format(val));
         } else {
            try {
               val = NumberFormat.getNumberInstance().parse(text).doubleValue() * (double)multi;
               source.setText(VNAFormatFactory.getFrequencyFormat().format(val));
            } catch (ParseException var10) {
               this.message = VNAMessages.getString("Input.Frq.6");
               rc = false;
            }
         }
      }

      if (rc && val < (double)source.getLowerLimit()) {
         this.message = MessageFormat.format(VNAMessages.getString("Input.Frq.7"), VNAFormatFactory.getFrequencyFormat().format(source.getLowerLimit()));
         rc = false;
      }

      if (rc && val > (double)source.getUpperLimit()) {
         this.message = MessageFormat.format(VNAMessages.getString("Input.Frq.8"), VNAFormatFactory.getFrequencyFormat().format(source.getUpperLimit()));
         rc = false;
      }

      source.setValidData(rc);
      TraceHelper.exitWithRC(this, "verify", "rc=%d", rc);
      return rc;
   }
}
