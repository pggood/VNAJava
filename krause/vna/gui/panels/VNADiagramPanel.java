package krause.vna.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.export.CSVExporter;
import krause.vna.export.JpegExporter;
import krause.vna.export.PDFExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.XLSRawExporter;
import krause.vna.export.XMLExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.scale.VNAFrequencyScale;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNADiagramPanel extends JPanel implements IVNADataConsumer, VNAApplicationStateObserver {
   private transient VNAMainFrame mainFrame;
   private VNAFrequencyScale frequencyScale = null;
   private VNAMeasurementScale scaleLeft = null;
   private VNAMeasurementScale scaleRight = null;
   private VNAImagePanel imagePanel = null;
   private VNAScaleSelectPanel scaleSelectPanel = null;
   private VNAConfig config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();

   public VNADiagramPanel(VNAMainFrame pMainFrame) {
      this.setBorder(new EtchedBorder(1, (Color)null, (Color)null));
      TraceHelper.entry(this, "VNADiagramPanel");
      this.mainFrame = pMainFrame;
      this.setLayout(new BorderLayout());
      this.imagePanel = new VNAImagePanel(this.mainFrame);
      this.add(this.imagePanel, "Center");
      this.scaleLeft = new VNAMeasurementScale((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS), true, this.mainFrame.getJFrame());
      this.add(this.scaleLeft, "West");
      this.scaleRight = new VNAMeasurementScale((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE), false, this.mainFrame.getJFrame());
      this.add(this.scaleRight, "East");
      this.frequencyScale = new VNAFrequencyScale(this.scaleLeft, this.scaleRight);
      this.add(this.frequencyScale, "Last");
      this.scaleSelectPanel = new VNAScaleSelectPanel(this.mainFrame, this.scaleLeft, this.scaleRight);
      this.add(this.scaleSelectPanel, "First");
      TraceHelper.exit(this, "VNADiagramPanel");
   }

   public void setupColors() {
      this.scaleSelectPanel.setupColors();
      this.scaleLeft.setupColors();
      this.scaleRight.setupColors();
      this.imagePanel.repaint();
   }

   public VNAFrequencyScale getScaleFrequency() {
      return this.frequencyScale;
   }

   public VNAMeasurementScale getScaleLeft() {
      return this.scaleLeft;
   }

   public VNAMeasurementScale getScaleRight() {
      return this.scaleRight;
   }

   public VNAImagePanel getImagePanel() {
      return this.imagePanel;
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      String method = "consumeDataBlock";
      TraceHelper.entry(this, "consumeDataBlock");
      if (jobs != null) {
         VNABackgroundJob job = (VNABackgroundJob)jobs.get(0);
         if (job != null) {
            List<VNASampleBlock> rawBlocks = this.datapool.getRawDataBlocks();
            TraceHelper.text(this, "consumeDataBlock", "previously %d raw block(s) in datapool", rawBlocks.size());
            VNASampleBlock result = job.getResult();
            VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
            if (result == null) {
               ErrorLogHelper.text(this, "consumeDataBlock", "result in job is empty");
               TraceHelper.exit(this, "consumeDataBlock");
               return;
            }

            if (dib.isPeakSuppression()) {
               VNASampleBlockHelper.removeSwitchPoints(result, dib.getSwitchPoints());
            }

            if (!rawBlocks.isEmpty() && ((VNASampleBlock)rawBlocks.get(0)).getNumberOfSteps() != result.getNumberOfSteps()) {
               rawBlocks.clear();
            }

            rawBlocks.add(result);

            while(rawBlocks.size() > job.getAverage()) {
               rawBlocks.remove(0);
               TraceHelper.text(this, "consumeDataBlock", "removed first element from buffer to match average size");
            }

            if (rawBlocks.size() < job.getAverage()) {
               while(rawBlocks.size() < job.getAverage()) {
                  rawBlocks.add(result);
                  TraceHelper.text(this, "consumeDataBlock", "added block to end of buffer to fill up average buffer");
               }
            }

            TraceHelper.text(this, "consumeDataBlock", "now %d raw block(s) in datapool", rawBlocks.size());
            VNASampleBlock data = VNASampleBlockHelper.calculateAverageSampleBlock(rawBlocks);
            this.datapool.setRawData(data);
            this.processRawData();
         } else {
            ErrorLogHelper.text(this, "consumeDataBlock", "job is empty");
         }
      } else {
         ErrorLogHelper.text(this, "consumeDataBlock", "no jobs returned");
      }

      this.repaint();
      TraceHelper.exit(this, "consumeDataBlock");
   }

   public void processRawData() {
      TraceHelper.entry(this, "processRawData");
      VNASampleBlock data = this.datapool.getRawData();
      if (data != null) {
         if (this.config.isExportRawData()) {
            XLSRawExporter.export(data, "RawData");
         }

         IVNADriverMathHelper mathHelper = data.getMathHelper();
         if (mathHelper != null) {
            mathHelper.applyFilter(data.getSamples());
            VNACalibrationBlock calBlock = this.datapool.getResizedCalibrationBlock();
            if (calBlock != null) {
               VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(calBlock);
               context.setConversionTemperature(data.getDeviceTemperature());
               VNACalibratedSampleBlock calSamples = mathHelper.createCalibratedSamples(context, data);
               this.datapool.setCalibratedData(calSamples);
               if (this.config.isExportRawData()) {
                  XLSRawExporter.export(calBlock.getCalibrationPoints(), "CalibrationData");
                  XLSRawExporter.export(calSamples.getCalibratedSamples(), "calibratedSamples");
               }

               this.updateMarkerPanel();
               if (this.config.isAutoscaleEnabled()) {
                  this.rescaleScalesToData();
               }

               this.handleAutoExport();
               this.showMemoryUsage();
               if (this.scaleSelectPanel != null && this.scaleSelectPanel.getSmithDialog() != null) {
                  this.scaleSelectPanel.getSmithDialog().consumeCalibratedData(this.datapool.getCalibratedData());
               }
            }
         }
      }

      this.repaint();
      TraceHelper.exit(this, "processRawData");
   }

   private void showMemoryUsage() {
      long total = Runtime.getRuntime().totalMemory();
      long free = Runtime.getRuntime().freeMemory();
      String msg = VNAFormatFactory.formatMemoryInMiB(free) + "MiB/" + VNAFormatFactory.formatMemoryInMiB(total) + "MiB";
      this.scaleSelectPanel.getLabelDebug().setText(msg);
   }

   private void handleAutoExport() {
      TraceHelper.entry(this, "handleAutoExport");
      String filename = null;

      try {
         if (this.config.getAutoExportFormat() == 1) {
            filename = this.internalAutoExport(new CSVExporter(this.mainFrame));
         }

         if (this.config.getAutoExportFormat() == 2) {
            filename = this.internalAutoExport(new JpegExporter(this.mainFrame));
         }

         if (this.config.getAutoExportFormat() == 3) {
            filename = this.internalAutoExport(new PDFExporter(this.mainFrame));
         }

         if (this.config.getAutoExportFormat() == 4) {
            filename = this.internalAutoExport(new SnPExporter(this.mainFrame));
         }

         if (this.config.getAutoExportFormat() == 5) {
            filename = this.internalAutoExport(new XLSExporter(this.mainFrame));
         }

         if (this.config.getAutoExportFormat() == 6) {
            filename = this.internalAutoExport(new XMLExporter(this.mainFrame));
         }

         if (this.config.getAutoExportFormat() == 7) {
            filename = this.internalAutoExport(new ZPlotsExporter(this.mainFrame));
         }
      } catch (ProcessingException var3) {
         JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.5"), var3.getMessage()), VNAMessages.getString("Message.Export.6"), 0);
      }

      TraceHelper.exitWithRC(this, "handleAutoExport", filename);
   }

   private String internalAutoExport(VNAExporter exporter) throws ProcessingException {
      TraceHelper.entry(this, "internalAutoExport");
      String fnp = this.config.getAutoExportDirectory() + System.getProperty("file.separator") + this.config.getAutoExportFilename();
      String filename = exporter.export(fnp, true);
      TraceHelper.exit(this, "internalAutoExport");
      return filename;
   }

   private void updateMarkerPanel() {
      TraceHelper.entry(this, "updateMarkerPanel");
      VNACalibratedSampleBlock cd = this.datapool.getCalibratedData();
      VNAMarker[] var5;
      int var4 = (var5 = this.mainFrame.getMarkerPanel().getMarkers()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         VNAMarker mark = var5[var3];
         if (mark.isVisible()) {
            mark.moveMarkerToData(cd);
         }
      }

      TraceHelper.exit(this, "updateMarkerPanel");
   }

   public void rescaleScalesToData() {
      TraceHelper.entry(this, "rescaleScales");
      VNACalibratedSampleBlock currentData = this.datapool.getCalibratedData();
      if (currentData != null) {
         HashMap<VNAScaleSymbols.SCALE_TYPE, VNAGenericScale> mst = VNAScaleSymbols.MAP_SCALE_TYPES;
         Iterator var4 = mst.values().iterator();

         while(var4.hasNext()) {
            VNAGenericScale aScale = (VNAGenericScale)var4.next();
            if (aScale.getType() != VNAScaleSymbols.SCALE_TYPE.SCALE_NONE) {
               aScale.setCurrentMinMaxValue(currentData.getMinMaxPair(aScale.getType()));
               aScale.rescale();
            }
         }
      }

      TraceHelper.exit(this, "rescaleScales");
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      if (newState == VNAApplicationState.INNERSTATE.DRIVERLOADED) {
         if (oldState == VNAApplicationState.INNERSTATE.DRIVERLOADED || oldState == VNAApplicationState.INNERSTATE.GUIINITIALIZED) {
            VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
            Iterator var5 = VNAScaleSymbols.MAP_SCALE_TYPES.values().iterator();

            while(var5.hasNext()) {
               VNAGenericScale scale = (VNAGenericScale)var5.next();
               scale.initScaleFromConfigOrDib(dib, this.config);
            }

            this.repaint();
         }

         if (oldState == VNAApplicationState.INNERSTATE.CALIBRATED) {
            this.datapool.setCalibratedData(new VNACalibratedSampleBlock(0));
            this.repaint();
         }
      } else if (newState != VNAApplicationState.INNERSTATE.CALIBRATED) {
         VNAApplicationState.INNERSTATE var10000 = VNAApplicationState.INNERSTATE.RUNNING;
      }

      this.scaleSelectPanel.changeState(oldState, newState);
      this.imagePanel.changeState(oldState, newState);
   }

   public VNAScaleSelectPanel getScaleSelectPanel() {
      return this.scaleSelectPanel;
   }

   public void setScaleSelectPanel(VNAScaleSelectPanel scaleSelectPanel) {
      this.scaleSelectPanel = scaleSelectPanel;
   }

   public void clearScanData() {
      this.datapool.setRawData((VNASampleBlock)null);
      this.datapool.setCalibratedData((VNACalibratedSampleBlock)null);
      this.repaint();
   }
}
