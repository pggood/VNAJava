package krause.vna.device.serial.tiny;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import krause.util.ras.logging.ErrorLogHelper;

public class VNADriverSerialTinyMessages {
   private static ResourceBundle localeBundle = null;
   public static final String BUNDLE_NAME = "krause.vna.device.serial.tiny.driver_serial_tiny";

   public static ResourceBundle getBundle() {
      return localeBundle;
   }

   public static String getString(String key) {
      if (localeBundle == null) {
         localeBundle = ResourceBundle.getBundle("krause.vna.device.serial.tiny.driver_serial_tiny");
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
