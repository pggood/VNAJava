package krause.util.ras.logging;

public class LogHelper {
   public static void text(Object theCaller, String theMethod, String theMsg) {
      if (LogManager.getSingleton().isErrorLoggingEnabled()) {
         LogManager.getSingleton().getErrorLogger().text(theCaller, theMethod, theMsg);
      }

   }
}
