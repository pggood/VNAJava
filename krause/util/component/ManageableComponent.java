package krause.util.component;

import java.util.Properties;
import krause.common.exception.InitializationException;

public interface ManageableComponent {
   void destroy();

   void initialize(Properties var1) throws InitializationException;

   void initializeDefault() throws InitializationException;

   String getVersion();
}
