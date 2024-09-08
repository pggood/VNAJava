package krause.common.validation;

import javax.swing.table.AbstractTableModel;
import krause.common.resources.CommonMessages;

public class ValidationResultTableModel extends AbstractTableModel {
   private ValidationResults results;

   public int getColumnCount() {
      return 3;
   }

   public int getRowCount() {
      return this.results != null ? this.results.size() : 0;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      ValidationResult result = (ValidationResult)this.results.get(rowIndex);
      if (columnIndex == 0) {
         return result.getErrorObject();
      } else if (columnIndex == 1) {
         return result.getMessage();
      } else {
         return columnIndex == 2 ? result.getException() : "???";
      }
   }

   public String getColumnName(int column) {
      if (column == 1) {
         return CommonMessages.getString("ValidationResultTableModel.Message");
      } else if (column == 0) {
         return CommonMessages.getString("ValidationResultTableModel.Field");
      } else {
         return column == 2 ? CommonMessages.getString("ValidationResultTableModel.Exception") : "???";
      }
   }

   public ValidationResults getResults() {
      return this.results;
   }

   public void setResults(ValidationResults results) {
      this.results = results;
      this.fireTableDataChanged();
   }
}
