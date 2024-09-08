package krause.util.ras.logging;

public class ApplicationLogHelper {
   public static void text(Object theCaller, String theMethod, String theMsg) {
      if (LogManager.getSingleton().isApplicationLoggingEnabled()) {
         LogManager.getSingleton().getApplicationLogger().text(theCaller, theMethod, theMsg);
      }

   }
}
