package krause.vna.update;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpdateInfoBlock {
   private String version;
   private String comment;
   private List<DownloadFile> files;

   public String getComment() {
      return this.comment;
   }

   public List<DownloadFile> getFiles() {
      return this.files;
   }

   public String getVersion() {
      return this.version;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public void setFiles(List<DownloadFile> files) {
      this.files = files;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String toString() {
      return "UpdateInfoBlock [comment=" + this.comment + ", files=" + this.files + ", version=" + this.version + "]";
   }

   public List<DownloadFile> getFilesForType(UpdateChecker.FILE_TYPE selectedType) {
      List<DownloadFile> rc = new ArrayList();
      Iterator var4 = this.files.iterator();

      while(var4.hasNext()) {
         DownloadFile file = (DownloadFile)var4.next();
         if (file.getType() == selectedType) {
            rc.add(file);
         }
      }

      return rc;
   }
}
