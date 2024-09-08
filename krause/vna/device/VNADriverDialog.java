package krause.vna.device;

import java.awt.Window;
import javax.swing.JFrame;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;

public abstract class VNADriverDialog extends KrauseDialog {
   protected transient VNAMainFrame mainFrame = null;
   protected transient VNAConfig config = VNAConfig.getSingleton();

   public VNADriverDialog(JFrame frame, VNAMainFrame pMainFrame) {
      super((Window)frame, true);
      String methodName = "VNADriverDialog";
      TraceHelper.entry(this, "VNADriverDialog");
      this.mainFrame = pMainFrame;
      TraceHelper.exit(this, "VNADriverDialog");
   }
}
