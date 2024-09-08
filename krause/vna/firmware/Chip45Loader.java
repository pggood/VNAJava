package krause.vna.firmware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.serial.VNADriverSerialBase;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class Chip45Loader extends VNABaseFirmwareFlasher {
   private static final byte LINE_END_CHIP45 = 10;
   private static final byte LINE_END_MINIVNA = 13;
   private static final long TIMEOUT_READ = 3000L;
   private int oldBaudRate;
   private int oldDataBits;
   private int oldFlowMode;
   private int oldParity;
   private int oldStopBits;

   public Chip45Loader() {
      TraceHelper.entry(this, "Chip45Loader");
      TraceHelper.exit(this, "Chip45Loader");
   }

   private void bootloaderSendLine(SerialPort port, String line, int lineNo) throws ProcessingException {
      this.flushInputBuffer(port);

      try {
         OutputStream oStream = port.getOutputStream();

         for(int i = 0; i < line.length(); ++i) {
            oStream.write((byte)line.charAt(i));
         }

         oStream.write(10);
         char c = (char)this.readChar(port, 3000L);
         if (c != '.') {
            if (c != '*') {
               throw new ProcessingException("Flash error");
            }

            this.getMessenger().publishMessage("Flashed page " + lineNo);
         }

      } catch (IOException var6) {
         ErrorLogHelper.exception(this, "bootloaderSendLine", var6);
         throw new ProcessingException(var6);
      }
   }

   private void bootloaderSendResetCommand(SerialPort port) throws IOException {
      TraceHelper.entry(this, "bootloaderSendResetCommand");
      this.getMessenger().publishMessage("Resetting miniVNAtiny");
      OutputStream oStream = port.getOutputStream();
      oStream.write(57);
      oStream.write(57);
      oStream.write(13);
      oStream.flush();
      TraceHelper.exit(this, "bootloaderSendResetCommand");
   }

   private void bootloaderStartApplication(SerialPort port) throws ProcessingException {
      String methodName = "bootloaderStartApplication";
      TraceHelper.entry(this, "bootloaderStartApplication");
      this.getMessenger().publishMessage("Restarting analyser ...");
      this.flushInputBuffer(port);

      try {
         OutputStream oStream = port.getOutputStream();
         oStream.write(103);
         oStream.write(10);
         oStream.flush();
         if (this.readChar(port, 3000L) != 103) {
            throw new ProcessingException("g missing");
         }

         if (this.readChar(port, 3000L) != 43) {
            throw new ProcessingException("+ missing");
         }

         this.flushInputBuffer(port);
         this.getMessenger().publishMessage("Device resetting");
      } catch (IOException var4) {
         ErrorLogHelper.exception(this, "bootloaderStartApplication", var4);
         throw new ProcessingException(var4);
      }

      TraceHelper.exit(this, "bootloaderStartApplication");
   }

   private void bootloaderStartCommandMode(SerialPort port) throws ProcessingException {
      TraceHelper.entry(this, "bootloaderStartCommandMode");
      this.getMessenger().publishMessage("Entering bootloader State #1");
      this.flushInputBuffer(port);

      try {
         OutputStream oStream = port.getOutputStream();

         for(int i = 0; i < 100; ++i) {
            oStream.write(85);
            oStream.flush();
            this.sleep(10);
         }

         this.getMessenger().publishMessage("Entering bootloader State #2");

         while(this.readChar(port, 3000L) != 62) {
         }

         this.getMessenger().publishMessage("Bootloader prompt received");
         oStream.write(10);
         if (this.readChar(port, 3000L) != 45) {
            throw new ProcessingException("- missing");
         }

         this.flushInputBuffer(port);
         this.getMessenger().publishMessage("Bootloader in command mode");
      } catch (IOException var4) {
         throw new ProcessingException(var4);
      }

      TraceHelper.exit(this, "bootloaderStartCommandMode");
   }

   private void bootloaderStartFlashMode(SerialPort port) throws ProcessingException {
      TraceHelper.entry(this, "bootloaderStartFlash");
      this.getMessenger().publishMessage("Start flash process ...");
      this.flushInputBuffer(port);

      try {
         OutputStream oStream = port.getOutputStream();
         oStream.write(112);
         oStream.write(102);
         oStream.write(10);
         oStream.flush();
         if (this.readChar(port, 500L) != 112) {
            throw new ProcessingException("p missing");
         }

         if (this.readChar(port, 500L) != 102) {
            throw new ProcessingException("f missing");
         }

         if (this.readChar(port, 500L) != 43) {
            throw new ProcessingException("+ missing");
         }

         this.flushInputBuffer(port);
         this.getMessenger().publishMessage("Flash process started");
      } catch (IOException var3) {
         ErrorLogHelper.exception(this, "bootloaderEnterCommandMode", var3);
         throw new ProcessingException(var3);
      }

      TraceHelper.exit(this, "bootloaderStartFlash");
   }

   public void burnBuffer(FirmwareFileParser hfp, IVNADriver driver) throws ProcessingException {
      String methodName = "burnBuffer";
      TraceHelper.entry(this, "burnBuffer");
      VNADriverSerialBase serialDriver = (VNADriverSerialBase)driver;
      SerialPort port = serialDriver.getPort();

      try {
         this.safeCommParms(port);
         this.bootloaderSendResetCommand(port);
         IVNAFlashableDevice fdev = (IVNAFlashableDevice)driver;
         this.setBootloaderCommParms(port, fdev.getFirmwareLoaderBaudRate());
         this.bootloaderStartCommandMode(port);
         int minAdr = hfp.getFlashMin();
         int maxAdr = hfp.getFlashMax() + 1;
         int blockSize = 1;
         int fullBlocks = (maxAdr - minAdr) / 16;
         int lastBlock = maxAdr - minAdr - fullBlocks * 16;
         this.bootloaderStartFlashMode(port);
         int addr = minAdr;
         int i = 0;

         while(true) {
            String line;
            if (i >= fullBlocks) {
               if (lastBlock != 0) {
                  line = hfp.getFlashAsHexFileLine(addr + 16 * fullBlocks, lastBlock);
                  this.bootloaderSendLine(port, line, i);
               }

               this.bootloaderSendLine(port, hfp.getLastHexFileLine(), 0);
               this.sleep(1000);
               this.bootloaderStartApplication(port);
               break;
            }

            line = hfp.getFlashAsHexFileLine(addr + i * 16, 16);
            this.bootloaderSendLine(port, line, i);
            ++i;
         }
      } catch (UnsupportedCommOperationException | IOException var22) {
         ErrorLogHelper.exception(this, "burnBuffer", var22);
         throw new ProcessingException(var22);
      } finally {
         try {
            this.restoreCommParms(port);
         } catch (UnsupportedCommOperationException var21) {
            ErrorLogHelper.exception(this, "burnBuffer", var21);
         }

      }

      TraceHelper.exit(this, "burnBuffer");
   }

   private void flushInputBuffer(SerialPort port) throws ProcessingException {
      try {
         InputStream iStream = port.getInputStream();
         int avl = iStream.available();

         for(int i = 0; i < avl; ++i) {
            iStream.read();
         }

      } catch (IOException var5) {
         ErrorLogHelper.exception(this, "bootloaderEnterCommandMode", var5);
         throw new ProcessingException(var5);
      }
   }

   private int readChar(SerialPort port, long timeout) throws ProcessingException {
      int rc = 1;
      long endTime = System.currentTimeMillis() + timeout;

      try {
         InputStream stream = port.getInputStream();

         while(stream.available() == 0) {
            this.sleep(10);
            if (System.currentTimeMillis() > endTime) {
               throw new ProcessingException("No char received in after " + timeout + "ms");
            }
         }

         rc = stream.read();
         if (LogManager.getSingleton().isTracingEnabled()) {
            if (rc >= 32) {
               LogManager.getSingleton().getTracer().text(this, "readChar", "'" + (char)rc + "'/" + rc);
            } else {
               LogManager.getSingleton().getTracer().text(this, "readChar", "CTRL-" + (char)(rc + 64) + "/" + rc);
            }
         }

         return rc;
      } catch (IOException var8) {
         ErrorLogHelper.exception(this, "readChar", var8);
         throw new ProcessingException(var8);
      }
   }

   private void restoreCommParms(SerialPort port) throws UnsupportedCommOperationException {
      TraceHelper.entry(this, "restoreCommParms");
      port.setFlowControlMode(this.oldFlowMode);
      port.setSerialPortParams(this.oldBaudRate, this.oldDataBits, this.oldStopBits, this.oldParity);
      this.getMessenger().publishMessage("Using " + this.oldBaudRate + "Bd");
      TraceHelper.exit(this, "restoreCommParms");
   }

   private void safeCommParms(SerialPort port) {
      TraceHelper.entry(this, "safeCommParms");
      this.oldBaudRate = port.getBaudRate();
      this.oldDataBits = port.getDataBits();
      this.oldFlowMode = port.getFlowControlMode();
      this.oldParity = port.getParity();
      this.oldStopBits = port.getStopBits();
      TraceHelper.exit(this, "safeCommParms");
   }

   private void setBootloaderCommParms(SerialPort port, int baud) throws UnsupportedCommOperationException {
      TraceHelper.entry(this, "setBootloaderCommParms");
      port.setFlowControlMode(12);
      port.setSerialPortParams(baud, 8, 1, 0);
      this.getMessenger().publishMessage("Using " + baud + "Bd");
      TraceHelper.exit(this, "setBootloaderCommParms");
   }

   private void sleep(int millis) {
      try {
         Thread.sleep((long)millis);
      } catch (InterruptedException var3) {
      }

   }
}
