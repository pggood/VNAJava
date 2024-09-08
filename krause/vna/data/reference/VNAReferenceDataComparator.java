package krause.vna.data.reference;

import java.util.Comparator;

public class VNAReferenceDataComparator implements Comparator<VNAReferenceDataBlock> {
   public int compare(VNAReferenceDataBlock object1, VNAReferenceDataBlock object2) {
      return object1 != null && object2 != null && object1.getFile() != null && object2.getFile() != null ? object1.getFile().getName().compareTo(object2.getFile().getName()) : 0;
   }
}
