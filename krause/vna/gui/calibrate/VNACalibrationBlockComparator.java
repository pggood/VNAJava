package krause.vna.gui.calibrate;

import java.util.Comparator;
import krause.vna.data.calibrated.VNACalibrationBlock;

public class VNACalibrationBlockComparator implements Comparator<VNACalibrationBlock> {
   public int compare(VNACalibrationBlock object1, VNACalibrationBlock object2) {
      return object1 != null && object2 != null ? object1.getFile().getName().compareTo(object2.getFile().getName()) : 0;
   }
}
