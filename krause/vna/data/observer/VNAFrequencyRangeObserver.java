package krause.vna.data.observer;

import krause.vna.data.VNAFrequencyRange;

public interface VNAFrequencyRangeObserver extends VNAObserver {
   void changeRange(VNAFrequencyRange var1, VNAFrequencyRange var2);
}
