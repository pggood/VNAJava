package krause.util;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
   public int compare(String o1, String o2) {
      if (o1 != null) {
         return o1.compareTo(o2);
      } else {
         return o1 == null && o2 == null ? 0 : 1;
      }
   }
}
