package krause.vna.device.serial.tiny;

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
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.tiny.gui.VNADriverSerialTinyDialog;
import krause.vna.firmware.Chip45Loader;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialTiny extends VNADriverSerialBase implements IVNAFlashableDevice {
   private static final int NUM_BYTES_PER_SAMPLE = 12;

   public VNADriverSerialTiny() {
      TraceHelper.entry(this, "VNADriverSerialTiny");
      this.setMathHelper(new VNADriverSerialTinyMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialTinyDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialTiny");
   }

   public long calculateInternalFrequencyValue(long frequency) {
      TraceHelper.entry(this, "calculateInternalFrequencyValue", "In=" + frequency);
      long rc = (long)((double)frequency / 1.0E7D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
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

   public int getFirmwareLoaderBaudRate() {
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)this.getDeviceInfoBlock();
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

   public Double getDeviceTemperature() {
      String methodName = "getDeviceTemperature";
      TraceHelper.entry(this, "getDeviceTemperature");
      Double rc = -1.0D;
      if (this.getPort() != null) {
         try {
            this.flushInputStream();
            this.sendAsAsciiString("10");
            byte[] innerdata = new byte[2];
            int ch = this.readBuffer(innerdata, 0, 2);
            if (ch == -1) {
               ProcessingException e = new ProcessingException("No data character received");
               ErrorLogHelper.exception(this, "getDeviceTemperature", e);
            }

            rc = (double)((innerdata[0] & 255) + (innerdata[1] & 255) * 256) / 10.0D;
         } catch (ProcessingException var6) {
            ErrorLogHelper.exception(this, "getDeviceTemperature", var6);
         }
      }

      TraceHelper.exitWithRC(this, "getDeviceTemperature", rc);
      return rc;
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.Tiny.";
   }

   public String getFirmwareLoaderClassName() {
      return this.config.isMac() ? null : Chip45Loader.class.getName();
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), 30999999L, 10000, 2), new VNACalibrationRange(31000000L, 48999999L, 500, 1), new VNACalibrationRange(49000000L, 52999999L, 2000, 2), new VNACalibrationRange(53000000L, 142999999L, 500, 1), new VNACalibrationRange(143000000L, 147999999L, 2000, 2), new VNACalibrationRange(148000000L, 428999999L, 500, 1), new VNACalibrationRange(429000000L, 441999999L, 2000, 2), new VNACalibrationRange(442000000L, 1229999999L, 500, 1), new VNACalibrationRange(1230000000L, 1310999999L, 2000, 2), new VNACalibrationRange(1311000000L, 2199999999L, 500, 1), new VNACalibrationRange(2200000000L, 2599999999L, 2000, 2), new VNACalibrationRange(2600000000L, this.getDeviceInfoBlock().getMaxFrequency(), 500, 1)};
   }

   public boolean hasResetButton() {
      return false;
   }

   public void init() throws InitializationException {
      String methodeName = "init";
      TraceHelper.entry(this, "init");
      super.init();
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)this.getDeviceInfoBlock();
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
      } catch (PortInUseException | UnsupportedCommOperationException var5) {
         ErrorLogHelper.exception(this, "init", var5);
         throw new InitializationException(var5);
      } catch (Throwable var6) {
         ErrorLogHelper.text(this, "init", var6.getMessage());
         throw new InitializationException(var6);
      }

      TraceHelper.exit(this, "init");
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      boolean rc = true;
      return rc;
   }

   protected VNABaseSample[] receiveRawMessage(long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "receiveRawMessage";
      TraceHelper.entry(this, "receiveRawMessage", "fs=" + pStartFrequency + " #=" + pNumSamples);
      VNABaseSample[] rc = new VNABaseSample[pNumSamples];
      if (pListener != null) {
         pListener.publishProgress(0);
      }

      byte[] buffer = this.receiveBytestream(12 * pNumSamples, pListener);
      long currentFrequency = pStartFrequency;

      for(int i = 0; i < pNumSamples; ++i) {
         int offset = i * 12;
         VNABaseSample tempSample = new VNABaseSample();
         int p1 = (buffer[offset + 0] & 255) + (buffer[offset + 1] & 255) * 256 + (buffer[offset + 2] & 255) * 65536;
         int p2 = (buffer[offset + 6] & 255) + (buffer[offset + 7] & 255) * 256 + (buffer[offset + 8] & 255) * 65536;
         double real = (double)(p1 - p2) / 2.0D;
         int p3 = (buffer[offset + 3] & 255) + (buffer[offset + 4] & 255) * 256 + (buffer[offset + 5] & 255) * 65536;
         int p4 = (buffer[offset + 9] & 255) + (buffer[offset + 10] & 255) * 256 + (buffer[offset + 11] & 255) * 65536;
         double imaginary = (double)(p3 - p4) / 2.0D;
         tempSample.setP1(p1);
         tempSample.setP2(p2);
         tempSample.setP3(p3);
         tempSample.setP4(p4);
         tempSample.setHasPData(true);
         tempSample.setLoss(real);
         tempSample.setAngle(imaginary);
         tempSample.setFrequency(currentFrequency);
         rc[i] = tempSample;
         currentFrequency += pFrequencyStep;
      }

      if (pListener != null) {
         pListener.publishProgress(100);
      }

      TraceHelper.text(this, "receiveRawMessage", "Last frequency requested was " + (currentFrequency - pFrequencyStep));
      TraceHelper.text(this, "receiveRawMessage", pNumSamples + " step(s) done");
      TraceHelper.exit(this, "receiveRawMessage");
      return rc;
   }

   public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int numSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan");
      VNASampleBlock rc = new VNASampleBlock();
      rc.setDeviceTemperature(this.getDeviceTemperature());
      rc.setDeviceSupply(this.getDeviceSupply());
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)this.getDeviceInfoBlock();
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
         this.sendFrequency(frequencyHigh);
         this.sendAsAsciiString(Integer.toString(numSamples));
         this.sendAsAsciiString("");
         long frequencyStep = (frequencyHigh - frequencyLow) / (long)(numSamples - 1);
         TraceHelper.text(this, "scan", "Start %d", frequencyLow);
         TraceHelper.text(this, "scan", "Stop  %d", frequencyHigh);
         TraceHelper.text(this, "scan", "Steps %d", numSamples);
         TraceHelper.text(this, "scan", "Step  %d", frequencyStep);
         TraceHelper.text(this, "scan", "Last  %d", frequencyLow + (long)(numSamples - 1) * frequencyStep);
         rc.setSamples(this.receiveRawMessage(frequencyLow, numSamples, frequencyStep, listener));
         rc.setScanMode(scanMode);
         rc.setNumberOfSteps(numSamples);
         rc.setStartFrequency(frequencyLow);
         rc.setStopFrequency(frequencyHigh);
         rc.setAnalyserType(this.getDeviceInfoBlock().getType());
         rc.setMathHelper(this.getMathHelper());
      }

      TraceHelper.exit(this, "scan");
      return rc;
   }

   protected void sendFrequency(long frq) throws ProcessingException {
      long corrFrq = this.calculateInternalFrequencyValue(frq);
      VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB)this.getDeviceInfoBlock();
      String msg = getFrequencyFormat().format(corrFrq / (long)dib.getPrescaler());
      this.sendAsAsciiString(msg);
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialTinyDialog dlg = new VNADriverSerialTinyDialog(pMF, this);
      dlg.dispose();
      TraceHelper.exit(this, "showDriverDialog");
   }

   public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
      TraceHelper.entry(this, "showGeneratorDialog");
      new VNAGeneratorDialog(pMF, this);
      TraceHelper.exit(this, "showGeneratorDialog");
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      String methodName = "startGenerator";
      TraceHelper.entry(this, "startGenerator");
      TraceHelper.text(this, "startGenerator", "fI=" + frequencyI);
      TraceHelper.text(this, "startGenerator", "fQ=" + frequencyQ);
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("21");
         this.sendFrequency(frequencyI);
         this.sendFrequency(frequencyI);
         this.sendAsAsciiString("1");
         this.sendAsAsciiString("0");
         this.receiveRawMessage(0L, 1, 1L, (IVNABackgroundTaskStatusListener)null);
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
         this.sendAsAsciiString("7");
         this.sendFrequency(0L);
         this.sendFrequency(0L);
         this.sendAsAsciiString("1");
         this.sendAsAsciiString("0");
         this.receiveRawMessage(0L, 1, 1L, (IVNABackgroundTaskStatusListener)null);
         VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock)this.getDeviceInfoBlock();
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
         String fw = this.getDeviceFirmwareInfo();
         rc = fw.startsWith("FW Tiny ");
      } catch (InitializationException var8) {
         ErrorLogHelper.exception(this, "checkForDevicePresence", var8);
      } finally {
         this.destroy();
      }

      TraceHelper.exitWithRC(this, "checkForDevicePresence", rc);
      return rc;
   }
}
