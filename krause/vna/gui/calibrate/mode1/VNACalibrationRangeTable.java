package krause.vna.gui.calibrate.mode1;

import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNACalibrationRangeTable extends JTable {
   public VNACalibrationRangeTableModel getModel() {
      return (VNACalibrationRangeTableModel)super.getModel();
   }

   public VNACalibrationRangeTable(VNACalibrationRange[] calRanges) {
      super(new VNACalibrationRangeTableModel(calRanges));
      TraceHelper.entry(this, "VNACalibrationRangeTable");
      this.setSelectionMode(0);
      this.setAutoResizeMode(2);
      this.getColumnModel().getColumn(0).setPreferredWidth(100);
      this.getColumnModel().getColumn(1).setPreferredWidth(100);
      this.setDefaultRenderer(Long.class, new VNAFrequencyRenderer());
      TraceHelper.exit(this, "VNACalibrationRangeTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }

   public void setSelected(int firstIndex) {
   }
}
