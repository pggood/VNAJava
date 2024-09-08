package krause.vna.gui.mac;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.VNAMainFrame;

public class MacShutdownHandler extends Thread {
   private VNAMainFrame mainFrame;

   public MacShutdownHandler(VNAMainFrame pMainFrame) {
      this.mainFrame = pMainFrame;
   }

   public void run() {
      TraceHelper.entry(this, "run");
      this.mainFrame.doShutdown();
      TraceHelper.exit(this, "run");
   }
}
