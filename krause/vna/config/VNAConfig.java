package krause.vna.config;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Map.Entry;
import krause.common.TypedProperties;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import org.apache.commons.math3.complex.Complex;

public class VNAConfig extends TypedProperties {
   private static VNAConfig singleton = null;
   private boolean mac = false;
   private String propertiesFileName = null;
   private boolean windows = false;

   public static VNAConfig getSingleton() {
      if (singleton == null) {
         Class var0 = VNAConfig.class;
         synchronized(VNAConfig.class) {
            if (singleton == null) {
               try {
                  singleton = new VNAConfig();
               } catch (Exception var2) {
                  var2.printStackTrace();
               }
            }
         }
      }

      return singleton;
   }

   public static TypedProperties init(String name, Properties defaultProperties) {
      if (singleton == null) {
         Class var2 = VNAConfig.class;
         synchronized(VNAConfig.class) {
            if (singleton == null) {
               try {
                  singleton = new VNAConfig();
                  singleton.load(name, defaultProperties);
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
            }
         }
      }

      return singleton;
   }

   protected VNAConfig() {
      TraceHelper.entry(this, "VNAConfig");
      String os = System.getProperty("os.name").toLowerCase();
      this.mac = os.indexOf("mac") != -1;
      this.windows = os.indexOf("windows") != -1;
      String s = this.getVNAConfigDirectory();
      if (!(new File(s)).exists()) {
         System.out.println("INFO::Createing Config-Directory: " + s);
         (new File(s)).mkdirs();
      }

      s = this.getVNACalibrationDirectory();
      if (!(new File(s)).exists()) {
         System.out.println("INFO::Createing Calibration-Directory: " + s);
         (new File(s)).mkdirs();
      }

      s = this.getPresetsDirectory();
      if (!(new File(s)).exists()) {
         System.out.println("INFO::Createing Presets-Directory: " + s);
         (new File(s)).mkdirs();
      }

      s = this.getAutoExportDirectory();
      if (!(new File(s)).exists()) {
         System.out.println("INFO::Createing Autoexport-Directory: " + s);
         (new File(s)).mkdirs();
      }

      s = this.getExportDirectory();
      if (!(new File(s)).exists()) {
         System.out.println("INFO::Createing Export-Directory: " + s);
         (new File(s)).mkdirs();
      }

      s = this.getReferenceDirectory();
      if (!(new File(s)).exists()) {
         System.out.println("INFO::Createing Reference-Directory: " + s);
         (new File(s)).mkdirs();
      }

      TraceHelper.exit(this, "VNAConfig");
   }

   public String getAutoExportDirectory() {
      return this.getProperty("VNA.autoExportDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "export");
   }

   public String getAutoExportFilename() {
      return this.getProperty("VNA.autoExportFilename", "VNA_{0,date,yyMMdd}_{0,time,HHmmss}");
   }

   public int getAutoExportFormat() {
      return this.getInteger("VNA.autoExportFormat", 0);
   }

   public String getCalibrationExportDirectory() {
      return this.getProperty("VNA.calibrationExportDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "export");
   }

   public Color getColorBandmap() {
      return new Color(Integer.parseInt(this.getProperty("Color.Bandmap", Integer.toString(Color.DARK_GRAY.getRGB()))));
   }

   public Color getColorDiagram() {
      return new Color(Integer.parseInt(this.getProperty("Color.Diagram", Integer.toString(Color.BLACK.getRGB()))));
   }

   public Color getColorDiagramLines() {
      return new Color(Integer.parseInt(this.getProperty("Color.DiagramLines", Integer.toString(Color.LIGHT_GRAY.getRGB()))));
   }

   public Color getColorMarker(int i) {
      return new Color(Integer.parseInt(this.getProperty("Color.Marker." + i, Integer.toString(Color.WHITE.getRGB()))));
   }

   public Color getColorReference() {
      return new Color(Integer.parseInt(this.getProperty("Color.Reference", Integer.toString(Color.WHITE.getRGB()))));
   }

   public Color getColorScaleLeft() {
      return new Color(Integer.parseInt(this.getProperty("Color.ScaleLeft", Integer.toString(Color.GREEN.getRGB()))));
   }

   public Color getColorScaleRight() {
      return new Color(Integer.parseInt(this.getProperty("Color.ScaleRight", Integer.toString(Color.CYAN.getRGB()))));
   }

   public String getExportComment() {
      return this.getProperty("VNA.exportComment");
   }

   public String getExportDecimalSeparator() {
      return this.getProperty("VNA.exportDecimalSeparator", ".");
   }

   public int getExportDiagramHeight() {
      return this.getInteger("VNA.ExportDiagramHeight", 1024);
   }

   public int getExportDiagramWidth() {
      return this.getInteger("VNA.ExportDiagramWidth", 1280);
   }

   public String getExportDirectory() {
      return this.getProperty("VNA.exportDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "export");
   }

   public String getExportFilename() {
      return this.getProperty("VNA.exportFileName", "Export");
   }

   public String getExportTitle() {
      return this.getProperty("VNA.exportTitle");
   }

   public int getExportTitleFontSize() {
      return this.getInteger("VNA.exportTitleFontSize", 24);
   }

   public String getFlashFilename() {
      return this.getProperty("VNA.flashFilename", "");
   }

   public int getFontSizeTextMarker() {
      return this.getInteger("FontSizeTextMarkers", 15);
   }

   public long getGeneratorFrequency() {
      return this.getLong("GeneratorFrequency", 1000000L);
   }

   public String getInstallationDirectory() {
      return this.getProperty("VNA.installDirectory", System.getProperty("user.home"));
   }

   public String getLastRawComment() {
      return this.getProperty("VNA.lastRawComment", "");
   }

   public Locale getLocale() {
      Locale rc = null;
      String language = this.getProperty("selectedLanguage");
      String country = this.getProperty("selectedCountry");
      if (language != null && country != null) {
         rc = new Locale(language, country);
      }

      return rc;
   }

   public int getMarkerSize() {
      return this.getInteger("VNA.MarkerSize", 2);
   }

   public int getNumberOfOversample() {
      return this.getInteger("VNA.numberOfOversample", 1);
   }

   public int getNumberOfSamples() {
      return Integer.parseInt(this.getProperty("NumberOfSamples", "600"));
   }

   public String getPortName(IVNADriver driver) {
      return this.getProperty(driver.getDriverConfigPrefix() + "PortName");
   }

   public String getPresetsDirectory() {
      return this.getProperty("VNA.presetsDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "presets/.");
   }

   public String getPropertiesFileName() {
      return this.propertiesFileName;
   }

   public String getReferenceDirectory() {
      return this.getProperty("VNA.referenceDirectory", VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "reference/.");
   }

   public double getPortExtensionVf() {
      return this.getDouble("VNA.portExtensionVf", 1.0D);
   }

   public double getPortExtensionCableLength() {
      return this.getDouble("VNA.portExtensionCableLength", 0.0D);
   }

   public boolean isPortExtensionEnabled() {
      return this.getBoolean("VNA.portExtensionState", false);
   }

   public void setPortExtensionVf(double vf) {
      this.putDouble("VNA.portExtensionVf", vf);
   }

   public void setPortExtensionCableLength(double len) {
      this.putDouble("VNA.portExtensionCableLength", len);
   }

   public void setPortExtensionState(boolean state) {
      this.putBoolean("VNA.portExtensionState", state);
   }

   public Complex getSmithReference() {
      double i = this.getDouble("VNA.smithReferenceImaginary", 0.0D);
      double r = this.getDouble("VNA.smithReferenceReal", 50.0D);
      return new Complex(r, i);
   }

   public String getVNACalibrationDirectory() {
      return VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "calibration";
   }

   public String getVNAConfigDirectory() {
      return VNASystemConfig.getVNA_HOME_DIR() + System.getProperty("file.separator") + "config";
   }

   public String getCalibrationKitFilename() {
      return this.getVNAConfigDirectory() + System.getProperty("file.separator") + "calibrationKits.xml";
   }

   public String getGaussianFilterFileName() {
      return this.getVNAConfigDirectory() + System.getProperty("file.separator") + "Gaussian.txt";
   }

   public Properties getVNADeviceConfigSymbols(String prefix) {
      return PropertiesHelper.createProperties(this, prefix, true);
   }

   public String getVNADriverType() {
      return this.getProperty("VNA.type");
   }

   public boolean isAskOnExit() {
      return this.getBoolean("askOnExit", true);
   }

   public boolean isAutoscaleEnabled() {
      return this.getBoolean("AutoScale", true);
   }

   public boolean isControlPanelClose() {
      return this.getBoolean("controlClosesApp", false);
   }

   public boolean isExportOverwrite() {
      return this.getBoolean("exportOverwrite", true);
   }

   public boolean isExportRawData() {
      return this.getBoolean("exportRawData", false);
   }

   public boolean isMac() {
      return this.mac;
   }

   public boolean isMarkerModeLine() {
      return this.getBoolean("VNA.MarkerModeLine", false);
   }

   public boolean isApplyGaussianFilter() {
      return this.getBoolean("VNA.ApplyGaussianFilter", false);
   }

   public boolean isMarkerPanelClose() {
      return this.getBoolean("markerClosesApp", false);
   }

   public boolean isPrintFooter() {
      return this.getBoolean("PrintFooter", true);
   }

   public boolean isPrintMainLegend() {
      return this.getBoolean("PrintMainLegend", true);
   }

   public boolean isPrintMarkerDataHorizontal() {
      return this.getBoolean("PrintMarkerDataHorizontal", false);
   }

   public boolean isPrintMarkerDataInDiagramm() {
      return this.getBoolean("PrintMarkerDataInDiagramm", false);
   }

   public boolean isPrintSubLegend() {
      return this.getBoolean("PrintSubLegend", false);
   }

   public boolean isScanAfterTableSelect() {
      return this.getBoolean("scanAfterTableSelect", true);
   }

   public boolean isScanAfterZoom() {
      return this.getBoolean("scanAfterZoom", true);
   }

   public boolean isShowBandmap() {
      return this.getBoolean("VNA.ShowBandMap", false);
   }

   public boolean isTurnOffGenAfterScan() {
      return this.getBoolean("turnOffGenAfterScan", true);
   }

   public boolean isWindows() {
      return this.windows;
   }

   private void load(String name, Properties defaultProperties) {
      TraceHelper.entry(this, "load", name);
      this.propertiesFileName = this.getVNAConfigDirectory() + "/" + name;
      this.putAll(PropertiesHelper.loadXMLProperties(this.propertiesFileName, defaultProperties));
      Iterator var4 = System.getProperties().entrySet().iterator();

      while(var4.hasNext()) {
         Entry<Object, Object> entry = (Entry)var4.next();
         String key = (String)entry.getKey();
         if (!key.startsWith("java.") && !key.startsWith("awt.") && !key.startsWith("os.") && !key.startsWith("file.") && !key.startsWith("line.") && !key.startsWith("path.") && !key.startsWith("sun.") && !key.startsWith("user.")) {
            this.put(entry.getKey(), entry.getValue());
         }
      }

      TraceHelper.exit(this, "load");
   }

   public void save() {
      TraceHelper.entry(this, "save");
      PropertiesHelper.saveXMLProperties(this, this.propertiesFileName);
      TraceHelper.exit(this, "save");
   }

   public void setAskOnExit(boolean val) {
      this.putBoolean("askOnExit", val);
   }

   public void setAutoExportDirectory(String text) {
      this.setProperty("VNA.autoExportDirectory", text);
   }

   public void setAutoExportFilename(String text) {
      this.setProperty("VNA.autoExportFilename", text);
   }

   public void setAutoExportFormat(int val) {
      this.putInteger("VNA.autoExportFormat", val);
   }

   public void setAutoscaleEnabled(boolean val) {
      this.setProperty("AutoScale", Boolean.toString(val));
   }

   public void setCalibrationExportDirectory(String text) {
      this.setProperty("VNA.calibrationExportDirectory", text);
   }

   public void setColorBandmap(Color color) {
      this.setProperty("Color.Bandmap", Integer.toString(color.getRGB()));
   }

   public void setColorDiagram(Color color) {
      this.setProperty("Color.Diagram", Integer.toString(color.getRGB()));
   }

   public void setColorDiagramLines(Color color) {
      this.setProperty("Color.DiagramLines", Integer.toString(color.getRGB()));
   }

   public void setColorMarker(int i, Color color) {
      this.setProperty("Color.Marker." + i, Integer.toString(color.getRGB()));
   }

   public void setColorReference(Color color) {
      this.setProperty("Color.Reference", Integer.toString(color.getRGB()));
   }

   public void setColorScaleLeft(Color color) {
      this.setProperty("Color.ScaleLeft", Integer.toString(color.getRGB()));
   }

   public void setColorScaleRight(Color color) {
      this.setProperty("Color.ScaleRight", Integer.toString(color.getRGB()));
   }

   public void setControlPanelClose(boolean val) {
      this.putBoolean("controlClosesApp", val);
   }

   public void setExportComment(String comment) {
      this.setProperty("VNA.exportComment", comment);
   }

   public void setExportDecimalSeparator(String name) {
      this.setProperty("VNA.exportDecimalSeparator", name);
   }

   public void setExportDiagramHeight(int h) {
      this.putInteger("VNA.ExportDiagramHeight", h);
   }

   public void setExportDiagramWidth(int w) {
      this.putInteger("VNA.ExportDiagramWidth", w);
   }

   public void setExportDirectory(String name) {
      this.setProperty("VNA.exportDirectory", name);
   }

   public void setExportFilename(String name) {
      this.setProperty("VNA.exportFileName", name);
   }

   public void setExportOverwrite(boolean val) {
      this.setProperty("exportOverwrite", Boolean.toString(val));
   }

   public void setExportRawData(boolean val) {
      this.setProperty("exportRawData", Boolean.toString(val));
   }

   public void setExportTitle(String name) {
      this.setProperty("VNA.exportTitle", name);
   }

   public void setExportTitleFontSize(int size) {
      this.putInteger("VNA.exportTitleFontSize", size);
   }

   public void setFlashFilename(String name) {
      this.setProperty("VNA.flashFilename", name);
   }

   public void setFontSizeTextMarker(int s) {
      this.putInteger("FontSizeTextMarkers", s);
   }

   public void setGeneratorFrequency(long freq) {
      this.putLong("GeneratorFrequency", freq);
   }

   public void setInstallationDirectory(String name) {
      this.setProperty("VNA.installDirectory", name);
   }

   public void setLastRawComment(String comment) {
      this.setProperty("VNA.lastRawComment", comment);
   }

   public void setLocale(Locale loc) {
      TraceHelper.entry(this, "setLocale");
      if (loc == null) {
         this.remove("selectedLanguage");
         this.remove("selectedCountry");
      } else {
         this.setProperty("selectedLanguage", loc.getLanguage());
         this.setProperty("selectedCountry", loc.getCountry());
      }

      TraceHelper.exit(this, "setLocale");
   }

   public void setMarkerModeLine(boolean mode) {
      this.putBoolean("VNA.MarkerModeLine", mode);
   }

   public void setApplyGaussianFilter(boolean gauss) {
      this.putBoolean("VNA.ApplyGaussianFilter", gauss);
   }

   public void setMarkerPanelClose(boolean val) {
      this.putBoolean("markerClosesApp", val);
   }

   public void setMarkerSize(int size) {
      this.putInteger("VNA.MarkerSize", size);
   }

   public void setNumberOfOversample(int num) {
      this.putInteger("VNA.numberOfOversample", num);
   }

   public void setNumberOfSamples(int numberOfSamples) {
      this.setProperty("NumberOfSamples", Integer.toString(numberOfSamples));
   }

   public void setPortName(IVNADriver driver, String port) {
      this.setProperty(driver.getDriverConfigPrefix() + "PortName", port);
   }

   public void setPresetsDirectory(String name) {
      this.setProperty("VNA.presetsDirectory", name);
   }

   public void setPrintFooter(boolean val) {
      this.putBoolean("PrintFooter", val);
   }

   public void setPrintMainLegend(boolean val) {
      this.putBoolean("PrintMainLegend", val);
   }

   public void setPrintMarkerDataHorizontal(boolean val) {
      this.putBoolean("PrintMarkerDataHorizontal", val);
   }

   public void setPrintMarkerDataInDiagramm(boolean val) {
      this.putBoolean("PrintMarkerDataInDiagramm", val);
   }

   public void setPrintSubLegend(boolean val) {
      this.putBoolean("PrintSubLegend", val);
   }

   public void setReferenceDirectory(String name) {
      this.setProperty("VNA.referenceDirectory", name);
   }

   public void setScanAfterTableSelect(boolean val) {
      this.putBoolean("scanAfterTableSelect", val);
   }

   public void setScanAfterZoom(boolean val) {
      this.putBoolean("scanAfterZoom", val);
   }

   public void setShowBandmap(boolean val) {
      this.putBoolean("VNA.ShowBandMap", val);
   }

   public void setSmithReference(Complex val) {
      this.putDouble("VNA.smithReferenceImaginary", val.getImaginary());
      this.putDouble("VNA.smithReferenceReal", val.getReal());
   }

   public void setTurnOffGenAfterScan(boolean val) {
      this.setProperty("turnOffGenAfterScan", Boolean.toString(val));
   }

   public void setVNADriverType(String typ) {
      this.setProperty("VNA.type", typ);
   }

   public void setPhosphor(boolean val) {
      this.putBoolean("VNA.Phosphor", val);
   }

   public void setResizeLocked(boolean val) {
      this.putBoolean("VNA.ResizeLocked", val);
   }

   public boolean isPhosphor() {
      return this.getBoolean("VNA.Phosphor", false);
   }

   public boolean isResizeLocked() {
      return this.getBoolean("VNA.ResizeLocked", false);
   }

   public void setAverage(int value) {
      this.putInteger("VNA.Average", value);
   }

   public int getAverage() {
      return this.getInteger("VNA.Average", 1);
   }

   public void setScanSpeed(int value) {
      this.putInteger("VNA.ScanSpeed", value);
   }

   public int getScanSpeed() {
      return this.getInteger("VNA.ScanSpeed", 1);
   }

   public void setThemeID(int id) {
      String methodName = "setThemeID";
      TraceHelper.entry(this, "setThemeID", "id=%d", id);
      this.putInteger("VNA.Theme", id);
      TraceHelper.exit(this, "setThemeID");
   }

   public int getThemeID() {
      String methodName = "getThemeID";
      TraceHelper.entry(this, "getThemeID");
      int rc = this.getInteger("VNA.Theme", 0);
      TraceHelper.exitWithRC(this, "getThemeID", "id=%d", rc);
      return rc;
   }

   public String getCurrentCalSetID() {
      return this.getProperty("VNA.CurrentCalSet", (String)null);
   }

   public void setCurrentCalSetID(String id) {
      this.setProperty("VNA.CurrentCalSet", id);
   }

   public String getSmithChartConfigFilename() {
      return this.getVNAConfigDirectory() + System.getProperty("file.separator") + "SmithChartCircles.txt";
   }
}
