package krause.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import krause.util.ras.logging.ErrorLogHelper;

public class StreamCollector extends Thread {
   InputStream is;
   String codePage;
   StringBuffer sb = null;

   public StreamCollector(InputStream is, String codePage) {
      this.is = is;
      this.sb = new StringBuffer(5000);
      this.codePage = codePage;
      this.setName("StreamCollector");
   }

   public void run() {
      try {
         InputStreamReader isr = new InputStreamReader(this.is, this.codePage);
         BufferedReader br = new BufferedReader(isr);

         while(true) {
            int c = br.read();
            if (this.isInterrupted()) {
               break;
            }

            if (c != -1) {
               this.sb.append((char)c);
            } else {
               sleep(100L);
            }
         }
      } catch (Exception var4) {
         ErrorLogHelper.text(this, "run", var4.toString());
      }

   }

   public String getBuffer() {
      return this.sb.toString();
   }
}
