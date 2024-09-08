package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class JpegExporter extends VNAExporter {
   public JpegExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
   }

   private void writeChart2JPEG(JFreeChart aChart, String aFileName, int aWidth, int aHeight) throws IOException {
      TraceHelper.entry(this, "createChart");
      FileOutputStream fos = new FileOutputStream(aFileName);
      ChartUtilities.writeChartAsJPEG(fos, aChart, aWidth, aHeight);
      fos.close();
      TraceHelper.exit(this, "createChart");
   }

   public String export(String fnp, boolean overwrite) throws ProcessingException {
      String methodName = "export";
      TraceHelper.entry(this, "export", fnp);
      String currFilename = "not saved";
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      VNACalibratedSample[] samples = blk.getCalibratedSamples();

      try {
         currFilename = this.check4FileToDelete(fnp, overwrite);
         if (currFilename != null) {
            JFreeChart chart = this.createChart(samples);
            this.writeChart2JPEG(chart, currFilename, this.config.getExportDiagramWidth(), this.config.getExportDiagramHeight());
         }
      } catch (IOException var8) {
         ErrorLogHelper.exception(this, "export", var8);
         throw new ProcessingException(var8);
      }

      TraceHelper.exitWithRC(this, "export", currFilename);
      return currFilename;
   }

   public String getExtension() {
      return ".jpg";
   }
}
