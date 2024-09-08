package krause.util.ras.logging;

import java.io.PrintWriter;
import java.util.Properties;
import krause.common.exception.InitializationException;

public class ConsoleErrorLogger extends GenericLogger {
   public void initialize(Properties parmProps) throws InitializationException {
      super.initialize(parmProps);
      this.setWriter(new PrintWriter(System.err));
   }
}
