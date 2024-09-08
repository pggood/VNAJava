package krause.vna.gui.preset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.VNAScaleCheckBox;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.jfree.ui.ExtensionFileFilter;

public class VNAPresetSaveDialog extends KrauseDialog {
   private VNAMainFrame mainFrame = null;
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private JCheckBox cbFreq;
   private JCheckBox cbScanMode;
   private ArrayList<VNAScaleCheckBox> lstScales = new ArrayList();
   private JCheckBox cbMarkers;
   public static final String PREFS_EXTENSION = ".preset";
   public static final String PREFS_DESCRIPTION = "vna/J preset files(*.preset)";

   public VNAPresetSaveDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      TraceHelper.entry(this, "VNAPresetSaveDialog");
      this.mainFrame = pMainFrame;
      this.setConfigurationPrefix("VNAPresetSaveDialog");
      this.setProperties(this.config);
      this.setMinimumSize(new Dimension(350, 300));
      this.setPreferredSize(this.getMinimumSize());
      this.setTitle(VNAMessages.getString("VNAPresetSaveDialog.title"));
      this.getContentPane().setLayout(new BorderLayout());
      JPanel pnlButtons = new JPanel();
      pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", 4, 2, (Font)null, new Color(0, 0, 0)));
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.add(new HelpButton(this, "VNAPresetSaveDialog"));
      JButton btn = new JButton(VNAMessages.getString("Button.Close"));
      btn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAPresetSaveDialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(btn);
      pnlButtons.setLayout(new FlowLayout(2, 5, 5));
      btn = SwingUtil.createJButton("Button.Save", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAPresetSaveDialog.this.doSAVE();
         }
      });
      pnlButtons.add(btn);
      this.getRootPane().setDefaultButton(btn);
      JPanel dataPanel = new JPanel();
      dataPanel.setLayout(new MigLayout("", "[grow][][][][]", "[]"));
      this.getContentPane().add(this.createDataPanel(), "Center");
      this.doDialogInit();
      TraceHelper.entry(this, "VNAPresetSaveDialog");
   }

   private JPanel createDataPanel() {
      TraceHelper.entry(this, "createDataPanel");
      JPanel rc = new JPanel();
      rc.setLayout(new MigLayout("", "[grow]", "[][][]"));
      rc.add(new JLabel(VNAMessages.getString("VNAPresetSaveDialog.help")), "grow,wrap");
      JPanel pnlScales = new JPanel(new GridLayout(4, 4));
      pnlScales.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNAPresetSaveDialog.scales"), 4, 2, (Font)null, (Color)null));
      Iterator var4 = VNAScaleSymbols.MAP_SCALE_TYPES.values().iterator();

      while(var4.hasNext()) {
         VNAGenericScale currScale = (VNAGenericScale)var4.next();
         if (currScale.supportsCustomScaling()) {
            VNAScaleCheckBox cb = new VNAScaleCheckBox(currScale.getName(), currScale);
            cb.setSelected(true);
            this.lstScales.add(cb);
            pnlScales.add(cb);
         }
      }

      rc.add(pnlScales, "wrap,grow");
      this.cbFreq = new JCheckBox(VNAMessages.getString("VNAPresetSaveDialog.frequency"));
      this.cbFreq.setSelected(true);
      rc.add(this.cbFreq, "wrap,grow");
      this.cbScanMode = new JCheckBox(VNAMessages.getString("VNAPresetSaveDialog.scanmode"));
      this.cbScanMode.setSelected(true);
      rc.add(this.cbScanMode, "wrap,grow");
      this.cbMarkers = new JCheckBox(VNAMessages.getString("VNAPresetSaveDialog.markers"));
      this.cbMarkers.setSelected(true);
      rc.add(this.cbMarkers, "wrap,grow");
      TraceHelper.exit(this, "createDataPanel");
      return rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void doSAVE() {
      TraceHelper.entry(this, "doSAVE");
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new ExtensionFileFilter("vna/J preset files(*.preset)", ".preset"));
      fc.setCurrentDirectory(new File(this.config.getPresetsDirectory()));
      int returnVal = fc.showSaveDialog(this.mainFrame.getJFrame());
      if (returnVal == 0) {
         try {
            File file = fc.getSelectedFile();
            this.config.setPresetsDirectory(file.getParent());
            if (!file.getName().endsWith(".preset")) {
               file = new File(file.getAbsolutePath() + ".preset");
            }

            if (file.exists()) {
               String msg = MessageFormat.format(VNAMessages.getString("VNAPresetSaveDialog.save.1"), file.getName());
               int response = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), msg, VNAMessages.getString("VNAPresetSaveDialog.title"), 0, 3, (Icon)null, (Object[])null, (Object)null);
               if (response == 2) {
                  return;
               }
            }

            TypedProperties props = this.createProperties();
            PropertiesHelper.saveXMLProperties(props, file.getAbsolutePath());
            this.doDialogCancel();
         } catch (Exception var6) {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), var6.getMessage(), VNAMessages.getString("Message.Export.2"), 0);
            ErrorLogHelper.exception(this, "doExport", var6);
         }
      }

      TraceHelper.exit(this, "doSAVE");
   }

   private TypedProperties createProperties() {
      TraceHelper.entry(this, "createProperties");
      TypedProperties rc = new TypedProperties();
      Iterator var3 = this.lstScales.iterator();

      while(var3.hasNext()) {
         VNAScaleCheckBox cb = (VNAScaleCheckBox)var3.next();
         if (cb.getScale().supportsCustomScaling() && cb.isSelected()) {
            cb.getScale().saveToProperties(rc);
         }
      }

      if (this.cbFreq.isSelected()) {
         this.datapool.getFrequencyRange().saveToProperties(rc);
      }

      if (this.cbScanMode.isSelected()) {
         this.datapool.getScanMode().saveToProperties(rc);
      }

      if (this.cbMarkers.isSelected()) {
         VNAMarkerPanel mp = this.mainFrame.getMarkerPanel();
         VNAMarker m = mp.getMarker(0);
         if (m.isVisible()) {
            rc.putLong("Marker1.frq", m.getFrequency());
         }

         m = mp.getMarker(1);
         if (m.isVisible()) {
            rc.putLong("Marker2.frq", m.getFrequency());
         }

         m = mp.getMarker(2);
         if (m.isVisible()) {
            rc.putLong("Marker3.frq", m.getFrequency());
         }

         m = mp.getMarker(3);
         if (m.isVisible()) {
            rc.putLong("Marker4.frq", m.getFrequency());
         }
      }

      TraceHelper.exit(this, "createProperties");
      return rc;
   }
}
