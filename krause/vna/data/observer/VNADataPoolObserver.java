package krause.vna.data.observer;

public interface VNADataPoolObserver extends VNAObserver {
   void dataChanged(VNADataPoolObserver.CHANGEDOBJECT var1, Object var2, Object var3);

   public static enum CHANGEDOBJECT {
      SCANMODE;
   }
}
