package krause.util.ras.logging;

import java.io.Writer;
import java.util.Properties;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;

public interface Logger {
   void destroy() throws ProcessingException;

   void initialize(Properties var1) throws InitializationException;

   void text(Object var1, String var2, String var3, Object... var4);

   void text(Object var1, String var2, Exception var3);

   Writer getWriter();
}
