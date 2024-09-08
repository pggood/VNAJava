package krause.vna.device;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

public class VNADriverMenuItem extends JMenuItem {
   private String driverClassname;
   private String mathHelperClassName;
   private String type;

   public VNADriverMenuItem() {
   }

   public VNADriverMenuItem(Action a) {
      super(a);
   }

   public VNADriverMenuItem(Icon icon) {
      super(icon);
   }

   public VNADriverMenuItem(String text, Icon icon) {
      super(text, icon);
   }

   public VNADriverMenuItem(String text, int mnemonic) {
      super(text, mnemonic);
   }

   public VNADriverMenuItem(String text) {
      super(text);
   }

   public String getDriverClassname() {
      return this.driverClassname;
   }

   public void setDriverClassname(String driverClassname) {
      this.driverClassname = driverClassname;
   }

   public String getMathHelperClassName() {
      return this.mathHelperClassName;
   }

   public void setMathHelperClassName(String mathHelperClassName) {
      this.mathHelperClassName = mathHelperClassName;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }
}
