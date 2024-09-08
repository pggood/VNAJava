package krause.vna.gui.calibrate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.calibrate.file.VNACalibrationFileTable;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationLoadDialog extends KrauseDialog implements IVNACalibrationSelectionListener {
   private final VNAConfig config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();
   private final VNADeviceInfoBlock dib;
   private VNACalibrationFileTable lstFiles;
   private JButton btCancel;
   private JButton btOK;
   private JCheckBox cbShowAll;
   private JPanel mainPanel;
   private VNACalibrationBlock selectedCalBlock;

   public VNACalibrationLoadDialog(Window pOwner) {
      super(pOwner, true);
      this.dib = this.datapool.getDriver().getDeviceInfoBlock();
      this.selectedCalBlock = null;
      this.setResizable(true);
      String methodName = "VNACalibrationLoadDialog";
      TraceHelper.entry(this, "VNACalibrationLoadDialog");
      this.setConfigurationPrefix("VNACalibrationLoadDialog");
      this.setProperties(this.config);
      String tit = VNAMessages.getString("VNACalibrationLoadDialog.title");
      this.setTitle(MessageFormat.format(tit, this.datapool.getScanMode().toString()));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(800, 200));
      this.mainPanel = new JPanel(new MigLayout("", "[grow,fill][][][]", "[grow,fill][][]"));
      this.mainPanel.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.getContentPane().add(this.mainPanel);
      this.lstFiles = new VNACalibrationFileTable(this);
      JScrollPane scrollPane = new JScrollPane(this.lstFiles);
      scrollPane.setAlignmentX(0.0F);
      scrollPane.setViewportBorder((Border)null);
      this.mainPanel.add(scrollPane, "grow, span 4,wrap");
      this.cbShowAll = new JCheckBox(VNAMessages.getString("VNACalibrationLoadDialog.cbShowAll"));
      this.mainPanel.add(this.cbShowAll);
      this.mainPanel.add(new HelpButton(this, "VNACalibrationLoadDialog"), "wmin 100px");
      this.btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
      this.mainPanel.add(this.btCancel, "wmin 100px");
      this.btOK = new JButton(VNAMessages.getString("Button.OK"));
      this.mainPanel.add(this.btOK, "wmin 100px");
      this.btOK.addActionListener((e) -> {
         this.doOK();
      });
      this.btCancel.addActionListener((e) -> {
         this.doDialogCancel();
      });
      this.cbShowAll.addActionListener((e) -> {
         this.loadDirectory(this.cbShowAll.isSelected());
      });
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalibrationLoadDialog");
   }

   public VNACalibrationBlock getSelectedCalibrationBlock() {
      return this.selectedCalBlock;
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      this.setVisible(false);
      TraceHelper.exit(this, "doOK");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.addEscapeKey();
      this.loadDirectory(false);
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void loadDirectory(boolean showAllFiles) {
      String methodName = "loadDirectory";
      TraceHelper.entry(this, "loadDirectory");
      this.lstFiles.getModel().clear();
      File file = new File(this.config.getVNACalibrationDirectory());
      FilenameFilter fnf = new FilenameFilter() {
         public boolean accept(File dir, String name) {
            return name.endsWith(".cal");
         }
      };
      File[] files = file.listFiles(fnf);

      for(int i = 0; i < files.length; ++i) {
         File currFile = files[i];

         try {
            VNACalibrationBlock blk = VNACalibrationBlockHelper.loadHeader(currFile);
            if (showAllFiles) {
               blk.setFile(currFile);
               this.lstFiles.addCalibrationBlock(blk);
            } else {
               boolean matches = blk.blockMatches(this.dib, this.datapool.getScanMode());
               TraceHelper.text(this, "loadDirectory", currFile.getName() + (matches ? " matches" : " not matching"));
               if (matches) {
                  blk.setFile(currFile);
                  this.lstFiles.addCalibrationBlock(blk);
               }
            }
         } catch (ProcessingException var10) {
            ErrorLogHelper.exception(this, "loadDirectory", var10);
         }
      }

      this.btOK.setEnabled(false);
      Collections.sort(this.lstFiles.getModel().getData(), new VNACalibrationBlockComparator());
      this.lstFiles.updateUI();
      TraceHelper.exit(this, "loadDirectory");
   }

   public void valueChanged(VNACalibrationBlock blk, boolean doubleClick) {
      String methodName = "valueChanged";
      TraceHelper.entry(this, "valueChanged", "dbal=%b", doubleClick);
      boolean matches = blk.blockMatches(this.dib, this.datapool.getScanMode());
      if (matches) {
         try {
            this.selectedCalBlock = VNACalibrationBlockHelper.load(blk.getFile(), this.datapool.getDriver(), this.datapool.getCalibrationKit());
            if (this.selectedCalBlock != null) {
               this.btOK.setEnabled(true);
               if (doubleClick) {
                  this.doOK();
               }
            }
         } catch (ProcessingException var6) {
            ErrorLogHelper.exception(this, "valueChanged", var6);
            OptionDialogHelper.showExceptionDialog((Window)null, "Serializer.Error.1", "Serializer.Error.2", var6);
         }
      } else {
         Toolkit.getDefaultToolkit().beep();
         this.btOK.setEnabled(false);
      }

      TraceHelper.exit(this, "valueChanged");
   }
}
