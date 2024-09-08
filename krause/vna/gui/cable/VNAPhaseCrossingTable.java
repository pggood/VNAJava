package krause.vna.gui.cable;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import krause.util.ras.logging.TraceHelper;

public class VNAPhaseCrossingTable extends JTable {
   private DefaultTableCellRenderer crRight;

   public VNAPhaseCrossingTableModel getModel() {
      return (VNAPhaseCrossingTableModel)super.getModel();
   }

   public VNAPhaseCrossingTable() {
      super(new VNAPhaseCrossingTableModel());
      TraceHelper.entry(this, "VNAPhaseCrossingTable");
      this.crRight = new DefaultTableCellRenderer();
      this.crRight.setHorizontalAlignment(4);
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(50);
      this.getColumnModel().getColumn(1).setPreferredWidth(50);
      TraceHelper.exit(this, "VNAPhaseCrossingTable");
   }

   public Class getColumnClass(int c) {
      return String.class;
   }

   public TableCellRenderer getCellRenderer(int row, int col) {
      return this.crRight;
   }
}
