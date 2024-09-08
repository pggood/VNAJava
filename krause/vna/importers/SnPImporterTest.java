package krause.vna.importers;

import junit.framework.TestCase;
import krause.common.exception.ProcessingException;

public class SnPImporterTest extends TestCase {
   public void test1() {
      SnPImporter importer = new SnPImporter();

      try {
         SnPInfoBlock ib = importer.readFile("C:/Users/Dietmar/vnaJ.3.4/reference/dlawik_T37-2_10zw-nanoVNA.s1p", "US-ASCII");
         System.out.println(ib);
      } catch (ProcessingException var3) {
         var3.printStackTrace();
      }

   }
}
