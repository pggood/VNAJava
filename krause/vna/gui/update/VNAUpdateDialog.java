package krause.vna.gui.update;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.config.VNASystemConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import krause.vna.update.DownloadFile;
import krause.vna.update.FileDownloadJob;
import krause.vna.update.FileDownloadStatusListener;
import krause.vna.update.FileDownloadTask;
import krause.vna.update.UpdateChecker;
import krause.vna.update.UpdateInfoBlock;
import net.miginfocom.swing.MigLayout;

public class VNAUpdateDialog extends KrauseDialog implements ActionListener, FileDownloadStatusListener {
   private VNAConfig config = VNAConfig.getSingleton();
   private JTextField txtCurrentVersion;
   private JTextField txtNewVersion;
   private UpdateChecker updateChecker = new UpdateChecker();
   private UpdateInfoBlock infoBlock;
   private JButton btInstall;
   private VNAUpdateFileTable lstFiles;
   private JTextField txtInstallDir;
   private JTextField txtComment;
   private JButton btClose;
   private JButton btAbort;
   FileDownloadTask backgroundTask = null;
   private JButton btCheck;
   private JButton btSearch;
   private JTextField txtUpdateSite;
   private JButton btPropose;
   private JCheckBox rbAllPlattforms;
   private JButton btReadme;

   public VNAUpdateDialog(Frame owner) {
      super((Window)owner, true);
      TraceHelper.exit(this, "VNAUpdateDialog");
      this.setResizable(true);
      this.setPreferredSize(new Dimension(600, 400));
      this.setDefaultCloseOperation(0);
      this.setConfigurationPrefix("VNAUpdateDialog");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNAUpdateDialog.title"));
      this.setLayout(new MigLayout("", "[][grow][][]", ""));
      this.add(new JLabel(VNAMessages.getString("VNAUpdateDialog.updateSite")), "");
      this.add(this.txtUpdateSite = new JTextField(), "span 3,grow,wrap");
      this.txtUpdateSite.setColumns(20);
      this.txtUpdateSite.setEditable(false);
      this.add(new JLabel(VNAMessages.getString("VNAUpdateDialog.currentVersion")), "");
      this.add(this.txtCurrentVersion = new JTextField(), "span 3,grow,wrap");
      this.txtCurrentVersion.setColumns(20);
      this.txtCurrentVersion.setEditable(false);
      this.add(new JLabel(VNAMessages.getString("VNAUpdateDialog.newVersion")), "");
      this.add(this.txtNewVersion = new JTextField(VNAMessages.getString("VNAUpdateDialog.unknownVersion")), "span 2,grow");
      this.txtNewVersion.setColumns(20);
      this.txtNewVersion.setEditable(false);
      this.add(this.btCheck = SwingUtil.createJButton("Button.Check", this), "right,grow,wrap");
      this.add(new JLabel(VNAMessages.getString("VNAUpdateDialog.comment")), "");
      this.add(this.txtComment = new JTextField(), "span 2,grow");
      this.txtComment.setColumns(20);
      this.txtComment.setEditable(false);
      this.add(this.btReadme = SwingUtil.createJButton("Button.Readme", this), "right,wrap");
      this.btReadme.setEnabled(false);
      this.add(new JLabel(VNAMessages.getString("VNAUpdateDialog.filelist")), "");
      this.lstFiles = new VNAUpdateFileTable();
      JScrollPane listScroller = new JScrollPane(this.lstFiles);
      listScroller.setPreferredSize(new Dimension(400, 400));
      this.add(listScroller, "span 3,grow,wrap");
      this.add(new JLabel(VNAMessages.getString("VNAUpdateDialog.installDir")), "");
      this.add(this.txtInstallDir = new JTextField(), "span 3,grow,wrap");
      this.txtInstallDir.setEditable(false);
      this.txtInstallDir.setColumns(128);
      this.add(new JLabel(), "");
      this.add(this.btPropose = SwingUtil.createJButton("Button.Propose", this), "left");
      this.add(this.rbAllPlattforms = SwingUtil.createJCheckbox("VNAUpdateDialog.allOS", this), "left");
      this.add(this.btSearch = SwingUtil.createJButton("Button.Search", this), "right,wrap");
      this.add(this.btClose = SwingUtil.createJButton("Button.Close", this), "");
      this.add(new HelpButton(this, "VNAUpdateDialog"), "");
      this.add(this.btAbort = SwingUtil.createJButton("Button.Abort", this), "");
      this.add(this.btInstall = SwingUtil.createJButton("Button.Install", this), "right,wrap");
      this.doDialogInit();
      TraceHelper.exit(this, "VNAUpdateDialog");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.txtUpdateSite.setText(VNASystemConfig.getVNA_UPDATEURL());
      this.txtCurrentVersion.setText(VNAMessages.getString("Application.version"));
      this.btInstall.setEnabled(false);
      this.btAbort.setEnabled(false);
      this.btPropose.setEnabled(false);
      this.txtInstallDir.setText(this.config.getInstallationDirectory());
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      String cmd = e.getActionCommand();
      if (VNAMessages.getString("Button.Close.Command").equals(cmd)) {
         this.doDialogCancel();
      } else if (VNAMessages.getString("Button.Install.Command").equals(cmd)) {
         this.doINSTALL();
      } else if (VNAMessages.getString("Button.Check.Command").equals(cmd)) {
         this.doCheck();
      } else if (VNAMessages.getString("Button.Abort.Command").equals(cmd)) {
         this.doABORT();
      } else if (VNAMessages.getString("Button.Search.Command").equals(cmd)) {
         this.doUPDATE();
      } else if (VNAMessages.getString("Button.Propose.Command").equals(cmd)) {
         this.doPROPOSE();
      } else if (VNAMessages.getString("Button.Readme.Command").equals(cmd)) {
         this.doShowReadme();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void doShowReadme() {
      TraceHelper.entry(this, "doShowReadme");
      List<DownloadFile> readmeFiles = this.infoBlock.getFilesForType(UpdateChecker.FILE_TYPE.README);
      if (readmeFiles != null && readmeFiles.size() > 0) {
         DownloadFile readmeFile = (DownloadFile)readmeFiles.get(0);
         String url = readmeFile.getRemoteFileName();

         try {
            Desktop.getDesktop().browse(URI.create(url));
         } catch (IOException var5) {
            ErrorLogHelper.exception(this, "mouseClicked", var5);
         }
      }

      TraceHelper.exit(this, "doShowReadme");
   }

   private void doPROPOSE() {
      TraceHelper.entry(this, "doPROPOSE");
      String userDir = System.getProperty("user.dir");
      File f = new File(userDir);
      TraceHelper.text(this, "doPROPOSE", "f=" + f.getAbsolutePath());
      File p = f.getParentFile();
      TraceHelper.text(this, "doPROPOSE", "p=" + p.getAbsolutePath());
      String n = p.getAbsolutePath() + System.getProperty("file.separator") + this.txtNewVersion.getText();
      this.txtInstallDir.setText(n);
      TraceHelper.exit(this, "doPROPOSE");
   }

   private void doABORT() {
      TraceHelper.entry(this, "doABORT");
      if (this.backgroundTask != null) {
         this.backgroundTask.abort();
      }

      this.btAbort.setEnabled(false);
      this.btClose.setEnabled(true);
      this.btInstall.setEnabled(true);
      this.btCheck.setEnabled(true);
      this.btSearch.setEnabled(true);
      TraceHelper.exit(this, "doABORT");
   }

   private void doINSTALL() {
      TraceHelper.entry(this, "doINSTALL");
      String localDirectory = this.txtInstallDir.getText();
      if (!this.validateDownloadDirectory(localDirectory)) {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("VNAUpdateDialog.dirErr.1"), VNAMessages.getString("VNAUpdateDialog.dirErr.2"), 0);
      } else {
         this.backgroundTask = new FileDownloadTask(this);
         Iterator var3 = this.lstFiles.getModel().getJobs().iterator();

         while(var3.hasNext()) {
            FileDownloadJob job = (FileDownloadJob)var3.next();
            job.setLocalDirectory(localDirectory);
            this.backgroundTask.addJob(job);
         }

         this.backgroundTask.execute();
         this.btAbort.setEnabled(true);
         this.btClose.setEnabled(false);
         this.btInstall.setEnabled(false);
         this.btCheck.setEnabled(false);
         this.btSearch.setEnabled(false);
         this.btPropose.setEnabled(false);
         TraceHelper.exit(this, "doINSTALL");
      }
   }

   private boolean validateDownloadDirectory(String localDirectory) {
      boolean rc = false;
      TraceHelper.entry(this, "validateDownloadDirectory");
      String userDir = System.getProperty("user.dir");
      File f = new File(userDir);
      TraceHelper.text(this, "validateDownloadDirectory", "f=" + f.getAbsolutePath());
      rc = !f.getAbsolutePath().equalsIgnoreCase(localDirectory);
      TraceHelper.exit(this, "validateDownloadDirectory");
      return rc;
   }

   private void doCheck() {
      TraceHelper.entry(this, "doCheck");
      this.lstFiles.getModel().clear();

      try {
         this.infoBlock = this.updateChecker.readUpdateInfoFile(VNASystemConfig.getVNA_UPDATEURL(), this.rbAllPlattforms.isSelected());
         if (this.infoBlock != null && this.infoBlock.getFiles() != null) {
            Iterator var2 = this.infoBlock.getFiles().iterator();

            while(var2.hasNext()) {
               DownloadFile oneFile = (DownloadFile)var2.next();
               FileDownloadJob job = new FileDownloadJob();
               job.setFile(oneFile);
               this.lstFiles.getModel().addElement(job);
            }

            String remoteVersion = this.infoBlock.getVersion();
            if (remoteVersion != null) {
               this.txtNewVersion.setText(remoteVersion);
               this.txtComment.setText(this.infoBlock.getComment());
               this.btPropose.setEnabled(true);
               boolean ok = this.txtCurrentVersion.getText().length() > 0 && this.txtInstallDir.getText().length() > 0;
               this.btInstall.setEnabled(ok);
            }

            List<DownloadFile> readme = this.infoBlock.getFilesForType(UpdateChecker.FILE_TYPE.README);
            this.btReadme.setEnabled(readme != null && readme.size() > 0);
         }
      } catch (ProcessingException var4) {
         OptionDialogHelper.showExceptionDialog(this.getOwner(), "VNAUpdateDialog.versionCheckError.title", "VNAUpdateDialog.versionCheckError.message", var4);
      }

      TraceHelper.exit(this, "doCheck");
   }

   private void doUPDATE() {
      TraceHelper.entry(this, "doUPDATE");
      File currFile = new File(this.txtInstallDir.getText());
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle(VNAMessages.getString("VNAUpdateDialog.chooseDirectory"));
      fc.setFileSelectionMode(1);
      fc.setCurrentDirectory(currFile);
      int returnVal = fc.showOpenDialog(this.getOwner());
      if (returnVal == 0) {
         String currentDirectory = fc.getSelectedFile().getAbsolutePath();
         this.txtInstallDir.setText(currentDirectory);
         this.config.setInstallationDirectory(currentDirectory);
      }

      TraceHelper.exit(this, "doUPDATE");
   }

   public void publishState(FileDownloadJob job) {
      TraceHelper.entry(this, "publishState");
      this.lstFiles.getModel().updateElement(job);
      TraceHelper.exit(this, "publishState");
   }

   public void done() {
      TraceHelper.entry(this, "done");
      this.btAbort.setEnabled(false);
      this.btClose.setEnabled(true);
      this.btInstall.setEnabled(true);
      this.btCheck.setEnabled(true);
      this.btSearch.setEnabled(true);
      this.btPropose.setEnabled(true);
      int opt = JOptionPane.showOptionDialog(this, VNAMessages.getString("VNAUpdateDialog.done.1"), VNAMessages.getString("VNAUpdateDialog.done.2"), 0, 3, (Icon)null, (Object[])null, (Object)null);
      if (opt == 0) {
         File file = new File(this.txtInstallDir.getText());

         try {
            Desktop.getDesktop().open(file);
         } catch (IOException var4) {
            ErrorLogHelper.exception(this, "actionPerformed", var4);
         }
      }

      TraceHelper.exit(this, "done");
   }
}
