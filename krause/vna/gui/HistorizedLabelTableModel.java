package krause.vna.gui;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class HistorizedLabelTableModel extends AbstractTableModel {
   private transient List<HistorizedLabelEntry> data = null;

   public HistorizedLabelTableModel(List<HistorizedLabelEntry> data) {
      this.data = data;
   }

   public int getColumnCount() {
      return 2;
   }

   public int getRowCount() {
      return this.data != null ? this.data.size() : 0;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      HistorizedLabelEntry pair = (HistorizedLabelEntry)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = DateFormat.getDateTimeInstance().format(new Date(pair.getTimestamp()));
      } else if (columnIndex == 1) {
         rc = pair.getText();
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<HistorizedLabelEntry> getData() {
      return this.data;
   }
}
