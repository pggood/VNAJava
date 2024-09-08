package krause.vna.gui.calibrate.mode1;

import java.util.Comparator;

public class VNACalibrationRangeComparator implements Comparator<VNACalibrationRange> {
   public int compare(VNACalibrationRange f1, VNACalibrationRange f2) {
      if (f1 != null && f2 != null) {
         if (f1.getStart() > f2.getStart()) {
            return 1;
         } else if (f1.getStart() < f2.getStart()) {
            return -1;
         } else if (f1.getStop() > f2.getStop()) {
            return 1;
         } else {
            return f1.getStop() < f2.getStop() ? -1 : 0;
         }
      } else {
         return 0;
      }
   }
}
