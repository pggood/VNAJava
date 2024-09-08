package krause.vna.gui.scale;

import java.awt.Graphics;
import javax.swing.JPanel;

public abstract class VNADiagramScale extends JPanel {
   public void paint(Graphics g) {
      g.setColor(this.getBackground());
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
   }
}
