package krause.vna.gui;

import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogManager;
import krause.vna.config.VNAConfig;
import krause.vna.config.VNAConfigDefaultProperties;
import krause.vna.data.VNADataPool;
import krause.vna.gui.laf.VNALookAndFeelHelper;
import krause.vna.resources.VNAMessages;
import purejavacomm.PureJavaComm;

public class VNAMain {
   private static final String P_CONFIGFILE = "configfile";
   private static final String P_CONFIGFILE_DEFAULT = "vna.settings.xml";

   public static void main(String[] args) {
      if ("Wolfhard".equalsIgnoreCase(System.getProperty("user.name"))) {
         System.out.println("user not supported - bye");
         System.exit(1);
      }

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            try {
               String cfn = System.getProperty("configfile", "vna.settings.xml");
               VNAConfig.init(cfn, new VNAConfigDefaultProperties());
               VNAConfig config = VNAConfig.getSingleton();
               Locale loc = config.getLocale();
               if (loc != null) {
                  Locale.setDefault(loc);
               }

               System.out.println("INFO::Application version.......[" + VNAMessages.getString("Application.version") + " " + VNAMessages.getString("Application.date") + "]");
               System.out.println("INFO::Java version..............[" + System.getProperty("java.version") + "]");
               System.out.println("INFO::Java runtime.version......[" + System.getProperty("java.runtime.version") + "]");
               System.out.println("INFO::Java vm.version...........[" + System.getProperty("java.vm.version") + "]");
               System.out.println("INFO::Java vm.vendor............[" + System.getProperty("java.vm.vendor") + "]");
               System.out.println("INFO::OS........................[" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "]");
               System.out.println("INFO::Country/Language..........[" + Locale.getDefault().getCountry() + "/" + Locale.getDefault().getLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]");
               System.out.println("INFO::                          [" + Locale.getDefault().getDisplayCountry() + "/" + Locale.getDefault().getDisplayLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]");
               System.out.println("INFO::User .....................[" + System.getProperty("user.name") + "]");
               System.out.println("INFO::User.home ................[" + System.getProperty("user.home") + "]");
               System.out.println("INFO::User.dir .................[" + System.getProperty("user.home") + "]");
               System.out.println("INFO::Installation directory....[" + config.getInstallationDirectory() + "]");
               System.out.println("INFO::Configuration directory...[" + config.getVNAConfigDirectory() + "] overwrite with -Duser.home=XXX");
               System.out.println("INFO::Configuration file........[" + cfn + "] overwrite with -Dconfigfile=YYY");
               System.out.println("INFO::Serial library version ...[" + PureJavaComm.getVersion() + "/" + PureJavaComm.getFork() + "]");
               LogManager.getSingleton().initialize(VNAConfig.getSingleton());
               VNADataPool.init(VNAConfig.getSingleton());

               try {
                  if (config.isMac()) {
                     System.setProperty("apple.laf.useScreenMenuBar", "true");
                  } else {
                     (new VNALookAndFeelHelper()).setThemeBasedOnConfig();
                  }
               } catch (UnsupportedLookAndFeelException var5) {
                  ErrorLogHelper.exception(this, "main", var5);
               }

               new VNAMainFrame();
            } catch (ProcessingException var6) {
               var6.printStackTrace();
            }

         }
      });
   }
}
