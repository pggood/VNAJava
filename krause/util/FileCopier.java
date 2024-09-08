package krause.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileCopier {
   public static InputStream getInputStream(String fileName) throws IOException {
      Object input;
      if (fileName.startsWith("http:")) {
         URL url = new URL(fileName);
         URLConnection connection = url.openConnection();
         input = connection.getInputStream();
      } else {
         input = new FileInputStream(fileName);
      }

      return (InputStream)input;
   }

   public static OutputStream getOutputStream(String fileName) throws IOException {
      return new FileOutputStream(fileName);
   }

   public static int copy(InputStream in, OutputStream out) throws IOException {
      int bytesCopied = 0;
      byte[] buffer = new byte[4096];

      int bytes;
      try {
         while((bytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
            bytesCopied += bytes;
         }
      } finally {
         in.close();
         out.close();
      }

      return bytesCopied;
   }
}
