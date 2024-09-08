package krause.vna.firmware;

import java.awt.Font;
import java.util.Locale;
import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;

public class SimpleStringListbox extends JTable {
   public void addMessage(String message) {
      this.getModel().addElement(message);
      this.scrollRectToVisible(this.getCellRect(this.getModel().getMessages().size() - 1, 0, true));
   }

   public SimpleStringListboxModel getModel() {
      return (SimpleStringListboxModel)super.getModel();
   }

   public SimpleStringListbox(String title) {
      super(new SimpleStringListboxModel(title));
      TraceHelper.entry(this, "SimpleStringListbox");
      this.setSelectionMode(0);
      if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
         this.setFont(new Font("Monospaced", 0, 12));
      } else {
         this.setFont(new Font("Courier New", 0, 12));
      }

      TraceHelper.exit(this, "VNAUpdateFileTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }

   public void clear() {
      this.getModel().getMessages().clear();
      this.getModel().fireTableDataChanged();
   }
}
