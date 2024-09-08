package krause.vna.gui;

import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import krause.util.ResourceLoader;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class BatteryButton extends JButton {
   private ImageIcon iconEmpty;
   private ImageIcon iconRed;
   private ImageIcon iconYellow;
   private ImageIcon iconGreen;

   private static ImageIcon readIconFromResource(String resName, String altText) {
      byte[] iconBytes = null;

      try {
         iconBytes = ResourceLoader.getResourceAsByteArray(resName);
      } catch (IOException var4) {
         ErrorLogHelper.exception(SwingUtil.class, "readIconFromResource", var4);
      }

      return iconBytes == null ? new ImageIcon() : new ImageIcon(iconBytes, altText);
   }

   public BatteryButton(String pResPrefix, ActionListener pListener) {
      String command = VNAMessages.getString(pResPrefix + ".Command");
      String tooltip = VNAMessages.getString(pResPrefix + ".Tooltip");
      this.setActionCommand(command);
      this.setToolTipText(tooltip);
      if (pListener != null) {
         this.addActionListener(pListener);
      }

      this.iconEmpty = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Empty"), "Empty");
      this.iconRed = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Red"), "Red");
      this.iconGreen = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Green"), "Green");
      this.iconYellow = readIconFromResource(VNAMessages.getString(pResPrefix + ".Image.Yellow"), "Yellow");
      this.setState(0);
   }

   public void setState(int pState) {
      if (pState == 1) {
         this.setIcon(this.iconGreen);
      } else if (pState == 2) {
         this.setIcon(this.iconYellow);
      } else if (pState == 3) {
         this.setIcon(this.iconRed);
      } else {
         this.setIcon(this.iconEmpty);
      }

   }
}
