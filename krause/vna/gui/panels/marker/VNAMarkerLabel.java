package krause.vna.gui.panels.marker;

import java.awt.Font;
import javax.swing.JLabel;
import krause.vna.config.VNASystemConfig;

public class VNAMarkerLabel extends JLabel {
   private final Font textFont = new Font("Arial", 0, 12);

   public VNAMarkerLabel(String string) {
      super(string);
      if (VNASystemConfig.getPlatform() == VNASystemConfig.OS_PLATFORM.MAC) {
         this.setFont(this.textFont);
      }

   }
}
