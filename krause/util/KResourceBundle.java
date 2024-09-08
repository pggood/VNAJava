package krause.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import krause.util.ras.logging.ErrorLogHelper;

public class KResourceBundle {
   private ResourceBundle innerBundle = null;
   private String bundleName = null;

   public KResourceBundle(String bundleName) {
      this.bundleName = bundleName;
   }

   public String getString(String key) {
      if (this.innerBundle == null) {
         this.innerBundle = ResourceBundle.getBundle(this.bundleName);
      }

      String rc;
      try {
         rc = this.innerBundle.getString(key);
      } catch (MissingResourceException var4) {
         rc = "?:" + key + ":?";
         ErrorLogHelper.text(this.innerBundle, "getString", "Ressource [" + key + "] missing");
      }

      return rc;
   }

   public static KResourceBundle getBundle(String bundleName) {
      return new KResourceBundle(bundleName);
   }
}
