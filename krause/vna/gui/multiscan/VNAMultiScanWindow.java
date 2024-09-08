package krause.vna.gui.multiscan;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.scale.VNAMeasurementScale;

public class VNAMultiScanWindow extends KrauseDialog {
   private VNAMainFrame mainFrame;
   private TypedProperties config = VNAConfig.getSingleton();
   private VNAMultiScanControl control;
   private JDesktopPane desktop;
   private VNAMeasurementScale scale;

   public VNAMultiScanWindow(JFrame jFrame, VNAMainFrame pMainFrame, VNAMeasurementScale pScale) {
      super((Window)jFrame, true);
      this.setDefaultCloseOperation(2);
      this.setTitle("MultiTune [" + pScale.getScale().getName() + "]");
      this.mainFrame = pMainFrame;
      this.setScale(pScale);
      this.setBounds(new Rectangle(0, 0, 810, 360));
      Container content = this.getContentPane();
      content.setBackground(Color.white);
      this.desktop = new JDesktopPane();
      this.desktop.setBackground(Color.GRAY);
      content.add(this.desktop, "Center");
      this.control = new VNAMultiScanControl(this);
      this.desktop.add(this.control);
      this.doDialogInit();
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.config.restoreWindowPosition("MultiTune", this, new Point(100, 100));
      this.pack();
      this.config.restoreWindowSize("MultiTune", this, new Dimension(640, 480));
      this.setVisible(true);
      TraceHelper.exit(this, "doInit");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.config.storeWindowPosition("MultiTune", this);
      this.config.storeWindowSize("MultiTune", this);
      if (this.control != null) {
         this.control.dispose();
      }

      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   public VNAMainFrame getMainFrame() {
      return this.mainFrame;
   }

   public TypedProperties getConfig() {
      return this.config;
   }

   public VNAMultiScanControl getControl() {
      return this.control;
   }

   public JDesktopPane getDesktop() {
      return this.desktop;
   }

   public void setScale(VNAMeasurementScale scale) {
      this.scale = scale;
   }

   public VNAMeasurementScale getScale() {
      return this.scale;
   }
}
