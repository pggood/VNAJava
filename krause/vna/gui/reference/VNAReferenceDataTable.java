package krause.vna.gui.reference;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNAReferenceDataTable extends JTable {
   private IVNAReferenceDataSelectionListener owner;

   public void addReferenceData(VNAReferenceDataBlock block) {
      this.getModel().addElement(block);
   }

   public VNAReferenceDataTableModel getModel() {
      return (VNAReferenceDataTableModel)super.getModel();
   }

   public VNAReferenceDataTable(IVNAReferenceDataSelectionListener pOwner) {
      super(new VNAReferenceDataTableModel());
      TraceHelper.entry(this, "VNACalibrationFileTable");
      this.owner = pOwner;
      this.setSelectionMode(0);
      this.getColumnModel().getColumn(0).setPreferredWidth(300);
      this.getColumnModel().getColumn(1).setPreferredWidth(60);
      this.getColumnModel().getColumn(2).setPreferredWidth(120);
      this.getColumnModel().getColumn(3).setPreferredWidth(120);
      this.getColumnModel().getColumn(4).setPreferredWidth(150);
      this.getColumnModel().getColumn(5).setPreferredWidth(200);
      this.getColumnModel().getColumn(2).setCellRenderer(new VNAFrequencyRenderer());
      this.getColumnModel().getColumn(3).setCellRenderer(new VNAFrequencyRenderer());
      this.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            TraceHelper.entry(this, "mouseClicked");
            int row = VNAReferenceDataTable.this.getSelectedRow();
            if (row >= 0) {
               if (e.getButton() == 1) {
                  VNAReferenceDataBlock blk = (VNAReferenceDataBlock)VNAReferenceDataTable.this.getModel().getData().get(row);
                  VNAReferenceDataTable.this.owner.valueChanged(blk, e.getClickCount() > 1);
               } else {
                  VNAReferenceDataTable.this.owner.valueChanged((VNAReferenceDataBlock)null, false);
               }
            } else {
               VNAReferenceDataTable.this.owner.valueChanged((VNAReferenceDataBlock)null, false);
            }

            TraceHelper.exit(this, "mouseClicked");
         }
      });
      TraceHelper.exit(this, "VNACalibrationFileTable");
   }

   public Class getColumnClass(int c) {
      Object o = this.getValueAt(0, c);
      return o == null ? String.class : o.getClass();
   }

   public void setSelected(int firstIndex) {
   }
}
