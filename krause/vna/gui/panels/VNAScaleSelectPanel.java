package krause.vna.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.VNATemperatureButton;
import krause.vna.gui.calibrate.VNACalibrationDataDetailsDialog;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.portextension.VNAPortExtensionParameterDialog;
import krause.vna.gui.reference.VNAReferenceDataLoadDialog;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.smith.VNASyncedSmithDiagramDialog;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNAScaleSelectPanel extends JPanel implements ActionListener, VNAApplicationStateObserver {
   private JButton buttonPortExtension;
   private JButton buttonPortExtensionActive;
   private JButton buttonPortExtensionInactive;
   private JButton buttonReference;
   private JButton buttonReferenceLoaded;
   private JButton buttonReferenceNotLoaded;
   private JCheckBox cbAutoScale;
   private VNAScaleSelectComboBox cbLeftScale;
   private VNAScaleSelectComboBox cbRightScale;
   private JToggleButton cbSmith;
   private final VNAConfig config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();
   private JLabel labelMemory;
   private JLabel labelPowerstatus;
   private VNATemperatureButton labelTemperature;
   private transient VNAMainFrame mainFrame;
   private VNASyncedSmithDiagramDialog smithDialog;

   public VNAScaleSelectPanel(VNAMainFrame pMainFrame, VNAMeasurementScale leftScale, VNAMeasurementScale rightScale) {
      TraceHelper.entry(this, "VNAScaleSelectPanel");
      this.mainFrame = pMainFrame;
      this.setLayout(new BorderLayout());
      this.cbLeftScale = new VNAScaleSelectComboBox();
      this.cbLeftScale.setToolTipText(VNAMessages.getString("Panel.Scale.Left"));
      this.cbRightScale = new VNAScaleSelectComboBox();
      this.cbRightScale.setToolTipText(VNAMessages.getString("Panel.Scale.Right"));
      this.cbAutoScale = SwingUtil.createJCheckBox("Panel.Scale.AutoScale", this);
      this.cbAutoScale.setSelected(this.config.isAutoscaleEnabled());
      this.cbLeftScale.setSelectedItem(leftScale.getScale());
      this.cbRightScale.setSelectedItem(rightScale.getScale());
      this.cbLeftScale.addActionListener(this);
      this.cbRightScale.addActionListener(this);
      this.add(this.cbLeftScale, "West");
      JToolBar pnlX = new JToolBar();
      pnlX.setFloatable(false);
      pnlX.add(this.cbAutoScale);
      pnlX.addSeparator();
      this.cbSmith = SwingUtil.createToggleButton("Panel.Scale.Smith", this);
      pnlX.add(this.cbSmith);
      pnlX.addSeparator();
      this.buttonReference = SwingUtil.createToolbarButton("Button.Reference.NotLoaded", this);
      this.buttonReferenceLoaded = SwingUtil.createToolbarButton("Button.Reference.Loaded", this);
      this.buttonReferenceNotLoaded = SwingUtil.createToolbarButton("Button.Reference.NotLoaded", this);
      pnlX.add(this.buttonReference);
      pnlX.addSeparator();
      this.buttonPortExtension = SwingUtil.createToolbarButton("Button.PortExtension.Inactive", this);
      this.buttonPortExtensionActive = SwingUtil.createToolbarButton("Button.PortExtension.Active", this);
      this.buttonPortExtensionInactive = SwingUtil.createToolbarButton("Button.PortExtension.Inactive", this);
      pnlX.add(this.buttonPortExtension);
      pnlX.addSeparator();
      this.labelPowerstatus = new JLabel();
      this.labelPowerstatus.setVisible(false);
      pnlX.add(this.labelPowerstatus);
      this.labelTemperature = new VNATemperatureButton(this.mainFrame, "Panel.Scale.Templabel", (ActionListener)null);
      this.labelTemperature.setVisible(false);
      pnlX.add(this.labelTemperature);
      pnlX.addSeparator();
      this.labelMemory = new JLabel();
      this.labelMemory.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (VNAScaleSelectPanel.this.datapool.getRawData() != null) {
               VNASampleBlock blk = new VNASampleBlock();
               blk.setSamples(VNAScaleSelectPanel.this.datapool.getRawData().getSamples());
               new VNACalibrationDataDetailsDialog(VNAScaleSelectPanel.this.mainFrame.getJFrame(), blk, "Panel.Scale.RawData");
            }

         }
      });
      pnlX.add(this.labelMemory);
      this.add(pnlX, "Center");
      this.add(this.cbRightScale, "East");
      this.setupColors();
      this.setPortExtensionState();
      TraceHelper.exit(this, "VNAScaleSelectPanel");
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      String methodName = "actionPerformed";
      TraceHelper.entry(this, "actionPerformed", cmd);
      VNADiagramPanel dp = this.mainFrame.getDiagramPanel();
      VNAGenericScale st;
      if (e.getSource() == this.cbLeftScale) {
         st = (VNAGenericScale)this.cbLeftScale.getSelectedItem();
         dp.getScaleLeft().setScale(st);
         dp.repaint();
      } else if (e.getSource() == this.cbRightScale) {
         st = (VNAGenericScale)this.cbRightScale.getSelectedItem();
         dp.getScaleRight().setScale(st);
         dp.repaint();
      } else if (e.getSource() == this.cbSmith) {
         this.doHandleSmithDiagram();
      } else if (e.getSource() == this.buttonReference) {
         this.doHandleReference();
      } else if (e.getSource() == this.buttonPortExtension) {
         this.doHandlePortExtension();
      } else if (e.getSource() == this.cbAutoScale) {
         this.doHandleAutoScale();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      this.cbSmith.setEnabled(newState != VNAApplicationState.INNERSTATE.RUNNING && this.datapool.getScanMode() != null && this.datapool.getScanMode().isReflectionMode());
      this.buttonReference.setEnabled(newState != VNAApplicationState.INNERSTATE.RUNNING);
      this.buttonPortExtension.setEnabled(newState != VNAApplicationState.INNERSTATE.RUNNING && this.datapool.getScanMode() != null && this.datapool.getScanMode().isReflectionMode());
      VNASampleBlock rawData = this.datapool.getRawData();
      if (rawData != null) {
         Double volt = rawData.getDeviceSupply();
         if (volt != null) {
            this.labelPowerstatus.setVisible(true);
            this.labelPowerstatus.setText(VNAFormatFactory.getTemperatureFormat().format(volt) + " V  ");
         } else {
            this.labelPowerstatus.setVisible(false);
         }

         Double temp = rawData.getDeviceTemperature();
         this.labelTemperature.setVisible(temp != null);
         this.labelTemperature.setTemperature(temp);
      }

   }

   public void disableAutoScale() {
      this.cbAutoScale.setSelected(false);
      this.config.setAutoscaleEnabled(false);
      VNADiagramPanel diagPanel = this.mainFrame.getDiagramPanel();
      diagPanel.repaint();
   }

   private void doHandleAutoScale() {
      TraceHelper.entry(this, "doHandleAutoScale");
      VNADiagramPanel diagPanel = this.mainFrame.getDiagramPanel();
      if (!this.cbAutoScale.isSelected()) {
         Iterator var3 = VNAScaleSymbols.MAP_SCALE_TYPES.values().iterator();

         while(var3.hasNext()) {
            VNAGenericScale scale = (VNAGenericScale)var3.next();
            scale.resetDefault();
         }
      } else {
         diagPanel.rescaleScalesToData();
      }

      this.config.setAutoscaleEnabled(this.cbAutoScale.isSelected());
      diagPanel.repaint();
      TraceHelper.exit(this, "doHandleAutoScale");
   }

   public void doHandlePortExtension() {
      TraceHelper.entry(this, "doHandlePortExtension");
      if (this.buttonPortExtension.isEnabled()) {
         new VNAPortExtensionParameterDialog(this.mainFrame);
         this.mainFrame.getDiagramPanel().clearScanData();
      }

      this.setPortExtensionState();
      this.mainFrame.getDiagramPanel().getImagePanel().updateUI();
      TraceHelper.exit(this, "doHandlePortExtension");
   }

   public void doHandleReference() {
      TraceHelper.entry(this, "doHandleReference");
      if (this.buttonReference.isEnabled()) {
         new VNAReferenceDataLoadDialog(this.mainFrame.getJFrame());
         if (this.datapool.getReferenceData() != null) {
            this.buttonReference.setIcon(this.buttonReferenceLoaded.getIcon());
            this.mainFrame.getStatusBarStatus().setText(VNAMessages.getString("Panel.Scale.Reference.ReferenceLoaded"));
         } else {
            this.buttonReference.setIcon(this.buttonReferenceNotLoaded.getIcon());
            this.mainFrame.getStatusBarStatus().setText(VNAMessages.getString("Panel.Scale.Reference.ReferenceCleared"));
         }

         this.mainFrame.getDiagramPanel().getImagePanel().updateUI();
      }

      TraceHelper.exit(this, "doHandleReference");
   }

   public void doHandleSmithDiagram() {
      TraceHelper.entry(this, "doHandleSmithDiagram");
      if (this.smithDialog == null) {
         this.smithDialog = new VNASyncedSmithDiagramDialog(this.mainFrame);
         if (this.datapool.getCalibratedData() != null) {
            this.smithDialog.consumeCalibratedData(this.datapool.getCalibratedData());
         }

         this.smithDialog.setVisible(true);
         this.cbSmith.setSelected(true);
      } else {
         this.smithDialog.setVisible(false);
         this.smithDialog.dispose();
         this.smithDialog = null;
         this.cbSmith.setSelected(false);
      }

      TraceHelper.exit(this, "doHandleSmithDiagram");
   }

   public void enableAutoScale() {
      if (!this.cbAutoScale.isSelected()) {
         this.cbAutoScale.setSelected(true);
      }

   }

   public VNAScaleSelectComboBox getCbLeftScale() {
      return this.cbLeftScale;
   }

   public VNAScaleSelectComboBox getCbRightScale() {
      return this.cbRightScale;
   }

   public JLabel getLabelDebug() {
      return this.labelMemory;
   }

   public VNASyncedSmithDiagramDialog getSmithDialog() {
      return this.smithDialog;
   }

   boolean setPortExtensionState() {
      if (this.config.isPortExtensionEnabled()) {
         this.buttonPortExtension.setIcon(this.buttonPortExtensionActive.getIcon());
         this.buttonPortExtension.setToolTipText(this.buttonPortExtensionActive.getToolTipText());
         return true;
      } else {
         this.buttonPortExtension.setIcon(this.buttonPortExtensionInactive.getIcon());
         this.buttonPortExtension.setToolTipText(this.buttonPortExtensionInactive.getToolTipText());
         return false;
      }
   }

   public void setSmithDialog(VNASyncedSmithDiagramDialog smithDialog) {
      this.smithDialog = smithDialog;
   }

   public void setupColors() {
      String methodName = "setupColors";
      TraceHelper.entry(this, "setupColors");
      this.cbRightScale.setBackground(new Color(~this.config.getColorScaleRight().getRGB()));
      this.cbRightScale.setForeground(this.config.getColorScaleRight());
      this.cbLeftScale.setBackground(new Color(~this.config.getColorScaleLeft().getRGB()));
      this.cbLeftScale.setForeground(this.config.getColorScaleLeft());
      TraceHelper.exit(this, "setupColors");
   }
}
