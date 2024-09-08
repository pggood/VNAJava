package krause.common.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D.Double;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;

public class JRoundButton2 extends JButton {
   protected transient Shape shape;
   protected transient Shape base;

   public JRoundButton2() {
      this((String)null, (Icon)null);
   }

   public JRoundButton2(Icon icon) {
      this((String)null, icon);
   }

   public JRoundButton2(String text) {
      this(text, (Icon)null);
   }

   public JRoundButton2(Action a) {
      this();
      this.setAction(a);
   }

   public JRoundButton2(String text, Icon icon) {
      this.setModel(new DefaultButtonModel());
      this.init(text, icon);
      if (icon != null) {
         this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
         this.setBackground(Color.BLACK);
         this.setContentAreaFilled(false);
         this.setFocusPainted(false);
         this.setAlignmentY(0.0F);
         this.initShape();
      }
   }

   protected void initShape() {
      if (!this.getBounds().equals(this.base)) {
         Dimension s = this.getPreferredSize();
         this.base = this.getBounds();
         this.shape = new Double(0.0D, 0.0D, (double)s.width - 1.0D, (double)s.height - 1.0D);
      }

   }

   public Dimension getPreferredSize() {
      Icon icon = this.getIcon();
      Insets i = this.getInsets();
      int iw = Math.max(icon.getIconWidth(), icon.getIconHeight());
      return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
   }

   protected void paintBorder(Graphics g) {
      this.initShape();
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(this.getBackground());
      g2.draw(this.shape);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
   }

   public boolean contains(int x, int y) {
      this.initShape();
      return this.shape.contains((double)x, (double)y);
   }
}
