package krause.util.ras.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;

public class GenericLogger implements Logger {
   private boolean fieldShortClassname = false;
   private Writer fieldWriter = null;
   public static final String SHORTCLASSNAME = "shortclassname";

   public boolean isShortClassname() {
      return this.fieldShortClassname;
   }

   public void setShortClassname(boolean shortClassname) {
      this.fieldShortClassname = shortClassname;
   }

   public void destroy() throws ProcessingException {
      try {
         if (this.getWriter() != null) {
            this.getWriter().flush();
            this.getWriter().close();
         }

      } catch (Exception var2) {
         throw new ProcessingException(var2);
      }
   }

   public void initialize(Properties parmProps) throws InitializationException {
      try {
         this.setShortClassname("true".equalsIgnoreCase((String)parmProps.get("shortclassname")));
      } catch (Exception var3) {
         throw new InitializationException(var3);
      }
   }

   public void text(Object theCaller, String theMethod, String theMsg, Object... theParms) {
      StringBuilder sbData = new StringBuilder();
      sbData.append(this.buildLineHeader(theCaller, theMethod)).append(' ').append(String.format(theMsg, theParms)).append(GlobalSymbols.LINE_SEPARATOR);

      try {
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (IOException var7) {
      }

   }

   protected StringBuilder buildLineHeader(Object theCaller, String theMethod) {
      StringBuilder sbData = new StringBuilder(100);
      sbData.append(DateFormat.getDateTimeInstance().format(new Date()));
      String classname = theCaller.getClass().getName();
      if (this.isShortClassname()) {
         int i = classname.lastIndexOf(46);
         if (i != -1) {
            classname = classname.substring(i + 1);
         }
      }

      sbData.append(" L:").append(classname).append("::").append(theMethod).append("() ");
      return sbData;
   }

   public void setWriter(Writer writer) {
      this.fieldWriter = writer;
   }

   public Writer getWriter() {
      return this.fieldWriter;
   }

   public void text(Object theCaller, String theMethod, Exception theExc) {
      StringBuffer sbData = new StringBuffer();
      sbData.append(this.buildLineHeader(theCaller, theMethod));
      sbData.append(theExc.toString()).append("\n");

      try {
         this.getWriter().write(sbData.toString());
         theExc.printStackTrace(new PrintWriter(this.getWriter()));
         this.getWriter().flush();
      } catch (IOException var6) {
      }

   }
}
