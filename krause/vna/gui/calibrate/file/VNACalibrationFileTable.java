package krause.vna.gui.calibrate.file;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.gui.calibrate.IVNACalibrationSelectionListener;

public class VNACalibrationFileTable extends JTable {
   private IVNACalibrationSelectionListener owner;

   public void addCalibrationBlock(VNACalibrationBlock pair) {
      this.getModel().addElement(pair);
   }

   public VNACalibrationFileTableModel getModel() {
      return (VNACalibrationFileTableModel)super.getModel();
   }

   public VNACalibrationFileTable(IVNACalibrationSelectionListener pOwner) {
      super(new VNACalibrationFileTableModel());
      TraceHelper.entry(this, "VNACalibrationFileTable");
      this.owner = pOwner;
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(200);
      this.getColumnModel().getColumn(1).setPreferredWidth(150);
      this.getColumnModel().getColumn(2).setPreferredWidth(250);
      this.getColumnModel().getColumn(3).setPreferredWidth(100);
      this.getColumnModel().getColumn(4).setPreferredWidth(50);
      this.getColumnModel().getColumn(5).setPreferredWidth(80);
      this.getColumnModel().getColumn(6).setPreferredWidth(70);
      this.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            TraceHelper.entry(this, "mouseClicked");
            int row = VNACalibrationFileTable.this.getSelectedRow();
            if (row >= 0 && e.getButton() == 1) {
               VNACalibrationBlock blk = (VNACalibrationBlock)VNACalibrationFileTable.this.getModel().getData().get(row);
               VNACalibrationFileTable.this.owner.valueChanged(blk, e.getClickCount() > 1);
            }

            TraceHelper.exit(this, "mouseClicked");
         }
      });
      TraceHelper.exit(this, "VNACalibrationFileTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }

   public void setSelected(int firstIndex) {
   }
}
