package krause.vna.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;

public class SnPExporter extends VNAExporter {
   public SnPExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
   }

   public String export(String fnp, boolean overwrite) throws ProcessingException {
      TraceHelper.entry(this, "export", fnp);
      String currFilename = "not saved";
      if (this.datapool.getScanMode().isTransmissionMode()) {
         currFilename = this.exportS2P(fnp, overwrite);
      } else if (this.datapool.getScanMode().isReflectionMode()) {
         currFilename = this.exportS1P(fnp, overwrite);
      } else if (this.datapool.getScanMode().isRss1Mode()) {
         currFilename = this.exportS2P(fnp, overwrite);
      }

      TraceHelper.exit(this, "export");
      return currFilename;
   }

private String exportS1P(String fnp, boolean overwrite) throws ProcessingException {
    String methodName = "exportS1P";
    TraceHelper.entry(this, "exportS1P");
    VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
    VNACalibratedSample[] samples = blk.getCalibratedSamples();
    String currFilename = this.check4FileToDelete(fnp, overwrite);

    if (currFilename != null) {
        DecimalFormatSymbols dfs = this.getDecimalFormatSymbols();
        DecimalFormat fmtFrequency = new DecimalFormat("0", dfs);
        DecimalFormat fmtLoss = new DecimalFormat("0.00000000", dfs);
        DecimalFormat fmtPhase = new DecimalFormat("0.00000000", dfs);

        try (FileOutputStream fos = new FileOutputStream(currFilename);
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1))) {

            w.write("! created by ");
            w.write(System.getProperty("user.name"));
            w.write(" at ");
            w.write((new Date()).toString());
            w.write(GlobalSymbols.LINE_SEPARATOR);
            w.write("! generated using vna/J Version ");
            w.write(VNAMessages.getString("Application.version"));
            w.write(GlobalSymbols.LINE_SEPARATOR);
            String resistance = "" + (int) this.datapool.getDriver().getDeviceInfoBlock().getReferenceResistance().getReal();
            w.write("# Hz S DB R " + resistance);
            w.write(GlobalSymbols.LINE_SEPARATOR);

            for (VNACalibratedSample data : samples) {
                w.write(fmtFrequency.format(data.getFrequency()));
                w.write(" ");
                w.write(fmtLoss.format(data.getReflectionLoss()));
                w.write(" ");
                w.write(fmtPhase.format(data.getReflectionPhase()));
                w.write(GlobalSymbols.LINE_SEPARATOR);
            }

        } catch (IOException e) {
            ErrorLogHelper.exception(this, "exportS1P", e);
            throw new ProcessingException(e);
        }

        TraceHelper.exitWithRC(this, "exportS1P", currFilename);
    }

    return currFilename;
}


   private DecimalFormatSymbols getDecimalFormatSymbols() {
      return ".".equals(this.config.getExportDecimalSeparator()) ? new DecimalFormatSymbols(Locale.ENGLISH) : new DecimalFormatSymbols(Locale.GERMAN);
   }

private String exportS2P(String fnp, boolean overwrite) throws ProcessingException {
    String methodName = "exportS2P";
    TraceHelper.entry(this, "exportS2P");
    VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
    VNACalibratedSample[] samples = blk.getCalibratedSamples();
    String currFilename = this.check4FileToDelete(fnp, overwrite);

    if (currFilename != null) {
        DecimalFormatSymbols dfs = this.getDecimalFormatSymbols();
        DecimalFormat fmtFrequency = new DecimalFormat("0", dfs);
        DecimalFormat fmtLoss = new DecimalFormat("0.00000000", dfs);
        DecimalFormat fmtPhase = new DecimalFormat("0.00000000", dfs);

        try (FileOutputStream fos = new FileOutputStream(currFilename);
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1))) {

            w.write("! created by ");
            w.write(System.getProperty("user.name"));
            w.write(" at ");
            w.write((new Date()).toString());
            w.write(GlobalSymbols.LINE_SEPARATOR);
            w.write("! generated using vna/J Version ");
            w.write(VNAMessages.getString("Application.version"));
            w.write(GlobalSymbols.LINE_SEPARATOR);
            w.write("# Hz S DB R 50");
            w.write(GlobalSymbols.LINE_SEPARATOR);

            for (VNACalibratedSample data : samples) {
                w.write(fmtFrequency.format(data.getFrequency()));
                w.write(" ");
                w.write(fmtLoss.format(data.getReflectionLoss()));
                w.write(" ");
                w.write(fmtPhase.format(data.getReflectionPhase()));
                w.write(" ");
                w.write(fmtLoss.format(data.getTransmissionLoss()));
                w.write(" ");
                w.write(fmtPhase.format(data.getTransmissionPhase()));
                w.write(" ");
                w.write(fmtLoss.format(0L)); // Assuming default values as 0 for demonstration
                w.write(" ");
                w.write(fmtPhase.format(0L));
                w.write(" ");
                w.write(fmtLoss.format(0L));
                w.write(" ");
                w.write(fmtPhase.format(0L));
                w.write(" ");
                w.write(GlobalSymbols.LINE_SEPARATOR);
            }

        } catch (IOException e) {
            ErrorLogHelper.exception(this, "exportS2P", e);
            throw new ProcessingException(e);
        }

        TraceHelper.exitWithRC(this, "exportS2P", currFilename);
    }

    return currFilename;
}


   public String getExtension() {
      if (this.datapool.getScanMode().isTransmissionMode()) {
         return ".s2p";
      } else if (this.datapool.getScanMode().isReflectionMode()) {
         return ".s1p";
      } else {
         return this.datapool.getScanMode().isRss1Mode() ? ".s2p" : ".xxx";
      }
   }
}
