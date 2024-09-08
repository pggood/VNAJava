package krause.vna.gui.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

public class VNAExportSettingsDialog extends KrauseDialog implements ActionListener {
   private final JPanel contentPanel;
   private VNAConfig config = VNAConfig.getSingleton();
   private JTextField txtName;
   private JTextField txtDirectory;
   private JTextArea txtComment;
   private JCheckBox cbOverwrite;
   private JTextField txtTitle;
   private JRadioButton rbDecSepComma;
   private JRadioButton rbDecSepDot;
   private JButton btnSearch;
   private JButton btnSave;
   private JButton btnCancel;
   private JTextField txtJPGWidth;
   private JTextField txtJPGHeight;
   private JRadioButton rbMarkerSizeSmall;
   private JRadioButton rbMarkerSizeMedium;
   private JRadioButton rbMarkerSizeLarge;
   private JCheckBox cbMarkerDataInDiagram;
   private JCheckBox cbMarkerDataHorizontal;
   private JCheckBox cbSubLegend;
   private JCheckBox cbMainLegend;
   private JCheckBox cbFooter;
   private JComboBox<String> cbFontTextMarker;
   private JComboBox<String> cbFontHeadline;

   public VNAExportSettingsDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      TraceHelper.entry(this, "VNAExportSettingsDialog");
      this.setTitle(VNAMessages.getString("VNAExportDialog.Title"));
      this.setDefaultCloseOperation(0);
      this.setConfigurationPrefix("VNAExportSettingsDialog");
      this.setProperties(this.config);
      this.setModal(true);
      this.setMinimumSize(new Dimension(630, 520));
      this.setPreferredSize(new Dimension(850, 550));
      this.getContentPane();
      this.contentPanel = new JPanel();
      this.contentPanel.setLayout(new MigLayout("", "[grow,fill]", "0[]0[grow,fill]0[]0[]0"));
      this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.getContentPane().add(this.contentPanel);
      JPanel panel_1 = new JPanel();
      panel_1.setLayout(new MigLayout("", "[][grow,fill][]", "0[]0[]0"));
      panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Outputfile"), 4, 2, (Font)null, new Color(0, 0, 0)));
      JLabel lblName = new JLabel(VNAMessages.getString("VNAExportDialog.Filename"));
      lblName.setBounds(12, 26, 87, 16);
      lblName.setLabelFor(this.txtName);
      panel_1.add(lblName, "");
      this.txtName = new JTextField();
      this.txtName.setToolTipText(VNAMessages.getString("VNAExportDialog.txtName.toolTipText"));
      this.txtName.setColumns(10);
      panel_1.add(this.txtName, "");
      this.cbOverwrite = new JCheckBox(VNAMessages.getString("VNAExportDialog.CbOverwrite"));
      this.cbOverwrite.setToolTipText(VNAMessages.getString("VNAExportDialog.cbOverwrite.toolTipText"));
      panel_1.add(this.cbOverwrite, "wrap");
      JLabel lblDirectory = new JLabel(VNAMessages.getString("VNAExportDialog.Directory"));
      lblDirectory.setBounds(12, 60, 87, 16);
      panel_1.add(lblDirectory, "");
      this.txtDirectory = new JTextField();
      this.txtDirectory.setEditable(false);
      panel_1.add(this.txtDirectory, "");
      this.btnSearch = new JButton(VNAMessages.getString("VNAExportDialog.ButtonSearch"));
      this.btnSearch.setToolTipText(VNAMessages.getString("VNAExportSettingsDialog.btnSearch.toolTipText"));
      this.btnSearch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAExportSettingsDialog.this.doSelectExportDirectory();
         }
      });
      panel_1.add(this.btnSearch, "wrap");
      JPanel panel_2 = new JPanel();
      panel_2.setLayout(new MigLayout("", "[grow,fill][]", "0[][grow,fill]"));
      panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Headline"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.txtTitle = new JTextField();
      this.txtTitle.setBorder(new LineBorder(new Color(171, 173, 179)));
      panel_2.add(this.txtTitle, "");
      panel_2.add(new JLabel(VNAMessages.getString("VNAExportDialog.FontSizeTextMarker")), "");
      this.cbFontHeadline = new JComboBox(new String[]{"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"});
      panel_2.add(this.cbFontHeadline, "wrap");
      this.txtComment = new JTextArea();
      if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
         this.txtComment.setFont(new Font("Monospaced", 0, 12));
      } else {
         this.txtComment.setFont(new Font("Courier New", 0, 12));
      }

      this.txtComment.setLineWrap(true);
      this.txtComment.setWrapStyleWord(true);
      JScrollPane sp = new JScrollPane(this.txtComment);
      panel_2.add(sp, "span 3");
      JPanel panel_3 = new JPanel();
      panel_3.setLayout(new MigLayout("", "[][]", "0[]0"));
      panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.DecimalSeparator"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.rbDecSepComma = SwingUtil.createJRadioButton("VNAExportDialog.DecimalSeparatorComma", this);
      this.rbDecSepDot = SwingUtil.createJRadioButton("VNAExportDialog.DecimalSeparatorDot", this);
      panel_3.add(this.rbDecSepComma, "");
      panel_3.add(this.rbDecSepDot, "");
      ButtonGroup aGroup = new ButtonGroup();
      aGroup.add(this.rbDecSepComma);
      aGroup.add(this.rbDecSepDot);
      JPanel panel_6 = new JPanel();
      panel_6.setLayout(new MigLayout("", "[][]", "0[]0"));
      panel_6.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.MarkerSize"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.rbMarkerSizeSmall = SwingUtil.createJRadioButton("VNAExportDialog.MarkerSizeSmall", this);
      this.rbMarkerSizeMedium = SwingUtil.createJRadioButton("VNAExportDialog.MarkerSizeMedium", this);
      this.rbMarkerSizeLarge = SwingUtil.createJRadioButton("VNAExportDialog.MarkerSizeLarge", this);
      panel_6.add(this.rbMarkerSizeSmall, "");
      panel_6.add(this.rbMarkerSizeMedium, "");
      panel_6.add(this.rbMarkerSizeLarge, "wrap");
      aGroup = new ButtonGroup();
      aGroup.add(this.rbMarkerSizeSmall);
      aGroup.add(this.rbMarkerSizeMedium);
      aGroup.add(this.rbMarkerSizeLarge);
      panel_6.add(new JLabel(VNAMessages.getString("VNAExportDialog.FontSizeTextMarker")), "");
      this.cbFontTextMarker = new JComboBox(new String[]{"10", "15", "20", "25"});
      panel_6.add(this.cbFontTextMarker, "");
      JPanel panel_5 = new JPanel();
      panel_5.setLayout(new MigLayout("", "[][]", "0[]0"));
      panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.MarkerData"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.cbMarkerDataInDiagram = SwingUtil.createJCheckbox("VNAExportDialog.MarkerDataInDiagramm", this);
      this.cbMarkerDataHorizontal = SwingUtil.createJCheckbox("VNAExportDialog.MarkerDataHorizontal", this);
      panel_5.add(this.cbMarkerDataInDiagram, "wrap");
      panel_5.add(this.cbMarkerDataHorizontal, "");
      JPanel panel_7 = new JPanel();
      panel_7.setLayout(new MigLayout("", "[][]", "0[]0"));
      panel_7.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Legends"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.cbSubLegend = SwingUtil.createJCheckbox("VNAExportDialog.ShowSubLegend", this);
      this.cbMainLegend = SwingUtil.createJCheckbox("VNAExportDialog.ShowMainLegend", this);
      this.cbFooter = SwingUtil.createJCheckbox("VNAExportDialog.ShowFooter", this);
      panel_7.add(this.cbMainLegend, "");
      panel_7.add(this.cbSubLegend, "");
      panel_7.add(this.cbFooter, "");
      JPanel panel_4 = new JPanel();
      panel_4.setLayout(new MigLayout("", "[][]", "0[]0"));
      panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.JPGSize"), 4, 2, (Font)null, new Color(0, 0, 0)));
      this.txtJPGWidth = new JTextField();
      this.txtJPGWidth.setColumns(8);
      this.txtJPGWidth.setHorizontalAlignment(4);
      this.txtJPGHeight = new JTextField();
      this.txtJPGHeight.setColumns(8);
      this.txtJPGHeight.setHorizontalAlignment(4);
      panel_4.add(new JLabel(VNAMessages.getString("VNAExportDialog.JPGSize.Width")), "");
      panel_4.add(this.txtJPGWidth, "");
      panel_4.add(new JLabel(VNAMessages.getString("VNAExportDialog.JPGSize.Height")), "");
      panel_4.add(this.txtJPGHeight, "");
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(2));
      this.btnSave = SwingUtil.createJButton("Button.Save", this);
      this.btnCancel = SwingUtil.createJButton("Button.Cancel", this);
      buttonPane.add(new HelpButton(this, "VNAExportSettingsDialog"));
      buttonPane.add(this.btnCancel);
      this.btnSave.setActionCommand("OK");
      buttonPane.add(this.btnSave);
      this.getRootPane().setDefaultButton(this.btnSave);
      this.contentPanel.add(panel_1, "grow, span 3,wrap");
      this.contentPanel.add(panel_2, "grow, span 3,wrap");
      this.contentPanel.add(panel_3, "grow");
      this.contentPanel.add(panel_5, "");
      this.contentPanel.add(panel_6, "wrap");
      this.contentPanel.add(panel_4, "");
      this.contentPanel.add(panel_7, "span 2, grow, wrap");
      this.contentPanel.add(buttonPane, "grow, span 3,wrap");
      this.doDialogInit();
      TraceHelper.exit(this, "VNAExportSettingsDialog");
   }

   protected void doDialogInit() {
      this.loadDefaults();
      this.addEscapeKey();
      this.doDialogShow();
   }

   private void loadDefaults() {
      this.txtDirectory.setText(this.config.getExportDirectory());
      this.txtName.setText(this.config.getExportFilename());
      this.txtComment.setText(this.config.getExportComment());
      this.txtTitle.setText(this.config.getExportTitle());
      this.cbOverwrite.setSelected(this.config.isExportOverwrite());
      this.rbDecSepComma.setSelected(",".equals(this.config.getExportDecimalSeparator()));
      this.rbDecSepDot.setSelected(".".equals(this.config.getExportDecimalSeparator()));
      this.rbMarkerSizeSmall.setSelected(this.config.getMarkerSize() == 1);
      this.rbMarkerSizeMedium.setSelected(this.config.getMarkerSize() == 2);
      this.rbMarkerSizeLarge.setSelected(this.config.getMarkerSize() == 3);
      this.cbMarkerDataHorizontal.setSelected(this.config.isPrintMarkerDataHorizontal());
      this.cbMarkerDataInDiagram.setSelected(this.config.isPrintMarkerDataInDiagramm());
      this.cbSubLegend.setSelected(this.config.isPrintSubLegend());
      this.cbMainLegend.setSelected(this.config.isPrintMainLegend());
      this.cbFooter.setSelected(this.config.isPrintFooter());
      this.txtJPGWidth.setText("" + this.config.getExportDiagramWidth());
      this.txtJPGHeight.setText("" + this.config.getExportDiagramHeight());
      this.cbFontTextMarker.setSelectedItem("" + this.config.getFontSizeTextMarker());
      this.cbFontHeadline.setSelectedItem("" + this.config.getExportTitleFontSize());
   }

   private void saveDefaults() {
      this.config.setExportComment(this.txtComment.getText());
      this.config.setExportDirectory(this.txtDirectory.getText());
      this.config.setExportFilename(this.txtName.getText());
      this.config.setExportTitle(this.txtTitle.getText());
      this.config.setExportOverwrite(this.cbOverwrite.isSelected());
      this.config.setExportDecimalSeparator(this.rbDecSepComma.isSelected() ? "," : ".");
      if (this.rbMarkerSizeLarge.isSelected()) {
         this.config.setMarkerSize(3);
      } else if (this.rbMarkerSizeMedium.isSelected()) {
         this.config.setMarkerSize(2);
      } else {
         this.config.setMarkerSize(1);
      }

      this.config.setPrintMarkerDataHorizontal(this.cbMarkerDataHorizontal.isSelected());
      this.config.setPrintMarkerDataInDiagramm(this.cbMarkerDataInDiagram.isSelected());
      this.config.setPrintSubLegend(this.cbSubLegend.isSelected());
      this.config.setPrintMainLegend(this.cbMainLegend.isSelected());
      this.config.setPrintFooter(this.cbFooter.isSelected());
      this.config.setExportDiagramWidth(Integer.parseInt(this.txtJPGWidth.getText()));
      this.config.setExportDiagramHeight(Integer.parseInt(this.txtJPGHeight.getText()));
      this.config.setFontSizeTextMarker(Integer.parseInt((String)this.cbFontTextMarker.getSelectedItem()));
      this.config.setExportTitleFontSize(Integer.parseInt((String)this.cbFontHeadline.getSelectedItem()));
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
      if (e.getSource() == this.btnCancel) {
         this.doDialogCancel();
      } else if (e.getSource() == this.btnSave) {
         this.doSave();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }
}
