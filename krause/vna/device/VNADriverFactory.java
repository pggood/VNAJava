package krause.vna.device;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import krause.common.exception.ProcessingException;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class VNADriverFactory {
   public static final String DRIVER_PREFIX = "Drv.";
   public static final String MENU_PREFIX = "Menu.";
   private static final Properties properties = new Properties();
   private static VNADriverFactory singleton = null;
   private static Map<String, IVNADriver> driverMapByType = new HashMap();
   private static Map<String, IVNADriver> driverMapByClassName = new HashMap();
   private static Map<String, IVNADriver> driverMapByShortName = new HashMap();

   public List<String> getDriverClassnameList() {
      List<String> rc = new ArrayList();
      Properties loc = PropertiesHelper.createProperties(properties, "Drv.");
      Enumeration keys = loc.keys();

      while(keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         rc.add(loc.getProperty(key));
      }

      return rc;
   }

   public static synchronized VNADriverFactory getSingleton() {
      if (singleton == null) {
         try {
            singleton = new VNADriverFactory();
            singleton.load();
         } catch (Exception var1) {
            ErrorLogHelper.exception((Object)null, "VNADriverFactory", var1);
         }
      }

      return singleton;
   }

   private void load() {
      String methodName = "load";
      TraceHelper.entry(this, "load");
      properties.putAll(PropertiesHelper.loadXMLProperties("drivers.xml", new VNADriverFactoryDefaultProperties()));
      List<String> drvList = this.getDriverClassnameList();
      Iterator var4 = drvList.iterator();

      while(var4.hasNext()) {
         String driverClassName = (String)var4.next();

         try {
            IVNADriver driver = this.createDriverForClassname(driverClassName);
            driverMapByClassName.put(driverClassName, driver);
            driverMapByType.put(driver.getDeviceInfoBlock().getType(), driver);
            driverMapByShortName.put(driver.getDeviceInfoBlock().getShortName(), driver);
         } catch (ProcessingException var7) {
            ErrorLogHelper.exception(this, "load", var7);
         }
      }

      TraceHelper.exit(this, "load");
   }

   public IVNADriver getDriverForType(String vnaDriverType) {
      TraceHelper.entry(this, "getDriverForType", vnaDriverType);
      IVNADriver driver = (IVNADriver)driverMapByType.get(vnaDriverType);
      TraceHelper.exit(this, "getDriverForType");
      return driver;
   }

   public IVNADriver getDriverForShortName(String name) {
      TraceHelper.entry(this, "getDriverForShortName", name);
      IVNADriver driver = (IVNADriver)driverMapByShortName.get(name);
      TraceHelper.exitWithRC(this, "getDriverForShortName", driver != null);
      return driver;
   }

   protected IVNADriver createDriverForClassname(String drvClassName) throws ProcessingException {
      String methodName = "createDriverForClassname";
      TraceHelper.entry(this, "createDriverForClassname", "class=[%s]", drvClassName);
      IVNADriver driver = null;

      try {
         driver = (IVNADriver)Class.forName(drvClassName.trim()).getDeclaredConstructor().newInstance();
      } catch (Exception var5) {
         ErrorLogHelper.exception(this, "createDriverForClassname", var5);
         throw new ProcessingException(var5);
      }

      TraceHelper.exit(this, "createDriverForClassname");
      return driver;
   }

   public List<IVNADriver> getDriverList() {
      List<IVNADriver> rc = new ArrayList();
      TraceHelper.entry(this, "getDriverList");
      Iterator it = driverMapByClassName.entrySet().iterator();

      while(it.hasNext()) {
         Entry<String, IVNADriver> ent = (Entry)it.next();
         rc.add((IVNADriver)ent.getValue());
      }

      TraceHelper.exit(this, "getDriverList");
      return rc;
   }
}
