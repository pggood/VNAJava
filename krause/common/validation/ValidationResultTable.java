package krause.common.validation;

import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;

public class ValidationResultTable extends JTable {
   public ValidationResultTable() {
      super(new ValidationResultTableModel());
      TraceHelper.entry(this, "ValidationResultTable");
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(200);
      this.getColumnModel().getColumn(1).setPreferredWidth(500);
      this.getColumnModel().getColumn(2).setPreferredWidth(100);
      TraceHelper.exit(this, "ValidationResultTable");
   }

   public ValidationResultTableModel getModel() {
      return (ValidationResultTableModel)super.getModel();
   }
}
