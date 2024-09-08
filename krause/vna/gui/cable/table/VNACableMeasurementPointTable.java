package krause.vna.gui.cable.table;

import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.cable.VNACableMeasurementPoint;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNACableMeasurementPointTable extends JTable {
   public void addPoint(VNACableMeasurementPoint point) {
      this.getModel().addElement(point);
   }

   public VNACableMeasurementPointTableModel getModel() {
      return (VNACableMeasurementPointTableModel)super.getModel();
   }

   public VNACableMeasurementPointTable() {
      super(new VNACableMeasurementPointTableModel());
      TraceHelper.entry(this, "VNAFrequencyPairTable");
      this.setDefaultRenderer(Integer.class, new VNAFrequencyRenderer());
      this.setSelectionMode(0);
      TraceHelper.exit(this, "VNAFrequencyPairTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }

   public void addPoint(VNACalibratedSample start, VNACalibratedSample stop) {
      VNACableMeasurementPoint point = new VNACableMeasurementPoint(false, false);
      point.setStart(start);
      point.setStop(stop);
      this.addPoint(point);
   }
}
