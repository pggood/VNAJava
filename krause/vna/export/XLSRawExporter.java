package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationPoint;
import org.apache.commons.math3.complex.Complex;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XLSRawExporter {
   private static final XLSRawExporter instance = new XLSRawExporter();

   private static String doubleValue(Double val) {
      return val == null ? "nan" : val.toString();
   }

   private static String complexRealValue(Complex val) {
      return val == null ? "nan" : Double.toString(val.getReal());
   }

   private static String complexImagValue(Complex val) {
      return val == null ? "nan" : Double.toString(val.getImaginary());
   }

   public static String export(VNASampleBlock block, String pFilename) {
      TraceHelper.entry(instance, "export");
      String currFilename = VNAConfig.getSingleton().getExportDirectory() + "/" + pFilename + ".xls";
      if (block != null && block.getSamples() != null) {
         VNABaseSample[] pSamples = block.getSamples();
         TraceHelper.text(instance, "export", "filename=" + currFilename);

         try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("raw");
            int rowNum = 1;
            int cell = 0;
            HSSFRow row = sheet.createRow(rowNum);
            int var13 = cell + 1;
            row.createCell(cell).setCellValue(new HSSFRichTextString("Frq"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("angle"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("loss"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P1"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P2"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P3"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P4"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P1ref"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P2ref"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P3ref"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("P4ref"));
            row.createCell(var13++).setCellValue(new HSSFRichTextString("Temp"));

            for(int i = 0; i < pSamples.length; ++i) {
               VNABaseSample data = pSamples[i];
               cell = 0;
               row = sheet.createRow(rowNum);
               var13 = cell + 1;
               row.createCell(cell).setCellValue((double)data.getFrequency());
               row.createCell(var13++).setCellValue(data.getAngle());
               row.createCell(var13++).setCellValue(data.getLoss());
               row.createCell(var13++).setCellValue((double)data.getP1());
               row.createCell(var13++).setCellValue((double)data.getP2());
               row.createCell(var13++).setCellValue((double)data.getP3());
               row.createCell(var13++).setCellValue((double)data.getP4());
               row.createCell(var13++).setCellValue((double)data.getP1Ref());
               row.createCell(var13++).setCellValue((double)data.getP2Ref());
               row.createCell(var13++).setCellValue((double)data.getP3Ref());
               row.createCell(var13++).setCellValue((double)data.getP4Ref());
               row.createCell(var13++).setCellValue(doubleValue(block.getDeviceTemperature()));
               ++rowNum;
            }

            sheet.autoSizeColumn(0);
            FileOutputStream fileOut = new FileOutputStream(currFilename);
            wb.write(fileOut);
            fileOut.close();
         } catch (IOException var11) {
            ErrorLogHelper.exception(instance, "export", var11);
         }

         TraceHelper.exitWithRC(instance, "export", currFilename);
      } else {
         TraceHelper.text(instance, "export", "No calibration data");
      }

      return currFilename;
   }

   public static void export(VNACalibrationPoint[] pSamples, String pFilename) {
      TraceHelper.entry(instance, "export");
      String currFilename = VNAConfig.getSingleton().getExportDirectory() + "/" + pFilename + ".xls";
      TraceHelper.text(instance, "export", "filename=" + currFilename);
      if (pSamples == null) {
         ErrorLogHelper.text(instance, "export", "No calibration points passed");
      } else {
         try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("CalPoints");
            int rowNum = 1;
            int cell = 0;
            HSSFRow row = sheet.createRow(rowNum);
            int var12 = cell + 1;
            row.createCell(cell).setCellValue(new HSSFRichTextString("Frequency (Hz)"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("Loss"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("Phase"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("real(DeltaE)"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("imag(DeltaE)"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("real(E00)"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("imag(E00)"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("real(E01)"));
            row.createCell(var12++).setCellValue(new HSSFRichTextString("imag(E01)"));

            for(int i = 0; i < pSamples.length; ++i) {
               VNACalibrationPoint data = pSamples[i];
               cell = 0;
               row = sheet.createRow(rowNum);
               var12 = cell + 1;
               row.createCell(cell).setCellValue((double)data.getFrequency());
               row.createCell(var12++).setCellValue(data.getLoss());
               row.createCell(var12++).setCellValue(data.getPhase());
               row.createCell(var12++).setCellValue(complexRealValue(data.getDeltaE()));
               row.createCell(var12++).setCellValue(complexImagValue(data.getDeltaE()));
               row.createCell(var12++).setCellValue(complexRealValue(data.getE00()));
               row.createCell(var12++).setCellValue(complexImagValue(data.getE00()));
               row.createCell(var12++).setCellValue(complexRealValue(data.getE11()));
               row.createCell(var12++).setCellValue(complexImagValue(data.getE11()));
               ++rowNum;
            }

            sheet.autoSizeColumn(0);
            FileOutputStream fileOut = new FileOutputStream(currFilename);
            wb.write(fileOut);
            fileOut.close();
         } catch (IOException var10) {
            ErrorLogHelper.exception(instance, "export", var10);
         }

         TraceHelper.exit(instance, "export");
      }
   }

   public static void export(VNACalibratedSample[] pSamples, String pFilename) {
      TraceHelper.entry(instance, "export");
      String currFilename = VNAConfig.getSingleton().getExportDirectory() + "/" + pFilename + ".xls";
      TraceHelper.text(instance, "export", "filename=" + currFilename);

      try {
         HSSFWorkbook wb = new HSSFWorkbook();
         HSSFSheet sheet = wb.createSheet("CalPoints");
         int rowNum = 1;
         int cell = 0;
         HSSFRow row = sheet.createRow(rowNum);
         int var12 = cell + 1;
         row.createCell(cell).setCellValue(new HSSFRichTextString("Frequency (Hz)"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("Magnitude"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("ReflLoss"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("ReflPhase"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("SWR"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("Theta"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("TransLoss"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("TransPhase"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("Rs"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("Xs"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("|Z|"));
         row.createCell(var12++).setCellValue(new HSSFRichTextString("GrpDly"));

         for(int i = 0; i < pSamples.length; ++i) {
            VNACalibratedSample data = pSamples[i];
            cell = 0;
            row = sheet.createRow(rowNum);
            var12 = cell + 1;
            row.createCell(cell).setCellValue((double)data.getFrequency());
            row.createCell(var12++).setCellValue(data.getMag());
            row.createCell(var12++).setCellValue(data.getReflectionLoss());
            row.createCell(var12++).setCellValue(data.getReflectionPhase());
            row.createCell(var12++).setCellValue(data.getSWR());
            row.createCell(var12++).setCellValue(data.getTheta());
            row.createCell(var12++).setCellValue(data.getTransmissionLoss());
            row.createCell(var12++).setCellValue(data.getTransmissionPhase());
            row.createCell(var12++).setCellValue(data.getR());
            row.createCell(var12++).setCellValue(data.getX());
            row.createCell(var12++).setCellValue(data.getZ());
            row.createCell(var12++).setCellValue(data.getGroupDelay());
            ++rowNum;
         }

         sheet.autoSizeColumn(0);
         FileOutputStream fileOut = new FileOutputStream(currFilename);
         wb.write(fileOut);
         fileOut.close();
      } catch (IOException var10) {
         ErrorLogHelper.exception(instance, "export", var10);
      }

      TraceHelper.exit(instance, "export");
   }
}
