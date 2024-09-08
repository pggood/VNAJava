package krause.common.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import krause.common.gui.ILocationAwareDialog;
import krause.common.gui.KrauseDialog;
import krause.common.resources.CommonMessages;
import krause.util.ras.logging.TraceHelper;

public class ValidationResultsDialog extends KrauseDialog implements ILocationAwareDialog {
   private final JPanel contentPanel = new JPanel();
   private ValidationResultTable lstMessages;

   public ValidationResultsDialog(Window mainFrame, ValidationResults pResults, String title) {
      super(mainFrame, true);
      this.setTitle(title);
      this.setDefaultCloseOperation(0);
      this.getContentPane().setLayout(new BorderLayout());
      JPanel panel = new JPanel();
      panel.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      FlowLayout flowLayout = (FlowLayout)panel.getLayout();
      flowLayout.setAlignment(0);
      this.getContentPane().add(panel, "North");
      JLabel lblOneOrMore = new JLabel(CommonMessages.getString("ValidationResultsDialog.headline"));
      lblOneOrMore.setForeground(Color.RED);
      lblOneOrMore.setFont(new Font("Segoe UI", 1, 18));
      panel.add(lblOneOrMore);
      FlowLayout flContentPanel = new FlowLayout();
      flContentPanel.setAlignment(0);
      this.contentPanel.setLayout(flContentPanel);
      this.contentPanel.setBorder(new EtchedBorder(1, (Color)null, (Color)null));
      this.getContentPane().add(this.contentPanel, "Center");
      this.lstMessages = new ValidationResultTable();
      this.lstMessages.setFillsViewportHeight(true);
      this.lstMessages.setSelectionMode(0);
      this.lstMessages.setRowSelectionAllowed(false);
      this.lstMessages.getModel().setResults(pResults);
      this.lstMessages.setPreferredScrollableViewportSize(new Dimension(600, 200));
      JScrollPane sp = new JScrollPane(this.lstMessages);
      sp.setAlignmentX(0.0F);
      this.contentPanel.add(sp);
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(2));
      this.getContentPane().add(buttonPane, "South");
      JButton okButton = new JButton(CommonMessages.getString("Button.OK"));
      okButton.addActionListener((e) -> {
         this.doDialogCancel();
      });
      buttonPane.add(okButton);
      this.getRootPane().setDefaultButton(okButton);
      this.doDialogInit();
   }

   protected void doDialogCancel() {
      this.setVisible(false);
      this.dispose();
   }

   protected void doDialogInit() {
      this.addEscapeKey();
      this.showInPlace();
   }

   public void restoreWindowPosition() {
      TraceHelper.entry(this, "restoreWindowPosition");
      Dimension dimRoot = Toolkit.getDefaultToolkit().getScreenSize();
      int x = dimRoot.width / 2 - this.getSize().width / 2;
      int y = dimRoot.height / 2 - this.getSize().height / 2;
      this.setLocation(x, y);
      TraceHelper.exit(this, "restoreWindowPosition");
   }

   public void restoreWindowSize() {
      TraceHelper.entry(this, "restoreWindowSize");
      TraceHelper.exit(this, "restoreWindowSize");
   }

   public void showInPlace() {
      TraceHelper.entry(this, "showInPlace");
      this.restoreWindowSize();
      this.pack();
      this.restoreWindowPosition();
      this.setVisible(true);
      TraceHelper.exit(this, "showInPlace");
   }

   public void storeWindowPosition() {
      TraceHelper.entry(this, "storeWindowPosition");
      TraceHelper.exit(this, "storeWindowPosition");
   }

   public void storeWindowSize() {
      TraceHelper.entry(this, "storeWindowSize");
      TraceHelper.exit(this, "storeWindowSize");
   }
}
