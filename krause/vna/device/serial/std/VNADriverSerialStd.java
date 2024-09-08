package krause.vna.device.serial.std;

import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.std.gui.VNADriverSerialStdDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialStd extends VNADriverSerialBase {
   static final int NUM_BYTES_PER_SAMPLE = 4;

   public VNADriverSerialStd() {
      String methodName = "VNADriverSerialStd";
      TraceHelper.entry(this, "VNADriverSerialStd");
      this.setMathHelper(new VNADriverSerialStdMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialStdDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialStd");
   }

   public void init() throws InitializationException {
      String methodeName = "init";
      TraceHelper.entry(this, "init");
      super.init();
      VNADriverSerialStdDIB dib = (VNADriverSerialStdDIB)this.getDeviceInfoBlock();
      dib.restore(this.config, this.getDriverConfigPrefix());

      try {
         CommPortIdentifier portId = this.getPortIDForName(this.getPortname());
         if (portId == null) {
            InitializationException e = new InitializationException("Port [" + this.getPortname() + "] not found");
            ErrorLogHelper.exception(this, "init", e);
            throw e;
         }

         this.setPort((SerialPort)portId.open(this.getAppname(), dib.getOpenTimeout()));
         this.getPort().setFlowControlMode(0);
         this.getPort().setSerialPortParams(dib.getBaudrate(), 8, 2, 0);
         this.getPort().enableReceiveTimeout(dib.getReadTimeout());
         this.getPort().setInputBufferSize(65536);
      } catch (PortInUseException | UnsupportedCommOperationException var5) {
         ErrorLogHelper.exception(this, "init", var5);
         throw new InitializationException(var5);
      }

      TraceHelper.exit(this, "init");
   }

   private VNABaseSample[] receiveRawMessage(long frequency, int pNumSamples, long frequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "receiveRawMessage";
      TraceHelper.entry(this, "receiveRawMessage", "fStart=" + frequency + " #=" + pNumSamples + " fStep=" + frequencyStep);
      VNABaseSample[] rc = new VNABaseSample[pNumSamples];
      if (pListener != null) {
         pListener.publishProgress(0);
      }

      byte[] buffer = this.receiveBytestream(4 * pNumSamples, pListener);
      long localFrequency = frequency;

      for(int i = 0; i < pNumSamples; ++i) {
         int offset = i * 4;
         VNABaseSample tempSample = new VNABaseSample();
         tempSample.setAngle((double)(buffer[offset] & 255) + 256.0D * (double)(buffer[offset + 1] & 255));
         tempSample.setLoss((double)(buffer[offset + 2] & 255) + 256.0D * (double)(buffer[offset + 3] & 255));
         tempSample.setFrequency(localFrequency);
         rc[i] = tempSample;
         localFrequency += frequencyStep;
      }

      if (pListener != null) {
         pListener.publishProgress(100);
      }

      TraceHelper.exit(this, "receiveRawMessage");
      return rc;
   }

   public void destroy() {
      String methodName = "destroy";
      TraceHelper.entry(this, "destroy");
      if (this.getPort() != null) {
         TraceHelper.text(this, "destroy", "closing " + this.getPort().getName());
         this.getPort().close();
         this.setPort((SerialPort)null);
         TraceHelper.text(this, "destroy", "port closed");
      }

      TraceHelper.exit(this, "destroy");
   }

   protected void sendFrequency(long frq) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)frq / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      TraceHelper.text(this, "sendFrequency", msg);
      this.sendAsAsciiString(msg);
   }

   public VNASampleBlock scan(VNAScanMode pScanMode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan");
      long frequencyStep = (frequencyHigh - frequencyLow) / (long)samples;
      VNASampleBlock rc = new VNASampleBlock();
      rc.setAnalyserType(this.getDeviceInfoBlock().getType());
      rc.setScanMode(pScanMode);
      rc.setNumberOfSteps(samples);
      rc.setStartFrequency(frequencyLow);
      rc.setStopFrequency(frequencyHigh);
      rc.setMathHelper(this.getMathHelper());
      if (this.getPort() != null) {
         this.flushInputStream();
         if (pScanMode.isTransmissionMode()) {
            this.sendAsAsciiString("1");
         } else {
            if (!pScanMode.isReflectionMode()) {
               throw new ProcessingException("Unsupported scan mode " + pScanMode);
            }

            this.sendAsAsciiString("0");
         }

         this.sendFrequency(frequencyLow);
         this.sendAsAsciiString(Integer.toString(samples));
         this.sendFrequency(frequencyStep);
         rc.setSamples(this.receiveRawMessage(frequencyLow, samples, frequencyStep, listener));
         this.stopGenerator();
      }

      TraceHelper.exit(this, "scan");
      return rc;
   }

   public void stopGenerator() throws ProcessingException {
      String methodName = "stopGenerator";
      TraceHelper.entry(this, "stopGenerator");
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("0");
         this.sendFrequency(0L);
         this.sendAsAsciiString("1");
         this.sendAsAsciiString("0");
         this.receiveRawMessage(0L, 1, 1000L, (IVNABackgroundTaskStatusListener)null);
         VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "stopGenerator");
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialStdDialog dlg = new VNADriverSerialStdDialog(pMF, this);
      dlg.dispose();
      TraceHelper.exit(this, "showDriverDialog");
   }

   public long calculateInternalFrequencyValue(long frequency) {
      TraceHelper.entry(this, "calculateInternalFrequencyValue", "In=" + frequency);
      long rc = (long)((double)frequency / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", rc);
      return rc;
   }

   public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
      TraceHelper.entry(this, "showGeneratorDialog");
      new VNAGeneratorDialog(pMF, this);
      TraceHelper.exit(this, "showGeneratorDialog");
   }

   public String getDeviceFirmwareInfo() {
      return "V1.0";
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      String methodName = "startGenerator";
      TraceHelper.entry(this, "startGenerator", "f=%d", frequencyI);
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("0");
         this.sendFrequency(frequencyI);
         this.sendAsAsciiString("1");
         this.sendAsAsciiString("0");
         this.receiveRawMessage(0L, 1, 1L, (IVNABackgroundTaskStatusListener)null);
         VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "startGenerator");
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.Std.";
   }

   public Double getDeviceSupply() {
      return 5.0D;
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      return true;
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), this.getDeviceInfoBlock().getMaxFrequency(), 30000, 1)};
   }

   public boolean checkForDevicePresence(boolean viaSlowConnection) {
      boolean rc = false;
      String methodName = "checkForDevicePresence";
      TraceHelper.entry(this, "checkForDevicePresence");

      try {
         VNADeviceInfoBlock dib = this.getDeviceInfoBlock();
         this.init();
         this.scan(VNAScanMode.MODE_REFLECTION, dib.getMinFrequency(), dib.getMaxFrequency(), 100, (IVNABackgroundTaskStatusListener)null);
         rc = true;
      } catch (Exception var8) {
         ErrorLogHelper.exception(this, "checkForDevicePresence", var8);
      } finally {
         this.destroy();
      }

      TraceHelper.exitWithRC(this, "checkForDevicePresence", rc);
      return rc;
   }
}
