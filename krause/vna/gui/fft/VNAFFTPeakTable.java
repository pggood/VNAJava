package krause.vna.gui.fft;

import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import krause.util.ras.logging.TraceHelper;

public class VNAFFTPeakTable extends JTable {
   private DefaultTableCellRenderer crRight;
   private DefaultTableCellRenderer crLeft;

   public VNAFFTPeakTableModel getModel() {
      return (VNAFFTPeakTableModel)super.getModel();
   }

   public VNAFFTPeakTable() {
      super(new VNAFFTPeakTableModel());
      String methodName = "VNAFFTPeakTable";
      TraceHelper.entry(this, "VNAFFTPeakTable");
      this.crLeft = new DefaultTableCellRenderer();
      this.crRight = new DefaultTableCellRenderer();
      this.crRight.setHorizontalAlignment(4);
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(30);
      this.getColumnModel().getColumn(1).setPreferredWidth(60);
      TraceHelper.exit(this, "VNAFFTPeakTable");
   }

   public Class getColumnClass(int c) {
      return String.class;
   }

   public TableCellRenderer getCellRenderer(int row, int col) {
      return col == 0 ? this.crLeft : this.crRight;
   }

   public void selectRow(int row) {
      ListSelectionModel selectionModel = this.getSelectionModel();
      selectionModel.setSelectionInterval(row, row);
      Rectangle rect = this.getCellRect(row, 0, true);
      this.scrollRectToVisible(rect);
   }

   public VNAFFTPeakTableEntry getSelectedItem() {
      int row = this.getSelectedRow();
      return row >= 0 ? this.getModel().getDataAtRow(row) : null;
   }
}
