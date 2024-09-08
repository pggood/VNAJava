package krause.vna.gui.panels.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;

public class VNAScanModeComboBox extends JComboBox<VNAScanModeParameter> {
   public VNAScanModeComboBox() {
      this.setModel(new VNAScanModeComboBoxModel());
      this.setEditable(false);
   }

   public VNAScanMode getSelectedMode() {
      VNAScanModeParameter req = (VNAScanModeParameter)this.getModel().getSelectedItem();
      return req.getMode();
   }

   public void setSelectedMode(VNAScanMode scanMode) {
      VNAScanModeComboBoxModel mod = (VNAScanModeComboBoxModel)this.getModel();
      List<VNAScanModeParameter> modes = mod.getModes();
      Iterator var5 = modes.iterator();

      while(var5.hasNext()) {
         VNAScanModeParameter mode = (VNAScanModeParameter)var5.next();
         if (mode.getMode().equals(scanMode)) {
            this.setSelectedItem(mode);
            break;
         }
      }

   }

   public void setModes(Map<VNAScanMode, VNAScanModeParameter> scanModeParameters) {
      this.removeAllItems();
      Iterator var3 = scanModeParameters.keySet().iterator();

      while(var3.hasNext()) {
         VNAScanMode key = (VNAScanMode)var3.next();
         VNAScanModeParameter ent = (VNAScanModeParameter)scanModeParameters.get(key);
         this.addItem(ent);
      }

   }
}
