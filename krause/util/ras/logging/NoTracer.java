package krause.util.ras.logging;

public class NoTracer extends GenericTracer {
   public void entry(Object theCaller, String theMethod, Object... theParms) {
   }

   public void entry(Object theCaller, String theMethod) {
   }

   public void exit(Object theCaller, String theMethod) {
   }

   public void exitWithRC(Object theCaller, String theMethod, Object rc) {
   }

   public void exitWithRC(Object theCaller, String theMethod, Object... theParms) {
   }

   public void text(Object theCaller, String theMethod, String theMsg) {
   }
}
