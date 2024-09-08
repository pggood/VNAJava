package krause.common.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import krause.util.ras.logging.ErrorLogHelper;

public class CommonMessages {
   private static ResourceBundle localeBundle = null;
   public static String BUNDLE_NAME = "krause.common.resources.messages";

   public static ResourceBundle getBundle() {
      return localeBundle;
   }

   public static String getString(String key) {
      if (localeBundle == null) {
         localeBundle = ResourceBundle.getBundle(BUNDLE_NAME);
      }

      String rc;
      try {
         rc = localeBundle.getString(key);
      } catch (MissingResourceException var3) {
         rc = key;
         ErrorLogHelper.text(localeBundle, "getString", "Ressource [" + key + "] missing");
      }

      return rc;
   }

   public static String getBUNDLE_NAME() {
      return BUNDLE_NAME;
   }
}
