package krause.vna.data.observer;

import krause.vna.data.calibrated.VNACalibrationBlock;

public interface VNACalibrationBlockObserver {
   void blockChanged(VNACalibrationBlock var1, VNACalibrationBlock var2);
}
