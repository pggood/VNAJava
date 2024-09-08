package krause.vna.gui.raw;

import java.awt.Window;
import java.io.File;
import java.text.MessageFormat;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.importers.VNASnPImportDialog;
import krause.vna.resources.VNAMessages;

public class VNARawHandler {
   protected Window owner;
   protected VNAConfig config = VNAConfig.getSingleton();
   protected VNADataPool datapool = VNADataPool.getSingleton();
   public static final String S1P_EXTENSION = "S1P";
   public static final String S2P_EXTENSION = "S2P";
   public static final String RAW_DESCRIPTION = "vna/J Import/Export files";
   public static final String RAW_EXTENSION_V2 = "XML";

   public VNARawHandler(Window pMainFrame) {
      TraceHelper.entry(this, "VNARawHandler");
      this.owner = pMainFrame;
      TraceHelper.exit(this, "VNARawHandler");
   }

   public String doExport(VNACalibratedSampleBlock blk) {
      TraceHelper.entry(this, "doExport");
      String rc = null;
      VNARawCommentField ac = new VNARawCommentField(this.owner, true);
      ac.setText(this.config.getLastRawComment());
      JFileChooser fc = new JFileChooser();
      fc.setAccessory(ac);
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new FileNameExtensionFilter("vna/J Import/Export files", new String[]{"XML"}));
      fc.setSelectedFile(new File(this.config.getReferenceDirectory() + "/."));
      int returnVal = fc.showSaveDialog(this.owner);
      if (returnVal == 0) {
         File file = fc.getSelectedFile();
         this.config.setReferenceDirectory(file.getParent());
         if (!file.getName().endsWith(".XML")) {
            file = new File(file.getAbsolutePath() + "." + "XML");
         }

         if (file.exists()) {
            String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), file.getName());
            int response = JOptionPane.showOptionDialog(this.owner, msg, VNAMessages.getString("Message.Export.2"), 0, 3, (Icon)null, (Object[])null, (Object)null);
            if (response == 2) {
               return rc;
            }
         }

         this.config.setLastRawComment(ac.getText());
         blk.setComment(ac.getText());

         try {
            (new VNARawXMLHandler()).writeXMLFile(blk, file);
         } catch (ProcessingException var9) {
            String msg = MessageFormat.format(VNAMessages.getString("Message.Export.6"), var9.getMessage());
            JOptionPane.showMessageDialog(this.owner, msg, VNAMessages.getString("Message.Export.5"), 0);
         }

         rc = file.getAbsolutePath();
      }

      TraceHelper.exitWithRC(this, "doExport", rc);
      return rc;
   }

   public VNACalibratedSampleBlock doImport() {
      TraceHelper.entry(this, "doImport");
      VNACalibratedSampleBlock rc = null;
      VNARawCommentField ac = new VNARawCommentField(this.owner, false);
      ac.setEnabled(false);
      JFileChooser fc = new JFileChooser();
      fc.setAccessory(ac);
      fc.addPropertyChangeListener(ac);
      fc.addActionListener(ac);
      fc.setFileSelectionMode(0);
      fc.setSelectedFile(new File(this.config.getReferenceDirectory() + "/."));
      fc.setFileFilter(new FileNameExtensionFilter("vna/J Import/Export files", new String[]{"XML", "S1P", "S2P"}));
      int returnVal = fc.showOpenDialog(this.owner);
      if (returnVal == 0) {
         File file = fc.getSelectedFile();
         this.config.setReferenceDirectory(file.getParent());

         try {
            rc = this.readFile(file);
         } catch (ProcessingException var8) {
            String m = MessageFormat.format(VNAMessages.getString("Message.Import.1"), var8.getMessage());
            JOptionPane.showMessageDialog(this.owner, m, VNAMessages.getString("Message.Import.2"), 0);
         }
      }

      TraceHelper.exit(this, "doImport");
      return rc;
   }

   public void exportMainDiagram() {
      TraceHelper.entry(this, "exportMainDiagram");
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      this.doExport(blk);
      TraceHelper.exit(this, "exportMainDiagram");
   }

   public String readComment(File file) throws ProcessingException {
      TraceHelper.entry(this, "readComment");
      String rc = null;
      if (file != null && file.getAbsolutePath().toUpperCase().endsWith("XML")) {
         TraceHelper.text(this, "readComment", "found XML in name");
         rc = (new VNARawXMLHandler()).readXMLCommentFromFile(file);
      }

      TraceHelper.exitWithRC(this, "readComment", rc);
      return rc;
   }

   public VNACalibratedSampleBlock readFile(File file) throws ProcessingException {
      TraceHelper.entry(this, "readFile");
      VNACalibratedSampleBlock rc = null;
      if (file.getAbsolutePath().toUpperCase().endsWith("XML")) {
         rc = (new VNARawXMLHandler()).readXMLFromFile(file);
      } else if (file.getAbsolutePath().toUpperCase().endsWith(".S1P")) {
         rc = this.readSParameterFile(file);
      } else {
         if (!file.getAbsolutePath().toUpperCase().endsWith(".S2P")) {
            throw new ProcessingException(VNAMessages.getString("Message.Import.3"));
         }

         rc = this.readSParameterFile(file);
      }

      TraceHelper.exit(this, "readFile");
      return rc;
   }

   private VNACalibratedSampleBlock readSParameterFile(File file) {
      VNACalibratedSampleBlock rc = null;
      TraceHelper.entry(this, "readSParameterFile");
      VNASnPImportDialog dlg = new VNASnPImportDialog(this.owner, file.getAbsolutePath());
      rc = dlg.getData();
      dlg.dispose();
      TraceHelper.exit(this, "readSParameterFile");
      return rc;
   }
}
