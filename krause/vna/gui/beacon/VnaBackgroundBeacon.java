package krause.vna.gui.beacon;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.firmware.IVNABackgroundFlashBurnerConsumer;
import krause.vna.firmware.SimpleStringListbox;
import krause.vna.firmware.StringMessenger;
import krause.vna.gui.format.VNAFormatFactory;
import org.jdesktop.swingworker.SwingWorker;

public class VnaBackgroundBeacon extends SwingWorker<Integer, String> implements StringMessenger {
   private IVNABackgroundFlashBurnerConsumer consumer = null;
   private SimpleStringListbox listbox = null;
   private IVNADriver driver = null;
   private long frequency = 10000000L;
   private int pause = 1;
   private int bpm = 60;
   private String message;
   private HashMap<Character, String> morseCodes = new HashMap();

   public VnaBackgroundBeacon() {
      this.morseCodes.put('A', "01");
      this.morseCodes.put('B', "1000");
      this.morseCodes.put('C', "1010");
      this.morseCodes.put('D', "100");
      this.morseCodes.put('E', "0");
      this.morseCodes.put('F', "0010");
      this.morseCodes.put('G', "110");
      this.morseCodes.put('H', "0000");
      this.morseCodes.put('I', "00");
      this.morseCodes.put('J', "0111");
      this.morseCodes.put('K', "101");
      this.morseCodes.put('L', "0100");
      this.morseCodes.put('M', "11");
      this.morseCodes.put('N', "10");
      this.morseCodes.put('O', "111");
      this.morseCodes.put('P', "0110");
      this.morseCodes.put('Q', "1101");
      this.morseCodes.put('R', "010");
      this.morseCodes.put('S', "000");
      this.morseCodes.put('T', "1");
      this.morseCodes.put('U', "001");
      this.morseCodes.put('V', "0001");
      this.morseCodes.put('W', "011");
      this.morseCodes.put('X', "1001");
      this.morseCodes.put('Y', "1011");
      this.morseCodes.put('Z', "1100");
      this.morseCodes.put('1', "01111");
      this.morseCodes.put('2', "00111");
      this.morseCodes.put('3', "00011");
      this.morseCodes.put('4', "00001");
      this.morseCodes.put('5', "00000");
      this.morseCodes.put('6', "10000");
      this.morseCodes.put('7', "11000");
      this.morseCodes.put('8', "11100");
      this.morseCodes.put('9', "11110");
      this.morseCodes.put('0', "11111");
      this.morseCodes.put('/', "10010");
      this.morseCodes.put('#', "000101");
      this.morseCodes.put('+', "01010");
      this.morseCodes.put('.', "010101");
      this.morseCodes.put(',', "110011");
      this.morseCodes.put('-', "100001");
      this.morseCodes.put('?', "001100");
      this.morseCodes.put('!', "101011");
      this.morseCodes.put('=', "10001");
      this.morseCodes.put(':', "111000");
   }

   public void setDataConsumer(IVNABackgroundFlashBurnerConsumer pConsumer) {
      TraceHelper.entry(this, "setDataConsumer");
      this.consumer = pConsumer;
      TraceHelper.exit(this, "setDataConsumer");
   }

   public void publishMessage(String message) {
      this.publish(new String[]{message});
   }

   public Integer doInBackground() {
      int rc = 0;
      TraceHelper.entry(this, "doInBackground");

      try {
         this.publish(new String[]{"Starting beacon mode ..."});

         while(!this.isCancelled()) {
            this.sendMorse();
            Thread.sleep((long)(this.pause * 1000));
         }
      } catch (Exception var3) {
         ErrorLogHelper.exception(this, "doInBackground", var3);
         this.publish(new String[]{var3.getMessage()});
         rc = 1;
      }

      TraceHelper.exit(this, "doInBackground");
      return Integer.valueOf(rc);
   }

   private void sendMorse() {
      TraceHelper.entry(this, "sendMorse");
      this.publish(new String[]{VNAFormatFactory.getDateTimeFormat().format(new Date()) + "-" + this.message});

      for(int i = 0; i < this.message.length() && !this.isCancelled(); ++i) {
         char c = this.message.charAt(i);
         this.sendChar(c);
      }

      TraceHelper.exit(this, "sendMorse");
   }

   private void sendChar(char c) {
      int dit = (int)(50.0D / (double)this.bpm * 100.0D);
      TraceHelper.text(this, "sendChar", "" + dit);

      try {
         if (c == ' ') {
            Thread.sleep((long)(4 * dit));
         } else {
            String code = (String)this.morseCodes.get(Character.toUpperCase(c));
            if (code != null) {
               for(int i = 0; i < code.length(); ++i) {
                  this.driver.startGenerator(this.frequency, this.frequency, 0, 0, 0, 0);
                  if (code.charAt(i) == '1') {
                     Thread.sleep((long)(4 * dit));
                  } else {
                     Thread.sleep((long)dit);
                  }

                  this.driver.stopGenerator();
                  Thread.sleep((long)dit);
               }
            }
         }

         Thread.sleep((long)(3 * dit));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   protected void process(List<String> messages) {
      TraceHelper.entry(this, "process");
      if (this.listbox != null) {
         Iterator var3 = messages.iterator();

         while(var3.hasNext()) {
            String message = (String)var3.next();
            this.listbox.addMessage(message);
         }
      }

      TraceHelper.exit(this, "process");
   }

   protected void done() {
      TraceHelper.entry(this, "done");
      this.consumer.consumeReturnCode(0);
      TraceHelper.exit(this, "done");
   }

   public void setListbox(SimpleStringListbox listbox) {
      this.listbox = listbox;
   }

   public void setDriver(IVNADriver driver) {
      this.driver = driver;
   }

   public void setFrequency(long frequency) {
      this.frequency = frequency;
   }

   public void setPause(int pause) {
      this.pause = pause;
   }

   public void setBpm(int bpm) {
      this.bpm = bpm;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
