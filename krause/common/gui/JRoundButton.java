package krause.common.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D.Float;
import javax.swing.JButton;

public class JRoundButton extends JButton {
   transient Shape shape;

   public JRoundButton() {
      Dimension size = this.getPreferredSize();
      size.width = size.height = Math.max(size.width, size.height);
      this.setPreferredSize(size);
      this.setContentAreaFilled(false);
   }

   public JRoundButton(String label) {
      super(label);
      Dimension size = this.getPreferredSize();
      size.width = size.height = Math.max(size.width, size.height);
      this.setPreferredSize(size);
      this.setContentAreaFilled(false);
   }

   protected void paintComponent(Graphics g) {
      if (this.getModel().isArmed()) {
         g.setColor(Color.lightGray);
      } else {
         g.setColor(this.getBackground());
      }

      g.fillOval(0, 0, this.getSize().width - 1, this.getSize().height - 1);
      super.paintComponent(g);
   }

   protected void paintBorder(Graphics g) {
      g.setColor(this.getForeground());
      g.drawOval(0, 0, this.getSize().width - 1, this.getSize().height - 1);
   }

   public boolean contains(int x, int y) {
      if (this.shape == null || !this.shape.getBounds().equals(this.getBounds())) {
         this.shape = new Float(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
      }

      return this.shape.contains((double)x, (double)y);
   }
}
