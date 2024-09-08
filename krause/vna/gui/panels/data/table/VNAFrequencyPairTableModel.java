package krause.vna.gui.panels.data.table;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.vna.gui.util.VNAFrequencyPair;

public class VNAFrequencyPairTableModel extends AbstractTableModel {
   private List<VNAFrequencyPair> data = new ArrayList();

   public void addElement(VNAFrequencyPair newPair) {
      boolean isNew = true;
      Iterator var4 = this.data.iterator();

      while(var4.hasNext()) {
         VNAFrequencyPair pair = (VNAFrequencyPair)var4.next();
         if (pair.equals(newPair)) {
            isNew = false;
            break;
         }
      }

      if (isNew) {
         this.data.add(newPair);
         this.fireTableDataChanged();
      } else {
         Toolkit.getDefaultToolkit().beep();
      }

   }

   public void clear() {
      this.data.clear();
   }

   public int getColumnCount() {
      return 2;
   }

   public String getColumnName(int column) {
      if (column == 0) {
         return "Start";
      } else {
         return column == 1 ? "Stop" : "???";
      }
   }

   public List<VNAFrequencyPair> getData() {
      return this.data;
   }

   public int getRowCount() {
      return this.data.size();
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      VNAFrequencyPair pair = (VNAFrequencyPair)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = pair.getStartFrequency();
      } else if (columnIndex == 1) {
         rc = pair.getStopFrequency();
      } else {
         rc = "???";
      }

      return rc;
   }
}
