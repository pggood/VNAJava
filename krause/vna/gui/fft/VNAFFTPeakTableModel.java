package krause.vna.gui.fft;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;

public class VNAFFTPeakTableModel extends AbstractTableModel {
   private final List<VNAFFTPeakTableEntry> values = new ArrayList();

   public List<VNAFFTPeakTableEntry> getValues() {
      return this.values;
   }

   public VNAFFTPeakTableModel() {
      String methodName = "VNAFFTPeakTableModel";
      TraceHelper.entry(this, "VNAFFTPeakTableModel");
      TraceHelper.exit(this, "VNAFFTPeakTableModel");
   }

   public int getSize() {
      return this.values.size();
   }

   public int getColumnCount() {
      return 2;
   }

   public int getRowCount() {
      return this.values.size();
   }

   public Object getValueAt(int row, int column) {
      VNAFFTPeakTableEntry item = (VNAFFTPeakTableEntry)this.values.get(row);
      switch(column) {
      case 0:
         return item.getBin();
      case 1:
         return VNAFormatFactory.getLengthFormat().format(item.getLength());
      case 2:
         return item.getValue();
      default:
         return "???";
      }
   }

   public String getColumnName(int column) {
      switch(column) {
      case 0:
         return "bin";
      case 1:
         return "length (m)";
      case 2:
         return "value";
      default:
         return "??";
      }
   }

   public VNAFFTPeakTableEntry getDataAtRow(int row) {
      return row >= 0 && row < this.values.size() ? (VNAFFTPeakTableEntry)this.values.get(row) : null;
   }
}
