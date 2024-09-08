package krause.vna.gui.generator.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;

public class VNAFrequencyTableModel extends AbstractTableModel {
   private List<Long> data = new ArrayList();

   public int getRowCount() {
      return this.data.size();
   }

   public int getColumnCount() {
      return 1;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      Long pair = (Long)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = pair;
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<Long> getData() {
      return this.data;
   }

   public void addElement(Long pair) {
      TraceHelper.entry(this, "addElement", pair.toString());
      this.data.add(pair);
      Collections.sort(this.data);
      this.fireTableDataChanged();
      TraceHelper.exit(this, "addElement");
   }

   public String getColumnName(int column) {
      return column == 0 ? VNAMessages.getString("FrequencyTable.Header") : "???";
   }

   public void clear() {
      this.data.clear();
   }
}
