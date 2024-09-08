package krause.vna.gui.panels.marker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JTextField;
import krause.vna.config.VNASystemConfig;

public class VNAMarkerTextField extends JTextField {
   private final Font annotateFont = new Font("Arial", 0, 8);
   private final Font textFont = new Font("Arial", 0, 12);
   private VNAMarkerSearchMode markerSearchMode;

   public VNAMarkerTextField(int columns) {
      super(columns);
      this.setEditable(false);
      this.setHorizontalAlignment(4);
   }

   public VNAMarkerTextField(int columns, boolean editable) {
      super(columns);
      this.setEditable(editable);
      this.setHorizontalAlignment(11);
      if (VNASystemConfig.getPlatform() == VNASystemConfig.OS_PLATFORM.MAC) {
         this.setFont(this.textFont);
      }

   }

   public void paint(Graphics g) {
      super.paint(g);
      g.setColor(Color.BLACK);
      g.setFont(this.annotateFont);
      if (this.markerSearchMode != null) {
         if (this.markerSearchMode.isMinimum()) {
            g.drawString("*", 1, 22);
         } else if (this.markerSearchMode.isMaximum()) {
            g.drawString("*", 1, 10);
         }
      }

   }

   public VNAMarkerSearchMode getMarkerSearchMode() {
      return this.markerSearchMode;
   }

   public void setMarkerSearchMode(VNAMarkerSearchMode markerSearchMode) {
      this.markerSearchMode = markerSearchMode;
   }

   public boolean toggleSearchMode() {
      boolean rc = false;
      if (this.markerSearchMode != null) {
         rc = this.markerSearchMode.toggle();
         this.repaint();
      }

      return rc;
   }

   public void clearSearchMode() {
      if (this.markerSearchMode != null) {
         this.markerSearchMode.clearSearchMode();
         this.repaint();
      }

   }
}
