package krause.common.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;

public abstract class KrauseDialog extends JDialog implements WindowListener {
   private Window owner = null;
   private String configurationPrefix = null;
   private TypedProperties properties = null;

   public KrauseDialog(Window aWnd, boolean modal) {
      super(aWnd, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
      this.addWindowListener(this);
      this.owner = aWnd;
   }

   public KrauseDialog(boolean modal) {
      super((Frame)null, modal);
      this.addWindowListener(this);
      this.owner = null;
   }

   public KrauseDialog(Dialog aDlg, boolean modal) {
      super(aDlg, modal);
      this.addWindowListener(this);
      this.owner = aDlg;
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      if (this.getConfigurationPrefix() != null && this.getProperties() != null) {
         TraceHelper.text(this, "dispose", "Saving properties ...");
         this.getProperties().storeWindowPosition(this.getConfigurationPrefix(), this);
         this.getProperties().storeWindowSize(this.getConfigurationPrefix(), this);
      }

      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void showCenteredOnScreen() {
      this.pack();
      Dimension dimRoot = Toolkit.getDefaultToolkit().getScreenSize();
      int x = dimRoot.width / 2 - this.getSize().width / 2;
      int y = dimRoot.height / 2 - this.getSize().height / 2;
      this.setLocation(x, y);
      this.setVisible(true);
   }

   protected void showNormal(int width, int height) {
      this.pack();
      this.setSize(width, height);
      this.setVisible(true);
   }

   protected void showCentered(int width, int height) {
      this.pack();
      this.setSize(width, height);
      this.centerOnComponent(this.getOwner());
      this.setVisible(true);
   }

   protected void showCentered(Component root) {
      this.pack();
      this.centerOnComponent(root);
      this.setVisible(true);
   }

   protected void centerOnComponent(Component root) {
      Dimension dimRoot = root.getSize();
      Point locRoot = root.getLocation();
      int x = (int)(locRoot.getX() + (double)(dimRoot.width / 2) - (double)(this.getSize().width / 2));
      int y = (int)(locRoot.getY() + (double)(dimRoot.height / 2) - (double)(this.getSize().height / 2));
      if (x < 0) {
         x = 0;
      }

      if (y < 0) {
         y = 0;
      }

      this.setLocation(x, y);
   }

   protected void addEscapeKey() {
      Action actionListener = new AbstractAction() {
         public void actionPerformed(ActionEvent actionEvent) {
            KrauseDialog.this.doDialogCancel();
         }
      };
      KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
      InputMap inputMap = this.rootPane.getInputMap(2);
      inputMap.put(stroke, "ESCAPE");
      this.rootPane.getActionMap().put("ESCAPE", actionListener);
   }

   protected abstract void doDialogCancel();

   protected abstract void doDialogInit();

   public Window getOwner() {
      return this.owner;
   }

   public void setOwner(Window owner) {
      this.owner = owner;
   }

   public void windowActivated(WindowEvent e) {
   }

   public void windowClosed(WindowEvent e) {
   }

   public void windowClosing(WindowEvent e) {
      this.doDialogCancel();
   }

   public void windowDeactivated(WindowEvent e) {
   }

   public void windowDeiconified(WindowEvent e) {
   }

   public void windowIconified(WindowEvent e) {
   }

   public void windowOpened(WindowEvent e) {
   }

   public void showCenteredOnOwner() {
      this.centerOnComponent(this.owner);
      this.pack();
      this.setVisible(true);
   }

   public void setConfigurationPrefix(String configurationPrefix) {
      this.configurationPrefix = configurationPrefix;
   }

   public String getConfigurationPrefix() {
      return this.configurationPrefix;
   }

   public void setProperties(TypedProperties properties) {
      this.properties = properties;
   }

   public TypedProperties getProperties() {
      return this.properties;
   }

   public void doDialogShow() {
      TraceHelper.entry(this, "doDialogShow");
      if (this.getConfigurationPrefix() != null && this.getProperties() != null) {
         Dimension sz = this.getPreferredSize();
         this.getProperties().restoreWindowPosition(this.getConfigurationPrefix(), this, new Point(100, 100));
         this.pack();
         this.getProperties().restoreWindowSize(this.getConfigurationPrefix(), this, sz);
         this.setVisible(true);
      } else {
         this.pack();
         this.setVisible(true);
      }

      TraceHelper.exit(this, "doDialogShow");
   }
}
