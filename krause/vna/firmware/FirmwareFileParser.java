package krause.vna.firmware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class FirmwareFileParser {
   private static final String LAST_LINE = ":00000001FF";
   private static final int RADIX = 16;
   private static final byte DATA_RECORD = 0;
   private static final byte EOF_RECORD = 1;
   private static final byte EXTENDED_ADDRESS_RECORD = 2;
   private static final byte EXTENDED_LINEAR_ADDRESS_RECORD = 4;
   private int flashMin;
   private int flashMax;
   private int memOffset;
   private int memUsage;
   private byte[] flash = null;
   private boolean intelHexFile = false;
   private File file = null;

   public FirmwareFileParser(File file) {
      this.file = file;
      this.intelHexFile = file.getName().toUpperCase().endsWith(".HEX");
   }

   public void parseFile() throws ProcessingException {
      String methodName = "parseFile";
      TraceHelper.entry(this, "parseFile");
      if (this.isIntelHexFile()) {
         this.parseHexFile();
      } else {
         this.parseBinFile();
      }

      TraceHelper.entry(this, "parseFile");
   }

public void parseBinFile() throws ProcessingException {
    String methodName = "parseBinFile";
    TraceHelper.entry(this, "parseBinFile");
    int fileLen = (int) this.file.length();
    this.flash = new byte[fileLen];

    try (FileInputStream fin = new FileInputStream(this.file)) {
        long numRead = fin.read(this.flash, 0, fileLen);
        TraceHelper.text(this, "parseBinFile", numRead + " bytes read");
    } catch (IOException e) {
        ErrorLogHelper.exception(this, "parseBinFile", e);
        throw new ProcessingException(e);
    }

    TraceHelper.exit(this, "parseBinFile");
}

public void parseHexFile() throws ProcessingException {
    String methodName = "parseHexFile";
    TraceHelper.entry(this, "parseHexFile");
    String record;
    int recordNum = 0;
    this.flashMin = Integer.MAX_VALUE;
    this.flashMax = 0;
    this.memOffset = 0;
    this.memUsage = 0;
    this.flash = new byte[262144];

    try (FileReader fileReader = new FileReader(this.file);
         BufferedReader br = new BufferedReader(fileReader)) {

        while ((record = br.readLine()) != null) {
            byte computedChecksum = 0;
            if (!record.startsWith(":")) {
                throw new ProcessingException("No Intel Hex file");
            }

            ++recordNum;
            byte dataLength = (byte) Integer.parseInt(record.substring(1, 3), 16);
            computedChecksum = (byte) (computedChecksum + dataLength);
            byte addressHi = (byte) Integer.parseInt(record.substring(3, 5), 16);
            computedChecksum += addressHi;
            byte addressLo = (byte) Integer.parseInt(record.substring(5, 7), 16);
            computedChecksum += addressLo;
            int address = Integer.parseInt(record.substring(3, 7), 16);
            byte recordType = (byte) Integer.parseInt(record.substring(7, 9), 16);
            computedChecksum += recordType;

            if (recordType == 0) {
                if (this.flashMin > this.memOffset + address) {
                    this.flashMin = this.memOffset + address;
                }

                for (int i = 0; i < dataLength; ++i) {
                    int x = 9 + 2 * i;
                    this.flash[this.memOffset + address + i] = (byte) Integer.parseInt(record.substring(x, x + 2), 16);
                    computedChecksum += this.flash[this.memOffset + address + i];
                    if (this.flashMax < this.memOffset + address + i) {
                        this.flashMax = this.memOffset + address + i;
                    }
                }
            } else {
                if (recordType == 1) {
                    this.memUsage = this.flashMax - this.flashMin + 1;
                    break;
                }

                if (recordType == 2) {
                    addressHi = (byte) Integer.parseInt(record.substring(9, 11), 16);
                    computedChecksum += addressHi;
                    addressLo = (byte) Integer.parseInt(record.substring(11, 13), 16);
                    computedChecksum += addressLo;
                    this.memOffset = Integer.parseInt(record.substring(9, 13), 16);
                    this.memOffset <<= 4;
                } else if (recordType == 4) {
                    addressHi = (byte) Integer.parseInt(record.substring(9, 11), 16);
                    computedChecksum += addressHi;
                    addressLo = (byte) Integer.parseInt(record.substring(11, 13), 16);
                    computedChecksum += addressLo;
                    this.memOffset = Integer.parseInt(record.substring(9, 13), 16);
                    this.memOffset <<= 16;
                } else {
                    throw new ProcessingException("No valid record identifier [" + recordType + "]");
                }
            }

            byte fileChecksum = (byte) Integer.parseInt(record.substring(record.length() - 2), 16);
            computedChecksum = (byte) (256 - computedChecksum);
            this.memUsage = this.flashMax - this.flashMin + 1;
            if (computedChecksum != fileChecksum) {
                throw new IllegalArgumentException("Invalid checksum in record=" + recordNum + " read=" + fileChecksum + " calc=" + computedChecksum);
            }
        }
    } catch (IOException e) {
        throw new ProcessingException(e);
    }

    TraceHelper.exit(this, "parseHexFile");
}

   public int getFlashMin() {
      return this.flashMin;
   }

   public int getFlashMax() {
      return this.flashMax;
   }

   public int getMemOffset() {
      return this.memOffset;
   }

   public int getMemUsage() {
      return this.memUsage;
   }

   public byte[] getFlash() {
      return this.flash;
   }

   public String getFlashAsHexFileLine(int address, int length) {
      StringBuilder rc = new StringBuilder(":");
      byte cs = 0;
      rc.append(String.format("%02X", length));
      cs = (byte)(cs + length);
      rc.append(String.format("%04X", address));
      cs = (byte)(cs + address / 256);
      cs = (byte)(cs + address % 256);
      rc.append(String.format("%02X", 0));
      cs = (byte)(cs + 0);

      for(int i = 0; i < length; ++i) {
         byte b = this.flash[address + i];
         rc.append(String.format("%02X", b));
         cs += b;
      }

      cs = (byte)(256 - cs);
      rc.append(String.format("%02X", cs));
      return rc.toString();
   }

   public String getLastHexFileLine() {
      return ":00000001FF";
   }

   public boolean isIntelHexFile() {
      return this.intelHexFile;
   }
}
