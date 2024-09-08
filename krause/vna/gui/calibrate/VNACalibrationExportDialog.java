package krause.vna.gui.calibrate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class VNACalibrationExportDialog extends KrauseDialog implements IVNACalibrationSelectionListener {
   private static VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNADeviceInfoBlock dib;
   private VNACalibrationFileTable lstFiles;
   private JButton btCancel;
   private JButton btOK;
   private VNACalibrationBlock selectedCalBlock;
   private JPanel pnlButtons;
   private JCheckBox cbShowAll;
   private JPanel pnlFiles;

   public VNACalibrationExportDialog(Window pOwner) {
      super(pOwner, true);
      this.dib = this.datapool.getDriver().getDeviceInfoBlock();
      this.selectedCalBlock = null;
      this.setResizable(false);
      TraceHelper.entry(this, "VNACalibrationExportDialog");
      String tit = VNAMessages.getString("VNACalibrationExportDialog.title");
      this.setTitle(MessageFormat.format(tit, this.datapool.getScanMode().toString()));
      this.setDefaultCloseOperation(0);
      this.setBounds(100, 100, 800, 333);
      this.pnlButtons = new JPanel();
      this.pnlButtons.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.getContentPane().add(this.pnlButtons, "South");
      this.cbShowAll = new JCheckBox(VNAMessages.getString("VNACalibrationExportDialog.cbShowAll"));
      this.pnlButtons.add(this.cbShowAll);
      this.btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
      this.pnlButtons.add(this.btCancel);
      this.pnlButtons.add(new HelpButton(this, "VNACalibrationExportDialog"));
      this.btOK = new JButton(VNAMessages.getString("Button.OK"));
      this.pnlButtons.add(this.btOK);
      this.btOK.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNACalibrationExportDialog.this.doOK();
         }
      });
      this.btCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNACalibrationExportDialog.this.doDialogCancel();
         }
      });
      this.cbShowAll.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNACalibrationExportDialog.this.loadDirectory(VNACalibrationExportDialog.this.cbShowAll.isSelected());
         }
      });
      this.pnlFiles = new JPanel();
      this.pnlFiles.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.getContentPane().add(this.pnlFiles, "Center");
      this.lstFiles = new VNACalibrationFileTable(this);
      JScrollPane scrollPane = new JScrollPane(this.lstFiles);
      this.pnlFiles.add(scrollPane);
      scrollPane.setViewportBorder((Border)null);
      scrollPane.setPreferredSize(new Dimension(800, 245));
      scrollPane.setMinimumSize(new Dimension(800, 245));
      scrollPane.setAlignmentX(0.0F);
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalibrationExportDialog");
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
      this.showCentered(this.getWidth(), this.getHeight());
      TraceHelper.exit(this, "doInit");
   }

   private void loadDirectory(boolean showAllFiles) {
      TraceHelper.entry(this, "loadDirectory");
      this.lstFiles.getModel().clear();
      File file = new File(config.getVNACalibrationDirectory());
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
               if (matches) {
                  blk.setFile(currFile);
                  this.lstFiles.addCalibrationBlock(blk);
               }
            }
         } catch (ProcessingException var9) {
            ErrorLogHelper.exception(this, "loadDirectory", var9);
         }
      }

      this.btOK.setEnabled(false);
      Collections.sort(this.lstFiles.getModel().getData(), new VNACalibrationBlockComparator());
      this.lstFiles.updateUI();
      TraceHelper.exit(this, "loadDirectory");
   }

   public void valueChanged(VNACalibrationBlock blk, boolean doubleClick) {
      TraceHelper.entry(this, "valueChanged", "dbl=" + doubleClick);
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      boolean matches = blk.blockMatches(dib, this.datapool.getScanMode());
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
