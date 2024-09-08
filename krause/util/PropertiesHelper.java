package krause.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import krause.common.exception.PropertyNotFoundException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class PropertiesHelper {
   private PropertiesHelper() {
      String methodName = "PropertiesHelper";
      TraceHelper.entry(this, "PropertiesHelper");
      TraceHelper.exit(this, "PropertiesHelper");
   }

   public static Properties load(String fullFileName) {
      try {
         Properties properties = new Properties();
         InputStream in = PropertiesLoader.getResourceAsStream(fullFileName);

         try {
            properties.load(in);
         } finally {
            in.close();
         }

         return properties;
      } catch (IOException var7) {
         return null;
      }
   }

   public static Properties createProperties(Properties props, String key) {
      return createProperties(props, key, false);
   }

   public static Properties createProperties(Properties props, String key, boolean chopOffKey) {
      Properties newProps = new Properties();
      Enumeration<Object> keys = props.keys();
      String oldKey = null;
      String newKey = null;

      while(keys.hasMoreElements()) {
         oldKey = (String)keys.nextElement();
         if (oldKey.startsWith(key)) {
            newKey = oldKey;
            if (chopOffKey) {
               newKey = oldKey.substring(key.length(), oldKey.length());
            }

            newProps.put(newKey, props.getProperty(oldKey));
         }
      }

      return newProps;
   }

   public static String getProperty(Properties props, String key) throws PropertyNotFoundException {
      String prop = props.getProperty(key);
      if (prop != null && prop.length() != 0) {
         return prop;
      } else {
         throw new PropertyNotFoundException(key);
      }
   }
/*
   public static Properties loadXMLProperties(String userFileName, Properties defaultProperties) {
      Properties rc = new Properties();

      try {
         Throwable var3 = null;
         Object var4 = null;

         try {
            FileInputStream is = new FileInputStream(userFileName);

            try {
               rc.loadFromXML(is);
            } finally {
               if (is != null) {
                  is.close();
               }

            }
         } catch (Throwable var13) {
            if (var3 == null) {
               var3 = var13;
            } else if (var3 != var13) {
               var3.addSuppressed(var13);
            }

            throw var3;
         }
      } catch (IOException var14) {
         rc = defaultProperties;
      }

      return rc;
   }

   public static void saveXMLProperties(Properties props, String userFileName) {
      String methodName = "saveXMLProperties";
      TraceHelper.entry("", "saveXMLProperties", userFileName);

      try {
         Throwable var3 = null;
         Object var4 = null;

         try {
            FileOutputStream os = new FileOutputStream(userFileName);

            try {
               props.storeToXML(os, (new Date()).toString());
            } finally {
               if (os != null) {
                  os.close();
               }

            }
         } catch (Throwable var13) {
            if (var3 == null) {
               var3 = var13;
            } else if (var3 != var13) {
               var3.addSuppressed(var13);
            }

            throw var3;
         }
      } catch (IOException var14) {
         ErrorLogHelper.exception("", "saveXMLProperties", var14);
      }

      TraceHelper.exit("", "saveXMLProperties");
   }
   
   */
   public static Properties loadXMLProperties(String userFileName, Properties defaultProperties) {
    Properties rc = new Properties();

    FileInputStream is = null;
    try {
        // Open file input stream
        is = new FileInputStream(userFileName);

        // Load properties from XML
        rc.loadFromXML(is);
    } catch (IOException e) {
        // Handle IOExceptions that may occur
        rc = defaultProperties;
    } finally {
        // Ensure the input stream is closed
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // Log or handle the exception during closing
                ErrorLogHelper.exception("", "loadXMLProperties", e);
            }
        }
    }

    return rc;
}
   public static void saveXMLProperties(Properties props, String userFileName) {
    String methodName = "saveXMLProperties";
    TraceHelper.entry("", methodName, userFileName);

    FileOutputStream os = null;
    try {
        // Open file output stream
        os = new FileOutputStream(userFileName);

        // Store properties to XML
        props.storeToXML(os, (new Date()).toString());
    } catch (IOException e) {
        // Handle IOExceptions that may occur
        ErrorLogHelper.exception("", methodName, e);
    } finally {
        // Ensure the output stream is closed
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                // Handle the exception during closing
                ErrorLogHelper.exception("", methodName, e);
            }
        }
    }

    TraceHelper.exit("", methodName);
  }
}
