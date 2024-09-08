package krause.vna.update;

public interface FileDownloadStatusListener {
   void publishState(FileDownloadJob var1);

   void done();
}
