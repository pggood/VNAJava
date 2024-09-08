package krause.vna.gui.cable;

import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import krause.util.ras.logging.TraceHelper;

public class VNAVelocityFactorTable extends JTable {
   private DefaultTableCellRenderer crRight;
   private DefaultTableCellRenderer crLeft;

   public VNAVelocityFactorTableModel getModel() {
      return (VNAVelocityFactorTableModel)super.getModel();
   }

   public VNAVelocityFactorTable() {
      super(new VNAVelocityFactorTableModel());
      TraceHelper.entry(this, "VNAVelocityFactorTable");
      this.crLeft = new DefaultTableCellRenderer();
      this.crRight = new DefaultTableCellRenderer();
      this.crRight.setHorizontalAlignment(4);
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(100);
      this.getColumnModel().getColumn(1).setPreferredWidth(30);
      this.getColumnModel().getColumn(2).setPreferredWidth(30);
      TraceHelper.exit(this, "VNAVelocityFactorTable");
   }

   public Class<?> getColumnClass(int c) {
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

   public VNAVelocityFactor getSelectedItem() {
      int row = this.getSelectedRow();
      return row >= 0 ? this.getModel().getDataAtRow(row) : null;
   }
}
