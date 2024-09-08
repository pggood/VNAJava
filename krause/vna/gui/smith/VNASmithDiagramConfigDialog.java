package krause.vna.gui.smith;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.input.ComplexInputFieldValueChangeListener;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.complex.Complex;

public class VNASmithDiagramConfigDialog extends KrauseDialog implements ComplexInputFieldValueChangeListener, MouseListener {
   public static final Color SMITH_PANEL_DEFCOL_IMPEDANCE;
   public static final Color SMITH_PANEL_DEFCOL_ADMITTANCE;
   public static final Color SMITH_PANEL_DEFCOL_SWR;
   public static final Color SMITH_PANEL_DEFCOL_MARKER;
   public static final Color SMITH_PANEL_DEFCOL_BACKGROUND;
   public static final Color SMITH_PANEL_DEFCOL_DATA;
   public static final Color SMITH_PANEL_DEFCOL_TEXT;
   public static final String SMITH_PANEL_COL_MARKER = "SmithPanel.colMarker";
   public static final String SMITH_PANEL_COL_BACKGROUND = "SmithPanel.colBackground";
   public static final String SMITH_PANEL_COL_DATA = "SmithPanel.colLines";
   public static final String SMITH_PANEL_COL_TEXT = "SmithPanel.colText";
   public static final String SMITH_PANEL_COL_SWR = "SmithPanel.colSWR";
   public static final String SMITH_PANEL_COL_IMPEDANCE = "SmithPanel.colImpedance";
   public static final String SMITH_PANEL_COL_ADMITTANCE = "SmithPanel.colInductance";
   public static final String SMITH_PANEL_SHOW_MARKER_SWR = "SmithPanel.showMarker.SWR";
   public static final String SMITH_PANEL_SHOW_MARKER_Z = "SmithPanel.showMarker.Z";
   public static final String SMITH_PANEL_SHOW_MARKER_XS = "SmithPanel.showMarker.XS";
   public static final String SMITH_PANEL_SHOW_MARKER_RS = "SmithPanel.showMarker.RS";
   public static final String SMITH_PANEL_SHOW_MARKER_RL = "SmithPanel.showMarker.RL";
   public static final String SMITH_PANEL_SHOW_MARKER_PHASE = "SmithPanel.showMarker.Phase";
   public static final String SMITH_PANEL_SHOW_MARKER_MAG = "SmithPanel.showMarker.Mag";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_TEXT_COLOR = "VNASmithDiagramConfigDialog.textColor";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_BACK_COLOR = "VNASmithDiagramConfigDialog.backColor";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_SWR_COLOR = "VNASmithDiagramConfigDialog.swrColor";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_MARKER_COLOR = "VNASmithDiagramConfigDialog.markerColor";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_DATA_COLOR = "VNASmithDiagramConfigDialog.dataColor";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_IMPEDANCE_COLOR = "VNASmithDiagramConfigDialog.impedanceColor";
   private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_ADMITTANCE_COLOR = "VNASmithDiagramConfigDialog.admittanceColor";
   public static final String SMITH_PANEL_CIRCLES_SWR = "SmithPanel.circlesSWR";
   public static final String SMITH_PANEL_CIRCLES_ADMITTANCE_REAL = "SmithPanel.circlesAdmittanceReal";
   public static final String SMITH_PANEL_CIRCLES_ADMITTANCE_IMAGINARY = "SmithPanel.circlesAdmittanceImaginary";
   public static final String SMITH_PANEL_CIRCLES_IMPEDANCE_REAL = "SmithPanel.circlesImpedanceReal";
   public static final String SMITH_PANEL_CIRCLES_IMPEDANCE_IMAGINARY = "SmithPanel.circlesImpedanceImaginary";
   private VNAConfig config = VNAConfig.getSingleton();
   public static final String DEFAULT_CIRCLES_ADMITTANCE_IMAG = "";
   public static final String DEFAULT_CIRCLES_ADMITTANCE_REAL = "";
   public static final String DEFAULT_CIRCLES_IMPEDANCE_IMAG = "-5.0 -2.0 -1.0 -0.5 -0.2 0.0 0.2 0.5 1.0 2.0 5.0";
   public static final String DEFAULT_CIRCLES_IMPEDANCE_REAL = "0.0 0.2 0.5 1.0 2.0 5.0";
   public static final String DEFAULT_CIRCLES_SWR = "2.0 3.0";
   private JCheckBox cbRL;
   private JCheckBox cbRS;
   private JCheckBox cbXS;
   private JCheckBox cbZ;
   private JCheckBox cbPHASE;
   private ComplexInputField referenceResistance;
   private JTextField txtColBackground;
   private JTextField txtColText;
   private JTextField txtColData;
   private JTextField txtColMarker;
   private JTextField txtColSWR;
   private JTextField txtColAdmittance;
   private JTextField txtColImpedance;
   private JCheckBox cbMag;
   private AbstractButton cbSWR;
   private JTextField txtAdmittanceRealCircles;
   private JTextField txtAdmittanceImaginaryCircles;
   private JTextField txtImpedanceRealCircles;
   private JTextField txtImpedanceImaginaryCircles;
   private JTextField txtSWRCircles;

   static {
      SMITH_PANEL_DEFCOL_IMPEDANCE = Color.RED;
      SMITH_PANEL_DEFCOL_ADMITTANCE = Color.BLUE;
      SMITH_PANEL_DEFCOL_SWR = Color.CYAN;
      SMITH_PANEL_DEFCOL_MARKER = Color.YELLOW;
      SMITH_PANEL_DEFCOL_BACKGROUND = Color.BLACK;
      SMITH_PANEL_DEFCOL_DATA = Color.GREEN;
      SMITH_PANEL_DEFCOL_TEXT = Color.WHITE;
   }

   public VNASmithDiagramConfigDialog() {
      super(true);
      String methodName = "VNASmithDiagramConfigDialog";
      TraceHelper.entry(this, "VNASmithDiagramConfigDialog");
      this.setConfigurationPrefix("VNASmithDiagramConfigDialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setDefaultCloseOperation(0);
      this.setTitle(VNAMessages.getString("VNASmithDiagramConfigDialog.title"));
      this.setResizable(true);
      this.setPreferredSize(new Dimension(390, 670));
      this.getContentPane().setLayout(new MigLayout("", "", ""));
      this.getContentPane().add(this.createCenterPanel(), "grow,wrap");
      this.getContentPane().add(this.createButtonPanel(), "grow,wrap");
      this.doDialogInit();
      TraceHelper.exit(this, "VNASmithDiagramConfigDialog");
   }

   private Component createButtonPanel() {
      String methodName = "createButtonPanel";
      TraceHelper.entry(this, "createButtonPanel");
      JPanel pnlButton = new JPanel();
      pnlButton.add(SwingUtil.createJButton("Button.Default", (e) -> {
         this.doDefaults();
      }));
      pnlButton.add(SwingUtil.createJButton("Button.Cancel", (e) -> {
         this.doDialogCancel();
      }));
      pnlButton.add(SwingUtil.createJButton("Button.OK", (e) -> {
         this.doOK();
      }));
      TraceHelper.exit(this, "createButtonPanel");
      return pnlButton;
   }

   private JPanel createMarkerOptionPanel() {
      String methodName = "createMarkerOptionPanel";
      TraceHelper.entry(this, "createMarkerOptionPanel");
      JPanel pnlMarkerOpt = new JPanel(new MigLayout("", "[][][][]", "[]"));
      pnlMarkerOpt.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASmithDiagramConfigDialog.markers"), 4, 2, (Font)null, (Color)null));
      pnlMarkerOpt.add(this.cbRL = SwingUtil.createJCheckbox("Marker.RL", (ActionListener)null), "");
      pnlMarkerOpt.add(this.cbPHASE = SwingUtil.createJCheckbox("Marker.PhaseRL", (ActionListener)null), "");
      pnlMarkerOpt.add(this.cbZ = SwingUtil.createJCheckbox("Marker.Z", (ActionListener)null), "");
      pnlMarkerOpt.add(this.cbRS = SwingUtil.createJCheckbox("Marker.R", (ActionListener)null), "wrap");
      pnlMarkerOpt.add(this.cbXS = SwingUtil.createJCheckbox("Marker.X", (ActionListener)null), "");
      pnlMarkerOpt.add(this.cbSWR = SwingUtil.createJCheckbox("Marker.SWR", (ActionListener)null), "");
      pnlMarkerOpt.add(this.cbMag = SwingUtil.createJCheckbox("Marker.Magnitude", (ActionListener)null), "");
      this.cbRL.setToolTipText((String)null);
      this.cbPHASE.setToolTipText((String)null);
      this.cbZ.setToolTipText((String)null);
      this.cbRS.setToolTipText((String)null);
      this.cbXS.setToolTipText((String)null);
      this.cbSWR.setToolTipText((String)null);
      this.cbMag.setToolTipText((String)null);
      TraceHelper.exit(this, "createMarkerOptionPanel");
      return pnlMarkerOpt;
   }

   private JPanel createColorOptionPanel() {
      TraceHelper.entry(this, "createColorOptionPanel");
      JPanel pnlColors = new JPanel(new MigLayout("", "[grow,fill][][]", "[][][][][][][]"));
      pnlColors.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASmithDiagramConfigDialog.colors"), 4, 2, (Font)null, (Color)null));
      this.txtColBackground = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.backColor", this);
      this.txtColText = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.textColor", this);
      this.txtColData = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.dataColor", this);
      this.txtColMarker = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.markerColor", this);
      this.txtColAdmittance = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.admittanceColor", this);
      this.txtColImpedance = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.impedanceColor", this);
      this.txtColSWR = this.createColorSelectField(pnlColors, "VNASmithDiagramConfigDialog.swrColor", this);
      pnlColors.add(SwingUtil.createJButton("VNASmithDiagramConfigDialog.invert", (e) -> {
         this.doInvert();
      }), "grow,wrap");
      TraceHelper.exit(this, "createColorOptionPanel");
      return pnlColors;
   }

   private JPanel createReferenceOptionPanel() {
      TraceHelper.entry(this, "createReferenceOptionPanel");
      JPanel pnlRefRes = new JPanel(new MigLayout("", "[][grow,fill]", ""));
      pnlRefRes.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASmithDiagramConfigDialog.reference"), 4, 2, (Font)null, (Color)null));
      this.referenceResistance = new ComplexInputField(this.config.getSmithReference());
      this.referenceResistance.setMaximum(new Complex(5000.0D, 5000.0D));
      this.referenceResistance.setMinimum(new Complex(-5000.0D, -5000.0D));
      this.referenceResistance.setListener(this);
      pnlRefRes.add(this.referenceResistance);
      TraceHelper.exit(this, "createReferenceOptionPanel");
      return pnlRefRes;
   }

   private JPanel createCircleOptionPanel() {
      TraceHelper.entry(this, "createCircleOptionPanel");
      JPanel rc = new JPanel(new MigLayout("", "[][grow,fill]", ""));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNASmithDiagramConfigDialog.circles"), 4, 2, (Font)null, (Color)null));
      rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceRealCircles")));
      this.txtAdmittanceRealCircles = new JTextField();
      this.txtAdmittanceRealCircles.setEditable(true);
      this.txtAdmittanceRealCircles.setHorizontalAlignment(4);
      this.txtAdmittanceRealCircles.setColumns(40);
      this.txtAdmittanceRealCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceRealCircles.tooltip"));
      rc.add(this.txtAdmittanceRealCircles, "grow,wrap");
      rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceImaginaryCircles")));
      this.txtAdmittanceImaginaryCircles = new JTextField();
      this.txtAdmittanceImaginaryCircles.setEditable(true);
      this.txtAdmittanceImaginaryCircles.setHorizontalAlignment(4);
      this.txtAdmittanceImaginaryCircles.setColumns(40);
      this.txtAdmittanceImaginaryCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceImaginaryCircles.tooltip"));
      rc.add(this.txtAdmittanceImaginaryCircles, "grow,wrap");
      rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceRealCircles")));
      this.txtImpedanceRealCircles = new JTextField();
      this.txtImpedanceRealCircles.setEditable(true);
      this.txtImpedanceRealCircles.setHorizontalAlignment(4);
      this.txtImpedanceRealCircles.setColumns(40);
      this.txtImpedanceRealCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceRealCircles.tooltip"));
      rc.add(this.txtImpedanceRealCircles, "grow,wrap");
      rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceImaginaryCircles")));
      this.txtImpedanceImaginaryCircles = new JTextField();
      this.txtImpedanceImaginaryCircles.setEditable(true);
      this.txtImpedanceImaginaryCircles.setHorizontalAlignment(4);
      this.txtImpedanceImaginaryCircles.setColumns(40);
      this.txtImpedanceImaginaryCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceImaginaryCircles.tooltip"));
      rc.add(this.txtImpedanceImaginaryCircles, "grow,wrap");
      rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.swrCircles")));
      this.txtSWRCircles = new JTextField();
      this.txtSWRCircles.setEditable(true);
      this.txtSWRCircles.setHorizontalAlignment(4);
      this.txtSWRCircles.setColumns(40);
      this.txtSWRCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.swrCircles.tooltip"));
      rc.add(this.txtSWRCircles, "grow,wrap");
      TraceHelper.exit(this, "createCircleOptionPanel");
      return rc;
   }

   private JPanel createCenterPanel() {
      TraceHelper.entry(this, "createCenterPanel");
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", ""));
      rc.add(this.createMarkerOptionPanel(), "wrap,grow");
      rc.add(this.createReferenceOptionPanel(), "wrap,grow");
      rc.add(this.createColorOptionPanel(), "wrap, grow");
      rc.add(this.createCircleOptionPanel(), "wrap, grow");
      TraceHelper.exit(this, "createCenterPanel");
      return rc;
   }

   private JTextField createColorSelectField(JPanel panel, String id, MouseListener mouseListener) {
      TraceHelper.entry(this, "createColorSelectField");
      JLabel lbl = new JLabel(VNAMessages.getString(id));
      panel.add(lbl, "");
      JTextField rc = new JTextField(10);
      rc.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      rc.setEditable(false);
      rc.setOpaque(true);
      rc.setForeground(SMITH_PANEL_DEFCOL_BACKGROUND);
      rc.addMouseListener(mouseListener);
      rc.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.clickToSelect"));
      panel.add(rc, "width 40,wrap");
      TraceHelper.exit(this, "createColorSelectField");
      return rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDefaults() {
      TraceHelper.entry(this, "doDefaults");
      this.cbZ.setSelected(true);
      this.cbRS.setSelected(true);
      this.cbXS.setSelected(true);
      this.cbRL.setSelected(true);
      this.cbPHASE.setSelected(true);
      this.cbMag.setSelected(true);
      this.referenceResistance.setComplexValue(new Complex(50.0D, 0.0D));
      this.txtColText.setBackground(SMITH_PANEL_DEFCOL_TEXT);
      this.txtColData.setBackground(SMITH_PANEL_DEFCOL_DATA);
      this.txtColBackground.setBackground(SMITH_PANEL_DEFCOL_BACKGROUND);
      this.txtColMarker.setBackground(SMITH_PANEL_DEFCOL_MARKER);
      this.txtColAdmittance.setBackground(SMITH_PANEL_DEFCOL_ADMITTANCE);
      this.txtColImpedance.setBackground(SMITH_PANEL_DEFCOL_IMPEDANCE);
      this.txtColSWR.setBackground(SMITH_PANEL_DEFCOL_SWR);
      this.txtAdmittanceImaginaryCircles.setText("");
      this.txtAdmittanceRealCircles.setText("");
      this.txtImpedanceImaginaryCircles.setText("-5.0 -2.0 -1.0 -0.5 -0.2 0.0 0.2 0.5 1.0 2.0 5.0");
      this.txtImpedanceRealCircles.setText("0.0 0.2 0.5 1.0 2.0 5.0");
      this.txtSWRCircles.setText("2.0 3.0");
      TraceHelper.exit(this, "doDefaults");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.cbMag.setSelected(this.config.getBoolean("SmithPanel.showMarker.Mag", true));
      this.cbPHASE.setSelected(this.config.getBoolean("SmithPanel.showMarker.Phase", true));
      this.cbRL.setSelected(this.config.getBoolean("SmithPanel.showMarker.RL", true));
      this.cbRS.setSelected(this.config.getBoolean("SmithPanel.showMarker.RS", true));
      this.cbXS.setSelected(this.config.getBoolean("SmithPanel.showMarker.XS", true));
      this.cbZ.setSelected(this.config.getBoolean("SmithPanel.showMarker.Z", true));
      this.cbSWR.setSelected(this.config.getBoolean("SmithPanel.showMarker.SWR", true));
      this.referenceResistance.setComplexValue(this.config.getSmithReference());
      this.txtColText.setBackground(this.config.getColor("SmithPanel.colText", SMITH_PANEL_DEFCOL_TEXT));
      this.txtColData.setBackground(this.config.getColor("SmithPanel.colLines", SMITH_PANEL_DEFCOL_DATA));
      this.txtColBackground.setBackground(this.config.getColor("SmithPanel.colBackground", SMITH_PANEL_DEFCOL_BACKGROUND));
      this.txtColMarker.setBackground(this.config.getColor("SmithPanel.colMarker", SMITH_PANEL_DEFCOL_MARKER));
      this.txtColSWR.setBackground(this.config.getColor("SmithPanel.colSWR", SMITH_PANEL_DEFCOL_SWR));
      this.txtColAdmittance.setBackground(this.config.getColor("SmithPanel.colInductance", SMITH_PANEL_DEFCOL_ADMITTANCE));
      this.txtColImpedance.setBackground(this.config.getColor("SmithPanel.colImpedance", SMITH_PANEL_DEFCOL_IMPEDANCE));
      this.txtAdmittanceImaginaryCircles.setText(this.config.getProperty("SmithPanel.circlesAdmittanceImaginary", ""));
      this.txtAdmittanceRealCircles.setText(this.config.getProperty("SmithPanel.circlesAdmittanceReal", ""));
      this.txtImpedanceImaginaryCircles.setText(this.config.getProperty("SmithPanel.circlesImpedanceImaginary", "-5.0 -2.0 -1.0 -0.5 -0.2 0.0 0.2 0.5 1.0 2.0 5.0"));
      this.txtImpedanceRealCircles.setText(this.config.getProperty("SmithPanel.circlesImpedanceReal", "0.0 0.2 0.5 1.0 2.0 5.0"));
      this.txtSWRCircles.setText(this.config.getProperty("SmithPanel.circlesSWR", "2.0 3.0"));
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doInvert() {
      TraceHelper.entry(this, "doInvert");
      invertBackgroundColor(this.txtColBackground);
      invertBackgroundColor(this.txtColData);
      invertBackgroundColor(this.txtColAdmittance);
      invertBackgroundColor(this.txtColMarker);
      invertBackgroundColor(this.txtColText);
      invertBackgroundColor(this.txtColSWR);
      TraceHelper.exit(this, "doInvert");
   }

   private static void invertBackgroundColor(JTextField tf) {
      tf.setBackground(new Color(~tf.getBackground().getRGB()));
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      this.config.putBoolean("SmithPanel.showMarker.Mag", this.cbMag.isSelected());
      this.config.putBoolean("SmithPanel.showMarker.Phase", this.cbPHASE.isSelected());
      this.config.putBoolean("SmithPanel.showMarker.RL", this.cbRL.isSelected());
      this.config.putBoolean("SmithPanel.showMarker.RS", this.cbRS.isSelected());
      this.config.putBoolean("SmithPanel.showMarker.XS", this.cbXS.isSelected());
      this.config.putBoolean("SmithPanel.showMarker.Z", this.cbZ.isSelected());
      this.config.putBoolean("SmithPanel.showMarker.SWR", this.cbSWR.isSelected());
      Complex oldRes = this.config.getSmithReference();
      Complex newRes = this.referenceResistance.getComplexValue();
      if (!oldRes.equals(newRes)) {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("SmithPanel.1"), this.getTitle(), 1);
      }

      this.config.setSmithReference(newRes);
      this.config.putColor("SmithPanel.colText", this.txtColText.getBackground());
      this.config.putColor("SmithPanel.colLines", this.txtColData.getBackground());
      this.config.putColor("SmithPanel.colBackground", this.txtColBackground.getBackground());
      this.config.putColor("SmithPanel.colMarker", this.txtColMarker.getBackground());
      this.config.putColor("SmithPanel.colSWR", this.txtColSWR.getBackground());
      this.config.putColor("SmithPanel.colInductance", this.txtColAdmittance.getBackground());
      this.config.putColor("SmithPanel.colImpedance", this.txtColImpedance.getBackground());
      this.config.put("SmithPanel.circlesAdmittanceImaginary", this.txtAdmittanceImaginaryCircles.getText());
      this.config.put("SmithPanel.circlesAdmittanceReal", this.txtAdmittanceRealCircles.getText());
      this.config.put("SmithPanel.circlesImpedanceImaginary", this.txtImpedanceImaginaryCircles.getText());
      this.config.put("SmithPanel.circlesImpedanceReal", this.txtImpedanceRealCircles.getText());
      this.config.put("SmithPanel.circlesSWR", this.txtSWRCircles.getText());
      this.doDialogCancel();
      TraceHelper.exit(this, "doOK");
   }

   public void mouseClicked(MouseEvent e) {
      JTextField tf = (JTextField)e.getComponent();
      tf.setBackground(JColorChooser.showDialog(this, VNAMessages.getString("ColorDialog.select"), tf.getBackground()));
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void valueChanged(Complex oldValue, Complex newValue) {
      TraceHelper.entry(this, "valueChanged");
      TraceHelper.exit(this, "valueChanged");
   }
}
