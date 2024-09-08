package krause.vna.export;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;

public class XMLExporter extends VNAExporter {
   public XMLExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
   }

   public String export(String fnp, boolean overwrite) throws ProcessingException {
      TraceHelper.entry(this, "export");
      String currFilename = "not saved";
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      VNACalibratedSample[] pDataList = blk.getCalibratedSamples();

      try {
         currFilename = this.check4FileToDelete(fnp, overwrite);
         if (currFilename != null) {
            FileOutputStream fileOut = new FileOutputStream(currFilename);
            XMLEncoder enc = new XMLEncoder(fileOut);
            enc.writeObject(pDataList.length);
            enc.writeObject(blk.getMmRP());
            enc.writeObject(blk.getMmRL());
            enc.writeObject(blk.getMmRS());
            enc.writeObject(blk.getMmSWR());
            enc.writeObject(blk.getMmXS());
            enc.writeObject(blk.getMmZABS());

            for(int i = 0; i < pDataList.length; ++i) {
               enc.writeObject(pDataList[i]);
            }

            enc.close();
            fileOut.close();
         }
      } catch (Exception var9) {
         ErrorLogHelper.exception(this, "export", var9);
         throw new ProcessingException(var9);
      }

      TraceHelper.exit(XLSExporter.class, "export");
      return currFilename;
   }

   public String getExtension() {
      return ".xml";
   }
}
