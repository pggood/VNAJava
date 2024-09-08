package krause.vna.gui.util.tables;

import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import krause.vna.gui.format.VNAFormatFactory;

public class VNALossRenderer extends JLabel implements TableCellRenderer {
   NumberFormat nf = null;
   Border unselectedBorder = null;
   Border selectedBorder = null;

   public VNALossRenderer() {
      this.setHorizontalAlignment(4);
      this.nf = VNAFormatFactory.getReflectionLossFormat();
      this.setFont(new Font("Dialog", 0, this.getFont().getSize()));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value != null) {
         double loss = (Double)value;
         if (Double.isNaN(loss)) {
            this.setText("-");
         } else {
            this.setText(this.nf.format(loss));
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
      }

      return this;
   }
}
