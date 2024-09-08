package krause.vna.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.util.VNAFrequencyPair;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class VNABandMap {
   private static final VNAConfig config = VNAConfig.getSingleton();
   private static final List<VNAFrequencyPair> list = new ArrayList();
   private static final String FILE_SEP = System.getProperty("file.separator");
   private static final String CONFIG_FILE = "bandmap.csv";
   private static final String CONFIG_PATHNAME;
   private static final CellProcessor[] cellProcessors;

   static {
      CONFIG_PATHNAME = config.getVNAConfigDirectory() + FILE_SEP + "bandmap.csv";
      cellProcessors = new CellProcessor[]{new ParseLong(), new ParseLong()};
   }

   public List<VNAFrequencyPair> getList() {
      return list;
   }

   public VNABandMap() {
      this.loadBandmap();
   }

private void loadBandmap() {
    TraceHelper.entry(this, "loadBandmap");

    CsvBeanReader beanReader = null;
    try {
        // Initialize CsvBeanReader with FileReader and preferences
        beanReader = new CsvBeanReader(new FileReader(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE);

        // Read the header
        String[] header = beanReader.getHeader(true);

        // Read and process each record
        VNAFrequencyPair frqPair;
        while ((frqPair = (VNAFrequencyPair) beanReader.read(VNAFrequencyPair.class, header, cellProcessors)) != null) {
            list.add(frqPair);
        }
    } catch (IOException e) {
        // Handle IOExceptions and perform fallback actions
        ErrorLogHelper.text(this, "readCSV", e.getMessage());
        list.clear();
        this.loadDefaultBandmap();
        this.saveBandmap();
    } finally {
        // Ensure CsvBeanReader is closed
        if (beanReader != null) {
            try {
                beanReader.close();
            } catch (IOException e) {
                // Log or handle the exception during closing
                ErrorLogHelper.text(this, "closeCSVReader", e.getMessage());
            }
        }
    }

    TraceHelper.exit(this, "loadBandmap");
}


   private void loadDefaultBandmap() {
      TraceHelper.entry(this, "loadDefaultBandmap");
      list.add(new VNAFrequencyPair(135700L, 137800L));
      list.add(new VNAFrequencyPair(472000L, 479000L));
      list.add(new VNAFrequencyPair(1810000L, 2000000L));
      list.add(new VNAFrequencyPair(3500000L, 3800000L));
      list.add(new VNAFrequencyPair(7000000L, 7200000L));
      list.add(new VNAFrequencyPair(10100000L, 10150000L));
      list.add(new VNAFrequencyPair(14000000L, 14350000L));
      list.add(new VNAFrequencyPair(18068000L, 18168000L));
      list.add(new VNAFrequencyPair(21000000L, 21450000L));
      list.add(new VNAFrequencyPair(24890000L, 24990000L));
      list.add(new VNAFrequencyPair(28000000L, 29700000L));
      list.add(new VNAFrequencyPair(50000000L, 52000000L));
      list.add(new VNAFrequencyPair(70000000L, 70500000L));
      list.add(new VNAFrequencyPair(144000000L, 146000000L));
      list.add(new VNAFrequencyPair(430000000L, 440000000L));
      list.add(new VNAFrequencyPair(1240000000L, 1325000000L));
      list.add(new VNAFrequencyPair(2310000000L, 2450000000L));
      TraceHelper.exit(this, "loadDefaultBandmap");
   }

private void saveBandmap() {
    TraceHelper.entry(this, "saveBandmap");

    CsvBeanWriter beanWriter = null;
    try {
        // Initialize CsvBeanWriter with FileWriter and preferences
        beanWriter = new CsvBeanWriter(new FileWriter(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE);

        // Define the header for the CSV
        String[] header = new String[]{"startFrequency", "stopFrequency"};
        beanWriter.writeHeader(header);

        // Write each item in the list to the CSV
        for (VNAFrequencyPair frqPair : list) {
            beanWriter.write(frqPair, header, cellProcessors);
        }
    } catch (IOException e) {
        // Handle IOExceptions and log the error
        ErrorLogHelper.exception(this, "saveBandmap", e);
    } finally {
        // Ensure CsvBeanWriter is closed
        if (beanWriter != null) {
            try {
                beanWriter.close();
            } catch (IOException e) {
                // Log any exception that occurs during closing
                ErrorLogHelper.exception(this, "saveBandmap", e);
            }
        }
    }

    TraceHelper.exit(this, "saveBandmap");
}

}
