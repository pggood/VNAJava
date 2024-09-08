package krause.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import krause.common.exception.ProcessingException;

public class URIHelper {
   public static final String SHOW_URI_RESOLVE_INFO = "showURIResolverInfo";

   public static URL getResource(String relativeFileName) throws ProcessingException {
      boolean showLoadInfo = "yes".equalsIgnoreCase(System.getProperty("showURIResolverInfo", "no"));
      if (showLoadInfo) {
         System.out.println("Determining URL for resource: [" + relativeFileName + "]");
      }

      URL url = null;

      try {
         File file = new File(relativeFileName);
         if (file.isAbsolute()) {
            url = new URL("file", (String)null, relativeFileName);
            if (showLoadInfo) {
               System.out.println("Provided resource name is absolute file location, use file URL");
            }
         } else {
            if (showLoadInfo) {
               System.out.println("Provided resource name is relative location");
            }

            url = URIHelper.class.getClassLoader().getResource(relativeFileName);
            if (showLoadInfo) {
               if (url == null) {
                  System.out.println("Provided resource is not found via the classloader");
               } else {
                  System.out.println("Provided resource can be loaded using the classloader");
               }
            }

            if (url == null) {
               url = new URL(relativeFileName);
            }
         }
      } catch (IOException var4) {
         throw new ProcessingException("Resource [" + relativeFileName + "] could not be found. Exception is [" + var4 + "]");
      }

      if (showLoadInfo) {
         System.out.println("Returning URL: " + url);
      }

      return url;
   }

   public static InputStream getResourceAsStream(String relativeFileName) throws ProcessingException {
      URL url = getResource(relativeFileName);

      try {
         return url.openStream();
      } catch (IOException var3) {
         throw new ProcessingException("Resource [" + relativeFileName + "] could not be loaded: " + var3);
      }
   }

   public static boolean isUrlExistent(URL url) {
      try {
         InputStream in = url.openStream();
         in.close();
         return true;
      } catch (IOException var2) {
         return false;
      }
   }
}
