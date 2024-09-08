package krause.vna.device.serial.metro;

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
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.metro.gui.VNADriverSerialMetroDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialMetro extends VNADriverSerialBase {
   private static final int NUM_BYTES_PER_SAMPLE = 4;

   public VNADriverSerialMetro() {
      String methodName = "VNADriverSerialMetro";
      TraceHelper.entry(this, "VNADriverSerialMetro");
      this.setMathHelper(new VNADriverSerialMetroMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialMetroDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialMetro");
   }

   public long calculateInternalFrequencyValue(long frequency) {
      TraceHelper.entry(this, "calculateInternalFrequencyValue", "In=" + frequency);
      long rc = (long)((double)frequency / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", rc);
      return rc;
   }

   public void destroy() {
      String methodName = "destroy";
      TraceHelper.entry(this, "destroy");
      if (this.getPort() != null) {
         this.getPort().close();
         this.setPort((SerialPort)null);
      }

      TraceHelper.exit(this, "destroy");
   }

   public String getDeviceFirmwareInfo() {
      return "???";
   }

   public Double getDeviceSupply() {
      return 5.0D;
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.MetroVNA.";
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), this.getDeviceInfoBlock().getMaxFrequency(), 30000, 1)};
   }

   public void init() throws InitializationException {
      String methodeName = "init";
      TraceHelper.entry(this, "init");
      super.init();
      VNADriverSerialMetroDIB dib = (VNADriverSerialMetroDIB)this.getDeviceInfoBlock();
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
      } catch (PortInUseException var5) {
         ErrorLogHelper.exception(this, "init", var5);
         throw new InitializationException(var5);
      } catch (UnsupportedCommOperationException var6) {
         ErrorLogHelper.exception(this, "init", var6);
         throw new InitializationException(var6);
      } catch (Throwable var7) {
         ErrorLogHelper.text(this, "init", var7.getMessage());
         throw new InitializationException(var7);
      }

      TraceHelper.exit(this, "init");
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      return true;
   }

   public VNASampleBlock scan(VNAScanMode pScanMode, long pFrequencyLow, long pFrequencyHigh, int pSamples, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan");
      long frequencyStep = (pFrequencyHigh - pFrequencyLow) / (long)pSamples;
      VNASampleBlock rc = new VNASampleBlock();
      rc.setAnalyserType(this.getDeviceInfoBlock().getType());
      rc.setScanMode(pScanMode);
      rc.setNumberOfSteps(pSamples);
      rc.setStartFrequency(pFrequencyLow);
      rc.setStopFrequency(pFrequencyHigh);
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

         this.sendFrequency(pFrequencyLow);
         this.sendAsAsciiString(Integer.toString(pSamples));
         this.sendFrequency(frequencyStep);
         rc.setSamples(this.receiveRawMessage(pScanMode, pFrequencyLow, pSamples, frequencyStep, pListener));
         this.stopGenerator();
      }

      TraceHelper.exit(this, "scan");
      return rc;
   }

   protected void sendFrequency(long frq) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)frq / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      TraceHelper.text(this, "sendFrequency", msg);
      this.sendAsAsciiString(msg);
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialMetroDialog dlg = new VNADriverSerialMetroDialog(pMF, this);
      dlg.dispose();
      TraceHelper.exit(this, "showDriverDialog");
   }

   public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
      TraceHelper.entry(this, "showGeneratorDialog");
      new VNAGeneratorDialog(pMF, this);
      TraceHelper.exit(this, "showGeneratorDialog");
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      TraceHelper.entry(this, "startGenerator");
      TraceHelper.entry(this, "startGenerator", "" + frequencyI);
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("0");
         this.sendFrequency(frequencyI);
         this.sendAsAsciiString("1");
         this.sendAsAsciiString("0");
         this.receiveRawMessage(VNAScanMode.MODE_TRANSMISSION, 0L, 1, 1L, (IVNABackgroundTaskStatusListener)null);
         VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "startGenerator");
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
         this.receiveRawMessage(VNAScanMode.MODE_TRANSMISSION, 0L, 1, 1L, (IVNABackgroundTaskStatusListener)null);
         VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "stopGenerator");
   }

   private VNABaseSample[] receiveRawMessage(VNAScanMode pScanMode, long frequency, int pNumSamples, long frequencyStep, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "receiveRawMessage";
      TraceHelper.entry(this, "receiveRawMessage", "fStart=" + frequency + " #=" + pNumSamples + " fStep=" + frequencyStep);
      VNABaseSample[] rc = new VNABaseSample[pNumSamples];
      if (listener != null) {
         listener.publishProgress(0);
      }

      byte[] buffer = this.receiveBytestream(4 * pNumSamples, listener);
      long localFrequency = frequency;

      for(int i = 0; i < pNumSamples; ++i) {
         int offset = i * 4;
         VNABaseSample tempSample = new VNABaseSample();
         tempSample.setAngle((double)((buffer[offset] & 255) + 256 * (buffer[offset + 1] & 255)));
         tempSample.setLoss((double)((buffer[offset + 2] & 255) + 256 * (buffer[offset + 3] & 255)));
         tempSample.setFrequency(localFrequency);
         rc[i] = tempSample;
         localFrequency += frequencyStep;
      }

      TraceHelper.exit(this, "receiveRawMessage");
      return rc;
   }
}
