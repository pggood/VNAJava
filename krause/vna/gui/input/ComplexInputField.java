package krause.vna.gui.input;

import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import krause.common.validation.ComplexValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;
import org.apache.commons.math3.complex.Complex;

public class ComplexInputField extends JPanel {
   private transient ComplexInputFieldValueChangeListener listener = null;
   private JTextField txtRefR;
   private JTextField txtRefI;
   private Complex value = new Complex(0.0D, 0.0D);
   private Complex minimum = new Complex(-1.7976931348623157E308D, -1.7976931348623157E308D);
   private Complex maximum = new Complex(Double.MAX_VALUE, Double.MAX_VALUE);
   private NumberFormat numberFormat = NumberFormat.getNumberInstance();

   public ComplexInputField(Complex val) {
      TraceHelper.entry(this, "ComplexInputField");
      this.add(new JLabel(VNAMessages.getString("ComplexField.real")));
      this.txtRefR = new JTextField(5);
      this.add(this.txtRefR);
      this.add(new JLabel(VNAMessages.getString("ComplexField.img")));
      this.txtRefI = new JTextField(5);
      this.add(this.txtRefI);
      if (val != null) {
         this.value = val;
      }

      this.complexValueToFields();
      this.txtRefI.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent e) {
            JTextField tf = (JTextField)e.getComponent();
            tf.select(0, 999);
         }

         public void focusLost(FocusEvent e) {
            Complex old = ComplexInputField.this.value;
            ValidationResults results = new ValidationResults();
            double img = ComplexValidator.parseImaginary(ComplexInputField.this.txtRefI.getText(), ComplexInputField.this.minimum, ComplexInputField.this.maximum, VNAMessages.getString("ComplexField.realField"), results);
            if (results.isEmpty()) {
               ComplexInputField.this.value = new Complex(old.getReal(), img);
               ComplexInputField.this.complexValueToFields();
               if (ComplexInputField.this.listener != null) {
                  ComplexInputField.this.listener.valueChanged(old, ComplexInputField.this.value);
               }
            } else {
               new ValidationResultsDialog((Window)null, results, VNAMessages.getString("ComplexField.ErrorDialogHeader"));
            }

         }
      });
      this.txtRefR.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent e) {
            JTextField tf = (JTextField)e.getComponent();
            tf.select(0, 999);
         }

         public void focusLost(FocusEvent e) {
            Complex old = ComplexInputField.this.value;
            ValidationResults results = new ValidationResults();
            double real = ComplexValidator.parseReal(ComplexInputField.this.txtRefR.getText(), ComplexInputField.this.minimum, ComplexInputField.this.maximum, VNAMessages.getString("ComplexField.imaginaryField"), results);
            if (results.isEmpty()) {
               ComplexInputField.this.value = new Complex(real, old.getImaginary());
               ComplexInputField.this.complexValueToFields();
               if (ComplexInputField.this.listener != null) {
                  ComplexInputField.this.listener.valueChanged(old, ComplexInputField.this.value);
               }
            } else {
               new ValidationResultsDialog((Window)null, results, VNAMessages.getString("ComplexField.ErrorDialogHeader"));
            }

         }
      });
      TraceHelper.exit(this, "ComplexInputField");
   }

   private void complexValueToFields() {
      TraceHelper.entry(this, "complexValueToFields");
      this.txtRefI.setText(this.numberFormat.format(this.value.getImaginary()));
      this.txtRefR.setText(this.numberFormat.format(this.value.getReal()));
      TraceHelper.exit(this, "complexValueToFields");
   }

   public Complex getComplexValue() {
      return this.value;
   }

   public void setComplexValue(Complex complexValue) {
      this.value = complexValue;
      this.complexValueToFields();
   }

   public ComplexInputFieldValueChangeListener getListener() {
      return this.listener;
   }

   public void setListener(ComplexInputFieldValueChangeListener listener) {
      this.listener = listener;
   }

   public Complex getMinimum() {
      return this.minimum;
   }

   public void setMinimum(Complex minimum) {
      this.minimum = minimum;
   }

   public Complex getMaximum() {
      return this.maximum;
   }

   public void setMaximum(Complex maximum) {
      this.maximum = maximum;
   }
}
