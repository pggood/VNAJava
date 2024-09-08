package krause.vna.gui.cable.table;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.vna.gui.cable.VNACableMeasurementPoint;
import krause.vna.gui.format.VNAFormatFactory;

public class VNACableMeasurementPointTableModel extends AbstractTableModel {
   private transient List<VNACableMeasurementPoint> data = new ArrayList();

   public void addElement(VNACableMeasurementPoint newPair) {
      boolean isNew = true;
      Iterator var4 = this.data.iterator();

      while(var4.hasNext()) {
         VNACableMeasurementPoint pair = (VNACableMeasurementPoint)var4.next();
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
      return 3;
   }

   public String getColumnName(int column) {
      if (column == 0) {
         return "Start";
      } else if (column == 1) {
         return "Stop";
      } else {
         return column == 2 ? "Length" : "???";
      }
   }

   public List<VNACableMeasurementPoint> getData() {
      return this.data;
   }

   public int getRowCount() {
      return this.data.size();
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      VNACableMeasurementPoint pair = (VNACableMeasurementPoint)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = VNAFormatFactory.getFrequencyFormat().format(pair.getStart().getFrequency()) + " - " + VNAFormatFactory.getPhaseFormat().format(pair.getStart().getReflectionPhase());
      } else if (columnIndex == 1) {
         rc = VNAFormatFactory.getFrequencyFormat().format(pair.getStop().getFrequency()) + " - " + VNAFormatFactory.getPhaseFormat().format(pair.getStop().getReflectionPhase());
      } else if (columnIndex == 2) {
         rc = VNAFormatFactory.getLengthFormat().format(pair.getLength());
      } else {
         rc = "???";
      }

      return rc;
   }
}
