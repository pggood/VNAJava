package krause.vna.config;

import java.util.Properties;
import krause.util.PropertiesHelper;

public class VNASystemConfig {
   public static final String VNA_PREFIX = "VNA.";
   private static Properties systemProperties = PropertiesHelper.load("system.properties");

   public static String getVNA_HOME_DIR() {
      return System.getProperty("user.home") + System.getProperty("file.separator") + systemProperties.getProperty("VNA_HOME_DIR");
   }

   public static String getVNA_UPDATEURL() {
      return systemProperties.getProperty("VNA_UPDATEURL");
   }

   public static VNASystemConfig.OS_PLATFORM getPlatform() {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.startsWith("mac os x")) {
         return VNASystemConfig.OS_PLATFORM.MAC;
      } else {
         return os.startsWith("windows") ? VNASystemConfig.OS_PLATFORM.WINDOWS : VNASystemConfig.OS_PLATFORM.ALL;
      }
   }

   public static enum OS_PLATFORM {
      ALL,
      WINDOWS,
      MAC,
      UNIX;
   }
}
