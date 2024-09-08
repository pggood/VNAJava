package krause.vna.device.serial.pro;

import java.io.IOException;
import java.io.InputStream;
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
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.pro.generator.VNAGeneratorProDialog;
import krause.vna.device.serial.pro.gui.VNADriverSerialProDialog;
import krause.vna.firmware.MegaLoadLoader;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialPro extends VNADriverSerialBase implements IVNAFlashableDevice {
   public static final int NUM_BYTES_PER_SAMPLE = 8;

   public VNADriverSerialPro() {
      TraceHelper.entry(this, "VNADriverSerialPro");
      this.setMathHelper(new VNADriverSerialProMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialProDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialPro");
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
         TraceHelper.text(this, "destroy", "closing " + this.getPort().getName());
         this.getPort().close();
         this.setPort((SerialPort)null);
         TraceHelper.text(this, "destroy", "port closed");
      }

      TraceHelper.exit(this, "destroy");
   }

   public String getDeviceFirmwareInfo() {
      String methodName = "readFirmwareVersion";
      TraceHelper.entry(this, "readFirmwareVersion");
      String rc = "???";

      try {
         this.flushInputStream();
         this.sendAsAsciiString("9");
         rc = this.readLine(true);
      } catch (Exception var4) {
      }

      TraceHelper.exitWithRC(this, "readFirmwareVersion", rc);
      return rc;
   }

   public Double getDeviceSupply() {
      String methodName = "getDeviceSupply";
      TraceHelper.entry(this, "getDeviceSupply");
      Double rc = null;
      InputStream stream = null;
      if (this.getPort() != null) {
         try {
            this.flushInputStream();
            this.sendAsAsciiString("8");
            byte[] innerdata = new byte[2];
            int ch = this.readBuffer(innerdata, 0, 2);
            if (ch == -1) {
               ProcessingException e = new ProcessingException("No chars received");
               ErrorLogHelper.exception(this, "getDeviceSupply", e);
               throw e;
            }

            rc = (double)(((innerdata[0] & 255) + (innerdata[1] & 255) * 256) * 6) / 1024.0D;
         } catch (ProcessingException var15) {
         } finally {
            if (stream != null) {
               try {
                  ((InputStream)stream).close();
                  stream = null;
               } catch (IOException var14) {
                  ErrorLogHelper.exception(this, "getDeviceSupply", var14);
               }
            }

         }
      }

      TraceHelper.exitWithRC(this, "getDeviceSupply", rc);
      return rc;
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.Pro.";
   }

   public String getFirmwareLoaderClassName() {
      return MegaLoadLoader.class.getName();
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      long min = this.getDeviceInfoBlock().getMinFrequency();
      long max = this.getDeviceInfoBlock().getMaxFrequency();
      return new VNACalibrationRange[]{new VNACalibrationRange(min, 999999L, 4000, 1), new VNACalibrationRange(1000000L, 9999999L, 4000, 1), new VNACalibrationRange(10000000L, 29999999L, 10000, 1), new VNACalibrationRange(30000000L, max, 10000, 1)};
   }

   public boolean hasResetButton() {
      return true;
   }

   public void init() throws InitializationException {
      String methodeName = "init";
      TraceHelper.entry(this, "init");
      super.init();
      VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
      dib.restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.text(this, "init", "Trying to open port [%s]", this.getPortname());

      try {
         CommPortIdentifier portId = this.getPortIDForName(this.getPortname());
         if (portId == null) {
            InitializationException e = new InitializationException("Port [" + this.getPortname() + "] not found");
            ErrorLogHelper.exception(this, "init", e);
            throw e;
         }

         TraceHelper.text(this, "init", "port [%s] found", this.getPortname());
         SerialPort aPort = (SerialPort)portId.open(this.getAppname(), dib.getOpenTimeout());
         TraceHelper.text(this, "init", "port [%s] opened", this.getPortname());
         aPort.setFlowControlMode(0);
         TraceHelper.text(this, "init", "port [%s] set to [%d]Bd", this.getPortname(), dib.getBaudrate());
         aPort.setSerialPortParams(dib.getBaudrate(), 8, 1, 0);
         aPort.enableReceiveTimeout(dib.getReadTimeout());
         aPort.setInputBufferSize(32000);
         TraceHelper.text(this, "init", "port [%s] setup done", this.getPortname());
         this.setPort(aPort);
      } catch (UnsupportedCommOperationException | PortInUseException var5) {
         ErrorLogHelper.exception(this, "init", var5);
         throw new InitializationException(var5);
      }

      TraceHelper.exit(this, "init");
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      return true;
   }

   protected VNABaseSample[] receiveRawMessage(long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "receiveRawMessage";
      TraceHelper.entry(this, "receiveRawMessage", "fs=" + pStartFrequency + " #=" + pNumSamples);
      VNABaseSample[] rc = new VNABaseSample[pNumSamples];
      if (pListener != null) {
         pListener.publishProgress(0);
      }

      byte[] buffer = this.receiveBytestream(8 * pNumSamples, pListener);
      long currentFrequency = pStartFrequency;

      for(int i = 0; i < pNumSamples; ++i) {
         int offset = i * 8;
         VNABaseSample tempSample = new VNABaseSample();
         int p1 = (buffer[offset + 0] & 255) + (buffer[offset + 1] & 255) * 256;
         int p2 = (buffer[offset + 4] & 255) + (buffer[offset + 5] & 255) * 256;
         int real = (p1 - p2) / 2;
         int p3 = (buffer[offset + 2] & 255) + (buffer[offset + 3] & 255) * 256;
         int p4 = (buffer[offset + 6] & 255) + (buffer[offset + 7] & 255) * 256;
         int imaginary = (p3 - p4) / 2;
         tempSample.setLoss((double)real);
         tempSample.setAngle((double)imaginary);
         tempSample.setFrequency(currentFrequency);
         tempSample.setP1(p1);
         tempSample.setP2(p2);
         tempSample.setP3(p3);
         tempSample.setP4(p4);
         tempSample.setHasPData(true);
         rc[i] = tempSample;
         currentFrequency += pFrequencyStep;
      }

      TraceHelper.text(this, "receiveRawMessage", "Last frequency stored was " + (currentFrequency - pFrequencyStep));
      if (pListener != null) {
         pListener.publishProgress(100);
      }

      TraceHelper.exit(this, "receiveRawMessage");
      return rc;
   }

   public VNASampleBlock scan(VNAScanMode mode, long frequencyLow, long frequencyHigh, int requestedSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan");
      VNASampleBlock rc = new VNASampleBlock();
      if (this.getPort() != null) {
         VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
         rc.setDeviceTemperature(this.getDeviceTemperature());
         rc.setDeviceSupply(this.getDeviceSupply());
         this.flushInputStream();
         if (mode.isTransmissionMode()) {
            if (dib.isFixed6dBOnThru()) {
               this.sendAsAsciiString("20");
            } else {
               this.sendAsAsciiString("0");
            }
         } else {
            if (!mode.isReflectionMode()) {
               throw new ProcessingException("Unsupported scan mode " + mode);
            }

            this.sendAsAsciiString("1");
         }

         this.sendFrequency(frequencyLow);
         this.sendAsAsciiString("0");
         boolean oldVNA = dib.getFirmwareVersion() < 1;
         int realSamples = requestedSamples;
         if (oldVNA) {
            if (requestedSamples < 100) {
               realSamples = 100;
            } else if (requestedSamples % 100 != 0) {
               realSamples = (requestedSamples / 100 + 1) * 100;
            }
         }

         this.sendAsAsciiString(Integer.toString(realSamples));
         long frequencyStep = (frequencyHigh - frequencyLow) / (long)(realSamples - 1);
         this.sendFrequency(frequencyStep);
         TraceHelper.text(this, "scan", "Start %d", frequencyLow);
         TraceHelper.text(this, "scan", "Stop  %d", frequencyHigh);
         TraceHelper.text(this, "scan", "Steps %d", requestedSamples);
         TraceHelper.text(this, "scan", "Step  %d", frequencyStep);
         TraceHelper.text(this, "scan", "Last %d", frequencyLow + (long)(requestedSamples - 1) * frequencyStep);
         VNABaseSample[] readSamples = this.receiveRawMessage(frequencyLow, realSamples, frequencyStep, listener);
         if (requestedSamples == realSamples) {
            rc.setSamples(readSamples);
         } else {
            VNABaseSample[] newSamples = new VNABaseSample[requestedSamples];

            for(int i = 0; i < requestedSamples; ++i) {
               newSamples[i] = readSamples[i];
            }

            rc.setSamples(newSamples);
         }

         rc.setScanMode(mode);
         rc.setNumberOfSteps(requestedSamples);
         rc.setStartFrequency(frequencyLow);
         rc.setStopFrequency(frequencyHigh);
         rc.setAnalyserType(this.getDeviceInfoBlock().getType());
         rc.setMathHelper(this.getMathHelper());
      }

      TraceHelper.exit(this, "scan");
      return rc;
   }

   protected void sendAttenuation(int att) throws ProcessingException {
      String msg = getFrequencyFormat().format(Math.pow(10.0D, (60.2D - (double)att / 100.0D) / 20.0D));
      this.sendAsAsciiString(msg);
   }

   protected void sendFrequency(long frq) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)frq / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      this.sendAsAsciiString(msg);
   }

   protected void sendPhase(int phase) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)phase / 100.0D / 180.0D * 8192.0D);
      this.sendAsAsciiString(msg);
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialProDialog dlg = new VNADriverSerialProDialog(pMF, this);
      dlg.dispose();
      TraceHelper.exit(this, "showDriverDialog");
   }

   public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
      TraceHelper.entry(this, "showGeneratorDialog");
      VNAGeneratorProDialog dlg = new VNAGeneratorProDialog(pMF, this);
      dlg.showInPlace();
      TraceHelper.exit(this, "showGeneratorDialog");
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      String methodName = "startGenerator";
      TraceHelper.entry(this, "startGenerator");
      VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
      TraceHelper.text(this, "startGenerator", "fI=" + frequencyI);
      TraceHelper.text(this, "startGenerator", "fQ=" + frequencyQ);
      TraceHelper.text(this, "startGenerator", "aI=" + attenuationI);
      attenuationI = (int)((double)attenuationI + dib.getAttenOffsetI() * 100.0D);
      attenuationI = Math.max(0, attenuationI);
      TraceHelper.text(this, "startGenerator", "aI=" + attenuationI + " !corrected");
      TraceHelper.text(this, "startGenerator", "aQ=" + attenuationQ);
      attenuationQ = (int)((double)attenuationQ + dib.getAttenOffsetQ() * 100.0D);
      attenuationQ = Math.max(0, attenuationQ);
      TraceHelper.text(this, "startGenerator", "aQ=" + attenuationQ + " !corrected");
      TraceHelper.text(this, "startGenerator", "ph=" + phase);
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("2");
         this.sendFrequency(frequencyI);
         this.sendFrequency(frequencyQ);
         this.sendPhase(phase);
         this.sendAsAsciiString("3");
         this.sendAttenuation(attenuationQ);
         this.sendAttenuation(attenuationI);
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "startGenerator");
   }

   public void stopGenerator() throws ProcessingException {
      String methodName = "stopGenerator";
      TraceHelper.entry(this, "stopGenerator");
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("2");
         this.sendFrequency(0L);
         this.sendFrequency(0L);
         this.sendAsAsciiString("0");
         this.sendAsAsciiString("3");
         this.sendAsAsciiString("0");
         this.sendAsAsciiString("0");
         VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "stopGenerator");
   }

   public boolean supportsAutoReset() {
      return true;
   }

   public int getFirmwareLoaderBaudRate() {
      VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
      return dib.getBaudrate();
   }
}
