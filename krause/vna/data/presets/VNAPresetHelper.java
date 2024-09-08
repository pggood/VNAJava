package krause.vna.data.presets;

import java.awt.Graphics;
import java.io.File;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import krause.common.TypedProperties;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.data.VNAScanModeComboBox;
import krause.vna.gui.preset.VNAPresetSaveDialog;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;
import org.jfree.ui.ExtensionFileFilter;

public class VNAPresetHelper {
   private static VNAConfig config = VNAConfig.getSingleton();
   private VNAMainFrame mainFrame;

   public VNAPresetHelper(VNAMainFrame pMF) {
      TraceHelper.exit(this, "VNAPresetHelper");
      this.mainFrame = pMF;
      TraceHelper.exit(this, "VNAPresetHelper");
   }

   public void processScanMode(TypedProperties properties) {
      TraceHelper.exit(this, "processScanMode");
      VNAScanMode tsm = new VNAScanMode();
      tsm.restoreFromProperties(properties);
      if (tsm.getMode() != -1) {
         VNAScanModeComboBox cbm = this.mainFrame.getDataPanel().getCbMode();
         cbm.setSelectedMode(tsm);
         this.mainFrame.getApplicationState().evtScanModeChanged();
      }

      TraceHelper.exit(this, "processScanMode");
   }

   public void processFrequencyRange(TypedProperties properties) {
      TraceHelper.entry(this, "processFrequencyRange");
      VNAFrequencyRange fr = new VNAFrequencyRange();
      fr.restoreFromProperties(properties);
      if (fr.isValid()) {
         this.mainFrame.getDataPanel().changeFrequencyRange(fr);
      }

      TraceHelper.exit(this, "processFrequencyRange");
   }

   public void processScales(TypedProperties properties) {
      TraceHelper.entry(this, "processScales");
      Iterator var3 = VNAScaleSymbols.MAP_SCALE_TYPES.values().iterator();

      while(var3.hasNext()) {
         VNAGenericScale currScale = (VNAGenericScale)var3.next();
         if (currScale.supportsCustomScaling()) {
            VNAGenericScale genScale = new VNAGenericScale((String)null, (String)null, currScale.getType(), (String)null, (NumberFormat)null, 0.0D, 0.0D) {
               public void paintScale(int width, int height, Graphics g) {
               }

               public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
               }

               public int getScaledSampleValue(VNACalibratedSample sample, int height) {
                  return 0;
               }

               public int getScaledSampleValue(double value, int height) {
                  return 0;
               }
            };
            genScale.restoreFromProperties(properties);
            if (genScale.getCurrentMaxValue() != Double.MAX_VALUE && genScale.getCurrentMinValue() != Double.MIN_VALUE) {
               currScale.setCurrentMinMaxValue(genScale.getCurrentMinMaxValue());
               currScale.rescale();
            }
         }
      }

      this.mainFrame.getDiagramPanel().getScaleSelectPanel().disableAutoScale();
      TraceHelper.exit(this, "processScales");
   }

   public void doLoadPresets() {
      TraceHelper.entry(this, "doLoadPresets");
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new ExtensionFileFilter("vna/J preset files(*.preset)", ".preset"));
      fc.setSelectedFile(new File(config.getPresetsDirectory() + "/."));
      int returnVal = fc.showOpenDialog(this.mainFrame.getJFrame());
      if (returnVal == 0) {
         File file = fc.getSelectedFile();

         try {
            Properties props = PropertiesHelper.loadXMLProperties(file.getAbsolutePath(), (Properties)null);
            TypedProperties tProps = new TypedProperties();
            tProps.putAll(props);
            this.processMarkers(tProps);
            this.processScanMode(tProps);
            this.processFrequencyRange(tProps);
            this.processScales(tProps);
         } catch (Exception var6) {
            JOptionPane.showMessageDialog(this.mainFrame.getJFrame(), var6.getMessage(), VNAMessages.getString("Message.Export.2"), 0);
            ErrorLogHelper.exception(this, "doLoadPresets", var6);
         }
      }

      TraceHelper.exit(this, "doLoadPresets");
   }

   private void processMarkers(TypedProperties tProps) {
      TraceHelper.entry(this, "processMarkers");
      long freq1 = tProps.getLong("Marker1.frq", 0L);
      if (freq1 != 0L) {
         this.mainFrame.getMarkerPanel().getMarker(0).moveMarkerToFrequency(freq1);
      }

      long freq2 = tProps.getLong("Marker2.frq", 0L);
      if (freq2 != 0L) {
         this.mainFrame.getMarkerPanel().getMarker(1).moveMarkerToFrequency(freq2);
      }

      long freq3 = tProps.getLong("Marker3.frq", 0L);
      if (freq3 != 0L) {
         this.mainFrame.getMarkerPanel().getMarker(2).moveMarkerToFrequency(freq3);
      }

      long freq4 = tProps.getLong("Marker4.frq", 0L);
      if (freq4 != 0L) {
         this.mainFrame.getMarkerPanel().getMarker(3).moveMarkerToFrequency(freq4);
      }

      TraceHelper.exit(this, "processMarkers");
   }

   public void doSavePresets() {
      TraceHelper.entry(this, "doSavePresets");
      new VNAPresetSaveDialog(this.mainFrame);
      TraceHelper.exit(this, "doSavePresets");
   }
}
