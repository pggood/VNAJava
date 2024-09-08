package krause.vna.gui.input;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import javax.swing.JTextField;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class FrequencyInputField extends JTextField implements FocusListener {
   public static final int MAX_LEN = 9;
   public static final Dimension DEFAULT_SIZE = new Dimension(100, 20);
   private long lowerLimit = 1000000L;
   private long upperLimit = 9999999999L;
   private boolean validData = false;

   public FrequencyInputField(String name, long defaultValue) {
      this.setHorizontalAlignment(11);
      this.setFrequency(defaultValue);
      this.addFocusListener(this);
      this.setToolTipText(VNAMessages.getString("Input.Frq.1"));
      this.setName(name);
      this.setInputVerifier(new FrequencyInputVerifier());
   }

   public void setLowerLimit(long lowerLimit) {
      this.lowerLimit = lowerLimit;
   }

   public void setUpperLimit(long upperLimit) {
      this.upperLimit = upperLimit;
   }

   public FrequencyInputField(String name, long defaultValue, long lowLimit, long highLimit) {
      super(9);
      this.setHorizontalAlignment(11);
      this.setFrequency(defaultValue);
      this.addFocusListener(this);
      this.setToolTipText(VNAMessages.getString("Input.Frq.1"));
      this.setName(name);
      this.lowerLimit = lowLimit;
      this.upperLimit = highLimit;
      this.setInputVerifier(new FrequencyInputVerifier());
   }

   public long getLowerLimit() {
      return this.lowerLimit;
   }

   public long getUpperLimit() {
      return this.upperLimit;
   }

   public void setFrequency(long f) {
      this.setText(VNAFormatFactory.getFrequencyFormat().format(f));
      this.setValidData(true);
   }

   public long getFrequency() {
      long rc = 0L;

      try {
         rc = VNAFormatFactory.getFrequencyFormat().parse(this.getText()).longValue();
      } catch (ParseException var4) {
         ErrorLogHelper.exception(this, "getFrequency", var4);
      }

      return rc;
   }

   public void focusGained(FocusEvent e) {
      TraceHelper.entry(this, "focusGained");
      FrequencyInputField tf = (FrequencyInputField)e.getComponent();
      tf.select(0, 999);
      TraceHelper.exit(this, "focusGained");
   }

   public void focusLost(FocusEvent e) {
      TraceHelper.entry(this, "focusLost");
      TraceHelper.exit(this, "focusLost");
   }

   public boolean isValidData() {
      return this.validData;
   }

   public void setValidData(boolean validData) {
      this.validData = validData;
   }
}
