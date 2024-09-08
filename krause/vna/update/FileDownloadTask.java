package krause.vna.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingWorker;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class FileDownloadTask extends SwingWorker<List<FileDownloadJob>, FileDownloadJob> {
   private List<FileDownloadJob> jobs = new ArrayList();
   private FileDownloadStatusListener listener = null;
   private boolean abort = false;

   public FileDownloadTask(FileDownloadStatusListener pList) {
      this.listener = pList;
   }

   public void abort() {
      this.abort = true;
   }

   protected List<FileDownloadJob> doInBackground() throws Exception {
      TraceHelper.entry(this, "doInBackground");
      Iterator var2 = this.jobs.iterator();

      while(var2.hasNext()) {
         FileDownloadJob job = (FileDownloadJob)var2.next();
         job.setStatus(0);
         this.publish(new FileDownloadJob[]{job});
         int rc = this.downloadFile(job.getFile(), job.getLocalDirectory());
         job.setStatus(rc);
         this.publish(new FileDownloadJob[]{job});
      }

      TraceHelper.exit(this, "doInBackground");
      return this.jobs;
   }

   protected void process(List<FileDownloadJob> jobs) {
      TraceHelper.entry(this, "process");
      if (this.listener != null) {
         Iterator var3 = jobs.iterator();

         while(var3.hasNext()) {
            FileDownloadJob job = (FileDownloadJob)var3.next();
            this.listener.publishState(job);
         }
      }

      TraceHelper.exit(this, "process");
   }

   public void publishProgress(int percentage) {
   }

   public void addJob(FileDownloadJob job) {
      this.jobs.add(job);
   }

   public void addJobs(List<FileDownloadJob> jobs) {
      Iterator var3 = jobs.iterator();

      while(var3.hasNext()) {
         FileDownloadJob job = (FileDownloadJob)var3.next();
         this.addJob(job);
      }

   }

   private int downloadFile(DownloadFile file, String targetDirectory) {
      String methodName = "downloadFile";
      TraceHelper.entry(this, "downloadFile", file.getRemoteFileName());
      int rc = 0;
      URL url = null;
      URLConnection urlConn = null;
      InputStream inpStream = null;
      BufferedInputStream bufInpStream = null;
      FileOutputStream fileOutStream = null;
      BufferedOutputStream buffOutStream = null;
      String outputFileName = targetDirectory + System.getProperty("file.separator") + file.getLocalFileName();
      if (this.abort) {
         rc = 3;
      } else {
         try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            (new File(targetDirectory)).mkdirs();
            url = new URL(file.getRemoteFileName());
            urlConn = url.openConnection();
            inpStream = urlConn.getInputStream();
            bufInpStream = new BufferedInputStream(inpStream);
            fileOutStream = new FileOutputStream(outputFileName);
            buffOutStream = new BufferedOutputStream(fileOutStream);
            long filesize = 0L;

            int i;
            while((i = bufInpStream.read()) != -1 && !this.abort) {
               buffOutStream.write(i);
               ++filesize;
               messageDigest.update((byte)(i & 255));
            }

            file.setFileSize(filesize);
            buffOutStream.flush();
            if (this.abort) {
               rc = 3;
            } else {
               rc = 1;
            }

            byte[] resultByte = messageDigest.digest();
            StringBuilder sb = new StringBuilder();

            for(i = 0; i < resultByte.length; ++i) {
               sb.append(Integer.toHexString(resultByte[i] & 255 | 256).substring(1, 3));
            }

            TraceHelper.text(this, "downloadFile", "md5 for file=[" + sb.toString() + "] xml=[" + file.getHash() + "]");
         } catch (IOException | NoSuchAlgorithmException var38) {
            ErrorLogHelper.exception(this, "downloadFile", var38);
            rc = 2;
         } finally {
            if (buffOutStream != null) {
               try {
                  buffOutStream.close();
                  if (this.abort) {
                     File fi = new File(outputFileName);
                     fi.delete();
                  }
               } catch (IOException var37) {
                  ErrorLogHelper.exception(this, "downloadFile", var37);
               }
            }

            if (fileOutStream != null) {
               try {
                  fileOutStream.close();
               } catch (IOException var36) {
                  ErrorLogHelper.exception(this, "downloadFile", var36);
               }
            }

            if (bufInpStream != null) {
               try {
                  bufInpStream.close();
               } catch (IOException var35) {
                  ErrorLogHelper.exception(this, "downloadFile", var35);
               }
            }

            if (inpStream != null) {
               try {
                  inpStream.close();
               } catch (IOException var34) {
                  ErrorLogHelper.exception(this, "downloadFile", var34);
               }
            }

         }
      }

      TraceHelper.exitWithRC(this, "downloadFile", "rc=%d - [%s]", Integer.valueOf(rc), outputFileName);
      return rc;
   }

   protected void done() {
      String methodName = "done";
      TraceHelper.entry(this, "done");
      this.listener.done();
      TraceHelper.exit(this, "done");
   }
}
