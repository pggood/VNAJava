package krause.vna.data.observer;

import krause.vna.data.VNAScanMode;

public interface VNAScanModeObserver extends VNAObserver {
   void changeMode(VNAScanMode var1, VNAScanMode var2);
}
