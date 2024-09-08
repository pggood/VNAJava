package krause.vna.gui;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import javax.swing.JLabel;
import krause.util.ras.logging.TraceHelper;

public class HistorizedLabel extends JLabel implements MouseListener {
   private int maxHistorySize = 100;
   private transient LinkedList<HistorizedLabelEntry> history = null;

   public HistorizedLabel(String string) {
      super(string);
      TraceHelper.entry(this, "VNAHistorizedStatusLabel");
      this.addMouseListener(this);
      TraceHelper.exit(this, "VNAHistorizedStatusLabel");
   }

   public void mouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "mouseClicked");
      new HistorizedLabelDialog((Frame)null, this.history);
      TraceHelper.exit(this, "mouseClicked");
   }

   public void mouseEntered(MouseEvent e) {
      TraceHelper.entry(this, "mouseEntered");
      TraceHelper.exit(this, "mouseEntered");
   }

   public void mouseExited(MouseEvent e) {
      TraceHelper.entry(this, "mouseExited");
      TraceHelper.exit(this, "mouseExited");
   }

   public void mousePressed(MouseEvent e) {
      TraceHelper.entry(this, "mousePressed");
      TraceHelper.exit(this, "mousePressed");
   }

   public void mouseReleased(MouseEvent e) {
      TraceHelper.entry(this, "mouseReleased");
      TraceHelper.exit(this, "mouseReleased");
   }

   public void setText(String text) {
      if (text != null) {
         text = text.trim();
         if (text.length() > 0) {
            super.setText(text);
            HistorizedLabelEntry newEntry = new HistorizedLabelEntry(text, System.currentTimeMillis());
            if (this.history == null) {
               this.history = new LinkedList();
            }

            this.history.addFirst(newEntry);
            if (this.history.size() > this.maxHistorySize) {
               this.history.removeLast();
            }
         }
      }

   }
}
