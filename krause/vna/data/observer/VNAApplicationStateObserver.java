package krause.vna.data.observer;

import krause.vna.data.VNAApplicationState;

public interface VNAApplicationStateObserver {
   void changeState(VNAApplicationState.INNERSTATE var1, VNAApplicationState.INNERSTATE var2);
}
