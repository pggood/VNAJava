package krause.vna.gui.generator.digit;

import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;

public class VNADigitLabel extends JLabel {
   private void setupLookAndFeel() {
      this.setOpaque(true);
      this.setHorizontalAlignment(0);
      this.setFont(new Font("Tahoma", 0, 51));
   }

   public VNADigitLabel() {
      this.setupLookAndFeel();
   }

   public VNADigitLabel(String text, int horizontalAlignment) {
      super(text, horizontalAlignment);
      this.setupLookAndFeel();
   }

   public VNADigitLabel(Icon image, int horizontalAlignment) {
      super(image, horizontalAlignment);
      this.setupLookAndFeel();
   }

   public VNADigitLabel(Icon image) {
      super(image);
      this.setupLookAndFeel();
   }

   public VNADigitLabel(String text, Icon icon, int horizontalAlignment) {
      super(text, icon, horizontalAlignment);
      this.setupLookAndFeel();
   }

   public VNADigitLabel(String text) {
      super(text);
      this.setupLookAndFeel();
   }

   public void setValue(long value) {
      this.setText("" + value);
   }

   public long getValue() {
      return Long.parseLong(this.getText());
   }
}
