package krause.vna.gui.calibrate.calibrationkit;

import java.util.List;
import javax.swing.JList;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;

public class VNACalibrationKitTable extends JList<VNACalibrationKit> {
   public VNACalibrationKitTable() {
      TraceHelper.entry(this, "VNACalSetTable");
      this.setModel(new VNACalibrationKitTableListModel());
      this.setSelectionMode(0);
      TraceHelper.exit(this, "VNACalSetTable");
   }

   public void addCalSet(VNACalibrationKit newCalSet) {
      VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.getModel();
      model.addElement(newCalSet);
   }

   public void removeCalSet(VNACalibrationKit point) {
      VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.getModel();
      model.removeElement(point);
   }

   public void updateCalSet(VNACalibrationKit calSetToUpdate) {
      VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.getModel();
      List<VNACalibrationKit> elements = model.getData();

      for(int i = 0; i < elements.size(); ++i) {
         VNACalibrationKit element = (VNACalibrationKit)elements.get(i);
         if (element.getId().equals(calSetToUpdate.getId())) {
            model.set(i, calSetToUpdate);
            break;
         }
      }

   }
}
