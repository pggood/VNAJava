package krause.util.ras.logging;

public final class ErrorLogHelper {
   private ErrorLogHelper() {
   }

   public static void text(Object theCaller, String theMethod, String theFormat, Object... theParms) {
      if (LogManager.getSingleton().isErrorLoggingEnabled()) {
         LogManager.getSingleton().getErrorLogger().text(theCaller, theMethod, theFormat, theParms);
      }

   }

   public static void exception(Object theCaller, String theMethod, Exception e) {
      if (LogManager.getSingleton().isErrorLoggingEnabled()) {
         Logger logger = LogManager.getSingleton().getErrorLogger();
         logger.text(theCaller, theMethod, e);
      }

   }
}
