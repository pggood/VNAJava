package krause.vna.update;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNASystemConfig;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class UpdateChecker {
   public boolean isNewVersionAvailable(String currentVersion, String versionFilePath) throws ProcessingException {
      boolean rc = false;
      UpdateInfoBlock uib = this.readUpdateInfoFile(versionFilePath, false);
      if (uib != null) {
         String remoteVersion = uib.getVersion();
         if (remoteVersion != null && currentVersion != null) {
            rc = remoteVersion.compareTo(currentVersion) > 0;
         }
      }

      return rc;
   }

   public UpdateInfoBlock readUpdateInfoFile(String path, boolean readForAllOS) throws ProcessingException {
      TraceHelper.entry(this, "readUpdateInfoFile", path);
      UpdateInfoBlock rc = null;

      try {
         URL url = new URL(path);
         SAXBuilder builder = new SAXBuilder();
         Document doc = builder.build(url);
         Element root = doc.getRootElement();
         String version = this.readVersion(root);
         List<DownloadFile> files = this.readXMLFileEntries(root, readForAllOS);
         String comment = this.readComment(root);
         rc = new UpdateInfoBlock();
         rc.setFiles(files);
         rc.setVersion(version);
         rc.setComment(comment);
      } catch (Exception var11) {
         ErrorLogHelper.exception(this, "readUpdateInfoFile", var11);
         throw new ProcessingException(var11);
      }

      TraceHelper.exitWithRC(this, "readUpdateInfoFile", rc);
      return rc;
   }

   private List<DownloadFile> readXMLFileEntries(Element root, boolean readForAllOS) {
      List<DownloadFile> rc = new ArrayList();
      VNASystemConfig.OS_PLATFORM myOs = VNASystemConfig.getPlatform();
      Element eFiles = root.getChild("files");
      List<Element> lstFiles = eFiles.getChildren();
      Iterator var8 = lstFiles.iterator();

      while(true) {
         while(var8.hasNext()) {
            Element file = (Element)var8.next();
            DownloadFile ent = this.readXMLFile(file);
            if (readForAllOS) {
               rc.add(ent);
            } else if (myOs == ent.getPlattform() || ent.getPlattform() == VNASystemConfig.OS_PLATFORM.ALL) {
               rc.add(ent);
            }
         }

         return rc;
      }
   }

   private DownloadFile readXMLFile(Element elem) {
      DownloadFile rc = new DownloadFile();
      rc.setLocalFileName(elem.getChildText("local"));
      rc.setRemoteFileName(elem.getChildText("remote"));
      rc.setHash(elem.getChildText("md5"));
      String type = elem.getChildText("type");
      if (type != null) {
         if (type.equalsIgnoreCase("JAR")) {
            rc.setType(UpdateChecker.FILE_TYPE.JAR_FILE);
         } else if (type.equalsIgnoreCase("README")) {
            rc.setType(UpdateChecker.FILE_TYPE.README);
         } else {
            rc.setType(UpdateChecker.FILE_TYPE.OTHER);
         }
      } else {
         rc.setType(UpdateChecker.FILE_TYPE.OTHER);
      }

      String plattform = elem.getChildText("platform");
      if (plattform != null) {
         if (plattform.equalsIgnoreCase("WINDOWS")) {
            rc.setPlattform(VNASystemConfig.OS_PLATFORM.WINDOWS);
         } else if (plattform.equalsIgnoreCase("MAC")) {
            rc.setPlattform(VNASystemConfig.OS_PLATFORM.MAC);
         } else if (plattform.equalsIgnoreCase("UNIX")) {
            rc.setPlattform(VNASystemConfig.OS_PLATFORM.UNIX);
         } else {
            rc.setPlattform(VNASystemConfig.OS_PLATFORM.ALL);
         }
      } else {
         rc.setPlattform(VNASystemConfig.OS_PLATFORM.ALL);
      }

      return rc;
   }

   private String readComment(Element root) {
      String rc = null;
      Element eVersion = root.getChild("comment");
      if (eVersion != null) {
         rc = eVersion.getText();
      }

      return rc;
   }

   private String readVersion(Element root) {
      String rc = null;
      Element eVersion = root.getChild("version");
      rc = eVersion.getText();
      return rc;
   }

   public static enum FILE_TYPE {
      JAR_FILE,
      README,
      OTHER;
   }
}
