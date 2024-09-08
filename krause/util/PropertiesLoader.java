package krause.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertiesLoader {
   public static final InputStream getResourceAsStream(String name) throws IOException {
      PropertiesLoader p = new PropertiesLoader();
      return p.getDynamiceResourceAsStream(name);
   }

   public static final Properties getPropertiesFromFile(String propertiesFile) throws IOException {
      Properties props = new Properties();
      InputStream is = null;

      try {
         is = getResourceAsStream(propertiesFile);
         props.load(is);
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch (IOException var8) {
         }

      }

      return props;
   }

   private InputStream getDynamiceResourceAsStream(String resource) throws IOException {
      URL url = this.getClass().getClassLoader().getResource(resource);
      return url.openStream();
   }
}
