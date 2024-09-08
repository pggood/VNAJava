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

public class VNAFrequencyRenderer extends JLabel implements TableCellRenderer {
   NumberFormat nf = null;
   Border unselectedBorder = null;
   Border selectedBorder = null;

   public VNAFrequencyRenderer() {
      this.setHorizontalAlignment(11);
      this.nf = VNAFormatFactory.getFrequencyFormat();
      this.setFont(new Font("Dialog", 0, this.getFont().getSize()));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Long frq = (Long)value;
      this.setText(this.nf.format(frq));
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
