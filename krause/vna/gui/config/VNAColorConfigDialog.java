package krause.vna.gui.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.laf.VNALookAndFeelEntry;
import krause.vna.gui.laf.VNALookAndFeelHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAColorConfigDialog extends KrauseDialog {
   private VNAConfig config = VNAConfig.getSingleton();
   private JComboBox<VNALookAndFeelEntry> cbTheme;

   public VNAColorConfigDialog(VNAMainFrame mainFrame, Frame aFrame) {
      super((Window)aFrame, true);
      this.setConfigurationPrefix("ColorDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("ColorDialog.Title"));
      this.setPreferredSize(new Dimension(410, 370));
      this.getContentPane().setLayout(new BorderLayout(5, 5));
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "South");
      JButton btnDefault = SwingUtil.createJButton("Button.Default", (e) -> {
         this.doResetDefaults();
      });
      pnlButtons.add(btnDefault);
      JButton btnOK = SwingUtil.createJButton("Button.Close", (e) -> {
         this.doDialogCancel();
      });
      pnlButtons.add(btnOK);
      JPanel pnlCenter = new JPanel(new MigLayout("", "", ""));
      this.getContentPane().add(pnlCenter, "Center");
      JPanel pnlScales = new JPanel();
      pnlScales.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.scales"), 4, 2, (Font)null, (Color)null));
      pnlCenter.add(pnlScales, "wrap,grow");
      JButton btnLeftScale = SwingUtil.createJButton("ColorDialog.Title.LeftScale", (ActionListener)null);
      btnLeftScale.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), VNAMessages.getString("ColorDialog.select"), this.config.getColorScaleLeft());
         if (newColor != null) {
            this.config.setColorScaleLeft(newColor);
         }

      });
      pnlScales.add(btnLeftScale);
      JButton btnRightScale = SwingUtil.createJButton("ColorDialog.Title.RightScale", (ActionListener)null);
      btnRightScale.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), VNAMessages.getString("ColorDialog.select"), this.config.getColorScaleRight());
         if (newColor != null) {
            this.config.setColorScaleRight(newColor);
         }

      });
      pnlScales.add(btnRightScale);
      JPanel pnlMarkers = new JPanel();
      pnlMarkers.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.markers"), 4, 2, (Font)null, (Color)null));
      pnlCenter.add(pnlMarkers, "grow,wrap");
      JButton btnMarker = SwingUtil.createJButton("Marker.0", (ActionListener)null);
      btnMarker.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), "Color", this.config.getColorMarker(0));
         if (newColor != null) {
            this.config.setColorMarker(0, newColor);
         }

      });
      pnlMarkers.add(btnMarker);
      btnMarker = SwingUtil.createJButton("Marker.1", (ActionListener)null);
      btnMarker.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), "Color", this.config.getColorMarker(1));
         if (newColor != null) {
            this.config.setColorMarker(1, newColor);
         }

      });
      pnlMarkers.add(btnMarker);
      btnMarker = SwingUtil.createJButton("Marker.2", (ActionListener)null);
      btnMarker.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), "Color", this.config.getColorMarker(2));
         if (newColor != null) {
            this.config.setColorMarker(2, newColor);
         }

      });
      pnlMarkers.add(btnMarker);
      btnMarker = SwingUtil.createJButton("Marker.3", (ActionListener)null);
      btnMarker.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), "Color", this.config.getColorMarker(3));
         if (newColor != null) {
            this.config.setColorMarker(3, newColor);
         }

      });
      pnlMarkers.add(btnMarker);
      JPanel pnlDiag = new JPanel();
      pnlDiag.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.diagram"), 4, 2, (Font)null, (Color)null));
      pnlCenter.add(pnlDiag, "wrap");
      JButton btnDiagramBackground = SwingUtil.createJButton("ColorDialog.Title.DiagramBackground", (ActionListener)null);
      btnDiagramBackground.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), "Color", this.config.getColorDiagram());
         if (newColor != null) {
            this.config.setColorDiagram(newColor);
         }

      });
      JButton btnDiagramLines = SwingUtil.createJButton("ColorDialog.Title.DiagramLines", (ActionListener)null);
      btnDiagramLines.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), "Color", this.config.getColorDiagramLines());
         if (newColor != null) {
            this.config.setColorDiagramLines(newColor);
         }

      });
      JButton btnReferenceColor = SwingUtil.createJButton("ColorDialog.Title.Reference", (ActionListener)null);
      btnReferenceColor.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), VNAMessages.getString("ColorDialog.select"), this.config.getColorReference());
         if (newColor != null) {
            this.config.setColorReference(newColor);
         }

      });
      JButton btnBandmapColor = SwingUtil.createJButton("ColorDialog.Title.Bandmap", (ActionListener)null);
      btnBandmapColor.addActionListener((e) -> {
         Color newColor = JColorChooser.showDialog(this.getOwner(), VNAMessages.getString("ColorDialog.select"), this.config.getColorBandmap());
         if (newColor != null) {
            this.config.setColorBandmap(newColor);
         }

      });
      pnlDiag.add(btnReferenceColor);
      pnlDiag.add(btnDiagramLines);
      pnlDiag.add(btnDiagramBackground);
      pnlDiag.add(btnBandmapColor);
      JPanel pnlScheme = new JPanel();
      pnlScheme.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.scheme"), 4, 2, (Font)null, (Color)null));
      pnlCenter.add(pnlScheme, "grow,wrap");
      this.cbTheme = new JComboBox((new VNALookAndFeelHelper()).getThemeList());
      pnlScheme.add(this.cbTheme, "grow,wrap");
      this.cbTheme.addItemListener((e) -> {
         int idx = this.cbTheme.getSelectedIndex();
         if (idx != -1) {
            this.config.setThemeID(idx);
         }

      });
      this.doDialogInit();
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.cbTheme.setSelectedIndex(this.config.getThemeID());
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void doResetDefaults() {
      TraceHelper.entry(this, "doResetDefaults");
      this.config.setColorDiagram(Color.BLACK);
      this.config.setColorDiagramLines(Color.LIGHT_GRAY);
      this.config.setColorMarker(0, Color.YELLOW);
      this.config.setColorMarker(1, Color.YELLOW);
      this.config.setColorMarker(2, Color.YELLOW);
      this.config.setColorMarker(3, Color.YELLOW);
      this.config.setColorScaleLeft(Color.GREEN);
      this.config.setColorScaleRight(Color.CYAN);
      this.config.setColorBandmap(Color.DARK_GRAY);
      this.cbTheme.setSelectedIndex(0);
      TraceHelper.exit(this, "doResetDefaults");
   }
}
