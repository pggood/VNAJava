package krause.vna.device.serial.proext;

import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.serial.pro.VNADriverSerialPro;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.pro.VNADriverSerialProMathHelper;
import krause.vna.device.serial.proext.gui.VNADriverSerialProExtDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;

public class VNADriverSerialProExt extends VNADriverSerialPro {
   public static final int NUM_BYTES_PER_SAMPLE = 4;

   public VNADriverSerialProExt() {
      TraceHelper.entry(this, "VNADriverSerialProExt");
      this.setMathHelper(new VNADriverSerialProMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialProExtDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialProExt");
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.ProExt.";
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      VNACalibrationRange[] rc = null;
      long min = this.getDeviceInfoBlock().getMinFrequency();
      long max = this.getDeviceInfoBlock().getMaxFrequency();
      rc = new VNACalibrationRange[]{new VNACalibrationRange(min, 419999999L, 1000, 1), new VNACalibrationRange(420000000L, 449999999L, 2000, 1), new VNACalibrationRange(450000000L, 849999999L, 500, 1), new VNACalibrationRange(850000000L, 899999999L, 2000, 1), new VNACalibrationRange(900000000L, 1099999999L, 1000, 1), new VNACalibrationRange(1100000000L, max, 2000, 1)};
      return rc;
   }

   public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
      boolean rc = true;
      return rc;
   }

   protected VNABaseSample[] receiveRawMessage(long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "receiveMessage";
      TraceHelper.entry(this, "receiveMessage", "fs=" + pStartFrequency + " #=" + pNumSamples);
      VNABaseSample[] rawSamples = new VNABaseSample[pNumSamples];
      if (pListener != null) {
         pListener.publishProgress(0);
      }

      byte[] buffer = this.receiveBytestream(4 * pNumSamples, pListener);
      long currentFrequency = pStartFrequency;

      for(int i = 0; i < pNumSamples; ++i) {
         int offset = i * 4;
         VNABaseSample tempSample = new VNABaseSample();
         int real = (buffer[offset + 0] & 255) + (buffer[offset + 1] & 255) * 256;
         int imaginary = (buffer[offset + 2] & 255) + (buffer[offset + 3] & 255) * 256;
         tempSample.setLoss((double)real);
         tempSample.setAngle((double)imaginary);
         tempSample.setFrequency(currentFrequency);
         rawSamples[i] = tempSample;
         currentFrequency += pFrequencyStep;
      }

      TraceHelper.text(this, "receiveMessage", "Last frequency stored was " + (currentFrequency - pFrequencyStep));
      if (pListener != null) {
         pListener.publishProgress(100);
      }

      TraceHelper.exit(this, "receiveMessage");
      return rawSamples;
   }

   public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int numSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
      String methodName = "scan";
      TraceHelper.entry(this, "scan", "trans=" + scanMode + " low=" + frequencyLow + " high=" + frequencyHigh + " #=" + numSamples);
      VNASampleBlock rc = new VNASampleBlock();
      VNADriverSerialProExtDIB dib = (VNADriverSerialProExtDIB)this.getDeviceInfoBlock();
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
         long frequencyStep = (frequencyHigh - frequencyLow) / (long)numSamples;
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
      TraceHelper.entry(this, "sendFrequency");
      VNADriverSerialProExtDIB dib = (VNADriverSerialProExtDIB)this.getDeviceInfoBlock();
      TraceHelper.text(this, "sendFrequency", "passed freq=" + frq);
      frq = (long)((double)frq * ((double)dib.getDdsTicksPerMHz() / 1000000.0D));
      frq /= (long)dib.getPrescaler();
      TraceHelper.text(this, "sendFrequency", "used   freq=" + frq);
      String msg = getFrequencyFormat().format(frq);
      this.sendAsAsciiString(msg);
      TraceHelper.exit(this, "sendFrequency");
   }

   public void showDriverDialog(VNAMainFrame pMF) {
      TraceHelper.entry(this, "showDriverDialog");
      VNADriverSerialProExtDialog dlg = new VNADriverSerialProExtDialog(pMF, this);
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
      VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
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
         VNADriverSerialProDIB dib = (VNADriverSerialProDIB)this.getDeviceInfoBlock();
         this.wait(dib.getAfterCommandDelay());
      }

      TraceHelper.exit(this, "stopGenerator");
   }
}
