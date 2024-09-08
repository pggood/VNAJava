package krause.vna.device.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAGenericDriver;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;
import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

public abstract class VNADriverSerialBase extends VNAGenericDriver {
   public static final int DEFAULT_INPUTBUFFER_SIZE = 32000;
   private static NumberFormat theFormat = null;
   private String appname;
   private CommPort commPort;
   private SerialPort port;

   public static NumberFormat getFrequencyFormat() {
      if (theFormat == null) {
         theFormat = NumberFormat.getNumberInstance(Locale.US);
         theFormat.setGroupingUsed(false);
         theFormat.setMaximumFractionDigits(0);
         theFormat.setMinimumFractionDigits(0);
      }

      return theFormat;
   }

   public boolean checkForDevicePresence(boolean viaSlowConnection) {
      boolean rc = false;
      String methodName = "checkForDevicePresence";
      TraceHelper.entry(this, "checkForDevicePresence");

      try {
         VNADeviceInfoBlock dib = this.getDeviceInfoBlock();
         this.init();
         if (viaSlowConnection) {
            Thread.sleep(4000L);
         }

         this.scan(VNAScanMode.MODE_REFLECTION, dib.getMinFrequency(), dib.getMaxFrequency(), 100, (IVNABackgroundTaskStatusListener)null);
         rc = true;
      } catch (InterruptedException | ProcessingException var8) {
         ErrorLogHelper.exception(this, "checkForDevicePresence", var8);
      } finally {
         this.destroy();
      }

      TraceHelper.exitWithRC(this, "checkForDevicePresence", rc);
      return rc;
   }

   public void flushInputStream() {
      String methodName = "flushInputStream";
      TraceHelper.entry(this, "flushInputStream");
      int cnt = 0;
      int read = 0;
      boolean var4 = false;

      int avl;
      try {
         do {
            avl = this.port.getInputStream().available();
            if (avl > 0) {
               byte[] tempBuffer = new byte[avl];
               read = this.port.getInputStream().read(tempBuffer);
               ErrorLogHelper.text(this, "flushInputStream", "Flushed %d chars", read);
               cnt += read;
               Thread.sleep(20L);
            }
         } while(avl > 0);
      } catch (InterruptedException | IOException var6) {
         ErrorLogHelper.exception(this, "flushInputStream", var6);
      }

      if (cnt > 0) {
         ErrorLogHelper.text(this, "flushInputStream", "total %d chars flushed from stream", cnt);
      }

      TraceHelper.exit(this, "flushInputStream");
   }

   public String getAppname() {
      return this.appname;
   }

   public CommPort getCommPort() {
      return this.commPort;
   }

   public SerialPort getPort() {
      return this.port;
   }

   protected CommPortIdentifier getPortIDForName(String portname) {
      CommPortIdentifier rc = null;
      TraceHelper.entry(this, "getPortIDForName", portname);
      Enumeration portList = CommPortIdentifier.getPortIdentifiers();

      while(portList.hasMoreElements()) {
         CommPortIdentifier aPortId = (CommPortIdentifier)portList.nextElement();
         if (aPortId.getPortType() == 1 && aPortId.getName().equals(this.getPortname())) {
            rc = aPortId;
            break;
         }
      }

      TraceHelper.exitWithRC(this, "getPortIDForName", rc);
      return rc;
   }
public final List<String> getPortList() throws ProcessingException {
    String methodName = "getPortList";
    List<String> rc = new ArrayList<>();
    TraceHelper.entry(this, "getPortList");

    try {
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                String name = portId.getName();
                if (!"tty".equals(name)) {
                    if (name.startsWith("tty")) {
                        String sName = name.substring(3);
                        try {
                            Integer.parseInt(sName);
                        } catch (NumberFormatException e) {
                            // If parsing fails, add the name to the list
                            rc.add(name);
                        }
                    } else {
                        rc.add(name);
                    }
                }
            }
        }
    } catch (Exception e) {
        // Catch any other exceptions and wrap them in ProcessingException
        ErrorLogHelper.exception(this, "getPortList", e);
        throw new ProcessingException(e);
    }

    TraceHelper.exitWithRC(this, "getPortList", rc);
    return rc;
}


   protected int getTimeoutBasedOnNumberOfBytesAndBaudrate(int pNumBytes, int realBaudrate) {
      int bytesPerSec = realBaudrate / 10;
      int rawTime = 1 + pNumBytes / bytesPerSec;
      return rawTime * 1000;
   }

   public void init() throws InitializationException {
      TraceHelper.entry(this, "init");
      super.init();
      this.appname = this.config.getProperty(this.getDriverConfigPrefix() + "appname", "VNA-J2");
      TraceHelper.exit(this, "init");
   }

   protected int readBuffer(byte[] buffer, int offset, int count) throws ProcessingException {
      int rc = -1;
      long endTime = System.currentTimeMillis() + (long)this.port.getReceiveTimeout();
      InputStream stream = null;

      int var10;
      try {
         stream = this.port.getInputStream();

         do {
            if (stream.available() >= count) {
               rc = stream.read(buffer, offset, count);
               return rc;
            }

            Thread.sleep(10L);
         } while(System.currentTimeMillis() <= endTime);

         var10 = rc;
      } catch (InterruptedException | IOException var18) {
         ErrorLogHelper.exception(this, "readBuffer", var18);
         throw new ProcessingException(var18);
      } finally {
         if (stream != null) {
            try {
               stream.close();
            } catch (IOException var17) {
               ErrorLogHelper.exception(this, "readBuffer", var17);
            }
         }

      }

      return var10;
   }

   public int readChar() throws ProcessingException {
      int rc = -1;

      try {
         long endTime = System.currentTimeMillis() + (long)this.port.getReceiveTimeout();
         InputStream stream = this.port.getInputStream();

         while(stream.available() == 0) {
            Thread.sleep(10L);
            if (System.currentTimeMillis() > endTime) {
               return rc;
            }
         }

         rc = stream.read();
         return rc;
      } catch (InterruptedException | IOException var5) {
         ErrorLogHelper.exception(this, "readChar", var5);
         throw new ProcessingException(var5);
      }
   }

   public String readLine(boolean endWithLF) throws ProcessingException {
      StringBuilder sb = new StringBuilder();
      boolean end = false;

      while(!end) {
         int ch = this.readChar();
         if (ch == -1) {
            ProcessingException e = new ProcessingException("Timeout");
            ErrorLogHelper.exception(this, "readLine", e);
            throw e;
         }

         char c = (char)ch;
         if (c == '\r') {
            if (!endWithLF) {
               end = true;
            }
         } else if (c == '\n') {
            if (endWithLF) {
               end = true;
            }
         } else {
            sb.append((char)ch);
         }
      }

      return sb.toString();
   }

   protected byte[] receiveBytestream(int pNumBytes, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
      String methodName = "receiveBytestream";
      TraceHelper.entry(this, "receiveBytestream", "#=%d", pNumBytes);
      InputStream stream = null;
      int remainingBytes = pNumBytes;
      byte[] buffer = new byte[pNumBytes];
      int lastPercentage = 0;

      try {
         stream = this.getPort().getInputStream();
         int readBytes = 0;
         int realBaudrate = this.getDeviceInfoBlock().calculateRealBaudrate(this.getPort().getBaudRate());
         long endTime = System.currentTimeMillis() + (long)this.getTimeoutBasedOnNumberOfBytesAndBaudrate(remainingBytes, realBaudrate);

         while(true) {
            if (remainingBytes <= 0) {
               TraceHelper.text(this, "receiveBytestream", "all bytes read");
               break;
            }

            if (stream.available() > 0) {
               int currBytesRead = stream.read(buffer, readBytes, remainingBytes);
               readBytes += currBytesRead;
               remainingBytes -= currBytesRead;
               if (pListener != null) {
                  int currentPercentage = (int)((double)readBytes * 100.0D / (double)(readBytes + remainingBytes));
                  if (currentPercentage >= lastPercentage + 10) {
                     pListener.publishProgress(currentPercentage);
                     lastPercentage = currentPercentage;
                  }
               }
            }

            long now = System.currentTimeMillis();
            if (now > endTime) {
               String msg = MessageFormat.format(VNAMessages.getString("Timeout"), readBytes, remainingBytes, endTime - now);
               ProcessingException e = new ProcessingException(msg);
               ErrorLogHelper.exception(this, "receiveBytestream", e);
               throw e;
            }

            endTime = now + (long)this.getTimeoutBasedOnNumberOfBytesAndBaudrate(remainingBytes, realBaudrate);
         }
      } catch (IOException var23) {
         ErrorLogHelper.exception(this, "receiveBytestream", var23);
         throw new ProcessingException(var23);
      } finally {
         if (stream != null) {
            try {
               stream.close();
               stream = null;
            } catch (IOException var22) {
               ErrorLogHelper.exception(this, "receiveBytestream", var22);
            }
         }

      }

      TraceHelper.exit(this, "receiveBytestream");
      return buffer;
   }

   protected void sendAsAsciiString(String buffer) throws ProcessingException {
      String methodName = "sendAsAsciiString";
      OutputStream oStream = null;

      try {
         String local = buffer + "\r";
         byte[] tbuf = local.getBytes(StandardCharsets.US_ASCII);
         oStream = this.port.getOutputStream();
         oStream.write(tbuf);
      } catch (IOException var13) {
         ErrorLogHelper.exception(this, "sendAsAsciiString", var13);
         throw new ProcessingException(var13);
      } finally {
         if (oStream != null) {
            try {
               oStream.close();
            } catch (IOException var12) {
               ErrorLogHelper.exception(this, "sendAsAsciiString", var12);
            }
         }

      }

   }

   public void setAppname(String appname) {
      this.appname = appname;
   }

   public void setCommPort(CommPort commPort) {
      this.commPort = commPort;
   }

   public void setPort(SerialPort port) {
      this.port = port;
   }

   public void showDriverNetworkDialog(VNAMainFrame pMF) {
      OptionDialogHelper.showInfoDialog(pMF.getJFrame(), "VNADriverSerialBase.Network.1", "VNADriverSerialBase.Network.2");
   }

   public void wait(int ms) throws ProcessingException {
      String methodName = "wait";
      if (ms > 0) {
         try {
            Thread.sleep((long)ms);
         } catch (InterruptedException var4) {
            ErrorLogHelper.exception(this, "wait", var4);
            throw new ProcessingException(var4);
         }
      }

   }
}
