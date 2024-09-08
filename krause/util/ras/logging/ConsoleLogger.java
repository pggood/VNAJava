package krause.util.ras.logging;

import java.io.PrintWriter;

public class ConsoleLogger extends GenericLogger {
   public ConsoleLogger() {
      this.setWriter(new PrintWriter(System.out));
   }
}
