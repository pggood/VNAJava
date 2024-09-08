package krause.vna.device.serial.std;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import krause.util.ras.logging.ErrorLogHelper;

public class VNADriverSerialStdMessages {
   private static ResourceBundle localeBundle = null;
   public static final String BUNDLE_NAME = "krause.vna.device.serial.std.driver_serial_std";

   private VNADriverSerialStdMessages() {
   }

   public static ResourceBundle getBundle() {
      return localeBundle;
   }

   public static String getString(String key) {
      if (localeBundle == null) {
         localeBundle = ResourceBundle.getBundle("krause.vna.device.serial.std.driver_serial_std");
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
}
