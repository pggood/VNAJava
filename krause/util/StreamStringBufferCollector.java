package krause.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import krause.util.ras.logging.ErrorLogHelper;

public class StreamStringBufferCollector extends Thread {
   InputStream is;
   String codePage;
   StringWriter os;

   public StreamStringBufferCollector(InputStream is, String codePage) {
      this.is = is;
      this.codePage = codePage;
      this.os = new StringWriter(4096);
      this.setName("StreamCollector");
   }

   public void run() {
      try {
         InputStreamReader isr = new InputStreamReader(this.is, this.codePage);
         BufferedReader br = new BufferedReader(isr);
         String line = null;

         while((line = br.readLine()) != null) {
            this.os.write(line);
            this.os.write(GlobalSymbols.LINE_SEPARATOR);
         }

         if (this.os != null) {
            this.os.flush();
         }
      } catch (Exception var4) {
         ErrorLogHelper.text(this, "run", var4.toString());
      }

   }

   public String getBuffer() {
      return this.os.toString();
   }
}
