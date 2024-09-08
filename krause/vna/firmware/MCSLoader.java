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

public class MCSLoader extends VNABaseFirmwareFlasher {
   private static final byte NAK = 21;
   private static final byte ACK = 6;
   private static final byte SOH = 1;
   private static final byte EOT = 4;
   private static final int BUFFER_SIZE = 128;
   private static final int DATA_OFFSET = 3;
   private static final int CHECKSUM_OFFSET = 131;
   private static final long TIMEOUT_READ = 1000L;
   private int oldBaudRate;
   private int oldDataBits;
   private int oldFlowMode;
   private int oldParity;
   private int oldStopBits;

   public MCSLoader() {
      TraceHelper.entry(this, "MCSLoader");
      TraceHelper.exit(this, "MCSLoader");
   }

   private void bootloaderSendResetCommand(SerialPort port) throws IOException {
      TraceHelper.entry(this, "bootloaderSendResetCommand");
      this.getMessenger().publishMessage("Resetting analyzer");
      OutputStream oStream = port.getOutputStream();
      oStream.write(57);
      oStream.write(57);
      oStream.write(13);
      oStream.flush();
      TraceHelper.exit(this, "bootloaderSendResetCommand");
   }

   private void bootloaderStartCommandMode(SerialPort port) throws ProcessingException {
      String methodName = "bootloaderStartCommandMode";
      TraceHelper.entry(this, "bootloaderStartCommandMode");
      this.getMessenger().publishMessage("Entering bootloader ...");
      this.flushInputBuffer(port);
      boolean connected = false;

      try {
         OutputStream oStream = port.getOutputStream();
         int i = 0;

         while(i < 100) {
            try {
               oStream.write(123);
               oStream.flush();

               int ch;
               do {
                  ch = this.readChar(port, 100L);
                  TraceHelper.text(this, "bootloaderStartCommandMode", "ch=" + ch);
               } while(ch != 123);

               connected = true;
               break;
            } catch (ProcessingException var7) {
               ++i;
            }
         }

         if (!connected) {
            throw new ProcessingException("No response from bootloader");
         }

         this.getMessenger().publishMessage("Bootloader in command mode");
      } catch (IOException var8) {
         throw new ProcessingException(var8);
      }

      TraceHelper.entry(this, "bootloaderStartCommandMode");
   }

   private void bootloaderStartFlashMode(SerialPort port) throws ProcessingException {
      String methodName = "bootloaderStartFlashMode";
      TraceHelper.entry(this, "bootloaderStartFlashMode");
      this.getMessenger().publishMessage("Waiting for bootloader start request ...");

      int ch;
      do {
         ch = this.readChar(port, 1000L);
         TraceHelper.text(this, "bootloaderStartFlashMode", "ch=" + ch);
      } while(ch != 21);

      this.getMessenger().publishMessage("Bootloader start request received");
      TraceHelper.exit(this, "bootloaderStartFlashMode");
   }

   public void burnBuffer(FirmwareFileParser hfp, IVNADriver driver) throws ProcessingException {
      String methodName = "burnBuffer";
      TraceHelper.entry(this, "burnBuffer");
      VNADriverSerialBase serialDriver = (VNADriverSerialBase)driver;
      SerialPort port = serialDriver.getPort();
      OutputStream oStream = null;

      try {
         oStream = port.getOutputStream();
         this.safeCommParms(port);
         this.bootloaderSendResetCommand(port);
         IVNAFlashableDevice fdev = (IVNAFlashableDevice)driver;
         this.setBootloaderCommParms(port, fdev.getFirmwareLoaderBaudRate());
         this.bootloaderStartCommandMode(port);
         this.bootloaderStartFlashMode(port);
         byte[] fileBuffer = hfp.getFlash();
         int fileRemaining = fileBuffer.length;
         this.getMessenger().publishMessage("Will send " + fileRemaining / 128 + " full packets to the AVR");
         byte[] flashBuffer = new byte[132];
         int packetNum = 1;
         byte last_response = 6;
         int fileOffset = 0;
         int toCopy = 128;

         do {
            flashBuffer[0] = 1;
            flashBuffer[1] = (byte)packetNum;
            flashBuffer[2] = (byte)(~flashBuffer[1]);
            int chksum;
            if (last_response != 21) {
               if (fileRemaining <= 128) {
                  toCopy = fileRemaining;
                  this.getMessenger().publishMessage("Sending LAST Page " + packetNum + " with " + fileRemaining + " bytes ...");
               }

               TraceHelper.text(this, "burnBuffer", "to copy  =" + toCopy);

               for(chksum = 0; chksum < toCopy; ++chksum) {
                  flashBuffer[3 + chksum] = fileBuffer[fileOffset + chksum];
               }

               fileRemaining -= toCopy;
               fileOffset += toCopy;
               TraceHelper.text(this, "burnBuffer", "remaining=" + fileRemaining);
               TraceHelper.text(this, "burnBuffer", "offset   =" + fileOffset);
            }

            TraceHelper.text(this, "burnBuffer", "[0]=" + flashBuffer[0]);
            TraceHelper.text(this, "burnBuffer", "[1]=" + flashBuffer[1]);
            TraceHelper.text(this, "burnBuffer", "[2]=" + flashBuffer[2]);
            chksum = 0;

            for(int i = 3; i < 131; ++i) {
               chksum += flashBuffer[i];
            }

            flashBuffer[131] = (byte)chksum;
            oStream.write(flashBuffer, 0, flashBuffer.length);
            oStream.flush();
            byte response = (byte)this.readChar(port, 1000L);
            if (response == 6) {
               this.getMessenger().publishMessage("Page " + packetNum + " flashed");
               ++packetNum;
            } else {
               if (response != 21) {
                  String msg = "Unknown response from bootloader [" + response + "]";
                  this.getMessenger().publishMessage(msg);
                  throw new ProcessingException(msg);
               }

               this.getMessenger().publishMessage("Page " + packetNum + " failed");
               int csSend = this.readChar(port, 1000L);
               int csAVR = this.readChar(port, 1000L);
               this.getMessenger().publishMessage(" CS local=" + chksum);
               this.getMessenger().publishMessage(" CS sent =" + csSend);
               this.getMessenger().publishMessage(" CS AVR  =" + csAVR);
               last_response = 21;
            }
         } while(fileRemaining > 0);

         oStream.write(4);
         byte response = (byte)this.readChar(port, 1000L);
         if (response != 6) {
            String msg = "Unknown response from bootloader [" + response + "] for END FLASH";
            this.getMessenger().publishMessage(msg);
            throw new ProcessingException(msg);
         }

         this.getMessenger().publishMessage("Flashing done");
      } catch (UnsupportedCommOperationException | IOException var26) {
         ErrorLogHelper.exception(this, "burnBuffer", var26);
         throw new ProcessingException(var26);
      } finally {
         try {
            this.restoreCommParms(port);
         } catch (UnsupportedCommOperationException var25) {
            ErrorLogHelper.exception(this, "burnBuffer", var25);
            throw new ProcessingException(var25);
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
      port.setFlowControlMode(0);
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
