package krause.vna.firmware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.serial.VNADriverSerialBase;
import purejavacomm.SerialPort;

public class MegaLoadLoader extends VNABaseFirmwareFlasher {
   public MegaLoadLoader() {
      String methodName = "MegaLoadLoader";
      TraceHelper.entry(this, "MegaLoadLoader");
      TraceHelper.entry(this, "MegaLoadLoader");
   }

   private int readChar(SerialPort port, long timeout) throws ProcessingException {
      int rc = 1;
      long endTime = System.currentTimeMillis() + timeout;

      try {
         InputStream stream = port.getInputStream();

         while(stream.available() == 0) {
            Thread.sleep(10L);
            if (System.currentTimeMillis() > endTime) {
               throw new ProcessingException("No chars");
            }
         }

         rc = stream.read();
         return rc;
      } catch (Exception var8) {
         ErrorLogHelper.exception(this, "readChar", var8);
         throw new ProcessingException(var8);
      }
   }

   public void burnBuffer(FirmwareFileParser hfp, IVNADriver driver) throws ProcessingException {
      TraceHelper.entry(this, "burnBuffer");
      VNADriverSerialBase serialDriver = (VNADriverSerialBase)driver;
      SerialPort port = serialDriver.getPort();
      serialDriver.flushInputStream();
      OutputStream oStream = null;
      InputStream iStream = null;

      try {
         oStream = port.getOutputStream();
         iStream = port.getInputStream();
         if (this.isAutoReset()) {
            TraceHelper.text(this, "burnBuffer", "sending reset command");
            oStream.write(57);
            oStream.write(57);
            oStream.write(13);
            oStream.flush();
         }

         MegaLoadLoader.MemoryType memoryType = null;

         label599:
         while(true) {
            while(true) {
               int lastChar = this.readChar(port, 5000L);
               TraceHelper.text(this, "burnBuffer", "read [" + (char)lastChar + "] - " + lastChar);
               if (lastChar == 62) {
                  oStream.write(60);
                  oStream.flush();
                  memoryType = MegaLoadLoader.MemoryType.FLASH;
                  this.setPagePtr(0);
                  this.setRetryCount(0);
               } else {
                  if (lastChar == 41) {
                     memoryType = MegaLoadLoader.MemoryType.EEPROM;
                     throw new ProcessingException("EEPROM not supported");
                  }

                  if (lastChar == 33) {
                     if (memoryType == MegaLoadLoader.MemoryType.FLASH) {
                        if (this.sendFlashBlock(oStream, hfp.getFlashMax(), hfp.getFlash())) {
                           break label599;
                        }

                        this.setPagePtr(this.getPagePtr() + 1);
                     } else if (memoryType == MegaLoadLoader.MemoryType.EEPROM) {
                        throw new ProcessingException("EEPROM not supported");
                     }
                  } else if (lastChar == 64) {
                     if (memoryType == MegaLoadLoader.MemoryType.FLASH) {
                        --this.pagePtr;
                        this.sendFlashBlock(oStream, hfp.getFlashMax(), hfp.getFlash());
                        ++this.pagePtr;
                        ++this.retryCount;
                        if (this.retryCount > 3) {
                           oStream.write(255);
                           oStream.write(255);
                           throw new ProcessingException("Retry count exceeded");
                        }
                     } else if (memoryType == MegaLoadLoader.MemoryType.EEPROM) {
                        throw new ProcessingException("EEPROM not supported");
                     }
                  } else {
                     if (lastChar == 37) {
                        throw new ProcessingException("LockBits not supported");
                     }

                     if (lastChar == 65) {
                        this.deviceType = "Mega 8";
                     } else if (lastChar == 66) {
                        this.deviceType = "Mega 16";
                     } else if (lastChar == 67) {
                        this.deviceType = "Mega 64";
                     } else if (lastChar == 68) {
                        this.deviceType = "Mega 128";
                     } else if (lastChar == 69) {
                        this.deviceType = "Mega 32";
                     } else if (lastChar == 70) {
                        this.deviceType = "Mega 162";
                     } else if (lastChar == 71) {
                        this.deviceType = "Mega 169";
                     } else if (lastChar == 72) {
                        this.deviceType = "Mega8515";
                     } else if (lastChar == 73) {
                        this.deviceType = "Mega8535";
                     } else if (lastChar == 74) {
                        this.deviceType = "Mega163";
                     } else if (lastChar == 75) {
                        this.deviceType = "Mega323";
                     } else if (lastChar == 76) {
                        this.deviceType = "Mega48";
                     } else if (lastChar == 77) {
                        this.deviceType = "Mega88";
                     } else if (lastChar == 78) {
                        this.deviceType = "Mega168";
                     } else if (lastChar == 128) {
                        this.deviceType = "Mega165";
                     } else if (lastChar == 129) {
                        this.deviceType = "Mega3250";
                     } else if (lastChar == 130) {
                        this.deviceType = "Mega6450";
                     } else if (lastChar == 131) {
                        this.deviceType = "Mega3290";
                     } else if (lastChar == 132) {
                        this.deviceType = "Mega6490";
                     } else if (lastChar == 133) {
                        this.deviceType = "Mega406";
                     } else if (lastChar == 134) {
                        this.deviceType = "Mega640";
                     } else if (lastChar == 135) {
                        this.deviceType = "Mega1280";
                     } else if (lastChar == 136) {
                        this.deviceType = "Mega2560";
                     } else if (lastChar == 137) {
                        this.deviceType = "MCAN128";
                     } else if (lastChar == 138) {
                        this.deviceType = "Mega164";
                     } else if (lastChar == 139) {
                        this.deviceType = "Mega328";
                     } else if (lastChar == 140) {
                        this.deviceType = "Mega324";
                     } else if (lastChar == 141) {
                        this.deviceType = "Mega325";
                     } else if (lastChar == 142) {
                        this.deviceType = "Mega644";
                     } else if (lastChar == 143) {
                        this.deviceType = "Mega645";
                     } else if (lastChar == 144) {
                        this.deviceType = "Mega1281";
                     } else if (lastChar == 145) {
                        this.deviceType = "Mega2561";
                     } else if (lastChar == 146) {
                        this.deviceType = "Mega2560";
                     } else if (lastChar == 147) {
                        this.deviceType = "Mega404";
                     } else if (lastChar == 148) {
                        this.deviceType = "MUSB1286";
                     } else if (lastChar == 149) {
                        this.deviceType = "MUSB1287";
                     } else if (lastChar == 150) {
                        this.deviceType = "MUSB162";
                     } else if (lastChar == 151) {
                        this.deviceType = "MUSB646";
                     } else if (lastChar == 152) {
                        this.deviceType = "MUSB647";
                     } else if (lastChar == 153) {
                        this.deviceType = "MUSB82";
                     } else if (lastChar == 154) {
                        this.deviceType = "MCAN32";
                     } else if (lastChar == 155) {
                        this.deviceType = "MCAN64";
                     } else if (lastChar == 156) {
                        this.deviceType = "Mega329";
                     } else if (lastChar == 157) {
                        this.deviceType = "Mega649";
                     } else if (lastChar == 158) {
                        this.deviceType = "Mega256";
                     } else if (lastChar == 81) {
                        this.pageSize = 32;
                     } else if (lastChar == 82) {
                        this.pageSize = 64;
                     } else if (lastChar == 83) {
                        this.pageSize = 128;
                     } else if (lastChar == 84) {
                        this.pageSize = 256;
                     } else if (lastChar == 86) {
                        this.pageSize = 512;
                     } else if (lastChar == 97) {
                        this.bootSize = 128;
                     } else if (lastChar == 98) {
                        this.bootSize = 256;
                     } else if (lastChar == 99) {
                        this.bootSize = 512;
                     } else if (lastChar == 100) {
                        this.bootSize = 1024;
                     } else if (lastChar == 101) {
                        this.bootSize = 2048;
                     } else if (lastChar == 102) {
                        this.bootSize = 4096;
                     } else if (lastChar == 103) {
                        this.flashSize = 1024;
                     } else if (lastChar == 104) {
                        this.flashSize = 2048;
                     } else if (lastChar == 105) {
                        this.flashSize = 4096;
                     } else if (lastChar == 108) {
                        this.flashSize = 8192;
                     } else if (lastChar == 109) {
                        this.flashSize = 16384;
                     } else if (lastChar == 110) {
                        this.flashSize = 32768;
                     } else if (lastChar == 111) {
                        this.flashSize = 65536;
                     } else if (lastChar == 112) {
                        this.flashSize = 131072;
                     } else if (lastChar == 113) {
                        this.flashSize = 262144;
                     } else if (lastChar == 114) {
                        this.flashSize = 40960;
                     } else if (lastChar == 46) {
                        this.eEpromSize = 512;
                     } else if (lastChar == 47) {
                        this.eEpromSize = 512;
                     } else if (lastChar == 48) {
                        this.eEpromSize = 512;
                     } else if (lastChar == 49) {
                        this.eEpromSize = 512;
                     } else if (lastChar == 50) {
                        this.eEpromSize = 1024;
                     } else if (lastChar == 51) {
                        this.eEpromSize = 2048;
                     } else if (lastChar == 52) {
                        this.eEpromSize = 4096;
                     }
                  }
               }
            }
         }
      } catch (IOException var19) {
         ErrorLogHelper.exception(this, "burnBuffer", var19);
         throw new ProcessingException(var19);
      } finally {
         if (oStream != null) {
            try {
               oStream.close();
            } catch (IOException var18) {
               ErrorLogHelper.exception(this, "burnBuffer", var18);
            }
         }

         if (iStream != null) {
            try {
               iStream.close();
            } catch (IOException var17) {
               ErrorLogHelper.exception(this, "burnBuffer", var17);
            }
         }

      }

      TraceHelper.text(this, "burnBuffer", "PageSize:   " + this.pageSize + " bytes");
      TraceHelper.text(this, "burnBuffer", "bootSize:   " + this.bootSize + " words");
      TraceHelper.text(this, "burnBuffer", "flashSize:  " + this.flashSize + " bytes");
      TraceHelper.text(this, "burnBuffer", "EEpromSize: " + this.eEpromSize + " bytes");
      TraceHelper.text(this, "burnBuffer", "retryCount: " + this.retryCount);
      TraceHelper.exit(this, "burnBuffer");
   }

   private boolean sendFlashBlock(OutputStream oStream, int flashMax, byte[] flash) throws IOException {
      boolean rc = false;
      TraceHelper.entry(this, "sendFlashBlock");
      TraceHelper.text(this, "sendFlashBlock", "pagePtr:" + this.pagePtr);
      if (this.pagePtr * this.pageSize > flashMax) {
         oStream.write(255);
         oStream.write(255);
         TraceHelper.text(this, "sendFlashBlock", "Last page send");
         rc = true;
      } else {
         this.getMessenger().publishMessage("Sending page " + this.pagePtr + " to device");
         TraceHelper.text(this, "sendFlashBlock", "sending page:" + this.pagePtr);
         oStream.write((byte)(this.pagePtr >> 8 & 255));
         oStream.write((byte)(this.pagePtr & 255));
         byte checkSum = 0;
         int bytesSend = 0;
         TraceHelper.text(this, "sendFlashBlock", "sending data");
         oStream.write(flash, this.pagePtr * this.pageSize, this.pageSize);

         while(bytesSend < this.pageSize) {
            checkSum += flash[this.pagePtr * this.pageSize + bytesSend];
            ++bytesSend;
         }

         TraceHelper.text(this, "sendFlashBlock", "sending checksum");
         oStream.write(checkSum);
      }

      TraceHelper.exit(this, "sendFlashBlock");
      return rc;
   }

   static enum MemoryType {
      FLASH,
      EEPROM;
   }
}
