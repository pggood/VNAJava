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

public class MouseAdapter4Label extends MouseAdapter implements ClipboardOwner {
   private JLabel label = null;

   public MouseAdapter4Label(JLabel pLabel) {
      this.label = pLabel;
      this.label.addMouseListener(this);
   }

   public void mouseClicked(MouseEvent e) {
      String txt = this.label.getText();
      if (txt != null) {
         txt = txt.trim();
         if (txt.length() > 0) {
            if (e.isShiftDown()) {
               Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
               StringSelection str = new StringSelection(txt);
               cb.setContents(str, this);
            } else {
               JOptionPane.showMessageDialog(this.label, txt, VNAMessages.getString("VNAMainFrame.StatusPanel.status.title"), 1);
            }
         } else {
            Toolkit.getDefaultToolkit().beep();
         }
      } else {
         Toolkit.getDefaultToolkit().beep();
      }

   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
      TraceHelper.entry(this, "lostOwnership");
      TraceHelper.exit(this, "lostOwnership");
   }
}
