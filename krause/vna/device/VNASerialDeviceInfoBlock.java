package krause.vna.device;

import krause.common.TypedProperties;
import krause.util.ras.logging.TraceHelper;

public abstract class VNASerialDeviceInfoBlock extends VNADeviceInfoBlock {
   public static final String PROPERTIES_OPEN_TIMEOUT = "openTimeout";
   public static final int DEFAULT_OPEN_TIMEOUT = 5000;
   public static final String PROPERTIES_READ_TIMEOUT = "readTimeout";
   public static final int DEFAULT_READ_TIMEOUT = 20000;
   public static final String PROPERTIES_AFTER_COMMAND_DELAY = "afterCommandDelay";
   public static final int DEFAULT_AFTER_COMMAND_DELAY = 50;
   public static final String PROPERTIES_BAUDRATE = "baudRate";
   public static final int DEFAULT_PROPERTIES_BAUDRATE = 115200;
   private int openTimeout;
   private int readTimeout;
   private int afterCommandDelay;
   private int baudrate;

   public void reset() {
      super.reset();
      this.setOpenTimeout(5000);
      this.setReadTimeout(20000);
      this.setAfterCommandDelay(50);
      this.setBaudrate(115200);
   }

   public void restore(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "restore");
      super.restore(config, prefix);
      this.setAfterCommandDelay(config.getInteger(prefix + "afterCommandDelay", this.getAfterCommandDelay()));
      this.setOpenTimeout(config.getInteger(prefix + "openTimeout", this.getOpenTimeout()));
      this.setReadTimeout(config.getInteger(prefix + "readTimeout", this.getReadTimeout()));
      this.setBaudrate(config.getInteger(prefix + "baudRate", this.getBaudrate()));
      TraceHelper.exit(this, "restore");
   }

   public void store(TypedProperties config, String prefix) {
      TraceHelper.entry(this, "store");
      super.store(config, prefix);
      config.putInteger(prefix + "afterCommandDelay", this.getAfterCommandDelay());
      config.putInteger(prefix + "openTimeout", this.getOpenTimeout());
      config.putInteger(prefix + "readTimeout", this.getReadTimeout());
      config.putInteger(prefix + "baudRate", this.getBaudrate());
      TraceHelper.exit(this, "store");
   }

   public int getOpenTimeout() {
      return this.openTimeout;
   }

   public void setOpenTimeout(int openTimeout) {
      this.openTimeout = openTimeout;
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   public void setReadTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
   }

   public int getAfterCommandDelay() {
      return this.afterCommandDelay;
   }

   public void setAfterCommandDelay(int afterCommandDelay) {
      this.afterCommandDelay = afterCommandDelay;
   }

   public void setBaudrate(int baudrate) {
      this.baudrate = baudrate;
   }

   public int getBaudrate() {
      return this.baudrate;
   }
}
