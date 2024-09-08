package krause.vna.gui.util.tables;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class VNAPropertyTableModel extends AbstractTableModel {
   private List<VNAProperty> data = new ArrayList();

   public int getRowCount() {
      return this.data.size();
   }

   public int getColumnCount() {
      return 2;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      VNAProperty pair = (VNAProperty)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = pair.getKey();
      } else if (columnIndex == 1) {
         rc = pair.getValue();
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<VNAProperty> getData() {
      return this.data;
   }

   public void addElement(VNAProperty pair) {
      this.data.add(pair);
      this.fireTableDataChanged();
   }

   public String getColumnName(int column) {
      if (column == 0) {
         return "Key";
      } else {
         return column == 1 ? "Value" : "???";
      }
   }

   public boolean isCellEditable(int row, int col) {
      return col == 1;
   }

   public void setValueAt(Object value, int row, int col) {
      String val = (String)value;
      ((VNAProperty)this.data.get(row)).setValue(val);
      this.fireTableCellUpdated(row, col);
   }
}
