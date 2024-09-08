package krause.vna.gui.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.filter.Gaussian;
import krause.vna.data.filter.VNABaseFilterHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAGaussianFilterCreatorDialog extends KrauseDialog implements ActionListener {
   private static final String PROPERTIES_PREFIX = "VNAGaussianFilterCreatorDialog";
   public static final int FONT_SIZE = 30;
   private VNAConfig config = VNAConfig.getSingleton();
   private JButton btOK;
   private JButton btCreate;
   private JPanel panel;
   private JTextField txtSigma;
   private JComboBox cbLength;

   public VNAGaussianFilterCreatorDialog(Window wnd) {
      super(wnd, true);
      this.setConfigurationPrefix("VNAGaussianFilterCreatorDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNAGaussianFilterCreatorDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(280, 130));
      this.addWindowListener(this);
      this.getContentPane().setLayout(new BorderLayout(5, 5));
      this.panel = new JPanel(new MigLayout("", "[100px]0[grow]", "[][][][]"));
      this.getContentPane().add(this.panel, "Center");
      this.panel.add(new JLabel(VNAMessages.getString("VNAGaussianFilterCreatorDialog.length")), "");
      this.cbLength = new JComboBox(new Integer[]{3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27});
      this.cbLength.setMaximumRowCount(4);
      this.panel.add(this.cbLength, "wrap");
      this.panel.add(new JLabel(VNAMessages.getString("VNAGaussianFilterCreatorDialog.sigma")), "");
      this.txtSigma = new JTextField();
      this.panel.add(this.txtSigma, "grow,wrap");
      this.btOK = SwingUtil.createJButton("Button.Cancel", this);
      this.panel.add(this.btOK, "left");
      this.btCreate = SwingUtil.createJButton("Button.Create", this);
      this.panel.add(this.btCreate, "right");
      this.doDialogInit();
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      TraceHelper.text(this, "actionPerformed", e.toString());
      if (e.getSource() == this.btOK) {
         this.doDialogCancel();
      } else if (e.getSource() == this.btCreate) {
         this.doCreateFile();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doCreateFile() {
      TraceHelper.entry(this, "doCreateFile");

      try {
         double sigma = VNAFormatFactory.getTemperatureFormat().parse(this.txtSigma.getText()).doubleValue();
         int length = (Integer)this.cbLength.getSelectedItem();
         double[] rc = (new Gaussian(sigma)).kernel1D(length);
         VNABaseFilterHelper.saveFilterdata(this.config.getGaussianFilterFileName(), rc);
         this.setVisible(false);
         this.dispose();
      } catch (ParseException var5) {
         ErrorLogHelper.exception(this, "doCreateFile", var5);
      }

      TraceHelper.exit(this, "doCreateFile");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.txtSigma.setText(VNAFormatFactory.getTemperatureFormat().format(1.0D));
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }
}
