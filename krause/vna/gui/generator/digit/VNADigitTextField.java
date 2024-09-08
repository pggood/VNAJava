package krause.vna.gui.generator.digit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.border.EtchedBorder;
import krause.vna.resources.VNAMessages;

public class VNADigitTextField extends VNADigitLabel implements MouseListener {
   private long factor;

   public VNADigitTextField(int pFactor, long pValue, int pFontSize) {
      this((long)pFactor, pValue);
      this.setFont(this.getFont().deriveFont((float)pFontSize));
      this.setToolTipText(VNAMessages.getString("VNADigitTextField.tooltip"));
   }

   public VNADigitTextField(long pFactor, long pValue) {
      this.setForeground(Color.YELLOW);
      this.setBackground(Color.BLACK);
      this.setCursor(Cursor.getPredefinedCursor(12));
      this.setBorder(new EtchedBorder(1, (Color)null, (Color)null));
      this.setToolTipText(VNAMessages.getString("VNADigitTextField.tooltip"));
      this.setFactor(pFactor);
      this.setValue(pValue);
      this.addMouseListener(this);
   }

   public void setFactor(long value) {
      this.factor = value;
   }

   public long getFactor() {
      return this.factor;
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
      this.setForeground(Color.BLACK);
      this.setBackground(Color.YELLOW);
   }

   public void mouseExited(MouseEvent e) {
      this.setForeground(Color.YELLOW);
      this.setBackground(Color.BLACK);
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public long getValue() {
      return Long.parseLong(this.getText());
   }

   public void setValue(long value) {
      this.setText("" + value);
   }
}
