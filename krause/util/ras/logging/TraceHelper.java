package krause.util.ras.logging;

public class TraceHelper {
   public static void text(Object theCaller, String theMethod, String theFormat, Object... theParms) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().text(theCaller, theMethod, theFormat, theParms);
      }

   }

   public static void entry(Object theCaller, String theMethod) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().entry(theCaller, theMethod);
      }

   }

   public static void exit(Object theCaller, String theMethod) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().exit(theCaller, theMethod);
      }

   }

   public static void entry(Object theCaller, String theMethod, String theFormat, Object... theParms) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().entry(theCaller, theMethod, theFormat, theParms);
      }

   }

   public static void entry(Object theCaller, String theMethod, String text) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().entry(theCaller, theMethod, text);
      }

   }

   public static void exitWithRC(Object theCaller, String theMethod, Object rc) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().exitWithRC(theCaller, theMethod, rc);
      }

   }

   public static void exitWithRC(Object theCaller, String theMethod, String theFormat, Object... rc) {
      if (LogManager.getSingleton().isTracingEnabled()) {
         LogManager.getSingleton().getTracer().exitWithRC(theCaller, theMethod, theFormat, rc);
      }

   }
}
