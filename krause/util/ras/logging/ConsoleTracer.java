package krause.util.ras.logging;

import java.io.PrintWriter;

public class ConsoleTracer extends GenericTracer {
   public ConsoleTracer() {
      this.setWriter(new PrintWriter(System.err));
   }
}
