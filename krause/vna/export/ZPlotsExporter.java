package krause.vna.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;

public class ZPlotsExporter extends VNAExporter {
   public ZPlotsExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
   }

   public String export(String fnp, boolean overwrite) throws ProcessingException {
      TraceHelper.entry(this, "export");
      String currFilename = "not saved";
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      VNACalibratedSample[] samples = blk.getCalibratedSamples();
      currFilename = this.check4FileToDelete(fnp, overwrite);
      boolean transmissionMode = this.datapool.getScanMode().isTransmissionMode();
      if (currFilename != null) {
         DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
         DecimalFormat df = new DecimalFormat("0.000", dfs);
         char valSep = 44;
         VNADeviceInfoBlock dib = this.datapool.getDriver().getDeviceInfoBlock();
         String phaseText = "???";
         if (dib.getMinPhase() < 0.0D) {
            phaseText = "±Phase(deg)";
         } else {
            phaseText = "Phase(deg)";
         }

         try {
            FileOutputStream fos = new FileOutputStream(currFilename);
            Writer w = new BufferedWriter(new OutputStreamWriter(fos, "ISO-8859-1"));
            if (transmissionMode) {
               w.write("\"Frequency(Hz)\",\"Transmission Loss(dB)\",\"" + phaseText + "\"");
            } else {
               w.write("\"Frequency(Hz)\",\"Return Loss(dB)\",\"" + phaseText + "\"");
            }

            w.write(GlobalSymbols.LINE_SEPARATOR);

            for(int i = 0; i < samples.length; ++i) {
               VNACalibratedSample data = samples[i];
               w.write(df.format(data.getFrequency()));
               w.write(valSep);
               if (transmissionMode) {
                  w.write(df.format(-data.getTransmissionLoss()));
               } else {
                  w.write(df.format(-data.getReflectionLoss()));
               }

               w.write(valSep);
               if (transmissionMode) {
                  w.write(df.format(data.getTransmissionPhase()));
               } else {
                  w.write(df.format(data.getReflectionPhase()));
               }

               w.write(GlobalSymbols.LINE_SEPARATOR);
            }

            w.flush();
            w.close();
            fos.close();
         } catch (IOException var16) {
            ErrorLogHelper.exception(ZPlotsExporter.class, "export", var16);
            throw new ProcessingException(var16);
         }
      }

      TraceHelper.exit(ZPlotsExporter.class, "export");
      return currFilename;
   }

   public String getExtension() {
      return ".zplot.csv";
   }
}
