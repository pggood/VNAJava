package krause.vna.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.device.IVNADriverMathHelper;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class VNASampleBlock implements Serializable {
   public static final String ANALYSER_TYPE_UNKNOWN = "99";
   private static final VNAConfig config = VNAConfig.getSingleton();
   private static final long serialVersionUID = -231006451661112222L;
   private String analyserType = "99";
   private transient Double deviceSupply = null;
   private Double deviceTemperature = null;
   private transient IVNADriverMathHelper mathHelper = null;
   private int numberOfSteps = 0;
   private int numberOfOverscans = 0;
   private VNABaseSample[] samples = null;
   private VNAScanMode scanMode;
   private long startFrequency = 0L;
   private long stopFrequency = 0L;

   public VNASampleBlock() {
   }

   public VNASampleBlock(VNACalibrationBlock calibration) {
      this.setStartFrequency(calibration.getStartFrequency());
      this.setStopFrequency(calibration.getStopFrequency());
      this.setNumberOfSteps(calibration.getNumberOfSteps());
      this.setScanMode(calibration.getScanMode());
      this.setAnalyserType(calibration.getAnalyserType());
      this.setSamples(new VNABaseSample[this.getNumberOfSteps()]);
      this.setDeviceTemperature(calibration.getTemperature());
   }

   public void dump() {
      TraceHelper.entry(this, "dump");
      HSSFWorkbook wb = new HSSFWorkbook();
      HSSFSheet sheet = wb.createSheet("vnaJ");
      int rowNum = 2;
      int cell = 0;
      //int rowNum = rowNum + 1;
      HSSFRow row = sheet.createRow(rowNum);
      int var20 = cell + 1;
      row.createCell(cell).setCellValue(new HSSFRichTextString("Frequency"));
      row.createCell(var20++).setCellValue(new HSSFRichTextString("Loss"));
      row.createCell(var20++).setCellValue(new HSSFRichTextString("Angle"));
      row.createCell(var20++).setCellValue(new HSSFRichTextString("P1"));
      row.createCell(var20++).setCellValue(new HSSFRichTextString("P2"));
      row.createCell(var20++).setCellValue(new HSSFRichTextString("P3"));
      row.createCell(var20++).setCellValue(new HSSFRichTextString("P4"));

      for(int i = 0; i < this.samples.length; ++i) {
         VNABaseSample data = this.samples[i];
         cell = 0;
         row = sheet.createRow(rowNum);
         var20 = cell + 1;
         row.createCell(cell).setCellValue((double)data.getFrequency());
         row.createCell(var20++).setCellValue(data.getLoss());
         row.createCell(var20++).setCellValue(data.getAngle());
         if (data.hasPData()) {
            row.createCell(var20++).setCellValue((double)data.getP1());
            row.createCell(var20++).setCellValue((double)data.getP2());
            row.createCell(var20++).setCellValue((double)data.getP3());
            row.createCell(var20++).setCellValue((double)data.getP4());
         }

         ++rowNum;
      }

      sheet.autoSizeColumn(0);
      sheet.autoSizeColumn(1);
      sheet.autoSizeColumn(2);
      File fi = null;
      FileOutputStream fileOut = null;

      try {
         fi = File.createTempFile("raw_", ".xls", new File(config.getExportDirectory()));
         fileOut = new FileOutputStream(fi);
         wb.write(fileOut);
      } catch (IOException var17) {
         ErrorLogHelper.exception(this, "dump", var17);
      } finally {
         if (fileOut != null) {
            try {
               fileOut.close();
            } catch (IOException var16) {
               ErrorLogHelper.exception(this, "dump", var16);
            }
         }

      }

      TraceHelper.exit(this, "dump");
   }

   public String getAnalyserType() {
      return this.analyserType;
   }

   public Double getDeviceSupply() {
      return this.deviceSupply;
   }

   public Double getDeviceTemperature() {
      return this.deviceTemperature;
   }

   public IVNADriverMathHelper getMathHelper() {
      return this.mathHelper;
   }

   public int getNumberOfSteps() {
      return this.numberOfSteps;
   }

   public VNABaseSample[] getSamples() {
      return this.samples;
   }

   public VNAScanMode getScanMode() {
      return this.scanMode;
   }

   public long getStartFrequency() {
      return this.startFrequency;
   }

   public long getStopFrequency() {
      return this.stopFrequency;
   }

   public void setAnalyserType(String analyserType) {
      this.analyserType = analyserType;
   }

   public void setDeviceSupply(Double deviceSupply) {
      this.deviceSupply = deviceSupply;
   }

   public void setDeviceTemperature(Double deviceTemperature) {
      this.deviceTemperature = deviceTemperature;
   }

   public void setMathHelper(IVNADriverMathHelper mathHelper) {
      this.mathHelper = mathHelper;
   }

   public void setNumberOfSteps(int numberOfSteps) {
      this.numberOfSteps = numberOfSteps;
   }

   public void setSamples(VNABaseSample[] samples) {
      this.samples = samples;
   }

   public void setScanMode(VNAScanMode scanMode) {
      this.scanMode = scanMode;
   }

   public void setStartFrequency(long startFrequency) {
      this.startFrequency = startFrequency;
   }

   public void setStopFrequency(long stopFrequency) {
      this.stopFrequency = stopFrequency;
   }

   public String toString() {
      return "VNASampleBlock [numberOfSteps=" + this.numberOfSteps + ", #samples=" + this.samples.length + ", startFrequency=" + this.startFrequency + ", stopFrequency=" + this.stopFrequency + "]";
   }

   public int getNumberOfOverscans() {
      return this.numberOfOverscans;
   }

   public void setNumberOfOverscans(int numberOfOverscans) {
      this.numberOfOverscans = numberOfOverscans;
   }
}
