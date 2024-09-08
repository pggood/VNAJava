package krause.vna.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.StringHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.presets.VNAPresetHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverFactory;
import krause.vna.export.CSVExporter;
import krause.vna.export.JpegExporter;
import krause.vna.export.PDFExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.firmware.VNAFirmwareUpdateDialog;
import krause.vna.gui.about.VNAAboutDialog;
import krause.vna.gui.analyse.VNADataAnalysisDialog;
import krause.vna.gui.beacon.VNABeaconDialog;
import krause.vna.gui.cable.VNACableLengthDialog;
import krause.vna.gui.cable.VNACableLossDialog;
import krause.vna.gui.calibrate.VNACalibrationDialog;
import krause.vna.gui.calibrate.VNACalibrationLoadDialog;
import krause.vna.gui.calibrate.calibrationkit.VNACalibrationKitDialog;
import krause.vna.gui.calibrate.frequency.VNAFrequencyCalibrationDialog;
import krause.vna.gui.config.VNAColorConfigDialog;
import krause.vna.gui.config.VNAConfigEditDialog;
import krause.vna.gui.config.VNAConfigLanguageDialog;
import krause.vna.gui.driver.VNADriverConfigDialog;
import krause.vna.gui.export.VNAAutoExportSettingsDialog;
import krause.vna.gui.export.VNAExportCommentDialog;
import krause.vna.gui.export.VNAExportSettingsDialog;
import krause.vna.gui.fft.VNAFFTDataDetailsDialog;
import krause.vna.gui.filter.VNAGaussianFilterCreatorDialog;
import krause.vna.gui.menu.VNAMenuBar;
import krause.vna.gui.multiscan.VNAMultiScanWindow;
import krause.vna.gui.padcalc.VNAPadCalculatorDialog;
import krause.vna.gui.raw.VNARawHandler;
import krause.vna.gui.readme.VNALicenseDialog;
import krause.vna.gui.readme.VNAReadmeDialog;
import krause.vna.gui.scale.VNAScaleSetupDialog;
import krause.vna.gui.scheduler.VNASchedulerDialog;
import krause.vna.gui.scollector.VNASCollectorDialog;
import krause.vna.gui.toolbar.VNAToolbar;
import krause.vna.gui.update.VNAUpdateDialog;
import krause.vna.resources.VNAMessages;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ExtensionFileFilter;

public class VNAMenuAndToolbarHandler implements ActionListener {
   private static VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNAMainFrame mainFrame = null;
   private VNAMenuBar menubar = null;
   private VNAToolbar toolbar = null;

   public VNAMenuAndToolbarHandler(VNAMainFrame pMainFrame) {
      this.mainFrame = pMainFrame;
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      TraceHelper.entry(this, "actionPerformed", cmd);
      if (VNAMessages.getString("Menu.File.Exit.Command").equals(cmd)) {
         this.mainFrame.doShutdown();
      } else if (VNAMessages.getString("Menu.Tools.Generator.Command").equals(cmd)) {
         this.doShowGeneratorDialog();
      } else if (VNAMessages.getString("Menu.Tools.Padcalc.Command").equals(cmd)) {
         this.doShowPadCalcDialog();
      } else if (VNAMessages.getString("Menu.Tools.CableLoss.Command").equals(cmd)) {
         if (this.datapool.getMainCalibrationBlock().getScanMode().isReflectionMode()) {
            new VNACableLossDialog(this.mainFrame.getJFrame());
         } else {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.OnlyInReflectionMode.msg"), VNAMessages.getString("Message.OnlyInReflectionMode.title"), 2);
         }
      } else if (VNAMessages.getString("Menu.Help.About.Command").equals(cmd)) {
         this.doAboutDialog();
      } else if (VNAMessages.getString("Menu.Help.Readme.Command").equals(cmd)) {
         new VNAReadmeDialog(this.mainFrame);
      } else if (VNAMessages.getString("Menu.Help.License.Command").equals(cmd)) {
         new VNALicenseDialog(this.mainFrame);
      } else if (VNAMessages.getString("Menu.Help.Support.Command").equals(cmd)) {
         this.doCopySupportInformation2Clipboard();
      } else if (VNAMessages.getString("Menu.Export.Setting.Command").equals(cmd)) {
         new VNAExportSettingsDialog(this.mainFrame);
      } else if (VNAMessages.getString("Menu.Export.AutoSetting.Command").equals(cmd)) {
         new VNAAutoExportSettingsDialog(this.mainFrame);
      } else if (VNAMessages.getString("Menu.Analyser.Setup.Command").equals(cmd)) {
         this.doAnalyserSetup();
      } else if (VNAMessages.getString("Menu.Analyser.Info.Command").equals(cmd)) {
         this.doShowDriverInfo();
      } else if (VNAMessages.getString("Menu.Analyser.Reconnect.Command").equals(cmd)) {
         this.doDriverReconnect();
      } else if (VNAMessages.getString("Menu.Tools.Gaussian.Command").equals(cmd)) {
         this.doShowGaussianCalculator();
      } else if (VNAMessages.getString("Menu.Tools.FFT.Command").equals(cmd)) {
         if (this.datapool.getMainCalibrationBlock().getScanMode().isReflectionMode()) {
            new VNAFFTDataDetailsDialog(this.mainFrame.getJFrame());
         } else {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.OnlyInReflectionMode.msg"), VNAMessages.getString("Message.OnlyInReflectionMode.title"), 2);
         }
      } else if (VNAMessages.getString("Menu.Tools.Beacon.Command").equals(cmd)) {
         this.doBeacon();
      } else if (VNAMessages.getString("Menu.Schedule.Execute.Command").equals(cmd)) {
         this.doScheduler();
      } else if (VNAMessages.getString("Menu.Export.S2P.Command").equals(cmd)) {
         this.doExportSnP();
      } else if (VNAMessages.getString("Menu.Export.S2PCollector.Command").equals(cmd)) {
         this.doShowS2PCollector();
      } else if (VNAMessages.getString("Menu.Export.XLS.Command").equals(cmd)) {
         this.doExportXLS(e);
      } else if (VNAMessages.getString("Menu.Export.XML.Command").equals(cmd)) {
         this.doExportXML();
      } else if (VNAMessages.getString("Menu.Raw.Write.Command").equals(cmd)) {
         this.doExportXML();
      } else if (VNAMessages.getString("Menu.Export.CSV.Command").equals(cmd)) {
         this.doExportCSV();
      } else if (VNAMessages.getString("Menu.Export.ZPlot.Command").equals(cmd)) {
         this.doExportZPlots();
      } else if (VNAMessages.getString("Menu.Export.JPG.Command").equals(cmd)) {
         this.doExportJPG(e);
      } else if (VNAMessages.getString("Menu.Export.PDF.Command").equals(cmd)) {
         this.doExportPDF(e);
      } else if (VNAMessages.getString("Menu.File.Settings.Command").equals(cmd)) {
         this.doConfigDialog();
      } else if (VNAMessages.getString("Menu.Tools.Cablelength.Command").equals(cmd)) {
         if (this.datapool.getMainCalibrationBlock().getScanMode().isReflectionMode()) {
            new VNACableLengthDialog(this.mainFrame);
         } else {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.OnlyInReflectionMode.msg"), VNAMessages.getString("Message.OnlyInReflectionMode.title"), 2);
         }
      } else if (VNAMessages.getString("Menu.Tools.Firmware.Command").equals(cmd)) {
         this.doUploadFirmware();
      } else if (VNAMessages.getString("Menu.Calibration.Calibrate.Command").equals(cmd)) {
         this.doCalibrationDialog();
      } else if (VNAMessages.getString("Menu.Calibration.CalibrationSet.Command").equals(cmd)) {
         new VNACalibrationKitDialog(this.mainFrame);
      } else if (VNAMessages.getString("Menu.Calibration.Load.Command").equals(cmd)) {
         this.doCalibrationLoad();
      } else if (VNAMessages.getString("Menu.Calibration.Import.Command").equals(cmd)) {
         this.doCalibrationImport();
      } else if (VNAMessages.getString("Menu.Calibration.Export.Command").equals(cmd)) {
         this.doCalibrationExport();
      } else if (VNAMessages.getString("Menu.Calibration.Frequency.Command").equals(cmd)) {
         this.doFrequencyCalibration();
      } else if (VNAMessages.getString("Menu.File.Color.Command").equals(cmd)) {
         if ((e.getModifiers() & 2) > 0) {
            this.mainFrame.getMenubar().add(this.mainFrame.getMenubar().createExperimentalMenu());
            this.mainFrame.getMenubar().revalidate();
         } else {
            this.doColorConfig();
         }
      } else if (VNAMessages.getString("Menu.File.Language.Command").equals(cmd)) {
         this.doLanguageConfig();
      } else if (VNAMessages.getString("Menu.Analysis.Command").equals(cmd)) {
         this.doAnalysis();
      } else if (VNAMessages.getString("Menu.Multitune.Command").equals(cmd)) {
         this.doMultiScan();
      } else if (VNAMessages.getString("Menu.Update.Command").equals(cmd)) {
         this.docCheckForUpdates();
      } else if (VNAMessages.getString("Menu.Presets.Load.Command").equals(cmd)) {
         (new VNAPresetHelper(this.mainFrame)).doLoadPresets();
      } else if (VNAMessages.getString("Menu.Presets.Save.Command").equals(cmd)) {
         (new VNAPresetHelper(this.mainFrame)).doSavePresets();
      } else if (VNAMessages.getString("Menu.Analyser.Single.Command").equals(cmd)) {
         this.mainFrame.getDataPanel().startSingleScan();
      } else if (VNAMessages.getString("Menu.Analyser.Free.Command").equals(cmd)) {
         this.mainFrame.getDataPanel().startFreeRun();
      } else if (VNAMessages.getString("Menu.File.SettingsScales.Command").equals(cmd)) {
         new VNAScaleSetupDialog(this.mainFrame);
      } else if (!VNAMessages.getString("Menu.Experimental.A.Command").equals(cmd)) {
         VNAMessages.getString("Menu.Experimental.B.Command").equals(cmd);
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void doShowGaussianCalculator() {
      new VNAGaussianFilterCreatorDialog(this.mainFrame.getJFrame());
   }

   public void doAboutDialog() {
      new VNAAboutDialog(this.mainFrame);
   }

   private void doAnalyserSetup() {
      TraceHelper.entry(this, "doAnalyserSetup");
      new VNADriverConfigDialog(this.mainFrame);
      TraceHelper.exit(this, "doAnalyserSetup");
   }

   private void doAnalysis() {
      TraceHelper.entry(this, "doAnalysis");
      new VNADataAnalysisDialog(this.mainFrame);
      TraceHelper.exit(this, "doAnalysis");
   }

   public void doCalibrationDialog() {
      TraceHelper.entry(this, "doCalibrationDialog");
      if (this.datapool.getScanMode() != null) {
         VNACalibrationDialog sDlg = new VNACalibrationDialog(this.mainFrame);
         if (sDlg.isDataValid()) {
            sDlg.getCalibration().calculateCalibrationTemperature();
            this.mainFrame.setMainCalibrationBlock(sDlg.getCalibration());
         }

         sDlg.dispose();
      } else {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), 2);
      }

      TraceHelper.exit(this, "doCalibrationDialog");
   }

   private void doCalibrationExport() {
      TraceHelper.entry(this, "doCalibrationExport");
      if (this.datapool.getScanMode() != null) {
         VNACalibrationBlock blk = this.datapool.getMainCalibrationBlock();
         if (blk != null) {
            String fn = null;
            if (blk.getFile() != null) {
               fn = blk.getFile().getName();
            }

            if (fn == null) {
               StringBuilder sb = new StringBuilder();
               sb.append(blk.getScanMode().shortText());
               sb.append("_");
               sb.append(this.datapool.getDriver().getDeviceInfoBlock().getShortName());
               sb.append(".cal");
               fn = sb.toString();
            }

            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(0);
            fc.setFileFilter(new ExtensionFileFilter("vna/J calibration files (*.cal)", ".cal"));
            fc.setSelectedFile(new File(config.getCalibrationExportDirectory() + System.getProperty("file.separator") + fn));
            int returnVal = fc.showSaveDialog(this.mainFrame.getJFrame());
            if (returnVal == 0) {
               File file = fc.getSelectedFile();
               if (file.exists()) {
                  String msg = MessageFormat.format(VNAMessages.getString("VNACalibrationSaveDialog.fileExists.2"), config.getCalibrationExportDirectory(), fn);
                  Object[] options = new Object[]{VNAMessages.getString("Button.Overwrite"), VNAMessages.getString("Button.Cancel")};
                  int n = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), msg, VNAMessages.getString("VNACalibrationSaveDialog.fileExists.1"), 2, 3, (Icon)null, options, options[0]);
                  if (n == 0) {
                     VNACalibrationBlockHelper.save(blk, file.getAbsolutePath());
                  }
               } else {
                  VNACalibrationBlockHelper.save(blk, file.getAbsolutePath());
               }

               config.setCalibrationExportDirectory(file.getParentFile().getAbsolutePath());
            }
         } else {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.NoCalPresent.1"), VNAMessages.getString("Message.NoCalPresent.2"), 2);
         }
      } else {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), 2);
      }

      TraceHelper.exit(this, "doCalibrationExport");
   }

   private void doCalibrationImport() {
      TraceHelper.entry(this, "doCalibrationImport");
      if (this.datapool.getScanMode() != null) {
         JFileChooser fc = new JFileChooser();
         fc.setFileSelectionMode(0);
         fc.setFileFilter(new ExtensionFileFilter("vna/J calibration files (*.cal)", ".cal"));
         fc.setSelectedFile(new File(config.getCalibrationExportDirectory() + System.getProperty("file.separator") + "."));
         int returnVal = fc.showOpenDialog(this.mainFrame.getJFrame());
         boolean doImport = false;
         if (returnVal == 0) {
            File importFile = fc.getSelectedFile();
            String importPath = importFile.getParentFile().getAbsolutePath();
            String importFilename = importFile.getName();
            File targetFile = new File(config.getVNACalibrationDirectory() + System.getProperty("file.separator") + importFilename);
            if (targetFile.exists()) {
               String msg = MessageFormat.format(VNAMessages.getString("VNACalibrationSaveDialog.fileExists.2"), config.getVNACalibrationDirectory(), importFilename);
               Object[] options = new Object[]{VNAMessages.getString("Button.Overwrite"), VNAMessages.getString("Button.Cancel")};
               int n = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), msg, VNAMessages.getString("VNACalibrationSaveDialog.fileExists.1"), 2, 3, (Icon)null, options, options[0]);
               if (n == 0) {
                  doImport = true;
               }
            } else {
               doImport = true;
            }

            if (doImport) {
               IVNADriver drv = this.datapool.getDriver();
               VNADeviceInfoBlock dib = drv.getDeviceInfoBlock();
               VNACalibrationKit kit = this.datapool.getCalibrationKit();
               VNACalibrationBlock blk = null;
               FileChannel channelIn = null;
               FileChannel channelOut = null;

               try {
                  blk = VNACalibrationBlockHelper.load(importFile, drv, kit);
                  if (blk.blockMatches(dib, this.datapool.getScanMode())) {
                     channelIn = (new FileInputStream(importFile)).getChannel();
                     channelOut = (new FileOutputStream(targetFile)).getChannel();
                     channelOut.transferFrom(channelIn, 0L, channelIn.size());
                     this.mainFrame.setMainCalibrationBlock(blk);
                  }
               } catch (ProcessingException var30) {
                  JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.ImportCal.1"), VNAMessages.getString("Message.ImportCal.2"), 2);
               } catch (IOException var31) {
                  JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.ImportCal.3"), VNAMessages.getString("Message.ImportCal.2"), 2);
               } finally {
                  if (channelIn != null) {
                     try {
                        channelIn.close();
                     } catch (IOException var29) {
                     }
                  }

                  if (channelOut != null) {
                     try {
                        channelOut.close();
                     } catch (IOException var28) {
                     }
                  }

               }
            }

            config.setCalibrationExportDirectory(importPath);
         }
      } else {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), 2);
      }

      TraceHelper.exit(this, "doCalibrationImport");
   }

   public void doCalibrationLoad() {
      TraceHelper.entry(this, "doCalibrationLoad");
      if (this.datapool.getScanMode() != null) {
         VNACalibrationLoadDialog sDlg = new VNACalibrationLoadDialog(this.mainFrame.getJFrame());
         VNACalibrationBlock blk = sDlg.getSelectedCalibrationBlock();
         if (blk != null) {
            this.mainFrame.setMainCalibrationBlock(blk);
         }

         sDlg.dispose();
      } else {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), 2);
      }

      TraceHelper.exit(this, "doCalibrationLoad");
   }

   private void docCheckForUpdates() {
      TraceHelper.entry(this, "docCheckForUpdates");
      new VNAUpdateDialog(this.mainFrame.getJFrame());
      TraceHelper.exit(this, "docCheckForUpdates");
   }

   private void doColorConfig() {
      TraceHelper.entry(this, "doColorConfig");
      VNAColorConfigDialog sDlg = new VNAColorConfigDialog(this.mainFrame, this.mainFrame.getJFrame());
      this.mainFrame.getDiagramPanel().setupColors();
      this.mainFrame.getMarkerPanel().setupColors();
      sDlg.dispose();
      TraceHelper.exit(this, "doColorConfig");
   }

   public void doConfigDialog() {
      new VNAConfigEditDialog(this.mainFrame);
   }

   private void doCopySupportInformation2Clipboard() {
      TraceHelper.entry(this, "doCopySupportInformation2Clipboard");
      String[] values = new String[]{"Application version....[" + VNAMessages.getString("Application.version") + " " + VNAMessages.getString("Application.date") + "]", "Java version...........[" + System.getProperty("java.version") + "]", "Java runtime.version...[" + System.getProperty("java.runtime.version") + "]", "Java vm.version........[" + System.getProperty("java.vm.version") + "]", "Java vm.vendor.........[" + System.getProperty("java.vm.vendor") + "]", "OS.....................[" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "]", "Country/Language.......[" + Locale.getDefault().getCountry() + "/" + Locale.getDefault().getLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]", "                       [" + Locale.getDefault().getDisplayCountry() + "/" + Locale.getDefault().getDisplayLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]", "Analyser ..............[" + this.datapool.getDriver().getDeviceInfoBlock().getLongName() + "]", "User ..................[" + System.getProperty("user.name") + "]", "Home ..................[" + System.getProperty("user.home") + "]", "Installation directory.[" + config.getInstallationDirectory() + "]", "Configuration directory[" + config.getVNAConfigDirectory() + "]"};
      String rc = StringHelper.array2String(values, GlobalSymbols.LINE_SEPARATOR);
      StringSelection str = new StringSelection(rc);
      Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
      cb.setContents(str, this.mainFrame);
      JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.Support.text"), VNAMessages.getString("Message.Support.title"), 1);
      TraceHelper.exit(this, "doCopySupportInformation2Clipboard");
   }

   private void doDriverReconnect() {
      TraceHelper.entry(this, "doDriverReconnect");
      final SimpleProgressPopup spp = new SimpleProgressPopup(this.mainFrame.getJFrame(), VNAMessages.getString("Message.Reconnect"));
      spp.setTask(new SwingWorker<Void, Void>() {
         protected Void doInBackground() throws Exception {
            VNAMenuAndToolbarHandler.this.mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(3));
            VNAMenuAndToolbarHandler.this.mainFrame.unloadDriver();

            int i;
            for(i = 0; i < 50; i += 10) {
               this.setProgress(i);
               Thread.sleep(200L);
            }

            VNAMenuAndToolbarHandler.this.mainFrame.loadDriver();

            for(i = 50; i < 100; i += 10) {
               this.setProgress(i);
               Thread.sleep(200L);
            }

            VNAMenuAndToolbarHandler.this.mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(0));
            return null;
         }

         protected void done() {
            spp.dispose();
         }
      });
      spp.run();
      TraceHelper.exit(this, "doDriverReconnect");
   }

   private void doBeacon() {
      new VNABeaconDialog(this.mainFrame.getJFrame());
   }

   private void doExportCSV() {
      TraceHelper.entry(this, "doExportCSV");
      this.doExportJPGFileInternal(new CSVExporter(this.mainFrame), true);
      TraceHelper.exit(this, "doExportCSV");
   }

   private void doExportJPG(ActionEvent e) {
      TraceHelper.entry(this, "doExportJPG");
      boolean toClipboard = (e.getModifiers() & 1) != 0;
      boolean autoOpen = (e.getModifiers() & 2) != 0;
      if (toClipboard) {
         this.doExportJPGClipboard();
      } else {
         String fileName = this.doExportJPGFileInternal(new JpegExporter(this.mainFrame), !autoOpen);
         if (autoOpen && fileName != null) {
            try {
               File file = new File(fileName);
               Desktop.getDesktop().open(file);
            } catch (IOException var6) {
               ErrorLogHelper.exception(this, "doExportJPG", var6);
            }
         }
      }

      TraceHelper.exit(this, "doExportJPG");
   }

   public void doExportJPGClipboard() {
      TraceHelper.entry(this, "doExportJPGClipboard");
      VNAExporter exporter = new VNAExporter(this.mainFrame) {
         public String export(String fnp, boolean overwrite) throws ProcessingException {
            return null;
         }

         public String getExtension() {
            return null;
         }
      };
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      VNACalibratedSample[] samples = blk.getCalibratedSamples();
      JFreeChart chart = exporter.createChart(samples);
      Image awtImg = chart.createBufferedImage(config.getExportDiagramWidth(), config.getExportDiagramHeight());
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

      class MyImageSelection implements Transferable {
         private Image img;
         DataFlavor myFlavor;

         public MyImageSelection(Image awtImg) {
            this.myFlavor = DataFlavor.imageFlavor;
            this.img = awtImg;
         }

         public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!this.myFlavor.equals(flavor)) {
               throw new UnsupportedFlavorException(flavor);
            } else {
               return this.img;
            }
         }

         public synchronized DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{this.myFlavor};
         }

         public boolean isDataFlavorSupported(DataFlavor flavor) {
            return this.myFlavor.equals(flavor);
         }
      }

      MyImageSelection select = new MyImageSelection(awtImg);
      clipboard.setContents(select, this.mainFrame);
      TraceHelper.exit(this, "doExportJPGClipboard");
   }

   private String doExportJPGFileInternal(VNAExporter exp, boolean showSuccess) {
      TraceHelper.entry(this, "doExportJPGFileInternal");
      String filename = null;
      if (this.datapool.getCalibratedData() != null) {
         try {
            String fnp = config.getExportDirectory() + System.getProperty("file.separator") + config.getExportFilename();
            filename = exp.export(fnp, config.isExportOverwrite());
            if (showSuccess) {
               JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.3"), filename), VNAMessages.getString("Message.Export.4"), 1);
            }
         } catch (ProcessingException var5) {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.6"), var5.getMessage()), VNAMessages.getString("Message.Export.5"), 0);
         }
      } else {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.Export.8"), VNAMessages.getString("Message.Export.5"), 0);
      }

      TraceHelper.exit(this, "doExportJPGFileInternal");
      return filename;
   }

   private void doExportPDF(ActionEvent e) {
      TraceHelper.entry(this, "doExportPDF");
      boolean autoOpen = (e.getModifiers() & 1) != 0;
      boolean autoEdit = (e.getModifiers() & 2) != 0;
      boolean goOn = true;
      if (autoEdit) {
         VNAExportCommentDialog dlg = new VNAExportCommentDialog(this.mainFrame);
         goOn = !dlg.isDialogCancelled();
      }

      if (goOn) {
         String filename = this.doExportJPGFileInternal(new PDFExporter(this.mainFrame), !autoOpen);
         if (autoOpen && filename != null) {
            try {
               File file = new File(filename);
               Desktop.getDesktop().open(file);
            } catch (IOException var7) {
               ErrorLogHelper.exception(this, "doExportPDF", var7);
            }
         }
      }

      TraceHelper.exit(this, "doExportPDF");
   }

   private void doExportSnP() {
      TraceHelper.entry(this, "doExportSnP");
      this.doExportJPGFileInternal(new SnPExporter(this.mainFrame), true);
      TraceHelper.exit(this, "doExportSnP");
   }

   private void doExportXLS(ActionEvent e) {
      TraceHelper.entry(this, "doExportXLS");
      boolean autoOpen = (e.getModifiers() & 1) != 0;
      String filename = this.doExportJPGFileInternal(new XLSExporter(this.mainFrame), !autoOpen);
      if (autoOpen && filename != null) {
         try {
            File file = new File(filename);
            Desktop.getDesktop().open(file);
         } catch (IOException var5) {
            ErrorLogHelper.exception(this, "doExportPDF", var5);
         }
      }

      TraceHelper.exit(this, "doExportXLS");
   }

   private void doExportXML() {
      TraceHelper.entry(this, "doExportXML");
      if (this.datapool.getCalibratedData() != null) {
         (new VNARawHandler(this.mainFrame.getJFrame())).exportMainDiagram();
      } else {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Message.Export.8"), VNAMessages.getString("Message.Export.5"), 0);
      }

      TraceHelper.exit(this, "doExportXML");
   }

   private void doExportZPlots() {
      TraceHelper.entry(this, "doExportZPlots");
      this.doExportJPGFileInternal(new ZPlotsExporter(this.mainFrame), true);
      TraceHelper.exit(this, "doExportZPlots");
   }

   private void doFrequencyCalibration() {
      TraceHelper.entry(this, "doFrequencyCalibration");
      new VNAFrequencyCalibrationDialog(this.mainFrame, this.datapool.getDriver());
      TraceHelper.exit(this, "doFrequencyCalibration");
   }

   private void doLanguageConfig() {
      TraceHelper.entry(this, "doLanguaageConfig");
      new VNAConfigLanguageDialog(this.mainFrame.getJFrame());
      TraceHelper.exit(this, "doLanguaageConfig");
   }

   private void doMultiScan() {
      TraceHelper.entry(this, "doMultiScan");
      new VNAMultiScanWindow(this.mainFrame.getJFrame(), this.mainFrame, this.mainFrame.getDiagramPanel().getScaleLeft());
      TraceHelper.exit(this, "doMultiScan");
   }

   private void doScheduler() {
      TraceHelper.entry(this, "doScheduler");
      new VNASchedulerDialog(this.mainFrame.getJFrame(), this.mainFrame);
      TraceHelper.exit(this, "doScheduler");
   }

   private void doShowDriverInfo() {
      TraceHelper.entry(this, "doShowDriverInfo");
      this.datapool.getDriver().showDriverDialog(this.mainFrame);
      TraceHelper.exit(this, "doShowDriverInfo");
   }

   private void doShowGeneratorDialog() {
      TraceHelper.entry(this, "doShowGeneratorDialog");
      IVNADriver drv = null;

      try {
         drv = VNADriverFactory.getSingleton().getDriverForType(this.datapool.getDriver().getDeviceInfoBlock().getType());
         drv.showGeneratorDialog(this.mainFrame);
      } catch (ProcessingException var3) {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("NoGeneratorAvailable.2"), VNAMessages.getString("VNAFrequencyCalibrationDialog.title"), 0);
      }

      TraceHelper.exit(this, "doShowGeneratorDialog");
   }

   private void doShowPadCalcDialog() {
      TraceHelper.entry(this, "doShowPadCalcDialog");
      new VNAPadCalculatorDialog(this.mainFrame.getJFrame());
      TraceHelper.exit(this, "doShowPadCalcDialog");
   }

   private void doShowS2PCollector() {
      TraceHelper.entry(this, "doShowS2PCollector");
      new VNASCollectorDialog();
      TraceHelper.exit(this, "doShowS2PCollector");
   }

   private void doUploadFirmware() {
      IVNADriver driver = this.datapool.getDriver();
      if (!(driver instanceof IVNAFlashableDevice)) {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), VNAMessages.getString("Firmware.Update.NotPossible.2"), VNAMessages.getString("Firmware.Update.NotPossible.1"), 0);
      } else {
         new VNAFirmwareUpdateDialog(this.mainFrame.getJFrame());
      }

   }

   public JFrame getMainFrame() {
      return this.mainFrame.getJFrame();
   }

   public VNAMenuBar getMenubar() {
      return this.menubar;
   }

   public VNAToolbar getToolbar() {
      return this.toolbar;
   }

   public void setMenubar(VNAMenuBar menubar) {
      this.menubar = menubar;
   }

   public void setToolbar(VNAToolbar toolbar) {
      this.toolbar = toolbar;
   }
}
