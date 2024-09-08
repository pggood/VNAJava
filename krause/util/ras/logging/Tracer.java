package krause.util.ras.logging;

import java.util.Properties;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;

public interface Tracer {
   void destroy() throws ProcessingException;

   void entry(Object var1, String var2);

   void exit(Object var1, String var2);

   void initialize(Properties var1) throws InitializationException;

   void text(Object var1, String var2, String var3, Object... var4);

   void entry(Object var1, String var2, String var3, Object... var4);

   void entry(Object var1, String var2, String var3);

   void exitWithRC(Object var1, String var2, Object var3);

   void exitWithRC(Object var1, String var2, String var3, Object... var4);
}
