package krause.util.file;

import java.io.File;

public class TimestampControlledFile {
   private String filename;
   private long lastTimestamp;

   private long getFileTS() {
      File file = new File(this.filename);
      return file.lastModified();
   }

   public TimestampControlledFile(String fn) {
      this.filename = fn;
      this.lastTimestamp = this.getFileTS();
   }

   public boolean needsReload() {
      return this.lastTimestamp != this.getFileTS();
   }

   public String getFilename() {
      return this.filename;
   }

   public long getLastTimestamp() {
      return this.lastTimestamp;
   }
}
