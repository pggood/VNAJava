package krause.vna.device;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import org.apache.commons.math3.complex.Complex;

public abstract class VNADeviceInfoBlock implements Serializable {
   public static final int FILTER_MODE1 = 1;
   public static final int FILTER_MODE2 = 2;
   public static final int FILTER_NONE = 0;
   public static final int DEFAULT_FILTERMODE = 1;
   public static final int DEFAULT_LOCAL_TIMEOUT = 1000;
   public static final double DEFAULT_REFERENCE_RESISTANCE_IMAG = 0.0D;
   public static final double DEFAULT_REFERENCE_RESISTANCE_REAL = 50.0D;
   public static final long ONE_KHZ = 1000L;
   public static final long ONE_MHZ = 1000000L;
   public static final long ONE_GHZ = 1000000000L;
   public static final String PROPERTIES_DDSTICKS = "ddsTicks";
   public static final String PROPERTIES_FILTERMODE = "filterMode";
   public static final String PROPERTIES_FREQUENCY_MAX = "freqMax";
   public static final String PROPERTIES_FREQUENCY_MIN = "freqMin";
   public static final String PROPERTIES_LOSS_MAX = "lossMax";
   public static final String PROPERTIES_LOSS_MIN = "lossMin";
   public static final String PROPERTIES_NUMBEROFSAMPLES4CALIB = "nOfSamples4Calibration";
   public static final String PROPERTIES_NUMBEROFOVERSCAN4CALIB = "nOfOversamples4Calibration";
   public static final String PROPERTIES_PEAKSUPPRESSION = "peakSuppression";
   public static final String PROPERTIES_REFERENCECHANNEL = "useReferenceChannel";
   public static final String PROPERTIES_REFERENCE_RESISTANCE_IMAG = "referenceImag";
   public static final String PROPERTIES_REFERENCE_RESISTANCE_REAL = "referenceReal";
   public static final String PROPERTIES_FIRMWARE_FILE_FILTER = "firmwareFileFilter";
   private long ddsTicksPerMHz;
   private int filterMode;
   private String longName;
   private long maxFrequency;
   private double maxLoss;
   private double maxPhase;
   private long minFrequency;
   private double minLoss;
   private double minPhase;
   private int numberOfOverscans4Calibration;
   private int numberOfSamples4Calibration;
   private boolean peakSuppression;
   private boolean referenceChannel;
   private Complex referenceResistance;
   private String firmwareFileFilter;
   private transient Map<VNAScanMode, VNAScanModeParameter> scanModeParameters = new HashMap();
   private String shortName;
   private String type;

   public void addScanModeParameter(VNAScanModeParameter pParm) {
      this.scanModeParameters.put(pParm.getMode(), pParm);
   }

   public void clearScanModeParameters() {
      this.scanModeParameters.clear();
   }

   public long getDdsTicksPerMHz() {
      return this.ddsTicksPerMHz;
   }

   public int getFilterMode() {
      return this.filterMode;
   }

   public String getLongName() {
      return this.longName;
   }

   public long getMaxFrequency() {
      return this.maxFrequency;
   }

   public double getMaxLoss() {
      return this.maxLoss;
   }

   public double getMaxPhase() {
      return this.maxPhase;
   }

   public long getMinFrequency() {
      return this.minFrequency;
   }

   public double getMinLoss() {
      return this.minLoss;
   }

   public double getMinPhase() {
      return this.minPhase;
   }

   public int getNumberOfOverscans4Calibration() {
      return this.numberOfOverscans4Calibration;
   }

   public int getNumberOfSamples4Calibration() {
      return this.numberOfSamples4Calibration;
   }

   public Complex getReferenceResistance() {
      return this.referenceResistance;
   }

   public VNAScanModeParameter getScanModeParameterForMode(VNAScanMode pScanMode) {
      return (VNAScanModeParameter)this.scanModeParameters.get(pScanMode);
   }

   public Map<VNAScanMode, VNAScanModeParameter> getScanModeParameters() {
      return this.scanModeParameters;
   }

   public String getShortName() {
      return this.shortName;
   }

   public long[] getSwitchPoints() {
      return null;
   }

   public String getType() {
      return this.type;
   }

   public boolean isPeakSuppression() {
      return this.peakSuppression;
   }

   public void reset() {
      this.numberOfSamples4Calibration = 1000;
      this.numberOfOverscans4Calibration = 1;
      this.peakSuppression = false;
      this.ddsTicksPerMHz = 1000000L;
      this.filterMode = 1;
      this.minFrequency = 1000000L;
      this.maxFrequency = 1000000000L;
      this.minLoss = 0.0D;
      this.maxLoss = -90.0D;
      this.minPhase = 0.0D;
      this.maxPhase = 90.0D;
      this.firmwareFileFilter = "*.hex";
      this.referenceResistance = new Complex(50.0D, 0.0D);
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore", prefix);
      this.setDdsTicksPerMHz(config.getLong(prefix + "ddsTicks", this.getDdsTicksPerMHz()));
      this.setMinLoss(config.getDouble(prefix + "minLoss", this.getMinLoss()));
      this.setMaxLoss(config.getDouble(prefix + "maxLoss", this.getMaxLoss()));
      this.setMinPhase(config.getDouble(prefix + "minPhase", this.getMinPhase()));
      this.setMaxPhase(config.getDouble(prefix + "maxPhase", this.getMaxPhase()));
      this.setMinFrequency(config.getLong(prefix + "minFrequency", this.getMinFrequency()));
      this.setMaxFrequency(config.getLong(prefix + "maxFrequency", this.getMaxFrequency()));
      double real = config.getDouble(prefix + "referenceReal", 50.0D);
      double imag = config.getDouble(prefix + "referenceImag", 0.0D);
      this.setReferenceResistance(new Complex(real, imag));
      this.setNumberOfSamples4Calibration(config.getInteger(prefix + "nOfSamples4Calibration", this.getNumberOfSamples4Calibration()));
      this.setNumberOfOverscans4Calibration(config.getInteger(prefix + "nOfOversamples4Calibration", this.getNumberOfOverscans4Calibration()));
      this.setFilterMode(config.getInteger(prefix + "filterMode", this.getFilterMode()));
      this.setPeakSuppression(config.getBoolean(prefix + "peakSuppression", this.isPeakSuppression()));
      this.setReferenceChannel(config.getBoolean(prefix + "useReferenceChannel", this.hasReferenceChannel()));
      this.setFirmwareFileFilter(config.getProperty(prefix + "firmwareFileFilter", this.getFirmwareFileFilter()));
      TraceHelper.exit(this, "restore");
   }

   public void setDdsTicksPerMHz(long ddsTicksPerMHz) {
      this.ddsTicksPerMHz = ddsTicksPerMHz;
   }

   public void setFilterMode(int filterMode) {
      this.filterMode = filterMode;
   }

   public void setLongName(String longName) {
      this.longName = longName;
   }

   public void setMaxFrequency(long maxFrequency) {
      this.maxFrequency = maxFrequency;
   }

   public void setMaxLoss(double maxLoss) {
      this.maxLoss = maxLoss;
   }

   public void setMaxPhase(double maxPhase) {
      this.maxPhase = maxPhase;
   }

   public void setMinFrequency(long minFrequency) {
      this.minFrequency = minFrequency;
   }

   public void setMinLoss(double minLoss) {
      this.minLoss = minLoss;
   }

   public void setMinPhase(double minPhase) {
      this.minPhase = minPhase;
   }

   public void setNumberOfOverscans4Calibration(int numberOfOverscans4Calibration) {
      this.numberOfOverscans4Calibration = numberOfOverscans4Calibration;
   }

   public void setNumberOfSamples4Calibration(int numberOfSamples) {
      this.numberOfSamples4Calibration = numberOfSamples;
   }

   public void setPeakSuppression(boolean pPeakSuppression) {
      this.peakSuppression = pPeakSuppression;
   }

   public void setReferenceResistance(Complex referenceResistance) {
      this.referenceResistance = referenceResistance;
   }

   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      config.putLong(prefix + "ddsTicks", this.getDdsTicksPerMHz());
      config.putDouble(prefix + "minLoss", this.getMinLoss());
      config.putDouble(prefix + "maxLoss", this.getMaxLoss());
      config.putDouble(prefix + "minPhase", this.getMinPhase());
      config.putDouble(prefix + "maxPhase", this.getMaxPhase());
      config.putLong(prefix + "minFrequency", this.getMinFrequency());
      config.putLong(prefix + "maxFrequency", this.getMaxFrequency());
      config.putDouble(prefix + "referenceReal", this.getReferenceResistance().getReal());
      config.putDouble(prefix + "referenceImag", this.getReferenceResistance().getImaginary());
      config.putInteger(prefix + "nOfSamples4Calibration", this.getNumberOfSamples4Calibration());
      config.putInteger(prefix + "nOfOversamples4Calibration", this.getNumberOfOverscans4Calibration());
      config.putInteger(prefix + "filterMode", this.getFilterMode());
      config.putBoolean(prefix + "peakSuppression", this.isPeakSuppression());
      config.putBoolean(prefix + "useReferenceChannel", this.hasReferenceChannel());
      config.setProperty(prefix + "firmwareFileFilter", this.getFirmwareFileFilter());
      TraceHelper.exit(this, "store");
   }

   public String getFirmwareFileFilter() {
      return this.firmwareFileFilter;
   }

   public void setFirmwareFileFilter(String firmwareFileFilter) {
      this.firmwareFileFilter = firmwareFileFilter;
   }

   public boolean hasReferenceChannel() {
      return this.referenceChannel;
   }

   public void setReferenceChannel(boolean useReferenceChannel) {
      this.referenceChannel = useReferenceChannel;
   }

   public abstract int calculateRealBaudrate(int var1);
}
