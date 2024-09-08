package krause.vna.resources;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import krause.util.SortedProperties;

public class VNAMessageExtractor2 {
   public static void main(String[] args) {
      extractMessagesForLocale(new Locale("cs", "CZ"));
      extractMessagesForLocale(new Locale("de", "DE"));
      extractMessagesForLocale(new Locale("es", "ES"));
      extractMessagesForLocale(new Locale("fr", "FR"));
      extractMessagesForLocale(new Locale("hu", "HU"));
      extractMessagesForLocale(new Locale("it", "IT"));
      extractMessagesForLocale(new Locale("ja", "JP"));
      extractMessagesForLocale(new Locale("nl", "NL"));
      extractMessagesForLocale(new Locale("pl", "PL"));
      extractMessagesForLocale(new Locale("ru", "RUS"));
      extractMessagesForLocale(new Locale("sv", "SE"));
      extractMessagesForLocale(new Locale("en", "US"));
   }

   public static void extractMessagesForLocale(Locale locale) {
      SortedProperties localeProps = new SortedProperties();
      Locale.setDefault(locale);
      ResourceBundle bundle = ResourceBundle.getBundle(VNAMessages.BUNDLE_NAME);
      Enumeration keys = bundle.getKeys();

      String filename;
      while(keys.hasMoreElements()) {
         filename = (String)keys.nextElement();
         String val = bundle.getString(filename);
         if (!filename.endsWith(".Command") && !filename.endsWith(".Image") && !filename.endsWith(".Key") && !filename.endsWith(".URL") && !filename.endsWith(".copyright") && !filename.endsWith(".date") && !filename.endsWith(".version")) {
            localeProps.put(filename, val);
         }
      }

      filename = "temp/VNAMessage_" + locale.getLanguage().toLowerCase() + ".properties";

      try {
         OutputStream os = new FileOutputStream(filename);
         localeProps.store(os, "Extracted by VNAMessageExtractor2 for " + locale.toString() + "\n\rPlease use only\n\r  http://propedit.sourceforge.jp/propertieseditor.jnlp\n\rto edit this file!!!");
         System.out.println("File [" + filename + "] saved");
         os.close();
      } catch (FileNotFoundException var7) {
         var7.printStackTrace();
      } catch (IOException var8) {
         var8.printStackTrace();
      }

   }
}
