package krause.vna.update;

public class FileDownloadJob {
   public static final int STATUS_NEW = -1;
   public static final int STATUS_STARTED = 0;
   public static final int STATUS_DOWNLOADED = 1;
   public static final int STATUS_ERROR = 2;
   public static final int STATUS_ABORTED = 3;
   private String localDirectory;
   private DownloadFile file;
   private int status = -1;

   public void setLocalDirectory(String localDirectory) {
      this.localDirectory = localDirectory;
   }

   public String getLocalDirectory() {
      return this.localDirectory;
   }

   public void setFile(DownloadFile file) {
      this.file = file;
   }

   public DownloadFile getFile() {
      return this.file;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public int getStatus() {
      return this.status;
   }
}
