package krause.vna.gui.reference;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class VNAReferenceDataTableModel extends AbstractTableModel {
   private List<VNAReferenceDataBlock> data = new ArrayList();
   private String[] columnNames = new String[]{VNAMessages.getString("VNAReferenceDataTableModel.name"), VNAMessages.getString("VNAReferenceDataTableModel.nofSteps"), VNAMessages.getString("VNAReferenceDataTableModel.startFreq"), VNAMessages.getString("VNAReferenceDataTableModel.stopFreq"), VNAMessages.getString("VNAReferenceDataTableModel.date"), VNAMessages.getString("VNAReferenceDataTableModel.comment")};

   public int getRowCount() {
      return this.data.size();
   }

   public int getColumnCount() {
      return 6;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = "";
      VNAReferenceDataBlock block = (VNAReferenceDataBlock)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = block.getFile().getName();
      } else if (columnIndex == 1) {
         rc = block.getSamples().length;
      } else if (columnIndex == 2) {
         rc = block.getMinFrequency();
      } else if (columnIndex == 3) {
         rc = block.getMaxFrequency();
      } else if (columnIndex == 4) {
         long zeit = block.getFile().lastModified();
         rc = VNAFormatFactory.getDateTimeFormat().format(zeit);
      } else if (columnIndex == 5) {
         rc = block.getComment();
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<VNAReferenceDataBlock> getData() {
      return this.data;
   }

   public void addElement(VNAReferenceDataBlock block) {
      TraceHelper.entry(this, "addElement");
      this.data.add(block);
      this.fireTableDataChanged();
      TraceHelper.exit(this, "addElement");
   }

   public String getColumnName(int column) {
      return this.columnNames[column];
   }

   public void clear() {
      this.data.clear();
   }
}
