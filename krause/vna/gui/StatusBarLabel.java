package krause.vna.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;

public class StatusBarLabel extends JLabel implements ClipboardOwner {
   private String fullText;
   private int maxLength;

   public StatusBarLabel(String string, int cutLength) {
      this.maxLength = cutLength;
      this.setText(string);
      this.setOpaque(true);
      this.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            TraceHelper.entry(this, "mouseClicked");
            StatusBarLabel.this.handleMouseClicked(e);
            TraceHelper.exit(this, "mouseClicked");
         }
      });
   }

   protected void handleMouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "handleMouseClicked");
      String txt = this.getFullText();
      if (txt != null) {
         txt = txt.trim();
         if (txt.length() > 0) {
            if (e.isShiftDown()) {
               Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
               StringSelection str = new StringSelection(txt);
               cb.setContents(str, this);
            } else {
               JOptionPane.showMessageDialog(this, txt, VNAMessages.getString("VNAMainFrame.StatusPanel.status.title"), 1);
            }
         } else {
            Toolkit.getDefaultToolkit().beep();
         }
      } else {
         Toolkit.getDefaultToolkit().beep();
      }

      TraceHelper.exit(this, "handleMouseClicked");
   }

   public String getFullText() {
      return this.fullText;
   }

   public void setText(String text) {
      this.fullText = text;
      if (text != null) {
         text = text.replace('\r', ' ');
         text = text.replace('\n', ' ');
         text = text.replace('\t', ' ');
         text = text.replace("<br/>", " ");
         if (text.length() > this.maxLength) {
            super.setText(text.substring(0, this.maxLength) + " ...");
         } else {
            super.setText(text);
         }
      }

   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      TraceHelper.entry(this, "lostOwnership");
      TraceHelper.exit(this, "lostOwnership");
   }
}
