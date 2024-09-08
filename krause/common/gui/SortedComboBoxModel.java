package krause.common.gui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

public class SortedComboBoxModel extends DefaultComboBoxModel {
   public SortedComboBoxModel() {
   }

   public SortedComboBoxModel(Object[] itemArray) {
      Arrays.sort(itemArray);

      for(int i = 0; i < itemArray.length; ++i) {
         super.addElement(itemArray[i]);
      }

   }

   public SortedComboBoxModel(Vector<Object> itemVector) {
      Iterator it = itemVector.iterator();

      while(it.hasNext()) {
         this.addElement(it.next());
      }

   }

   public void addElement(Object o) {
      int min = 0;
      int max = this.getSize() - 1;
      int insertAt = -1;

      for(boolean var5 = true; min <= max; ++min) {
         Comparable inList = (Comparable)this.getElementAt(min);
         int compare = inList.compareTo(o);
         if (compare > 0) {
            insertAt = min;
            break;
         }
      }

      if (insertAt == -1) {
         super.addElement(o);
      } else {
         super.insertElementAt(o, insertAt);
      }

      this.setSelectedItem(o);
   }
}
