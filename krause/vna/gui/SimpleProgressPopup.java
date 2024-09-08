package krause.vna.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Dialog.ModalityType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import krause.util.ras.logging.TraceHelper;

public class SimpleProgressPopup extends JDialog implements PropertyChangeListener {
   private SwingWorker<Void, Void> task = null;
   private JProgressBar progressBar;

   public SimpleProgressPopup(JFrame owner, String title) {
      super(owner, ModalityType.APPLICATION_MODAL);
      TraceHelper.entry(this, "SimpleProgressPopup");
      this.setDefaultCloseOperation(0);
      this.setResizable(false);
      this.setUndecorated(true);
      this.add(this.createContentPane(title));
      this.centerOnComponent(owner);
      this.pack();
      TraceHelper.exit(this, "SimpleProgressPopup");
   }

   protected void centerOnComponent(Component root) {
      Dimension dimRoot = root.getSize();
      Point locRoot = root.getLocation();
      int x = (int)locRoot.getX() + dimRoot.width / 2 - this.getSize().width / 2;
      int y = (int)locRoot.getY() + dimRoot.height / 2 - this.getSize().height / 2;
      if (x < 0) {
         x = 0;
      }

      if (y < 0) {
         y = 0;
      }

      this.setLocation(x, y);
   }

   public JPanel createContentPane(String title) {
      JPanel rc = new JPanel(new BorderLayout());
      rc.setOpaque(true);
      JLabel lbl = new JLabel(title);
      rc.add(lbl, "First");
      this.progressBar = new JProgressBar(0, 100);
      this.progressBar.setValue(0);
      this.progressBar.setStringPainted(true);
      rc.add(this.progressBar, "Last");
      rc.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      return rc;
   }

   public SwingWorker<Void, Void> getTask() {
      return this.task;
   }

   public void propertyChange(PropertyChangeEvent evt) {
      if ("progress".equals(evt.getPropertyName())) {
         int progress = (Integer)evt.getNewValue();
         this.progressBar.setIndeterminate(false);
         this.progressBar.setValue(progress);
      }

   }

   public void setTask(SwingWorker<Void, Void> task) {
      this.task = task;
   }

   public void run() {
      TraceHelper.entry(this, "run");
      if (this.task != null) {
         this.task.addPropertyChangeListener(this);
         this.task.execute();
      }

      this.setVisible(true);
      TraceHelper.exit(this, "run");
   }
}
