package krause.vna.device.serial.max6;

import java.text.MessageFormat;
import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.max6.gui.VNADriverSerialMax6Dialog;
import krause.vna.device.serial.max6.gui.VNAGeneratorMAX6Dialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialMax6 extends VNADriverSerialBase {
   public VNADriverSerialMax6() {
      String methodName = "VNADriverSerialMax6";
      TraceHelper.entry(this, "VNADriverSerialMax6");
      this.setMathHelper(new VNADriverSerialMax6MathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialMax6DIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialMax6");
   }

   public void init() throws InitializationException {
      String methodeName = "init";
      TraceHelper.entry(this, "init");
      super.init();
      VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB)this.getDeviceInfoBlock();
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
         this.getPort().setSerialPortParams(dib.getBaudrate(), 8, 1, 0);
         this.getPort().enableReceiveTimeout(dib.getReadTimeout());
         this.getPort().setInputBufferSize(20000);
         TraceHelper.text(this, "init", "getReceiveThreshold=" + this.getPort().getReceiveThreshold());
         TraceHelper.text(this, "init", "getInputBufferSize=" + this.getPort().getInputBufferSize());
      } catch (UnsupportedCommOperationException | PortInUseException var5) {
         ErrorLogHelper.exception(this, "init", var5);
         throw new InitializationException(var5);
      }

      TraceHelper.exit(this, "init");
   }

   private VNABaseSample[] receiveRawMessage(VNAScanMode mode, long frequency, int samples, long frequencyStep, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "receiveRawMessage";
      TraceHelper.entry(this, "receiveRawMessage");
      VNABaseSample[] rc = new VNABaseSample[samples];
      int totalChars = 0;
      long localFrequency = frequency;
      byte[] innerdata = new byte[20];
      if (listener != null) {
         listener.publishProgress(0);
      }

      for(int i = 0; i < samples; ++i) {
         if (listener != null && i % 100 == 0) {
            listener.publishProgress((int)((double)i * 100.0D / (double)samples));
         }

         VNABaseSample tempSample = new VNABaseSample();
         int read;
         String msg;
         ProcessingException e;
         if (mode.isRss1Mode()) {
            read = this.readBuffer(innerdata, 0, 6);
            if (read != 6) {
               msg = MessageFormat.format(VNADriverSerialMax6Messages.getString("NoChars"), i, read);
               e = new ProcessingException(msg);
               ErrorLogHelper.exception(this, "receiveRawMessage", e);
               throw e;
            }

            totalChars += read;
            tempSample.setAngle((double)((innerdata[0] & 255) + 256 * (innerdata[1] & 255)));
            tempSample.setLoss((double)((innerdata[2] & 255) + 256 * (innerdata[3] & 255)));
            tempSample.setRss1((innerdata[4] & 255) + 256 * (innerdata[5] & 255));
            tempSample.setFrequency(localFrequency);
         } else if (mode.isRss3Mode()) {
            read = this.readBuffer(innerdata, 0, 10);
            if (read != 10) {
               msg = MessageFormat.format(VNADriverSerialMax6Messages.getString("NoChars"), i, read);
               e = new ProcessingException(msg);
               ErrorLogHelper.exception(this, "receiveRawMessage", e);
               throw e;
            }

            totalChars += read;
            tempSample.setAngle((double)((innerdata[0] & 255) + 256 * (innerdata[1] & 255)));
            tempSample.setLoss((double)((innerdata[2] & 255) + 256 * (innerdata[3] & 255)));
            tempSample.setRss1((innerdata[4] & 255) + 256 * (innerdata[5] & 255));
            tempSample.setRss2((innerdata[6] & 255) + 256 * (innerdata[7] & 255));
            tempSample.setRss3((innerdata[8] & 255) + 256 * (innerdata[9] & 255));
            tempSample.setFrequency(localFrequency);
         } else if (mode.isReflectionMode()) {
            read = this.readBuffer(innerdata, 0, 4);
            if (read != 4) {
               msg = MessageFormat.format(VNADriverSerialMax6Messages.getString("NoChars"), i, read);
               e = new ProcessingException(msg);
               ErrorLogHelper.exception(this, "receiveRawMessage", e);
               throw e;
            }

            totalChars += read;
            tempSample.setAngle((double)((innerdata[0] & 255) + 256 * (innerdata[1] & 255)));
            tempSample.setLoss((double)((innerdata[2] & 255) + 256 * (innerdata[3] & 255)));
            tempSample.setFrequency(localFrequency);
         }

         rc[i] = tempSample;
         localFrequency += frequencyStep;
      }

      if (listener != null) {
         listener.publishProgress(100);
      }

      TraceHelper.exitWithRC(this, "receiveRawMessage", totalChars + " chars received");
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

   protected void sendFrequency(long frq) throws ProcessingException {
      String msg = getFrequencyFormat().format((double)frq / 1000000.0D * (double)this.getDeviceInfoBlock().getDdsTicksPerMHz());
      TraceHelper.text(this, "sendFrequency", msg);
      this.sendAsAsciiString(msg);
   }

   public VNASampleBlock scan(VNAScanMode mode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan");
      long frequencyStep = (frequencyHigh - frequencyLow) / (long)samples;
      VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB)this.getDeviceInfoBlock();
      VNASampleBlock rc = new VNASampleBlock();
      rc.setAnalyserType(this.getDeviceInfoBlock().getType());
      rc.setScanMode(mode);
      rc.setNumberOfSteps(samples);
      rc.setStartFrequency(frequencyLow);
      rc.setStopFrequency(frequencyHigh);
      rc.setMathHelper(this.getMathHelper());

      try {
         if (this.getPort() != null) {
            this.flushInputStream();
            if (mode.isRss1Mode()) {
               this.sendAsAsciiString("M2");
            } else if (mode.isRss3Mode()) {
               this.sendAsAsciiString("M3");
            } else {
               if (!mode.isReflectionMode()) {
                  throw new ProcessingException("Unsupported scan mode " + mode);
               }

               this.sendAsAsciiString("M0");
            }

            Thread.sleep((long)dib.getAfterCommandDelay());
            this.sendFrequency(frequencyLow);
            Thread.sleep((long)dib.getAfterCommandDelay());
            this.sendAsAsciiString(Integer.toString(samples));
            Thread.sleep((long)dib.getAfterCommandDelay());
            this.sendFrequency(frequencyStep);
            rc.setSamples(this.receiveRawMessage(mode, frequencyLow, samples, frequencyStep, listener));
         }
      } catch (InterruptedException var15) {
         ProcessingException p = new ProcessingException(var15);
         ErrorLogHelper.exception(this, "scan", p);
         throw p;
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
      }

      TraceHelper.exit(this, "stopGenerator");
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialMax6Dialog dlg = new VNADriverSerialMax6Dialog(pMF, this);
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
      String methodName = "showGeneratorDialog";
      TraceHelper.entry(this, "showGeneratorDialog");
      new VNAGeneratorMAX6Dialog(pMF, this);
      TraceHelper.exit(this, "showGeneratorDialog");
   }

   public String getDeviceFirmwareInfo() {
      String rc = "";
      String methodName = "readFirmwareVersion";
      TraceHelper.entry(this, "readFirmwareVersion");

      try {
         rc = rc + "Version:";
         rc = rc + this.readVersion();
         Thread.sleep((long)((VNADriverSerialMax6DIB)this.getDeviceInfoBlock()).getAfterCommandDelay());
         rc = rc + GlobalSymbols.LINE_SEPARATOR;
         rc = rc + "Serial:";
         rc = rc + this.readSerial();
      } catch (ProcessingException | InterruptedException var4) {
         ErrorLogHelper.exception(this, "readFirmwareVersion", var4);
      }

      TraceHelper.exitWithRC(this, "readFirmwareVersion", rc);
      return rc;
   }

   private String readVersion() {
      String rc = "???";
      String methodName = "readVersion";
      TraceHelper.entry(this, "readVersion");

      try {
         this.flushInputStream();
         this.sendAsAsciiString("version");
         rc = this.readLine(false);
         rc = rc + this.readLine(true);
      } catch (Exception var4) {
         ErrorLogHelper.exception(this, "readVersion", var4);
      }

      TraceHelper.exitWithRC(this, "readVersion", rc);
      return rc;
   }

   private String readSerial() throws ProcessingException {
      String rc = "";
      String methodName = "readSerial";
      TraceHelper.entry(this, "readSerial");
      this.flushInputStream();
      this.sendAsAsciiString("serial");
      rc = this.readLine(true);
      TraceHelper.exitWithRC(this, "readSerial", rc);
      return rc;
   }

   protected void sendAttenuation(int att) throws ProcessingException {
      String msg = getFrequencyFormat().format((long)(16383 - att));
      this.sendAsAsciiString(msg);
   }

   public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
      String methodName = "startGenerator";
      TraceHelper.entry(this, "startGenerator");
      TraceHelper.text(this, "startGenerator", "fI=" + frequencyI);
      TraceHelper.text(this, "startGenerator", "fQ=" + frequencyQ);
      TraceHelper.text(this, "startGenerator", "aI=" + attenuationI);
      TraceHelper.text(this, "startGenerator", "aQ=" + attenuationQ);
      TraceHelper.text(this, "startGenerator", "ph=" + phase);
      if (this.getPort() != null) {
         this.flushInputStream();
         this.sendAsAsciiString("M4");
         this.sendFrequency(frequencyI);
         this.sendAsAsciiString("1");
         this.sendAsAsciiString("0");
         this.sendAttenuation(attenuationI);
         this.receiveRawMessage(VNAScanMode.MODE_RSS3, frequencyI, 1, 0L, (IVNABackgroundTaskStatusListener)null);
      }

      TraceHelper.exit(this, "startGenerator");
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.MAX6.";
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
}
