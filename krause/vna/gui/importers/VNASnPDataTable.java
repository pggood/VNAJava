package krause.vna.gui.importers;

import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;
import krause.vna.gui.util.tables.VNALossRenderer;
import krause.vna.gui.util.tables.VNAPhaseRenderer;

public class VNASnPDataTable extends JTable {
   public VNASnPDataTableModel getModel() {
      return (VNASnPDataTableModel)super.getModel();
   }

   public VNASnPDataTable() {
      super(new VNASnPDataTableModel());
      TraceHelper.entry(this, "VNASnPDataTable");
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(300);
      this.getColumnModel().getColumn(0).setWidth(300);

      for(int i = 1; i < 9; ++i) {
         this.getColumnModel().getColumn(i).setPreferredWidth(200);
         this.getColumnModel().getColumn(i).setWidth(200);
      }

      this.getColumnModel().getColumn(0).setCellRenderer(new VNAFrequencyRenderer());
      this.getColumnModel().getColumn(1).setCellRenderer(new VNALossRenderer());
      this.getColumnModel().getColumn(2).setCellRenderer(new VNAPhaseRenderer());
      this.getColumnModel().getColumn(3).setCellRenderer(new VNALossRenderer());
      this.getColumnModel().getColumn(4).setCellRenderer(new VNAPhaseRenderer());
      this.getColumnModel().getColumn(5).setCellRenderer(new VNALossRenderer());
      this.getColumnModel().getColumn(6).setCellRenderer(new VNAPhaseRenderer());
      this.getColumnModel().getColumn(7).setCellRenderer(new VNALossRenderer());
      this.getColumnModel().getColumn(8).setCellRenderer(new VNAPhaseRenderer());
      TraceHelper.exit(this, "VNASnPDataTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }
}
