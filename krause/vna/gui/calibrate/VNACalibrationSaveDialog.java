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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
import krause.vna.gui.calibrate.file.VNACalibrationFileTable;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationSaveDialog extends KrauseDialog implements IVNACalibrationSelectionListener {
   private static VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNACalibrationFileTable lstFiles;
   private JButton btCancel;
   private JButton btSAVE;
   private JTextField txtFilename;
   private JTextField txtComment;
   private VNACalibrationBlock calibration = null;
   private JPanel panel;

   public VNACalibrationSaveDialog(Window pOwner, VNACalibrationBlock block2save) {
      super(pOwner, true);
      this.setResizable(true);
      TraceHelper.entry(this, "VNACalibrationSaveDialog");
      this.setConfigurationPrefix("VNACalibrationSaveDialog");
      this.setProperties(config);
      this.calibration = block2save;
      String tit = VNAMessages.getString("VNACalibrationSaveDialog.title");
      this.setTitle(MessageFormat.format(tit, this.datapool.getScanMode().toString()));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(800, 200));
      this.panel = new JPanel(new MigLayout("", "[][grow,fill][][][]", "[grow,fill][][]"));
      this.panel.setBorder(new TitledBorder((Border)null, "", 4, 2, (Font)null, (Color)null));
      this.getContentPane().add(this.panel);
      this.lstFiles = new VNACalibrationFileTable(this);
      JScrollPane scrollPane = new JScrollPane(this.lstFiles);
      scrollPane.setAlignmentX(0.0F);
      this.panel.add(scrollPane, "grow, span 6,wrap");
      this.panel.add(new JLabel(VNAMessages.getString("VNACalibrationSaveDialog.filename")), "");
      this.txtFilename = new JTextField();
      this.txtFilename.setColumns(30);
      this.panel.add(this.txtFilename, "");
      this.panel.add(new HelpButton(this, "VNACalibrationSaveDialog"), "wmin 100px");
      this.btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
      this.btCancel.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNACalibrationSaveDialog.this.doDialogCancel();
         }
      });
      this.panel.add(this.btCancel, "wmin 100px");
      this.btSAVE = new JButton(VNAMessages.getString("Button.Save"));
      this.btSAVE.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNACalibrationSaveDialog.this.doSAVE();
         }
      });
      this.panel.add(this.btSAVE, "wmin 100px, wrap");
      this.panel.add(new JLabel(VNAMessages.getString("VNACalibrationSaveDialog.comment")), "");
      this.txtComment = new JTextField();
      this.txtComment.setColumns(30);
      this.panel.add(this.txtComment, "");
      this.getRootPane().setDefaultButton(this.btSAVE);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalibrationSaveDialog");
   }

   protected void doSAVE() {
      TraceHelper.entry(this, "doSaveCalibration");
      this.calibration.setComment(this.txtComment.getText());
      File newFile = new File(config.getVNACalibrationDirectory(), this.txtFilename.getText());
      if (newFile.exists()) {
         String msg = MessageFormat.format(VNAMessages.getString("VNACalibrationSaveDialog.fileExists.2"), config.getVNACalibrationDirectory(), this.txtFilename.getText());
         Object[] options = new Object[]{VNAMessages.getString("Button.Overwrite"), VNAMessages.getString("Button.Cancel")};
         int n = JOptionPane.showOptionDialog(this.getOwner(), msg, VNAMessages.getString("VNACalibrationSaveDialog.fileExists.1"), 2, 3, (Icon)null, options, options[0]);
         if (n == 0) {
            VNACalibrationBlockHelper.save(this.calibration, newFile.getAbsolutePath());
            this.setVisible(false);
         }
      } else {
         VNACalibrationBlockHelper.save(this.calibration, newFile.getAbsolutePath());
         this.setVisible(false);
      }

      TraceHelper.exit(this, "doSaveCalibration");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doExit");
      this.setVisible(false);
      TraceHelper.exit(this, "doExit");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      StringBuffer sb = new StringBuffer();
      sb.append(this.calibration.getScanMode().shortText());
      sb.append("_");
      sb.append(this.datapool.getDriver().getDeviceInfoBlock().getShortName());
      sb.append(".cal");
      this.txtFilename.setText(sb.toString());
      this.addEscapeKey();
      this.loadDirectory(true);
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void loadDirectory(boolean showAllFiles) {
      TraceHelper.entry(this, "loadDirectory");
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
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
            } else if (blk.blockMatches(dib, this.datapool.getScanMode())) {
               blk.setFile(currFile);
               this.lstFiles.addCalibrationBlock(blk);
            }
         } catch (ProcessingException var10) {
            ErrorLogHelper.exception(this, "loadDirectory", var10);
         }
      }

      Collections.sort(this.lstFiles.getModel().getData(), new VNACalibrationBlockComparator());
      this.lstFiles.updateUI();
      TraceHelper.exit(this, "loadDirectory");
   }

   public void valueChanged(VNACalibrationBlock blk, boolean doubleClick) {
      TraceHelper.entry(this, "valueChanged", "dbl=" + doubleClick);
      VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
      boolean blkMatches = blk.blockMatches(dib, this.datapool.getScanMode());
      if (blkMatches) {
         this.txtFilename.setText(blk.getFile().getName());
      } else {
         Toolkit.getDefaultToolkit().beep();
      }

      TraceHelper.exit(this, "valueChanged");
   }
}
