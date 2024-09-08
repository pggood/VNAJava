package krause.vna.gui.reference;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.data.reference.VNAReferenceDataComparator;
import krause.vna.gui.raw.VNARawHandler;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAReferenceDataLoadDialog extends KrauseDialog implements IVNAReferenceDataSelectionListener {
   private static VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNAReferenceDataTable lstFiles;
   private File currentDirectoy;
   private JButton btCancel;
   private JButton btOK;
   private JLabel lblDirectory;
   private JTextField txtDirectory;
   private JButton btnSearch;
   private JButton btnRefresh;
   private VNAReferenceDataBlock selectedBlock;
   private JButton btClear;

   public VNAReferenceDataLoadDialog(Frame pOwner) {
      super((Window)pOwner, true);
      this.currentDirectoy = new File(config.getReferenceDirectory());
      this.selectedBlock = null;
      TraceHelper.entry(this, "VNAReferenceDataLoadDialog");
      this.setConfigurationPrefix("VNAReferenceDataLoadDialog");
      this.setProperties(config);
      this.setTitle(VNAMessages.getString("VNAReferenceDataLoadDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setBounds(100, 100, 800, 333);
      this.setLayout(new MigLayout("", "[][grow,fill][][]", "[][grow,fill][]"));
      this.lblDirectory = new JLabel(VNAMessages.getString("VNAReferenceDataLoadDialog.lblDirectory.text"));
      this.add(this.lblDirectory, "");
      this.txtDirectory = new JTextField();
      this.txtDirectory.setEditable(false);
      this.txtDirectory.setColumns(30);
      this.add(this.txtDirectory, "");
      this.btnRefresh = new JButton(VNAMessages.getString("VNAReferenceDataLoadDialog.btnRefresh.text"));
      this.btnRefresh.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAReferenceDataLoadDialog.this.loadDirectory();
         }
      });
      this.add(this.btnRefresh, "wmin 100px");
      this.btnSearch = new JButton(VNAMessages.getString("VNAReferenceDataLoadDialog.btnSearch.text"));
      this.btnSearch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAReferenceDataLoadDialog.this.doChangeDirectory();
         }
      });
      this.add(this.btnSearch, "wmin 100px, wrap");
      this.lstFiles = new VNAReferenceDataTable(this);
      JScrollPane scrollPane = new JScrollPane(this.lstFiles);
      scrollPane.setViewportBorder((Border)null);
      this.add(scrollPane, "span 4,grow,wrap");
      this.btClear = SwingUtil.createJButton("Button.Clear", (ActionListener)null);
      this.btClear.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAReferenceDataLoadDialog.this.doClearReference();
         }
      });
      this.add(this.btClear, "wmin 100px");
      this.add(new JLabel(), "");
      this.btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAReferenceDataLoadDialog.this.doDialogCancel();
         }
      });
      this.add(this.btCancel, "wmin 100px");
      this.btOK = SwingUtil.createJButton("Button.Load", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAReferenceDataLoadDialog.this.doOK();
         }
      });
      this.add(this.btOK, "wmin 100px, right");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNAReferenceDataLoadDialog");
   }

   protected void doClearReference() {
      TraceHelper.entry(this, "doClearReference");
      this.selectedBlock = null;
      this.doOK();
      TraceHelper.exit(this, "doClearReference");
   }

   protected void doChangeDirectory() {
      TraceHelper.entry(this, "doChangeDirectory");
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle(VNAMessages.getString("VNAReferenceDataLoadDialog.directoryChooser"));
      fc.setFileSelectionMode(1);
      fc.setCurrentDirectory(this.currentDirectoy);
      int returnVal = fc.showOpenDialog(this.getOwner());
      if (returnVal == 0) {
         this.currentDirectoy = fc.getSelectedFile();
         this.loadDirectory();
      }

      TraceHelper.exit(this, "doChangeDirectory");
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      this.datapool.setReferenceData(this.selectedBlock);
      config.setReferenceDirectory(this.currentDirectoy.getAbsolutePath());
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
      this.addEscapeKey();
      this.loadDirectory();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void loadDirectory() {
      TraceHelper.entry(this, "loadDirectory");
      this.txtDirectory.setText(this.currentDirectoy.getAbsolutePath());
      this.lstFiles.getModel().clear();
      FilenameFilter fnf = new FilenameFilter() {
         public boolean accept(File dir, String name) {
            return name.toUpperCase().endsWith("XML");
         }
      };
      File[] files = this.currentDirectoy.listFiles(fnf);

      for(int i = 0; i < files.length; ++i) {
         File currFile = files[i];

         try {
            VNACalibratedSampleBlock calSampleBlock = null;
            calSampleBlock = (new VNARawHandler(this)).readFile(currFile);
            if (calSampleBlock != null) {
               VNAReferenceDataBlock blk = new VNAReferenceDataBlock(calSampleBlock);
               blk.setFile(currFile);
               this.lstFiles.addReferenceData(blk);
            }
         } catch (ProcessingException var7) {
            ErrorLogHelper.exception(this, "loadDirectory", var7);
         }
      }

      this.btOK.setEnabled(false);
      Collections.sort(this.lstFiles.getModel().getData(), new VNAReferenceDataComparator());
      this.lstFiles.updateUI();
      TraceHelper.exit(this, "loadDirectory");
   }

   public void valueChanged(VNAReferenceDataBlock blk, boolean doubleClick) {
      TraceHelper.entry(this, "valueChanged", "dbl=" + doubleClick);
      if (blk != null) {
         this.selectedBlock = blk;
         this.btOK.setEnabled(true);
         if (doubleClick) {
            this.doOK();
         }
      } else {
         this.btOK.setEnabled(false);
      }

      TraceHelper.exit(this, "valueChanged");
   }

   public VNAReferenceDataBlock getSelectedBlock() {
      return this.selectedBlock;
   }
}
