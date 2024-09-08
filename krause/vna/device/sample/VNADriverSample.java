package krause.vna.device.sample;

import java.util.ArrayList;
import java.util.List;
import krause.common.TypedProperties;
import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAGenericDriver;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSample extends VNAGenericDriver {
   public VNADriverSample() {
      String methodName = "VNADriverSample";
      TraceHelper.entry(this, "VNADriverSample");
      this.setMathHelper(new VNADriverSampleMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSampleDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSample");
   }

   public long calculateInternalFrequencyValue(long frequency) {
      TraceHelper.entry(this, "calculateInternalFrequencyValue", "in=" + frequency);
      TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", frequency);
      return frequency;
   }

   public void destroy() {
      TraceHelper.entry(this, "destroy");
      TraceHelper.exit(this, "destroy");
   }

   public String getDeviceFirmwareInfo() {
      return "Sample Driver V1.0";
   }

   public Double getDeviceSupply() {
      return 5.0D;
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Sample.";
   }

   public List<String> getPortList() {
      List<String> rc = new ArrayList();
      rc.add("DummySamplePort");
      return rc;
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      VNACalibrationRange[] rc = null;
      long min = this.getDeviceInfoBlock().getMinFrequency();
      long max = this.getDeviceInfoBlock().getMaxFrequency();
      rc = new VNACalibrationRange[]{new VNACalibrationRange(min, max, 20000, 1)};
      return rc;
   }

   public void init() throws InitializationException {
      super.init();
      TraceHelper.entry(this, "init");
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "init");
   }

   public void init(TypedProperties vnaConfig) throws InitializationException {
      TraceHelper.entry(this, "init");
      TraceHelper.exit(this, "init");
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      return true;
   }

   public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) {
      TraceHelper.entry(this, "scan");
      VNASampleBlock rc = new VNASampleBlock();
      rc.setAnalyserType(this.getDeviceInfoBlock().getType());
      long freqSteps = (frequencyHigh - frequencyLow) / (long)samples;
      rc.setNumberOfSteps(samples);
      rc.setStartFrequency(frequencyLow);
      rc.setStopFrequency(frequencyHigh);
      rc.setScanMode(scanMode);
      rc.setMathHelper(this.getMathHelper());
      VNABaseSample[] rawSamples = new VNABaseSample[samples];
      double offset = Math.random();
      double factor = offset * (double)samples;

      for(int i = 0; i < samples; ++i) {
         if (i % 100 == 0) {
            listener.publishProgress((int)((double)i * 100.0D / (double)samples));
         }

         VNABaseSample sample = new VNABaseSample();
         sample.setFrequency(frequencyLow + freqSteps * (long)i);
         sample.setAngle((double)(512 + (int)(512.0D * Math.sin(offset + (double)i / factor) * Math.cos(offset + (double)i / factor))));
         sample.setLoss((double)(512 + (int)(512.0D * Math.cos(offset - (double)i / factor) * Math.sin(offset + (double)i / factor))));
         sample.setRss1(512 + (int)(237.0D * Math.cos(offset + (double)i / factor) * Math.sin(offset + (double)i / factor)));
         sample.setRss2(512 + (int)(148.0D * Math.cos(offset + (double)i / factor) * Math.sin(offset + (double)i / factor)));
         sample.setRss3(512 + (int)(347.0D * Math.cos(offset + (double)i / factor) * Math.sin(offset + (double)i / factor)));
         rawSamples[i] = sample;
      }

      rc.setSamples(rawSamples);
      TraceHelper.exit(this, "scan");
      return rc;
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSampleDialog dlg = new VNADriverSampleDialog(pMF, this);
      dlg.dispose();
      TraceHelper.exit(this, "showDriverDialog");
   }

   public void showDriverNetworkDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverNetworkDialog");
      OptionDialogHelper.showInfoDialog(pMF.getJFrame(), "VNADriverSerialBase.Network.1", "VNADriverSerialBase.Network.2");
      TraceHelper.exit(this, "showDriverNetworkDialog");
   }

   public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
      TraceHelper.entry(this, "showGeneratorDialog");
      throw new DialogNotImplementedException();
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      TraceHelper.entry(this, "startGenerator");
      TraceHelper.exit(this, "startGenerator");
   }

   public void stopGenerator() throws ProcessingException {
      TraceHelper.entry(this, "stopGenerator");
      TraceHelper.exit(this, "stopGenerator");
   }

   public boolean checkForDevicePresence(boolean viaSlowConnection) {
      return true;
   }
}
