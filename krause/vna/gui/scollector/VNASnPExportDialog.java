package krause.vna.gui.scollector;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.gui.HelpButton;
import krause.vna.gui.importers.VNASnPDataTable;
import krause.vna.gui.importers.VNASnPDataTableModel;
import krause.vna.gui.util.SwingUtil;
import krause.vna.importers.SnPRecord;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.jfree.ui.ExtensionFileFilter;

public class VNASnPExportDialog extends KrauseDialog {
   private static final String S2P_EXTENSION = ".s2p";
   private JButton btOK;
   private VNASnPDataTable lstData;
   private SnPRecord[] sRecords;
   private VNAConfig config = VNAConfig.getSingleton();

   public VNASnPExportDialog(Window aFrame, SnPRecord[] pRecords) {
      super(aFrame, true);
      TraceHelper.entry(this, "VNASnPExportDialog");
      this.sRecords = pRecords;
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNASnPExportDialog");
      this.setTitle(VNAMessages.getString("VNASnPExportDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(800, 600));
      this.getContentPane().setLayout(new MigLayout("", "[][grow,fill][][]", "[][grow,fill][]"));
      this.getContentPane().add(new JLabel(VNAMessages.getString("VNASnPExportDialog.headline")), "span 4,grow,wrap");
      this.lstData = new VNASnPDataTable();
      JScrollPane scrollPane = new JScrollPane(this.lstData);
      scrollPane.setViewportBorder((Border)null);
      this.getContentPane().add(scrollPane, "span 4,grow,wrap");
      this.getContentPane().add(SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNASnPExportDialog.this.doDialogCancel();
         }
      }), "left");
      this.getContentPane().add(new JLabel(), "");
      this.getContentPane().add(new HelpButton(this, "VNASnPExportDialog"), "");
      this.btOK = SwingUtil.createJButton("Button.Save", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNASnPExportDialog.this.doSave();
         }
      });
      this.getContentPane().add(this.btOK, "right");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNASnPImportDialog");
   }

   protected void doSave() {
      TraceHelper.entry(this, "doSave");
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new ExtensionFileFilter("S2P files", ".s2p"));
      fc.setSelectedFile(new File(this.config.getReferenceDirectory() + "/."));
      int returnVal = fc.showSaveDialog(this);
      if (returnVal == 0) {
         File file = fc.getSelectedFile();
         this.config.setReferenceDirectory(file.getParent());
         if (!file.getName().endsWith(".s2p")) {
            file = new File(file.getAbsolutePath() + ".s2p");
         }

         if (file.exists()) {
            String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), file.getName());
            int response = JOptionPane.showOptionDialog(this, msg, VNAMessages.getString("Message.Export.2"), 0, 3, (Icon)null, (Object[])null, (Object)null);
            if (response == 2) {
               return;
            }
         }

         try {
            this.exportS2P(file.getAbsolutePath());
         } catch (ProcessingException var6) {
            JOptionPane.showMessageDialog(this, var6.getMessage(), VNAMessages.getString("Message.Export.2"), 0);
            ErrorLogHelper.exception(this, "doExport", var6);
         }

         this.setVisible(false);
         this.dispose();
      }

      TraceHelper.exit(this, "doSave");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      VNASnPDataTableModel model = this.lstData.getModel();
      List<SnPRecord> recList = new ArrayList();
      SnPRecord[] var6;
      int var5 = (var6 = this.sRecords).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         SnPRecord record = var6[var4];
         recList.add(record);
      }

      model.getData().clear();
      model.getData().addAll(recList);
      model.fireTableDataChanged();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void exportS2P(String fnp) throws ProcessingException {
      TraceHelper.entry(this, "exportS2P");
      if (fnp != null) {
         DecimalFormatSymbols dfs = this.getDecimalFormatSymbols();
         DecimalFormat fmtFrequency = new DecimalFormat("000000000", dfs);
         DecimalFormat fmtLoss = new DecimalFormat("0.00000000", dfs);
         DecimalFormat fmtPhase = new DecimalFormat("0.00000000", dfs);
         DecimalFormat fmtReference = new DecimalFormat("0.0", dfs);
         FileOutputStream fos = null;
         BufferedWriter w = null;

         try {
            fos = new FileOutputStream(fnp);
            w = new BufferedWriter(new OutputStreamWriter(fos, "ISO-8859-1"));
            w.write("! created by ");
            w.write(System.getProperty("user.name"));
            w.write(" at ");
            w.write((new Date()).toString());
            w.write(GlobalSymbols.LINE_SEPARATOR);
            w.write("! generated using vna/J Version ");
            w.write(VNAMessages.getString("Application.version"));
            w.write(GlobalSymbols.LINE_SEPARATOR);
            w.write("# Hz S DB R ");
            double real = VNADataPool.getSingleton().getDriver().getDeviceInfoBlock().getReferenceResistance().getReal();
            w.write(fmtReference.format(real));
            w.write(GlobalSymbols.LINE_SEPARATOR);
            int numSamples = this.sRecords.length;

            for(int i = 0; i < numSamples; ++i) {
               SnPRecord data = this.sRecords[i];
               w.write(fmtFrequency.format(data.getFrequency()));
               w.write(" ");

               for(int j = 0; j < 4; ++j) {
                  w.write(fmtLoss.format(data.getLoss()[j]));
                  w.write(" ");
                  w.write(fmtPhase.format(data.getPhase()[j]));
                  w.write(" ");
               }

               w.write(GlobalSymbols.LINE_SEPARATOR);
            }
         } catch (IOException var25) {
            ErrorLogHelper.exception(this, "exportS2P", var25);
            throw new ProcessingException(var25);
         } finally {
            if (w != null) {
               try {
                  w.flush();
                  w.close();
                  w = null;
               } catch (IOException var24) {
                  ErrorLogHelper.exception(this, "exportS2P", var24);
               }
            }

            if (fos != null) {
               try {
                  fos.flush();
                  fos.close();
                  fos = null;
               } catch (IOException var23) {
                  ErrorLogHelper.exception(this, "exportS2P", var23);
               }
            }

         }
      }

      TraceHelper.exit(this, "exportS2P");
   }

   private DecimalFormatSymbols getDecimalFormatSymbols() {
      return ".".equals(this.config.getExportDecimalSeparator()) ? new DecimalFormatSymbols(Locale.ENGLISH) : new DecimalFormatSymbols(Locale.GERMAN);
   }
}
