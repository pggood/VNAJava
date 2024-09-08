package krause.common.gui;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class DocumentSizeFilter extends DocumentFilter {
   int maxCharacters;

   public DocumentSizeFilter(int maxChars) {
      this.maxCharacters = maxChars;
   }

   public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
      if (fb.getDocument().getLength() + str.length() <= this.maxCharacters) {
         super.insertString(fb, offs, str, a);
      } else {
         Toolkit.getDefaultToolkit().beep();
      }

   }

   public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
      if (fb.getDocument().getLength() + str.length() - length <= this.maxCharacters) {
         super.replace(fb, offs, length, str, a);
      } else {
         Toolkit.getDefaultToolkit().beep();
      }

   }
}
