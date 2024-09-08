package krause.vna.gui.calibrate.calibrationkit;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import krause.vna.data.calibrationkit.VNACalibrationKit;

public class VNACalibrationKitTableListModel extends DefaultListModel<VNACalibrationKit> {
   public List<VNACalibrationKit> getData() {
      List<VNACalibrationKit> rc = new ArrayList();

      for(int i = 0; i < this.getSize(); ++i) {
         rc.add((VNACalibrationKit)this.getElementAt(i));
      }

      return rc;
   }
}
