package krause.vna.device.serial.pro2;

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
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.pro2.generator.VNAGeneratorPro2Dialog;
import krause.vna.device.serial.pro2.gui.VNADriverSerialPro2Dialog;
import krause.vna.firmware.MCSLoader;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialPro2 extends VNADriverSerialBase {
   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialPro2Dialog dlg = new VNADriverSerialPro2Dialog(pMF, this);
      dlg.dispose();
      TraceHelper.exit(this, "showDriverDialog");
   }

   public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
      TraceHelper.entry(this, "showGeneratorDialog");
      VNAGeneratorPro2Dialog dlg = new VNAGeneratorPro2Dialog(pMF, this);
      dlg.showInPlace();
      TraceHelper.exit(this, "showGeneratorDialog");
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

   public VNADriverSerialPro2() {
      String methodName = "VNADriverSerialPro2";
      TraceHelper.entry(this, "VNADriverSerialPro2");
      this.setMathHelper(new VNADriverSerialPro2MathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialPro2DIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialPro2");
   }

   public long calculateInternalFrequencyValue(long frequency) {
      TraceHelper.entry(this, "calculateInternalFrequencyValue", "In=" + frequency);
      long rc = (long)((double)frequency / 8259595.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", rc);
      return rc;
   }

   public int getFirmwareLoaderBaudRate() {
      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)this.getDeviceInfoBlock();
      return dib.getBootloaderBaudrate();
   }

   public String getDeviceFirmwareInfo() {
      String methodName = "readFirmwareVersion";
      TraceHelper.entry(this, "readFirmwareVersion");
      String rc = "???";

      try {
         this.flushInputStream();
         this.sendAsAsciiString("9");
         rc = this.readLine(true);
      } catch (ProcessingException var4) {
      }

      TraceHelper.exitWithRC(this, "readFirmwareVersion", rc);
      return rc;
   }

   public Double getDeviceSupply() {
      String methodName = "getDevicePowerStatus";
      TraceHelper.entry(this, "getDevicePowerStatus");
      Double rc = null;
      if (this.getPort() != null) {
         try {
            this.flushInputStream();
            this.sendAsAsciiString("8");
            byte[] innerdata = new byte[2];
            int ch = this.readBuffer(innerdata, 0, 2);
            if (ch == -1) {
               ProcessingException e = new ProcessingException("No data character received");
               ErrorLogHelper.exception(this, "getDevicePowerStatus", e);
               throw e;
            }

            rc = (double)(((innerdata[0] & 255) + (innerdata[1] & 255) * 256) * 6) / 1024.0D;
         } catch (ProcessingException var6) {
            rc = -1.0D;
         }
      }

      TraceHelper.exitWithRC(this, "getDevicePowerStatus", rc);
      return rc;
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.Pro2.";
   }

   public String getFirmwareLoaderClassName() {
      return this.config.isMac() ? null : MCSLoader.class.getName();
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), 30999999L, 10000, 1), new VNACalibrationRange(31000000L, 48999999L, 500, 1), new VNACalibrationRange(49000000L, 52999999L, 2000, 1), new VNACalibrationRange(53000000L, 142999999L, 500, 1), new VNACalibrationRange(143000000L, 147999999L, 2000, 1), new VNACalibrationRange(148000000L, this.getDeviceInfoBlock().getMaxFrequency(), 2000, 1)};
   }

   public boolean hasResetButton() {
      return true;
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      boolean rc = true;
      return rc;
   }

   public void init() throws InitializationException {
      String methodeName = "init";
      TraceHelper.entry(this, "init");
      super.init();
      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)this.getDeviceInfoBlock();
      dib.restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.text(this, "init", "Trying to open port [" + this.getPortname() + "]");

      try {
         CommPortIdentifier portId = this.getPortIDForName(this.getPortname());
         if (portId == null) {
            InitializationException e = new InitializationException("Port [" + this.getPortname() + "] not found");
            ErrorLogHelper.exception(this, "init", e);
            throw e;
         }

         TraceHelper.text(this, "init", "port [" + this.getPortname() + "] found");
         SerialPort aPort = (SerialPort)portId.open(this.getAppname(), dib.getOpenTimeout());
         TraceHelper.text(this, "init", "port [" + this.getPortname() + "] opened");
         aPort.setFlowControlMode(0);
         TraceHelper.text(this, "init", "port [" + this.getPortname() + "] set to [" + dib.getBaudrate() + "]bd");
         aPort.setSerialPortParams(dib.getBaudrate(), 8, 1, 0);
         aPort.enableReceiveTimeout(dib.getReadTimeout());
         aPort.setInputBufferSize(32000);
         TraceHelper.text(this, "init", "port [" + this.getPortname() + "] setup done");
         this.setPort(aPort);
      } catch (UnsupportedCommOperationException | PortInUseException var5) {
         ErrorLogHelper.exception(this, "init", var5);
         throw new InitializationException(var5);
      } catch (Throwable var6) {
         ErrorLogHelper.text(this, "init", var6.getMessage());
         throw new InitializationException(var6);
      }

      TraceHelper.exit(this, "init");
   }

   protected VNABaseSample[] receiveRawMessage(VNAScanMode pMode, long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "receiveRawMessage";
      TraceHelper.entry(this, "receiveRawMessage", "fStart=%d #=%d", pStartFrequency, pNumSamples);
      VNABaseSample[] rc = new VNABaseSample[pNumSamples];
      if (pListener != null) {
         pListener.publishProgress(0);
      }

      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)this.getDeviceInfoBlock();
      int resolution = dib.getResolution();
      byte numBytesPerSample;
      if (resolution == 1) {
         numBytesPerSample = 8;
         TraceHelper.text(this, "receiveRawMessage", "using 8 bytes/sample");
      } else {
         if (resolution != 2) {
            throw new ProcessingException("Unsupported resolution mode " + resolution);
         }

         numBytesPerSample = 12;
         TraceHelper.text(this, "receiveRawMessage", "using 12 bytes/sample");
      }

      byte[] buffer = this.receiveBytestream(numBytesPerSample * pNumSamples, pListener);
      long currentFrequency;
      int i;
      int offset;
      VNABaseSample tempSample;
      int p1;
      int p2;
      int p3;
      int p4;
      if (resolution == 1) {
         currentFrequency = pStartFrequency;

         for(i = 0; i < pNumSamples; ++i) {
            offset = i * numBytesPerSample;
            tempSample = new VNABaseSample();
            p1 = (buffer[offset + 0] & 255) + (buffer[offset + 1] & 255) * 256;
            p2 = (buffer[offset + 4] & 255) + (buffer[offset + 5] & 255) * 256;
            int real = (p1 - p2) / 2;
            p3 = (buffer[offset + 2] & 255) + (buffer[offset + 3] & 255) * 256;
            p3 = (buffer[offset + 6] & 255) + (buffer[offset + 7] & 255) * 256;
            p4 = (p3 - p3) / 2;
            tempSample.setLoss((double)real);
            tempSample.setAngle((double)p4);
            tempSample.setFrequency(currentFrequency);
            tempSample.setP1(p1);
            tempSample.setP2(p2);
            tempSample.setP3(p3);
            tempSample.setP4(p3);
            tempSample.setHasPData(true);
            rc[i] = tempSample;
            currentFrequency += pFrequencyStep;
         }

         TraceHelper.text(this, "receiveRawMessage", "Last frequency stored was %d", currentFrequency - pFrequencyStep);
      } else if (resolution == 2) {
         currentFrequency = pStartFrequency;

         for(i = 0; i < pNumSamples; ++i) {
            offset = i * numBytesPerSample;
            tempSample = new VNABaseSample();
            p1 = (buffer[offset + 0] & 255) + (buffer[offset + 1] & 255) * 256 + (buffer[offset + 2] & 255) * 65536;
            p2 = (buffer[offset + 6] & 255) + (buffer[offset + 7] & 255) * 256 + (buffer[offset + 8] & 255) * 65536;
            double real = (double)(p1 - p2) / 2.0D;
            p3 = (buffer[offset + 3] & 255) + (buffer[offset + 4] & 255) * 256 + (buffer[offset + 5] & 255) * 65536;
            p4 = (buffer[offset + 9] & 255) + (buffer[offset + 10] & 255) * 256 + (buffer[offset + 11] & 255) * 65536;
            double imaginary = (double)(p3 - p4) / 2.0D;
            tempSample.setLoss(real);
            tempSample.setAngle(imaginary);
            tempSample.setFrequency(currentFrequency);
            tempSample.setP1(p1);
            tempSample.setP2(p2);
            tempSample.setP3(p3);
            tempSample.setP4(p4);
            tempSample.setHasPData(true);
            rc[i] = tempSample;
            currentFrequency += pFrequencyStep;
         }

         TraceHelper.text(this, "receiveRawMessage", "Last frequency requested was %d ", currentFrequency - pFrequencyStep);
         TraceHelper.text(this, "receiveRawMessage", "%d step(s) done", pNumSamples);
      }

      if (pListener != null) {
         pListener.publishProgress(100);
      }

      TraceHelper.exit(this, "receiveRawMessage");
      return rc;
   }

   public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int numSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan");
      VNASampleBlock rc = new VNASampleBlock();
      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)this.getDeviceInfoBlock();
      if (this.getPort() != null) {
         this.flushInputStream();
         if (scanMode.isTransmissionMode()) {
            this.sendAsAsciiString(dib.getScanCommandTransmission());
         } else {
            if (!scanMode.isReflectionMode()) {
               throw new ProcessingException("Unsupported scan mode " + scanMode);
            }

            this.sendAsAsciiString(dib.getScanCommandReflection());
         }

         this.sendFrequency(frequencyLow);
         this.sendAsAsciiString("" + dib.getSampleRate());
         this.sendAsAsciiString("" + numSamples);
         long frequencyStep = (frequencyHigh - frequencyLow) / (long)(numSamples - 1);
         this.sendFrequency(frequencyStep);
         TraceHelper.text(this, "scan", "Start %d", frequencyLow);
         TraceHelper.text(this, "scan", "Stop  %d", frequencyHigh);
         TraceHelper.text(this, "scan", "Steps %d", numSamples);
         TraceHelper.text(this, "scan", "Step  %d", frequencyStep);
         TraceHelper.text(this, "scan", "Last  %d", frequencyLow + (long)(numSamples - 1) * frequencyStep);
         rc.setSamples(this.receiveRawMessage(scanMode, frequencyLow, numSamples, frequencyStep, listener));
         rc.setScanMode(scanMode);
         rc.setNumberOfSteps(numSamples);
         rc.setStartFrequency(frequencyLow);
         rc.setStopFrequency(frequencyHigh);
         rc.setAnalyserType(this.getDeviceInfoBlock().getType());
         rc.setMathHelper(this.getMathHelper());
         if (listener != null) {
            listener.publishProgress(100);
         }
      }

      TraceHelper.exit(this, "scan");
      return rc;
   }

   protected void sendFrequency(long frq) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)frq / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      this.sendAsAsciiString(msg);
   }

   protected void sendAttenuation(int att) throws ProcessingException {
      String msg = getFrequencyFormat().format(Math.pow(10.0D, (60.2D - (double)att / 100.0D) / 20.0D));
      this.sendAsAsciiString(msg);
   }

   protected void sendPhase(int phase) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)phase / 100.0D / 180.0D * 8192.0D);
      this.sendAsAsciiString(msg);
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      String methodName = "startGenerator";
      TraceHelper.entry(this, "startGenerator");
      VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)this.getDeviceInfoBlock();
      TraceHelper.text(this, "startGenerator", "fI=" + frequencyI);
      TraceHelper.text(this, "startGenerator", "fQ=" + frequencyQ);
      TraceHelper.text(this, "startGenerator", "aI=" + attenuationI);
      attenuationI = Math.max(0, attenuationI);
      TraceHelper.text(this, "startGenerator", "aI=" + attenuationI + " !corrected");
      TraceHelper.text(this, "startGenerator", "aQ=" + attenuationQ);
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
         VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "stopGenerator");
   }

   public boolean supportsAutoReset() {
      return true;
   }

   public boolean checkForDevicePresence(boolean viaSlowConnection) {
      boolean rc = false;
      String methodName = "checkForDevicePresence";
      TraceHelper.entry(this, "checkForDevicePresence");

      try {
         this.init();
         if (viaSlowConnection) {
            Thread.sleep(5000L);
         }

         String fw = this.getDeviceFirmwareInfo();
         rc = fw.startsWith("PRO2 ");
      } catch (InitializationException | InterruptedException var8) {
         ErrorLogHelper.exception(this, "checkForDevicePresence", var8);
      } finally {
         this.destroy();
      }

      TraceHelper.exitWithRC(this, "checkForDevicePresence", rc);
      return rc;
   }
}
