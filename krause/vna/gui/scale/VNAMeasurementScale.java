package krause.vna.gui.scale;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import krause.util.ResourceLoader;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.resources.VNAMessages;

public class VNAMeasurementScale extends VNADiagramScale implements MouseListener, MouseMotionListener {
   private transient VNAGenericScale scale;
   protected static VNAConfig config = VNAConfig.getSingleton();
   private boolean leftScale;
   private Frame owner;
   private boolean dragging;
   private int lastDragPos;
   private boolean dragModeRange;
   private Cursor lastMouseCursor;
   private Cursor cursorRange;
   private Cursor cursorMove;
   private Cursor cursorScale;

   public VNAMeasurementScale(VNAGenericScale pScale, boolean isLeftScale, Frame pOwner) {
      this.scale = (VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_NONE);
      this.leftScale = false;
      this.dragging = false;
      this.lastDragPos = 0;
      this.dragModeRange = false;
      this.owner = pOwner;
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      this.scale = pScale;
      this.leftScale = isLeftScale;
      byte[] iconBytes = null;

      try {
         iconBytes = ResourceLoader.getResourceAsByteArray("images/zoomIn16.gif");
      } catch (Exception var6) {
      }

      ImageIcon icon = new ImageIcon(iconBytes, "zoom");
      this.cursorRange = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), new Point(0, 0), "img");
      this.cursorMove = Cursor.getPredefinedCursor(8);
      this.cursorScale = Cursor.getPredefinedCursor(12);
      this.setMinimumSize(new Dimension(40, 30));
      this.setPreferredSize(this.getMinimumSize());
   }

   public void paint(Graphics g) {
      super.paint(g);
      this.getScale().paintScale(this.getWidth(), this.getHeight(), g);
   }

   public VNAGenericScale getScale() {
      return this.scale;
   }

   public void setScale(VNAGenericScale scaleType) {
      this.scale = scaleType;
   }

   public boolean isLeftScale() {
      return this.leftScale;
   }

   public void setLeftScale(boolean leftScale) {
      this.leftScale = leftScale;
   }

   public void mouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "mouseClicked");
      if (e.getButton() == 1) {
         if (!this.getScale().supportsCustomScaling()) {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.NotSupported"), VNAMessages.getString("Scale.Customscale"), 2);
         } else if (config.isAutoscaleEnabled()) {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.Remove"), VNAMessages.getString("Scale.Customscale.Autoscale"), 2);
         } else {
            VNAScaleConfigDialog dlg = new VNAScaleConfigDialog(this.owner, this);
            if (dlg.isExitWithOK()) {
               this.getScale().rescale();
               this.owner.repaint();
            }
         }
      }

      TraceHelper.exit(this, "mouseClicked");
   }

   public void mouseEntered(MouseEvent e) {
      this.lastMouseCursor = this.getCursor();
      this.setCursor(this.cursorScale);
   }

   public void mouseExited(MouseEvent e) {
      this.setCursor(this.lastMouseCursor);
   }

   public void mousePressed(MouseEvent e) {
      if (!this.dragging) {
         if (!this.getScale().supportsCustomScaling()) {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.NotSupported"), VNAMessages.getString("Scale.Customscale"), 2);
         } else if (config.isAutoscaleEnabled()) {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.Remove"), VNAMessages.getString("Scale.Customscale.Autoscale"), 2);
         } else {
            this.dragging = true;
            this.lastDragPos = e.getY();
            this.dragModeRange = e.getButton() == 3;
            if (this.dragModeRange) {
               this.setCursor(this.cursorMove);
            } else {
               this.setCursor(this.cursorRange);
            }
         }
      }

   }

   public void mouseReleased(MouseEvent e) {
      if (this.dragging) {
         this.dragging = false;
         this.setCursor(this.cursorScale);
      }

   }

   public void mouseDragged(MouseEvent e) {
      if (this.dragging) {
         boolean dragUp = this.lastDragPos > e.getY();
         this.lastDragPos = e.getY();
         double min;
         double max;
         if (this.dragModeRange) {
            min = this.getScale().getCurrentMinValue();
            max = this.getScale().getCurrentMaxValue();
            double delta = this.getScale().getRange() / 40.0D;
            if (dragUp) {
               min -= delta;
               max -= delta;
            } else {
               min += delta;
               max += delta;
            }

            if (min >= this.getScale().getDefaultMinValue() && max <= this.getScale().getDefaultMaxValue()) {
               this.getScale().setCurrentMinValue(min);
               this.getScale().setCurrentMaxValue(max);
               this.getScale().rescale();
               this.owner.repaint();
            }
         } else {
            min = this.getScale().getCurrentMaxValue();
            max = this.getScale().getRange() / 30.0D;
            if (dragUp) {
               min += max;
            } else {
               min -= max;
            }

            this.getScale().setCurrentMaxValue(min);
            this.getScale().rescale();
            this.owner.repaint();
         }
      }

   }

   public void mouseMoved(MouseEvent e) {
   }

   public void setupColors() {
      TraceHelper.entry(this, "setupColors");
      TraceHelper.exit(this, "setupColors");
   }
}
