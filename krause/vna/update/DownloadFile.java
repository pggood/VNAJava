package krause.vna.update;

import krause.vna.config.VNASystemConfig;

public class DownloadFile {
   private String remoteFileName;
   private String localFileName;
   private long fileSize = -1L;
   private UpdateChecker.FILE_TYPE type;
   private VNASystemConfig.OS_PLATFORM plattform;
   private String hash;

   public String getRemoteFileName() {
      return this.remoteFileName;
   }

   public void setRemoteFileName(String url) {
      this.remoteFileName = url;
   }

   public String getLocalFileName() {
      return this.localFileName;
   }

   public void setLocalFileName(String localFileName) {
      this.localFileName = localFileName;
   }

   public String toString() {
      return "DownloadFile [fileSize=" + this.fileSize + ", localFileName=" + this.localFileName + ", plattform=" + this.plattform + ", remoteFileName=" + this.remoteFileName + ", type=" + this.type + "]";
   }

   public long getFileSize() {
      return this.fileSize;
   }

   public void setFileSize(long fileSize) {
      this.fileSize = fileSize;
   }

   public void setPlattform(VNASystemConfig.OS_PLATFORM plattform) {
      this.plattform = plattform;
   }

   public VNASystemConfig.OS_PLATFORM getPlattform() {
      return this.plattform;
   }

   public void setType(UpdateChecker.FILE_TYPE type) {
      this.type = type;
   }

   public UpdateChecker.FILE_TYPE getType() {
      return this.type;
   }

   public void setHash(String hash) {
      this.hash = hash;
   }

   public String getHash() {
      return this.hash;
   }
}
