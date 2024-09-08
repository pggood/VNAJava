package krause.vna.gui.importers;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.vna.importers.SnPRecord;

public class VNASnPDataTableModel extends AbstractTableModel {
   private List<SnPRecord> data = new ArrayList();

   public int getColumnCount() {
      return 9;
   }

   public String getColumnName(int column) {
      if (column == 0) {
         return "Freq";
      } else if (column == 1) {
         return "S11 (dB)";
      } else if (column == 2) {
         return "(°)";
      } else if (column == 3) {
         return "S21 (dB)";
      } else if (column == 4) {
         return "(°)";
      } else if (column == 5) {
         return "S12 (dB)";
      } else if (column == 6) {
         return "(°)";
      } else if (column == 7) {
         return "S22 (dB)";
      } else {
         return column == 8 ? "(°)" : "???";
      }
   }

   public List<SnPRecord> getData() {
      return this.data;
   }

   public int getRowCount() {
      return this.data.size();
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      SnPRecord d = (SnPRecord)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = d.getFrequency();
      } else if (columnIndex == 1) {
         rc = d.getLoss()[0];
      } else if (columnIndex == 2) {
         rc = d.getPhase()[0];
      } else if (columnIndex == 3) {
         rc = d.getLoss()[1];
      } else if (columnIndex == 4) {
         rc = d.getPhase()[1];
      } else if (columnIndex == 5) {
         rc = d.getLoss()[2];
      } else if (columnIndex == 6) {
         rc = d.getPhase()[2];
      } else if (columnIndex == 7) {
         rc = d.getLoss()[3];
      } else if (columnIndex == 8) {
         rc = d.getPhase()[3];
      } else {
         rc = "???";
      }

      return rc;
   }
}
