package krause.vna.device.serial.std2;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import krause.util.ras.logging.ErrorLogHelper;

public class VNADriverSerialStd2Messages {
   private static ResourceBundle localeBundle = null;
   public static final String BUNDLE_NAME = "krause.vna.device.serial.std2.driver_serial_std2";

   private VNADriverSerialStd2Messages() {
   }

   public static ResourceBundle getBundle() {
      return localeBundle;
   }

   public static String getString(String key) {
      if (localeBundle == null) {
         localeBundle = ResourceBundle.getBundle("krause.vna.device.serial.std2.driver_serial_std2");
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
