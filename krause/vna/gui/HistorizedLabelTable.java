package krause.vna.gui;

import java.util.List;
import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;

public class HistorizedLabelTable extends JTable {
   public HistorizedLabelTable(List<HistorizedLabelEntry> data) {
      super(new HistorizedLabelTableModel(data));
      TraceHelper.entry(this, "HistorizedLabelTable");
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(100);
      this.getColumnModel().getColumn(1).setPreferredWidth(300);
      TraceHelper.exit(this, "HistorizedLabelTable");
   }
}
