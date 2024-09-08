package krause.vna.gui.calibrate.mode1;

import javax.swing.table.AbstractTableModel;
import krause.vna.resources.VNAMessages;

public class VNACalibrationRangeTableModel extends AbstractTableModel {
   private VNACalibrationRange[] data = null;
   private String[] columnNames = new String[]{VNAMessages.getString("VNASCollectorDialog.lblStartFrequency.text"), VNAMessages.getString("VNASCollectorDialog.lblStopFrequency.text"), VNAMessages.getString("CalibrationFileTableModel.nofSteps"), VNAMessages.getString("CalibrationFileTableModel.novOverscans")};

   public VNACalibrationRangeTableModel(VNACalibrationRange[] calRanges) {
      this.data = calRanges;
   }

   public int getRowCount() {
      return this.data.length;
   }

   public int getColumnCount() {
      return this.columnNames.length;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = null;
      VNACalibrationRange block = this.data[rowIndex];
      if (columnIndex == 0) {
         rc = block.getStart();
      } else if (columnIndex == 1) {
         rc = block.getStop();
      } else if (columnIndex == 2) {
         rc = block.getNumScanPoints();
      } else if (columnIndex == 3) {
         rc = block.getNumOverScans();
      } else {
         rc = "???";
      }

      return rc;
   }

   public String getColumnName(int column) {
      return this.columnNames[column];
   }

   public void clear() {
   }
}
