package krause.vna.gui.export;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAAutoExportSettingsDialog extends KrauseDialog implements ActionListener {
   private VNAConfig config = VNAConfig.getSingleton();
   private JTextField txtName;
   private JTextField txtDirectory;
   private JButton btnSearch;
   private JButton btOK;
   private JButton btCancel;
   private JRadioButton rdbtnNone;
   private JRadioButton rdbtnXls;
   private JRadioButton rdbtnCsv;
   private JRadioButton rdbtnPdf;
   private JRadioButton rdbtnJpg;
   private JRadioButton rdbtnXml;
   private JRadioButton rdbtnZPlot;
   private JRadioButton rdbtnSParm;

   public VNAAutoExportSettingsDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      this.setResizable(false);
      TraceHelper.entry(this, "VNAAutoExportSettingsDialog");
      this.setTitle(VNAMessages.getString("VNAAutoExportDialog.Title"));
      this.setDefaultCloseOperation(0);
      this.setModal(true);
      this.setBounds(100, 100, 556, 250);
      this.getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[][][]"));
      JPanel panel = new JPanel();
      panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Outputfile", 4, 2, (Font)null, new Color(0, 0, 0)));
      this.getContentPane().add(panel, "wrap");
      panel.setLayout(new MigLayout("", "[][grow,fill][]", "[][]"));
      JLabel lblName = new JLabel(VNAMessages.getString("VNAAutoExportDialog.Filename"));
      panel.add(lblName, "");
      this.txtName = new JTextField();
      this.txtName.setToolTipText(VNAMessages.getString("VNAAutoExportDialog.Filename.toolTipText"));
      this.txtName.setColumns(10);
      lblName.setLabelFor(this.txtName);
      panel.add(this.txtName, "wrap");
      JLabel lblDirectory = new JLabel(VNAMessages.getString("VNAAutoExportDialog.Directory"));
      panel.add(lblDirectory, "");
      this.txtDirectory = new JTextField();
      this.txtDirectory.setEditable(false);
      this.txtDirectory.setColumns(10);
      panel.add(this.txtDirectory, "");
      this.btnSearch = SwingUtil.createJButton("VNAAutoExportDialog.ButtonSearch", this);
      panel.add(this.btnSearch, "wrap");
      panel = new JPanel();
      panel.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNAAutoExportDialog.Format"), 4, 2, (Font)null, (Color)null));
      this.rdbtnNone = SwingUtil.createJRadioButton("VNAAutoExportDialog.NoExport", (ActionListener)null);
      panel.add(this.rdbtnNone);
      this.rdbtnCsv = SwingUtil.createJRadioButton("Menu.Export.CSV", (ActionListener)null);
      panel.add(this.rdbtnCsv);
      this.rdbtnJpg = SwingUtil.createJRadioButton("Menu.Export.JPG", (ActionListener)null);
      panel.add(this.rdbtnJpg);
      this.rdbtnPdf = SwingUtil.createJRadioButton("Menu.Export.PDF", (ActionListener)null);
      panel.add(this.rdbtnPdf);
      this.rdbtnSParm = SwingUtil.createJRadioButton("Menu.Export.S2P", (ActionListener)null);
      panel.add(this.rdbtnSParm);
      this.rdbtnXls = SwingUtil.createJRadioButton("Menu.Export.XLS", (ActionListener)null);
      panel.add(this.rdbtnXls);
      this.rdbtnXml = SwingUtil.createJRadioButton("Menu.Export.XML", (ActionListener)null);
      panel.add(this.rdbtnXml);
      this.rdbtnZPlot = SwingUtil.createJRadioButton("Menu.Export.ZPlot", (ActionListener)null);
      panel.add(this.rdbtnZPlot);
      this.getContentPane().add(panel, "wrap");
      ButtonGroup bg = new ButtonGroup();
      bg.add(this.rdbtnNone);
      bg.add(this.rdbtnCsv);
      bg.add(this.rdbtnJpg);
      bg.add(this.rdbtnPdf);
      bg.add(this.rdbtnSParm);
      bg.add(this.rdbtnXls);
      bg.add(this.rdbtnXml);
      bg.add(this.rdbtnZPlot);
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(2));
      this.getContentPane().add(buttonPane, "right,wrap");
      this.btOK = SwingUtil.createJButton("Button.Save", this);
      this.btCancel = SwingUtil.createJButton("Button.Cancel", this);
      buttonPane.add(new HelpButton(this, "VNAAutoExportSettingsDialog"));
      buttonPane.add(this.btCancel);
      this.btOK.setActionCommand("OK");
      buttonPane.add(this.btOK);
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNAAutoExportSettingsDialog");
   }

   protected void doDialogInit() {
      this.loadDefaults();
      this.addEscapeKey();
      this.pack();
      this.showCentered(this.getWidth(), this.getHeight());
   }

   private void loadDefaults() {
      TraceHelper.entry(this, "loadDefaults");
      this.txtDirectory.setText(this.config.getAutoExportDirectory());
      this.txtName.setText(this.config.getAutoExportFilename());
      this.rdbtnNone.setSelected(true);
      this.rdbtnCsv.setSelected(this.config.getAutoExportFormat() == 1);
      this.rdbtnJpg.setSelected(this.config.getAutoExportFormat() == 2);
      this.rdbtnPdf.setSelected(this.config.getAutoExportFormat() == 3);
      this.rdbtnSParm.setSelected(this.config.getAutoExportFormat() == 4);
      this.rdbtnXls.setSelected(this.config.getAutoExportFormat() == 5);
      this.rdbtnXml.setSelected(this.config.getAutoExportFormat() == 6);
      this.rdbtnZPlot.setSelected(this.config.getAutoExportFormat() == 7);
      TraceHelper.exit(this, "loadDefaults");
   }

   private void saveDefaults() {
      TraceHelper.entry(this, "saveDefaults");
      this.config.setAutoExportDirectory(this.txtDirectory.getText());
      this.config.setAutoExportFilename(this.txtName.getText());
      if (this.rdbtnNone.isSelected()) {
         this.config.setAutoExportFormat(0);
      } else if (this.rdbtnCsv.isSelected()) {
         this.config.setAutoExportFormat(1);
      } else if (this.rdbtnJpg.isSelected()) {
         this.config.setAutoExportFormat(2);
      } else if (this.rdbtnPdf.isSelected()) {
         this.config.setAutoExportFormat(3);
      } else if (this.rdbtnSParm.isSelected()) {
         this.config.setAutoExportFormat(4);
      } else if (this.rdbtnXls.isSelected()) {
         this.config.setAutoExportFormat(5);
      } else if (this.rdbtnXml.isSelected()) {
         this.config.setAutoExportFormat(6);
      } else if (this.rdbtnZPlot.isSelected()) {
         this.config.setAutoExportFormat(7);
      }

      TraceHelper.exit(this, "saveDefaults");
   }

   protected void doDialogCancel() {
      this.setVisible(false);
      this.dispose();
   }

   protected void doSave() {
      this.saveDefaults();
      this.doDialogCancel();
   }

   private void doSelectExportDirectory() {
      TraceHelper.entry(this, "doSelectExportDirectory");
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(1);
      fc.setSelectedFile(new File(this.config.getExportDirectory()));
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == 0) {
         File file = fc.getSelectedFile();
         this.config.setExportDirectory(file.getAbsolutePath());
         this.txtDirectory.setText(this.config.getExportDirectory());
      }

      TraceHelper.exit(this, "doSelectExportDirectory");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      if (e.getSource() == this.btCancel) {
         this.doDialogCancel();
      } else if (e.getSource() == this.btOK) {
         this.doSave();
      } else if (e.getSource() == this.btnSearch) {
         this.doSelectExportDirectory();
      }

      TraceHelper.exit(this, "actionPerformed");
   }
}
