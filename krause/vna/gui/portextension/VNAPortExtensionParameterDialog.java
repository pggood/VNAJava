package krause.vna.gui.portextension;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAPortExtensionParameterDialog extends KrauseDialog {
   private JButton btCancel;
   private JButton btOK;
   private JTextField txtLength;
   private JTextField txtVf;
   private VNAConfig config = VNAConfig.getSingleton();
   private JPanel pnlButtons;
   private JCheckBox cbEnabled;

   public VNAPortExtensionParameterDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      TraceHelper.entry(this, "VNAPortExtensionParameterDialog");
      this.setConfigurationPrefix("VNAPortExtensionParameterDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNAPortExtensionParameterDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(350, 160));
      this.getContentPane().setLayout(new MigLayout("", "[][grow,fill]", "[top, grow,fill][][]"));
      this.add(new JLabel(VNAMessages.getString("VNAPortExtensionParameterDialog.lblVf")), "");
      this.txtVf = new JTextField();
      this.txtVf.setColumns(6);
      this.add(this.txtVf, "wrap");
      this.add(new JLabel(VNAMessages.getString("VNAPortExtensionParameterDialog.lblLength.text")), "");
      this.txtLength = new JTextField();
      this.txtLength.setColumns(6);
      this.add(this.txtLength, "wrap");
      this.add(new JLabel(VNAMessages.getString("VNAPortExtensionParameterDialog.enabled")), "");
      this.cbEnabled = new JCheckBox("");
      this.add(this.cbEnabled, "wrap");
      this.pnlButtons = new JPanel();
      this.pnlButtons.setLayout(new MigLayout("", "[][]", "[]"));
      this.getContentPane().add(this.pnlButtons, "span 2, right");
      this.btCancel = SwingUtil.createJButton("VNAPortExtensionParameterDialog.Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAPortExtensionParameterDialog.this.doDialogCancel();
         }
      });
      this.pnlButtons.add(this.btCancel, "width 100px");
      this.btOK = SwingUtil.createJButton("VNAPortExtensionParameterDialog.Button.OK", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAPortExtensionParameterDialog.this.doOK();
         }
      });
      this.pnlButtons.add(this.btOK, "width 100px");
      this.doDialogInit();
      TraceHelper.exit(this, "VNAPortExtensionParameterDialog");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");

      double len;
      try {
         len = VNAFormatFactory.getLengthFormat().parse(this.txtLength.getText()).doubleValue();
      } catch (ParseException var6) {
         JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNAPortExtensionParameterDialog.LenNoNumber.msg"), VNAMessages.getString("VNAPortExtensionParameterDialog.title"), 2);
         return;
      }

      double vf;
      try {
         vf = VNAFormatFactory.getVelocityFormat().parse(this.txtVf.getText()).doubleValue();
         if (vf <= 0.0D || vf > 1.0D) {
            JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNAPortExtensionParameterDialog.VFError.msg"), VNAMessages.getString("VNAPortExtensionParameterDialog.title"), 2);
            return;
         }
      } catch (ParseException var7) {
         JOptionPane.showMessageDialog(this.getOwner(), VNAMessages.getString("VNAPortExtensionParameterDialog.VFNoNumber.msg"), VNAMessages.getString("VNAPortExtensionParameterDialog.title"), 2);
         return;
      }

      this.config.setPortExtensionCableLength(len);
      this.config.setPortExtensionVf(vf);
      this.config.setPortExtensionState(this.cbEnabled.isSelected());
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doOK");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.txtVf.setText(VNAFormatFactory.getVelocityFormat().format(this.config.getPortExtensionVf()));
      this.txtLength.setText(VNAFormatFactory.getPortExtensionLengthFormat().format(this.config.getPortExtensionCableLength()));
      this.cbEnabled.setSelected(this.config.isPortExtensionEnabled());
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }
}
