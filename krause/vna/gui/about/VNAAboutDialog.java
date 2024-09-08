package krause.vna.gui.about;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;

public class VNAAboutDialog extends KrauseDialog {
   public VNAAboutDialog(VNAMainFrame f) {
      super((Window)f.getJFrame(), true);
      this.setDefaultCloseOperation(0);
      URL url = this.getClass().getResource(VNAMessages.getString("VNAAboutDialog.filename"));
      JLabel l = new JLabel(new ImageIcon(url));
      l.setToolTipText(VNAMessages.getString("VNAAboutDialog.tooltip"));
      l.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            try {
               Desktop.getDesktop().browse(URI.create(VNAMessages.getString("Application.URL")));
            } catch (IOException var3) {
               ErrorLogHelper.exception(this, "mouseClicked", var3);
            }

         }
      });
      this.getContentPane().add(l, "Center");
      this.setTitle(VNAMessages.getString("VNAAboutDialog.title"));
      this.addEscapeKey();
      this.showCentered(f.getJFrame());
   }

   protected void doDialogCancel() {
      this.setVisible(false);
      this.dispose();
   }

   protected void doDialogInit() {
   }
}
