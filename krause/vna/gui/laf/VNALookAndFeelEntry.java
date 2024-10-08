package krause.vna.gui.laf;

public class VNALookAndFeelEntry {
   private int id;
   private String name;

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public VNALookAndFeelEntry(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public String toString() {
      return this.name;
   }
}
