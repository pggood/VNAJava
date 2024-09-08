package krause.vna.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;

public class CSVExporter extends VNAExporter {
   public CSVExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
   }

 public String export(String fnp, boolean overwrite) throws ProcessingException {
    TraceHelper.entry(this, "export", fnp);
    VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
    VNACalibratedSample[] samples = blk.getCalibratedSamples();
    String currFilename = this.check4FileToDelete(fnp, overwrite);
    if (currFilename != null) {
        DecimalFormat df = new DecimalFormat();
        char decSep = df.getDecimalFormatSymbols().getDecimalSeparator();
        char valSep = (decSep == ',') ? ';' : ','; // Default to 44 if not ','
        
        FileOutputStream fos = null;
        BufferedWriter w = null;

        try {
            fos = new FileOutputStream(currFilename);
            w = new BufferedWriter(new OutputStreamWriter(fos, "Cp850"));

            // Write header
            w.write("Frequency(Hz)");
            w.write(valSep);
            boolean isTransmissionMode = this.datapool.getScanMode().isTransmissionMode();
            w.write(isTransmissionMode ? "Transmission Loss(dB)" : "Return Loss(dB)");
            w.write(valSep);
            w.write("Phase(deg)");
            w.write(valSep);
            w.write("Rs");
            w.write(valSep);
            w.write("SWR");
            w.write(valSep);
            w.write("Xs");
            w.write(valSep);
            w.write("|Z|");
            w.write(valSep);
            w.write("Theta");
            w.write(GlobalSymbols.LINE_SEPARATOR);

            // Write sample data
            for (VNACalibratedSample data : samples) {
                w.write(VNAFormatFactory.getFrequencyFormat4Export().format(data.getFrequency()));
                w.write(valSep);
                if (isTransmissionMode) {
                    w.write(VNAFormatFactory.getReflectionLossFormat().format(data.getTransmissionLoss()));
                    w.write(valSep);
                    w.write(VNAFormatFactory.getPhaseFormat().format(data.getTransmissionPhase()));
                    w.write(valSep);
                } else {
                    w.write(VNAFormatFactory.getReflectionLossFormat().format(data.getReflectionLoss()));
                    w.write(valSep);
                    w.write(VNAFormatFactory.getPhaseFormat().format(data.getReflectionPhase()));
                    w.write(valSep);
                }
                w.write(VNAFormatFactory.getRsFormat().format(data.getR()));
                w.write(valSep);
                w.write(VNAFormatFactory.getSwrFormat().format(data.getSWR()));
                w.write(valSep);
                w.write(VNAFormatFactory.getXsFormat().format(data.getX()));
                w.write(valSep);
                w.write(VNAFormatFactory.getZFormat().format(data.getZ()));
                w.write(valSep);
                w.write(VNAFormatFactory.getZFormat().format(data.getTheta()));
                w.write(GlobalSymbols.LINE_SEPARATOR);
            }

            w.flush();
        } catch (IOException e) {
            ErrorLogHelper.exception(CSVExporter.class, "export", e);
            throw new ProcessingException(e);
        } finally {
            // Ensure resources are closed in the finally block
            try {
                if (w != null) {
                    w.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // Handle exceptions during resource closing
                ErrorLogHelper.exception(CSVExporter.class, "export", e);
            }
        }
    }

    TraceHelper.exit(this, "export");
    return currFilename;
}


   public String getExtension() {
      return ".csv";
   }
}
