package krause.vna.gui.util.tables;

public class VNAProperty {
   private String key;
   private String value;

   public VNAProperty(String k, String v) {
      this.setKey(k);
      this.setValue(v);
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
