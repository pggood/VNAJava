package krause.vna.gui.config;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.comparators.VNAPropertyComparator;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.tables.VNAProperty;
import krause.vna.gui.util.tables.VNAPropertyTableModel;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAConfigEditDialog extends KrauseDialog implements ActionListener {
   private final VNAPropertyTableModel model = new VNAPropertyTableModel();
   private final VNAConfig config = VNAConfig.getSingleton();
   private JCheckBox cbAskOnExit;
   private JCheckBox cbEnableTrace;
   private JTable table;
   private JCheckBox cbAutoAfterSelect;
   private JCheckBox cbAutoAfterZoom;
   private AbstractButton cbMarkerLineMode;
   private JCheckBox cbShowBandmap;
   private JCheckBox cbExportRawData;
   private JCheckBox cbTunGenOffAfterScan;
   private JCheckBox cbDisableResize;

   private void copyConfig2Model() {
      TraceHelper.entry(this, "copyConfig2Model");
      this.model.getData().clear();
      Iterator it = this.config.keySet().iterator();

      while(it.hasNext()) {
         String key = (String)it.next();
         String value = this.config.getProperty(key);
         this.model.addElement(new VNAProperty(key, value));
      }

      Collections.sort(this.model.getData(), new VNAPropertyComparator());
      TraceHelper.exit(this, "copyConfig2Model");
   }

   private void copyModel2Config() {
      TraceHelper.entry(this, "copyModel2Config");
      List<VNAProperty> data = this.model.getData();

      for(int i = 0; i < data.size(); ++i) {
         VNAProperty p = (VNAProperty)data.get(i);
         this.config.put(p.getKey(), p.getValue());
      }

      Iterator it = this.config.keySet().iterator();

      while(it.hasNext()) {
         String key = (String)it.next();
         String value = this.config.getProperty(key);
         this.model.addElement(new VNAProperty(key, value));
      }

      TraceHelper.exit(this, "copyModel2Config");
   }

   public VNAConfigEditDialog(VNAMainFrame mainFrame) {
      super((Window)mainFrame.getJFrame(), true);
      TraceHelper.entry(this, "VNAConfigEditDialog");
      this.setConfigurationPrefix("VNAConfigEditDialog");
      this.setProperties(this.config);
      this.getContentPane().setLayout(new BorderLayout());
      this.setModal(true);
      this.setDefaultCloseOperation(0);
      this.setBounds(100, 100, 678, 472);
      this.setTitle(VNAMessages.getString("VNAConfigEditDialog.this.title"));
      JLabel label = new JLabel(VNAMessages.getString("Dlg.Settings.1"));
      label.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            VNAConfigEditDialog.this.table.setEnabled(true);
         }
      });
      this.getContentPane().add(label, "First");
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new MigLayout("", "[][][][]", "[][]"));
      this.getContentPane().add(centerPanel, "Center");
      this.table = new JTable(this.model);
      this.table.setPreferredScrollableViewportSize(new Dimension(600, 400));
      this.table.setEnabled(false);
      JScrollPane scrollPane = new JScrollPane(this.table);
      centerPanel.add(scrollPane, "span 4, wrap");
      this.cbAskOnExit = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbAskOnExit.text"));
      this.cbAskOnExit.addActionListener((e) -> {
         this.config.setAskOnExit(this.cbAskOnExit.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbAskOnExit, "");
      this.cbEnableTrace = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbEnableTrace.text"));
      this.cbEnableTrace.addActionListener((e) -> {
         this.config.putBoolean("Tracer.tracing", this.cbEnableTrace.isSelected());
         LogManager.getSingleton().setTracingEnabled(this.cbEnableTrace.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbEnableTrace, "");
      this.cbExportRawData = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbExportRawData.text"));
      this.cbExportRawData.addActionListener((e) -> {
         this.config.setExportRawData(this.cbExportRawData.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbExportRawData, "wrap");
      this.cbMarkerLineMode = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbMarkerLineMode"));
      this.cbMarkerLineMode.addActionListener((e) -> {
         this.config.setMarkerModeLine(this.cbMarkerLineMode.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbMarkerLineMode, "");
      this.cbTunGenOffAfterScan = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbTunGenOffAfterScan"));
      this.cbTunGenOffAfterScan.addActionListener((e) -> {
         this.config.setTurnOffGenAfterScan(this.cbTunGenOffAfterScan.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbTunGenOffAfterScan, "");
      this.cbShowBandmap = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbShowBandmap"));
      this.cbShowBandmap.addActionListener((e) -> {
         this.config.setShowBandmap(this.cbShowBandmap.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbShowBandmap, "wrap");
      this.cbAutoAfterSelect = SwingUtil.createJCheckBox("Panel.Data.cbAutoAfterSelect", this);
      this.cbAutoAfterSelect.addActionListener((e) -> {
         this.config.setScanAfterTableSelect(this.cbAutoAfterSelect.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbAutoAfterSelect, "");
      this.cbAutoAfterZoom = SwingUtil.createJCheckBox("Panel.Data.cbAutoAfterZoom", this);
      this.cbAutoAfterZoom.addActionListener((e) -> {
         this.config.setScanAfterZoom(this.cbAutoAfterZoom.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbAutoAfterZoom, "");
      this.cbDisableResize = SwingUtil.createJCheckBox("Panel.Data.cbDisableResize", this);
      this.cbDisableResize.addActionListener((e) -> {
         this.config.setResizeLocked(this.cbDisableResize.isSelected());
         this.copyConfig2Model();
      });
      centerPanel.add(this.cbDisableResize, "wrap");
      JPanel buttonPanel = new JPanel();
      buttonPanel.add(SwingUtil.createJButton("VNAConfigEditDialog.butShowConfigDir", this));
      buttonPanel.add(SwingUtil.createJButton("Button.Cancel", this));
      buttonPanel.add(SwingUtil.createJButton("Button.Save", this));
      this.getContentPane().add(buttonPanel, "Last");
      this.doDialogInit();
      TraceHelper.exit(this, "VNAConfigEditDialog");
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      TraceHelper.entry(this, "actionPerformed");
      if ("cmdSAVE".equals(cmd)) {
         this.doSave();
      } else if ("cmdCancel".equals(cmd)) {
         this.doDialogCancel();
      } else if ("cmdConfig".equals(cmd)) {
         File file = new File(this.config.getVNAConfigDirectory());

         try {
            Desktop.getDesktop().open(file);
         } catch (IOException var5) {
            ErrorLogHelper.exception(this, "actionPerformed", var5);
         }
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doSave() {
      TraceHelper.entry(this, "doSave");
      this.copyModel2Config();
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doSave");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      this.cbAskOnExit.setSelected(this.config.isAskOnExit());
      this.cbAutoAfterSelect.setSelected(this.config.isScanAfterTableSelect());
      this.cbAutoAfterZoom.setSelected(this.config.isScanAfterZoom());
      this.cbEnableTrace.setSelected(this.config.getBoolean("Tracer.tracing", false));
      this.cbMarkerLineMode.setSelected(this.config.isMarkerModeLine());
      this.cbShowBandmap.setSelected(this.config.isShowBandmap());
      this.cbExportRawData.setSelected(this.config.isExportRawData());
      this.cbDisableResize.setSelected(this.config.isResizeLocked());
      this.cbTunGenOffAfterScan.setSelected(this.config.isTurnOffGenAfterScan());
      this.copyConfig2Model();
      this.addEscapeKey();
      this.doDialogShow();
   }
}
