package krause.vna.gui.panels.data;

import java.util.ArrayList;
import java.util.List;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;
import krause.vna.device.VNAScanModeParameter;

public class VNAScanModeComboBoxModel implements MutableComboBoxModel {
   private List<VNAScanModeParameter> modes = new ArrayList();
   private int selectedIndex = -1;

   public Object getSelectedItem() {
      Object rc = null;
      if (this.selectedIndex != -1 && this.modes != null && this.selectedIndex >= 0 && this.selectedIndex < this.modes.size()) {
         rc = this.modes.get(this.selectedIndex);
      }

      return rc;
   }

   public void setSelectedItem(Object arg0) {
      if (this.modes != null) {
         this.selectedIndex = this.modes.indexOf(arg0);
      }

   }

   public void addListDataListener(ListDataListener arg0) {
   }

   public Object getElementAt(int arg0) {
      Object rc = null;
      if (arg0 >= 0 && arg0 < this.modes.size()) {
         rc = this.modes.get(arg0);
      }

      return rc;
   }

   public int getSize() {
      int rc = 0;
      if (this.modes != null) {
         rc = this.modes.size();
      }

      return rc;
   }

   public void removeListDataListener(ListDataListener arg0) {
   }

   public void addElement(Object arg0) {
      VNAScanModeParameter smp = (VNAScanModeParameter)arg0;
      if (this.modes.size() == 0) {
         this.modes.add(smp);
      } else if (smp.toString().compareToIgnoreCase(((VNAScanModeParameter)this.modes.get(0)).toString()) <= 0) {
         this.modes.add(0, smp);
      } else if (smp.toString().compareToIgnoreCase(((VNAScanModeParameter)this.modes.get(this.modes.size() - 1)).toString()) >= 0) {
         this.modes.add(smp);
      } else {
         int i;
         for(i = 0; smp.toString().compareToIgnoreCase(((VNAScanModeParameter)this.modes.get(i)).toString()) > 0; ++i) {
         }

         this.modes.add(i, smp);
      }

   }

   public void insertElementAt(Object arg0, int arg1) {
      this.modes.add(arg1, (VNAScanModeParameter)arg0);
   }

   public void removeElement(Object arg0) {
      this.modes.remove(arg0);
   }

   public void removeElementAt(int arg0) {
      this.modes.remove(arg0);
   }

   public List<VNAScanModeParameter> getModes() {
      return this.modes;
   }
}
