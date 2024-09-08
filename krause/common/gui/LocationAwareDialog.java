package krause.common.gui;

import java.awt.Frame;
import javax.swing.JDialog;
import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;

public class LocationAwareDialog extends JDialog {
   private String configurationPrefix = null;
   private TypedProperties properties = null;

   public LocationAwareDialog(Frame owner, String string) {
      super(owner, string);
      TraceHelper.exit(this, "LocationAwareDialog");
      TraceHelper.exit(this, "LocationAwareDialog");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      if (this.getConfigurationPrefix() != null && this.getProperties() != null) {
         this.getProperties().storeWindowPosition(this.getConfigurationPrefix(), this);
         this.getProperties().storeWindowSize(this.getConfigurationPrefix(), this);
      }

      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   public String getConfigurationPrefix() {
      return this.configurationPrefix;
   }

   public void setConfigurationPrefix(String configurationPrefix) {
      this.configurationPrefix = configurationPrefix;
   }

   public TypedProperties getProperties() {
      return this.properties;
   }

   public void setProperties(TypedProperties properties) {
      this.properties = properties;
   }

   public void setVisible(boolean b) {
      TraceHelper.entry(this, "setVisible");
      super.setVisible(b);
      if (this.getConfigurationPrefix() != null && this.getProperties() != null) {
         if (b) {
            this.getProperties().restoreWindowPosition(this.getConfigurationPrefix(), this, this.getLocation());
            this.getProperties().restoreWindowSize(this.getConfigurationPrefix(), this, this.getSize());
         } else {
            this.getProperties().storeWindowPosition(this.getConfigurationPrefix(), this);
            this.getProperties().storeWindowSize(this.getConfigurationPrefix(), this);
         }
      }

      TraceHelper.exit(this, "setVisible");
   }
}
