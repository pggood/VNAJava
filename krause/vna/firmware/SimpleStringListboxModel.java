package krause.vna.firmware;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.update.FileDownloadJob;

public class SimpleStringListboxModel extends AbstractTableModel {
   private List<String> messages = new ArrayList();
   private String columnTitle;

   public SimpleStringListboxModel(String title) {
      this.columnTitle = title;
   }

   public int getRowCount() {
      return this.messages.size();
   }

   public int getColumnCount() {
      return 1;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = "";
      String msg = (String)this.messages.get(rowIndex);
      if (columnIndex == 0) {
         rc = msg;
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<String> getMessages() {
      return this.messages;
   }

   public void addElement(String message) {
      this.messages.add(message);
      this.fireTableDataChanged();
   }

   public String getColumnName(int column) {
      return this.columnTitle;
   }

   public void clear() {
      this.messages.clear();
      this.fireTableDataChanged();
   }

   public void updateElement(FileDownloadJob job) {
      TraceHelper.entry(this, "updateElement");
      this.fireTableDataChanged();
      TraceHelper.exit(this, "updateElement");
   }
}
