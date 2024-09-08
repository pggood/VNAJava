package krause.vna.gui.export;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAExportCommentDialog extends KrauseDialog implements ActionListener {
   private final JPanel contentPanel;
   private VNAConfig config = VNAConfig.getSingleton();
   private JTextArea txtComment;
   private JTextField txtTitle;
   private JButton btnSave;
   private JButton btnCancel;
   boolean dialogCancelled = false;

   public VNAExportCommentDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      TraceHelper.entry(this, "VNAExportCommentDialog");
      this.setTitle(VNAMessages.getString("VNAExportCommentDialog.Title"));
      this.setDefaultCloseOperation(0);
      this.setModal(true);
      this.setBounds(100, 100, 678, 472);
      this.getContentPane();
      this.contentPanel = new JPanel();
      this.contentPanel.setLayout(new MigLayout("", "[grow,fill]", "[][grow,fill][]"));
      this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.getContentPane().add(this.contentPanel);
      JPanel panel_2 = new JPanel();
      panel_2.setLayout(new MigLayout("", "[grow,fill][]", "[]"));
      panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Headline"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.contentPanel.add(panel_2, "wrap");
      this.txtTitle = new JTextField();
      this.txtTitle.setBorder(new LineBorder(new Color(171, 173, 179)));
      panel_2.add(this.txtTitle, "");
      JPanel panel_1 = new JPanel();
      panel_1.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
      panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Comment"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.contentPanel.add(panel_1, "wrap");
      this.txtComment = new JTextArea();
      this.txtComment.setFont(new Font("Courier New", 0, 12));
      this.txtComment.setLineWrap(true);
      this.txtComment.setWrapStyleWord(true);
      JScrollPane sp = new JScrollPane(this.txtComment);
      panel_1.add(sp);
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(2));
      this.contentPanel.add(buttonPane, "wrap");
      buttonPane.add(new HelpButton(this, "VNAExportCommentDialog"));
      this.btnSave = SwingUtil.createJButton("Button.Save", this);
      this.btnCancel = SwingUtil.createJButton("Button.Cancel", this);
      buttonPane.add(this.btnCancel);
      this.btnSave.setActionCommand("OK");
      buttonPane.add(this.btnSave);
      this.getRootPane().setDefaultButton(this.btnSave);
      this.doDialogInit();
      TraceHelper.exit(this, "VNAExportCommentDialog");
   }

   protected void doDialogInit() {
      this.loadDefaults();
      this.addEscapeKey();
      this.showCentered(this.getWidth(), this.getHeight());
   }

   private void loadDefaults() {
      this.txtComment.setText(this.config.getExportComment());
      this.txtTitle.setText(this.config.getExportTitle());
   }

   private void saveDefaults() {
      this.config.setExportComment(this.txtComment.getText());
      this.config.setExportTitle(this.txtTitle.getText());
   }

   protected void doSave() {
      TraceHelper.entry(this, "doSave");
      this.dialogCancelled = false;
      this.saveDefaults();
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doSave");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      if (e.getSource() == this.btnCancel) {
         this.doDialogCancel();
      } else if (e.getSource() == this.btnSave) {
         this.doSave();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.dialogCancelled = true;
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   public boolean isDialogCancelled() {
      return this.dialogCancelled;
   }
}
