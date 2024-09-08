package krause.vna.gui.calibrate.file;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class VNACalibrationFileTableModel extends AbstractTableModel {
   private List<VNACalibrationBlock> data = new ArrayList();
   private String[] columnNames = new String[]{VNAMessages.getString("CalibrationFileTableModel.name"), VNAMessages.getString("CalibrationFileTableModel.date"), VNAMessages.getString("CalibrationFileTableModel.comment"), VNAMessages.getString("CalibrationFileTableModel.type"), VNAMessages.getString("CalibrationFileTableModel.mode"), VNAMessages.getString("CalibrationFileTableModel.nofSteps"), VNAMessages.getString("CalibrationFileTableModel.nofOverscans")};

   public int getRowCount() {
      return this.data.size();
   }

   public int getColumnCount() {
      return this.columnNames.length;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      VNACalibrationBlock block = (VNACalibrationBlock)this.data.get(rowIndex);
      if (columnIndex == 0) {
         rc = block.getFile().getName();
      } else if (columnIndex == 1) {
         long zeit = block.getFile().lastModified();
         rc = VNAFormatFactory.getDateTimeFormat().format(zeit);
      } else if (columnIndex == 2) {
         rc = block.getComment();
      } else if (columnIndex == 3) {
         String x = block.getAnalyserType();
         if ("0".equals(x)) {
            rc = "Sample";
         } else if ("1".equals(x)) {
            rc = "miniVNA";
         } else if ("2".equals(x)) {
            rc = "miniVNA pro";
         } else if ("50".equals(x)) {
            rc = "miniVNA pro2";
         } else if ("3".equals(x)) {
            rc = "miniVNA pro-ext";
         } else if ("4".equals(x)) {
            rc = "Max6";
         } else if ("5".equals(x)) {
            rc = "Max6-500";
         } else if ("10".equals(x)) {
            rc = "miniVNA LF";
         } else if ("12".equals(x)) {
            rc = "miniVNA pro-LF";
         } else if ("6".equals(x)) {
            rc = "miniVNA Test";
         } else if ("20".equals(x)) {
            rc = "tinyVNA";
         } else if ("21".equals(x)) {
            rc = "tinyVNA2";
         } else if ("30".equals(x)) {
            rc = "metroVNA";
         } else if ("40".equals(x)) {
            rc = "VNArduino";
         } else if ("51".equals(x)) {
            rc = "miniVNA V2";
         } else {
            rc = "?";
         }
      } else if (columnIndex == 4) {
         rc = block.getScanMode().shortText();
      } else if (columnIndex == 5) {
         rc = block.getNumberOfSteps();
      } else if (columnIndex == 6) {
         rc = block.getNumberOfOverscans();
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<VNACalibrationBlock> getData() {
      return this.data;
   }

   public void addElement(VNACalibrationBlock block) {
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
