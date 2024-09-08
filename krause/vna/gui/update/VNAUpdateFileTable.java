package krause.vna.gui.update;

import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;

public class VNAUpdateFileTable extends JTable {
   public VNAUpdateTableModel getModel() {
      return (VNAUpdateTableModel)super.getModel();
   }

   public VNAUpdateFileTable() {
      super(new VNAUpdateTableModel());
      TraceHelper.entry(this, "VNAUpdateFileTable");
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(300);
      this.getColumnModel().getColumn(1).setPreferredWidth(100);
      this.getColumnModel().getColumn(2).setPreferredWidth(100);
      TraceHelper.exit(this, "VNAUpdateFileTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }
}
