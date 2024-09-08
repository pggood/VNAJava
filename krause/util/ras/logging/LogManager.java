package krause.util.ras.logging;

import java.util.Properties;
import krause.common.exception.InitializationException;
import krause.util.PropertiesHelper;
import krause.util.component.ManageableComponent;

public class LogManager implements ManageableComponent {
   public static final String CLASSNAME = "classname";
   public static final String ERRORLOGGER_PREFIX = "ErrorLogger.";
   public static final String ERRORLOGGER_CLASSNAME = "ErrorLogger.classname";
   public static final String ERRORLOGGER_ENABLE = "ErrorLogger.logging";
   public static final String APPLOGGER_PREFIX = "ApplicationLogger.";
   public static final String APPLOGGER_CLASSNAME = "ApplicationLogger.classname";
   public static final String APPLOGGER_ENABLE = "ApplicationLogger.logging";
   public static final String TRACER_PREFIX = "Tracer.";
   public static final String TRACER_CLASSNAME = "Tracer.classname";
   public static final String TRACER_ENABLE = "Tracer.tracing";
   private static LogManager singleton = null;
   private Logger fieldErrorLogger = null;
   private Logger fieldApplicationLogger = null;
   private Tracer fieldTracer = null;
   private boolean errorLoggingEnabled = false;
   private boolean applicationLoggingEnabled = false;
   private boolean tracingEnabled = false;

   private LogManager() {
   }

   public void destroy() {
      TraceHelper.entry(this, "destroy");

      try {
         if (this.fieldErrorLogger != null) {
            this.fieldErrorLogger.destroy();
            this.fieldErrorLogger = null;
         }

         if (this.fieldApplicationLogger != null) {
            this.fieldApplicationLogger.destroy();
            this.fieldApplicationLogger = null;
         }

         if (this.fieldTracer != null) {
            this.fieldTracer.destroy();
            this.fieldTracer = null;
         }

         singleton = null;
      } catch (Exception var2) {
      }

      System.out.println("LogManager::destroy-exit");
   }

   public boolean readyToDestroy() {
      return true;
   }

   public void suspendActivity() {
   }

   public void resumeActivity() {
   }

   public void initialize(Properties theProps) throws InitializationException {
      try {
         String loggerClass = (String)theProps.get("ErrorLogger.classname");
         if (loggerClass != null) {
            Logger newLogger = (Logger)Class.forName(loggerClass.trim()).getDeclaredConstructor().newInstance();
            newLogger.initialize(PropertiesHelper.createProperties(theProps, "ErrorLogger.", true));
            this.fieldErrorLogger = newLogger;
            this.errorLoggingEnabled = "true".equalsIgnoreCase(theProps.getProperty("ErrorLogger.logging"));
         }

         String appLoggerClass = (String)theProps.get("ApplicationLogger.classname");
         if (appLoggerClass != null) {
            Logger newLogger = (Logger)Class.forName(appLoggerClass.trim()).getDeclaredConstructor().newInstance();
            newLogger.initialize(PropertiesHelper.createProperties(theProps, "ApplicationLogger.", true));
            this.fieldApplicationLogger = newLogger;
            this.applicationLoggingEnabled = "true".equalsIgnoreCase(theProps.getProperty("ApplicationLogger.logging"));
         }

         String tracerClass = (String)theProps.get("Tracer.classname");
         if (tracerClass != null) {
            Tracer newTracer = (Tracer)Class.forName(tracerClass.trim()).getDeclaredConstructor().newInstance();
            newTracer.initialize(PropertiesHelper.createProperties(theProps, "Tracer.", true));
            this.fieldTracer = newTracer;
            this.tracingEnabled = "true".equalsIgnoreCase(theProps.getProperty("Tracer.tracing"));
         }

      } catch (Exception var6) {
         throw new InitializationException(var6);
      }
   }

   public Logger getErrorLogger() {
      return this.fieldErrorLogger;
   }

   public Logger getApplicationLogger() {
      return this.fieldApplicationLogger;
   }

   public Tracer getTracer() {
      return this.fieldTracer;
   }

   public static LogManager getSingleton() {
      if (singleton == null) {
         Class var0 = LogManager.class;
         synchronized(LogManager.class) {
            if (singleton == null) {
               try {
                  LogManager manager = new LogManager();
                  manager.initializeDefault();
                  singleton = manager;
               } catch (Exception var2) {
                  var2.printStackTrace();
               }
            }
         }
      }

      return singleton;
   }

   public void initializeDefault() {
      this.fieldErrorLogger = new ConsoleLogger();
      this.fieldApplicationLogger = new ConsoleLogger();
      this.fieldTracer = new ConsoleTracer();
      this.errorLoggingEnabled = true;
      this.applicationLoggingEnabled = true;
      this.tracingEnabled = true;
   }

   public boolean isApplicationLoggingEnabled() {
      return this.applicationLoggingEnabled;
   }

   public boolean isErrorLoggingEnabled() {
      return this.errorLoggingEnabled;
   }

   public void setApplicationLoggingEnabled(boolean applicationLoggingEnabled) {
      this.applicationLoggingEnabled = applicationLoggingEnabled;
   }

   public void setErrorLoggingEnabled(boolean loggingEnabled) {
      this.errorLoggingEnabled = loggingEnabled;
   }

   public void setTracingEnabled(boolean tracingEnabled) {
      this.tracingEnabled = tracingEnabled;
   }

   public boolean isTracingEnabled() {
      return this.tracingEnabled;
   }

   public String getVersion() {
      return "1.0";
   }
}
