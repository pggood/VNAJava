package krause.vna.firmware;

public abstract class VNABaseFirmwareFlasher implements IVNAFirmwareFlasher {
   protected StringMessenger messenger;
   protected String deviceType;
   protected int pageSize;
   protected int bootSize;
   protected int flashSize;
   protected int eEpromSize;
   protected int pagePtr;
   protected int retryCount;
   private boolean autoReset;

   public String getDeviceType() {
      return this.deviceType;
   }

   public void setDeviceType(String deviceType) {
      this.deviceType = deviceType;
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public int getBootSize() {
      return this.bootSize;
   }

   public void setBootSize(int bootSize) {
      this.bootSize = bootSize;
   }

   public int getFlashSize() {
      return this.flashSize;
   }

   public void setFlashSize(int flashSize) {
      this.flashSize = flashSize;
   }

   public int getEEpromSize() {
      return this.eEpromSize;
   }

   public void setEEpromSize(int eEpromSize) {
      this.eEpromSize = eEpromSize;
   }

   public int getPagePtr() {
      return this.pagePtr;
   }

   public void setPagePtr(int pagePtr) {
      this.pagePtr = pagePtr;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   public void setRetryCount(int retryCount) {
      this.retryCount = retryCount;
   }

   public boolean isAutoReset() {
      return this.autoReset;
   }

   public void setAutoReset(boolean autoReset) {
      this.autoReset = autoReset;
   }

   public StringMessenger getMessenger() {
      return this.messenger;
   }

   public void setMessenger(StringMessenger messenger) {
      this.messenger = messenger;
   }
}
