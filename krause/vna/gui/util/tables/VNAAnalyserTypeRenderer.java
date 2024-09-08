package krause.vna.gui.util.tables;

import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverFactory;

public class VNAAnalyserTypeRenderer extends JLabel implements TableCellRenderer {
   private VNADriverFactory factory = VNADriverFactory.getSingleton();
   private Border unselectedBorder = null;
   private Border selectedBorder = null;

   public VNAAnalyserTypeRenderer() {
      this.setHorizontalAlignment(10);
      this.setFont(new Font("Dialog", 0, this.getFont().getSize()));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      String driverType = (String)value;
      IVNADriver drv = this.factory.getDriverForType(driverType);
      if (drv != null) {
         this.setText(drv.getDeviceInfoBlock().getShortName());
      }

      if (isSelected) {
         if (this.selectedBorder == null) {
            this.selectedBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, table.getSelectionBackground());
         }

         this.setBorder(this.selectedBorder);
      } else {
         if (this.unselectedBorder == null) {
            this.unselectedBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, table.getBackground());
         }

         this.setBorder(this.unselectedBorder);
      }

      return this;
   }
}
