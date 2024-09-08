package krause.vna.gui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class ComponentTitledBorder implements Border, MouseListener, MouseMotionListener, SwingConstants {
   private int offset = 10;
   private Component comp;
   private JComponent container;
   private Rectangle rect;
   private Border border;
   private boolean mouseEntered = false;

   public ComponentTitledBorder(Component comp, JComponent container, Border border) {
      this.comp = comp;
      this.container = container;
      this.border = border;
      container.addMouseListener(this);
      container.addMouseMotionListener(this);
   }

   public boolean isBorderOpaque() {
      return true;
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Insets borderInsets = this.border.getBorderInsets(c);
      Insets insets = this.getBorderInsets(c);
      int temp = (insets.top - borderInsets.top) / 2;
      this.border.paintBorder(c, g, x, y + temp, width, height - temp);
      Dimension size = this.comp.getPreferredSize();
      this.rect = new Rectangle(this.offset, 0, size.width, size.height);
      SwingUtilities.paintComponent(g, this.comp, (Container)c, this.rect);
   }

   public Insets getBorderInsets(Component c) {
      Dimension size = this.comp.getPreferredSize();
      Insets insets = this.border.getBorderInsets(c);
      insets.top = Math.max(insets.top, size.height);
      return insets;
   }

   private void dispatchEvent(MouseEvent me) {
      if (this.rect != null && this.rect.contains(me.getX(), me.getY())) {
         this.dispatchEvent(me, me.getID());
      }

   }

   private void dispatchEvent(MouseEvent me, int id) {
      Point pt = me.getPoint();
      pt.translate(-this.offset, 0);
      this.comp.setSize(this.rect.width, this.rect.height);
      this.comp.dispatchEvent(new MouseEvent(this.comp, id, me.getWhen(), me.getModifiersEx(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
      this.container.repaint();
   }

   public void mouseClicked(MouseEvent me) {
      this.dispatchEvent(me);
   }

   public void mouseEntered(MouseEvent me) {
   }

   public void mouseExited(MouseEvent me) {
      if (this.mouseEntered) {
         this.mouseEntered = false;
         this.dispatchEvent(me, 505);
      }

   }

   public void mousePressed(MouseEvent me) {
      this.dispatchEvent(me);
   }

   public void mouseReleased(MouseEvent me) {
      this.dispatchEvent(me);
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent me) {
      if (this.rect != null) {
         if (!this.mouseEntered && this.rect.contains(me.getX(), me.getY())) {
            this.mouseEntered = true;
            this.dispatchEvent(me, 504);
         } else if (this.mouseEntered) {
            if (!this.rect.contains(me.getX(), me.getY())) {
               this.mouseEntered = false;
               this.dispatchEvent(me, 505);
            } else {
               this.dispatchEvent(me, 503);
            }
         }

      }
   }
}
