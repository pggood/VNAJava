package krause.vna.device.serial.pro2;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import krause.util.ras.logging.ErrorLogHelper;

public class VNADriverSerialPro2Messages {
   private static ResourceBundle localeBundle = null;
   public static String BUNDLE_NAME = "krause.vna.device.serial.pro2.driver_serial_pro2";

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
