package krause.util.ras.logging;

import java.io.FileWriter;
import java.util.Properties;
import krause.common.exception.InitializationException;

public class SimpleFileTracer extends GenericTracer {
   public static final String FILENAME = "filename";
   public static final String APPEND = "append";
   private boolean fieldAppend = false;
   private String fieldFilename = null;

   public void setAppend(boolean append) {
      this.fieldAppend = append;
   }

   public boolean isAppend() {
      return this.fieldAppend;
   }

   public void setFilename(String filename) {
      this.fieldFilename = filename;
   }

   public String getFilename() {
      return this.fieldFilename;
   }

   public void initialize(Properties parmProps) throws InitializationException {
      System.out.println(this.getClass().getName() + "::initialize() entry");
      super.initialize(parmProps);

      try {
         this.setFilename((String)parmProps.get("filename"));
         this.setAppend((String)parmProps.get("append") != null);
         System.out.println(this.getClass().getName() + "::initialize() filename=" + this.getFilename());
         System.out.println(this.getClass().getName() + "::initialize() append=" + this.isAppend());
         this.setWriter(new FileWriter(this.getFilename(), this.isAppend()));
         System.out.println(this.getClass().getName() + "::initialize() writer=" + this.getWriter());
      } catch (Exception var3) {
         throw new InitializationException(var3);
      }

      System.out.println(this.getClass().getName() + "::initialize() exit");
   }
}
