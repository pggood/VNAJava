package krause.util.ras.logging;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Properties;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;

public class GenericTracer implements Tracer {
   private boolean fieldShortClassname = false;
   private Writer fieldWriter = null;
   public static final String SHORTCLASSNAME = "shortclassname";

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

   public void setShortClassname(boolean shortClassname) {
      this.fieldShortClassname = shortClassname;
   }

   public boolean isShortClassname() {
      return this.fieldShortClassname;
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

      sbData.append(" T:").append(classname).append("::").append(theMethod).append("()");
      return sbData;
   }

   public void setWriter(Writer writer) {
      this.fieldWriter = writer;
   }

   public Writer getWriter() {
      return this.fieldWriter;
   }

   public void text(Object theCaller, String theMethod, String theFormat, Object... theMsgParms) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append(' ').append(String.format(theFormat, theMsgParms)).append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (Exception var6) {
      }

   }

   public void entry(Object theCaller, String theMethod, String format, Object... theParms) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append("-entry ");
         sbData.append(String.format(format, theParms));
         sbData.append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (IOException | IllegalFormatException var6) {
         ErrorLogHelper.exception(this, "entry", var6);
      }

   }

   public void entry(Object theCaller, String theMethod, String text) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append("-entry ");
         sbData.append(text);
         sbData.append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (Exception var5) {
      }

   }

   public void exitWithRC(Object theCaller, String theMethod, Object rc) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append("- exit RC=[").append(rc).append("]").append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (Exception var5) {
      }

   }

   public void exitWithRC(Object theCaller, String theMethod, String format, Object... theParms) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append("- exit ");
         sbData.append(String.format(format, theParms));
         sbData.append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (Exception var6) {
      }

   }

   public void entry(Object theCaller, String theMethod) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append("-entry").append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (Exception var4) {
      }

   }

   public void exit(Object theCaller, String theMethod) {
      try {
         StringBuilder sbData = this.buildLineHeader(theCaller, theMethod);
         sbData.append("-exit").append(GlobalSymbols.LINE_SEPARATOR);
         this.getWriter().write(sbData.toString());
         this.getWriter().flush();
      } catch (Exception var4) {
      }

   }
}
