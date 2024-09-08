package krause.vna.gui.cable;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.format.VNAFormatFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class VNAVelocityFactorTableModel extends AbstractTableModel {
   private static final VNAConfig config = VNAConfig.getSingleton();
   private static final NumberFormat nfResistance = VNAFormatFactory.getResistanceFormat();
   private static final NumberFormat nfVelocity = VNAFormatFactory.getVelocityFormat();
   private static final String FILE_SEP = System.getProperty("file.separator");
   private static final String CONFIG_FILE = "cables.csv";
   private static final String CONFIG_PATHNAME;
   private static final List<VNAVelocityFactor> cables;
   private static final CellProcessor[] cellProcessors;

   static {
      CONFIG_PATHNAME = config.getVNAConfigDirectory() + FILE_SEP + "cables.csv";
      cables = new ArrayList();
      cellProcessors = new CellProcessor[]{new NotNull(), new ParseDouble(), new Optional(), new Optional(), new Optional(), new Optional(), new ParseDouble()};
   }

   private void fillDefaults() {
      TraceHelper.entry(this, "filLDefaults");
      cables.add(new VNAVelocityFactor("5D-2V", 0.67D));
      cables.add(new VNAVelocityFactor("8D-2V", 0.67D));
      cables.add(new VNAVelocityFactor("5D-FB", 0.8D));
      cables.add(new VNAVelocityFactor("8D-FB", 0.8D));
      cables.add(new VNAVelocityFactor("3.5D-SFA", 0.83D));
      cables.add(new VNAVelocityFactor("5D-SFA", 0.83D));
      cables.add(new VNAVelocityFactor("8D-SFA", 0.83D));
      cables.add(new VNAVelocityFactor("Aircell5", 50.0D, 0.82D, "9.40", "100.0", "31.09", "1000.0"));
      cables.add(new VNAVelocityFactor("Aircell7", 50.0D, 0.83D, "6.28", "100.0", "21.25", "1000.0"));
      cables.add(new VNAVelocityFactor("Aircom Plus", 50.0D, 0.83D, "3.80", "100.0", "13.40", "1000.0"));
      cables.add(new VNAVelocityFactor("Ecoflex10 Std.", 50.0D, 0.83D, "4.00", "100.0", "14.20", "1000.0"));
      cables.add(new VNAVelocityFactor("Ecoflex15 Std", 50.0D, 0.83D, "2.81", "100.0", "9.81", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 8240", 50.0D, 0.66D, "4.90", "100.0", "20.0", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 8267", 50.0D, 0.66D, "2.20", "100.0", "8.0", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 8208", 50.0D, 0.66D, "", "", "8.0", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 9258", 50.0D, 0.78D, "3.70", "100.0", "12.8", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 9880", 50.0D, 0.82D, "1.30", "100.0", "4.5", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 9913", 50.0D, 0.82D, "1.30", "100.0", "4.5", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden 9914", 50.0D, 0.66D, "", "", "9.0", "1000.0"));
      cables.add(new VNAVelocityFactor("Belden H155A01", 50.0D, 0.8D, "", "", "9.0", "1000.0"));
      cables.add(new VNAVelocityFactor("Foam (0.100 in. nominal diameter)", 0.66D));
      cables.add(new VNAVelocityFactor("Foam (0.195 in. nominal diameter)", 0.75D));
      cables.add(new VNAVelocityFactor("Foam (0.240 in. nominal diameter)", 0.83D));
      cables.add(new VNAVelocityFactor("Foam (0.300 in. nominal diameter)", 0.83D));
      cables.add(new VNAVelocityFactor("Foam (0.400 in. nominal diameter)", 0.85D));
      cables.add(new VNAVelocityFactor("Foam (0.500 in. nominal diameter)", 0.86D));
      cables.add(new VNAVelocityFactor("Foam (0.600 in. nominal diameter)", 0.87D));
      cables.add(new VNAVelocityFactor("H155", 50.0D, 0.81D));
      cables.add(new VNAVelocityFactor("H2000 Flex", 50.0D, 0.83D));
      cables.add(new VNAVelocityFactor("Hyperflex 5", 50.0D, 0.87D, "4.16", "28", "17.00", "430"));
      cables.add(new VNAVelocityFactor("Hyperflex 10", 50.0D, 0.87D, "2.07", "28", "8.60", "430"));
      cables.add(new VNAVelocityFactor("Highflexx 7", 50.0D, 0.87D, "3.00", "28", "12.30", "430"));
      cables.add(new VNAVelocityFactor("RG-5 /U", 52.5D, 0.659D, "0.77", "10.0", "2.90", "100"));
      cables.add(new VNAVelocityFactor("RG-5 B/U", 50.0D, 0.659D, "0.66", "10.0", "2.40", "100.0"));
      cables.add(new VNAVelocityFactor("RG-6 A/U", 75.0D, 0.659D, "0.78", "10.0", "2.90", "100.0"));
      cables.add(new VNAVelocityFactor("RG-6 Foam", 75.0D, 0.78D, "5.30", "50.0", "16.20", "500.0"));
      cables.add(new VNAVelocityFactor("RG-8 A/U", 50.0D, 0.659D, "0.55", "10.0", "2.00", "100.0"));
      cables.add(new VNAVelocityFactor("RG-8 foam", 50.0D, 0.8D, "1.70", "100.0", "6.0", "1000.0"));
      cables.add(new VNAVelocityFactor("RG-9 /U", 51.0D, 0.659D, "0.57", "10.0", "2.00", "100.0"));
      cables.add(new VNAVelocityFactor("RG-9 B/U", 50.0D, 0.659D, "0.61", "10.0", "2.10", "100.0"));
      cables.add(new VNAVelocityFactor("RG-10 A/U", 50.0D, 0.659D, "0.55", "10.0", "2.00", "100.0"));
      cables.add(new VNAVelocityFactor("RG-11 A/U", 75.0D, 0.66D, "0.70", "10.0", "2.30", "100.0"));
      cables.add(new VNAVelocityFactor("RG-11 foam", 75.0D, 0.78D, "3.30", "50.0", "12.10", "500.0"));
      cables.add(new VNAVelocityFactor("RG-12 A/U", 75.0D, 0.659D, "0.66", "10.0", "2.30", "100.0"));
      cables.add(new VNAVelocityFactor("RG-13 A/U", 75.0D, 0.659D, "0.66", "10.0", "2.30", "100.0"));
      cables.add(new VNAVelocityFactor("RG-14 A/U", 50.0D, 0.659D, "0.41", "10.0", "1.40", "100.0"));
      cables.add(new VNAVelocityFactor("RG-16 A/U", 52.0D, 0.67D, "0.40", "10.0", "1.20", "100.0"));
      cables.add(new VNAVelocityFactor("RG-17 A/U", 50.0D, 0.659D, "0.23", "10.0", "0.80", "100.0"));
      cables.add(new VNAVelocityFactor("RG-18 A/U", 50.0D, 0.659D, "0.23", "10.0", "0.80", "100.0"));
      cables.add(new VNAVelocityFactor("RG-19 A/U", 50.0D, 0.659D, "0.17", "10.0", "0.68", "100.0"));
      cables.add(new VNAVelocityFactor("RG-20 A/U", 50.0D, 0.659D, "0.17", "10.0", "0.68", "100.0"));
      cables.add(new VNAVelocityFactor("RG-21 A/U", 50.0D, 0.659D, "4.40", "10.0", "13.00", "100.0"));
      cables.add(new VNAVelocityFactor("RG-29 /U", 53.5D, 0.659D, "1.20", "10.0", "4.40", "100.0"));
      cables.add(new VNAVelocityFactor("RG-34 A/U", 75.0D, 0.659D, "0.29", "10.0", "1.30", "100.0"));
      cables.add(new VNAVelocityFactor("RG-34 B/U", 75.0D, 0.66D, "0.30", "10.0", "1.40", "100.0"));
      cables.add(new VNAVelocityFactor("RG-35 A/U", 75.0D, 0.659D, "0.24", "10.0", "0.85", "100.0"));
      cables.add(new VNAVelocityFactor("RG-54 A/U", 58.0D, 0.659D, "0.74", "10.0", "3.10", "100.0"));
      cables.add(new VNAVelocityFactor("RG-55 B/U", 53.5D, 0.659D, "1.30", "10.0", "4.80", "100.0"));
      cables.add(new VNAVelocityFactor("RG-55 A/U", 50.0D, 0.659D, "1.30", "10.0", "4.80", "100.0"));
      cables.add(new VNAVelocityFactor("RG-58 /U", 53.5D, 0.66D, "1.25", "10.0", "4.65", "100.0"));
      cables.add(new VNAVelocityFactor("RG-58 A/U", 53.5D, 0.659D, "1.25", "10.0", "4.65", "100.0"));
      cables.add(new VNAVelocityFactor("RG-58 C/U", 50.0D, 0.659D, "1.40", "10.0", "4.90", "100.0"));
      cables.add(new VNAVelocityFactor("RG-58 foam", 53.5D, 0.79D, "3.80", "100.0", "6.0", "300.0"));
      cables.add(new VNAVelocityFactor("RG-59 A/U", 75.0D, 0.659D, "1.10", "10.0", "3.40", "100.0"));
      cables.add(new VNAVelocityFactor("RG-59 B/U", 75.0D, 0.66D, "1.10", "10.0", "3.40", "100.0"));
      cables.add(new VNAVelocityFactor("RG-59 foam", 75.0D, 0.79D, "3.80", "100.0", "6.0", "300.0"));
      cables.add(new VNAVelocityFactor("RG-62 A/U", 93.0D, 0.84D, "0.85", "10.0", "2.70", "100.0"));
      cables.add(new VNAVelocityFactor("RG-74 A/U", 50.0D, 0.659D, "0.38", "10.0", "1.50", "100.0"));
      cables.add(new VNAVelocityFactor("RG-83/U", 35.0D, 0.66D, "0.80", "10.0", "2.80", "100.0"));
      cables.add(new VNAVelocityFactor("RG-142 B/U", 50.0D, 0.7D));
      cables.add(new VNAVelocityFactor("RG-174 A/U", 50.0D, 0.66D, "3.40", "10.0", "10.60", "100.0"));
      cables.add(new VNAVelocityFactor("RG-178 B/U", 50.0D, 0.7D));
      cables.add(new VNAVelocityFactor("RG-179 B/U", 75.0D, 0.7D));
      cables.add(new VNAVelocityFactor("RG-188 B/U", 50.0D, 0.7D));
      cables.add(new VNAVelocityFactor("RG-213/U", 50.0D, 0.66D, "0.60", "10.0", "1.90", "100.0"));
      cables.add(new VNAVelocityFactor("RG-218/U", 50.0D, 0.66D, "0.20", "10.0", "1.00", "100.0"));
      cables.add(new VNAVelocityFactor("RG-220/U", 50.0D, 0.66D, "0.20", "10.0", "0.70", "100.0"));
      cables.add(new VNAVelocityFactor("RG-316 B/U", 50.0D, 0.7D));
      cables.add(new VNAVelocityFactor("SUHNER RG-233/U-01", 50.0D, 0.66D, "0.20", "10.0", "0.70", "100.0"));
      cables.add(new VNAVelocityFactor("UR-43", 52.0D, 0.66D, "1.30", "10.0", "4.3", "100.0"));
      cables.add(new VNAVelocityFactor("UR-57", 75.0D, 0.66D, "0.60", "10.0", "1.9", "100.0"));
      cables.add(new VNAVelocityFactor("UR-63", 75.0D, 0.96D, "0.15", "10.0", "0.5", "100.0"));
      cables.add(new VNAVelocityFactor("UR-67", 50.0D, 0.66D, "0.60", "10.0", "2.0", "100.0"));
      cables.add(new VNAVelocityFactor("UR-70", 75.0D, 0.66D, "1.50", "10.0", "4.9", "100.0"));
      cables.add(new VNAVelocityFactor("UR-74", 51.0D, 0.66D, "0.30", "10.0", "1.0", "100.0"));
      cables.add(new VNAVelocityFactor("UR-76", 51.0D, 0.66D, "1.60", "10.0", "5.3", "100.0"));
      cables.add(new VNAVelocityFactor("UR-77", 75.0D, 0.66D, "0.30", "10.0", "1.0", "100.0"));
      cables.add(new VNAVelocityFactor("UR-79", 50.0D, 0.96D, "0.16", "10.0", "0.5", "100.0"));
      cables.add(new VNAVelocityFactor("UR-83", 50.0D, 0.96D, "0.25", "10.0", "0.8", "100.0"));
      cables.add(new VNAVelocityFactor("UR-85", 75.0D, 0.96D, "0.20", "10.0", "0.7", "100.0"));
      cables.add(new VNAVelocityFactor("UR-90", 75.0D, 0.66D, "1.10", "10.0", "3.5", "100.0"));
      cables.add(new VNAVelocityFactor("UR-95", 50.0D, 0.66D, "2.60", "10.0", "8.2", "100.0"));
      TraceHelper.entry(this, "filLDefaults");
   }

   public VNAVelocityFactorTableModel() {
      TraceHelper.entry(this, "VNAVelocityFactorTableModel");
      this.readCSV();
      TraceHelper.exit(this, "VNAVelocityFactorTableModel");
   }

private void readCSV() {
    String methodName = "readCSV";
    TraceHelper.entry(this, "readCSV");
    cables.clear();

    try (CsvBeanReader beanReader = new CsvBeanReader(new FileReader(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE)) {
        String[] header = beanReader.getHeader(true);

        VNAVelocityFactor cable;
        while ((cable = (VNAVelocityFactor) beanReader.read(VNAVelocityFactor.class, header, cellProcessors)) != null) {
            cables.add(cable);
        }
    } catch (IOException e) {
        // Handle file-related exceptions
        ErrorLogHelper.text(this, "readCSV", e.getMessage());
        cables.clear();
        this.fillDefaults();
        this.createCSV();
    } catch (Exception e) {
        // Handle any other exceptions that might occur
        ErrorLogHelper.text(this, "readCSV", e.getMessage());
        cables.clear();
        this.fillDefaults();
        this.createCSV();
    }

    TraceHelper.exit(this, "readCSV");
}


private void createCSV() {
    String methodName = "createCSV";
    TraceHelper.entry(this, "createCSV");

    try (CsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(CONFIG_PATHNAME), CsvPreference.STANDARD_PREFERENCE)) {
        String[] header = new String[]{"name", "z0", "f1", "attenF1", "f2", "attenF2", "vf"};
        beanWriter.writeHeader(header);

        for (VNAVelocityFactor cable : cables) {
            beanWriter.write(cable, header, cellProcessors);
        }
    } catch (IOException e) {
        ErrorLogHelper.exception(this, "createCSV", e);
    }

    TraceHelper.exit(this, "createCSV");
}


   public int getSize() {
      return cables.size();
   }

   public int getColumnCount() {
      return 7;
   }

   public int getRowCount() {
      return cables.size();
   }

   public Object getValueAt(int row, int column) {
      VNAVelocityFactor item = (VNAVelocityFactor)cables.get(row);
      switch(column) {
      case 0:
         return item.getName();
      case 1:
         return nfVelocity.format(item.getVf());
      case 2:
         return nfResistance.format(item.getZ0());
      case 3:
         return item.getF1();
      case 4:
         return item.getAttenF1();
      case 5:
         return item.getF2();
      case 6:
         return item.getAttenF2();
      default:
         return "???";
      }
   }

   public String getColumnName(int column) {
      switch(column) {
      case 0:
         return "Type";
      case 1:
         return "Vf";
      case 2:
         return "Z0";
      case 3:
         return "f1 (MHz)";
      case 4:
         return "loss@f1 (dB/100m)";
      case 5:
         return "f2 (MHz)";
      case 6:
         return "loss@f2 (dB/100m)";
      default:
         return "??";
      }
   }

   public VNAVelocityFactor getDataAtRow(int row) {
      return row >= 0 && row < cables.size() ? (VNAVelocityFactor)cables.get(row) : null;
   }
}
