package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class DoubleToXLSExporter {
   private DoubleToXLSExporter() {
   }

   private static void dumpData(double[] input, double[] output, HSSFWorkbook wb) {
      int idx = wb.getSheetIndex("vnaJ");
      if (idx >= 0) {
         wb.removeSheetAt(idx);
      }

      HSSFSheet sheet = wb.createSheet("vnaJ");
      int rowNum = 1;
      int cell = 1;
      HSSFRow row = sheet.createRow(rowNum);
      row.createCell(cell).setCellValue(new HSSFRichTextString("input"));
      row.createCell(cell).setCellValue(new HSSFRichTextString("output"));

      for(int i = 0; i < input.length; ++i) {
         cell = 0;
         row = sheet.createRow(rowNum);
         cell = cell + 1;
         row.createCell(cell).setCellValue(input[i]);
         row.createCell(cell).setCellValue(output[i]);
         ++rowNum;
      }

      sheet.autoSizeColumn(0);
      sheet.autoSizeColumn(1);
   }

   public static void export(String fnp, double[] input, double[] output) {
      String methodName = "export";
      TraceHelper.entry(XLSExporter.class, "export");

      try {
         HSSFWorkbook wb = new HSSFWorkbook();
         dumpData(input, output, wb);
         FileOutputStream fileOut = new FileOutputStream(fnp);
         wb.write(fileOut);
         fileOut.close();
      } catch (IOException var6) {
         ErrorLogHelper.exception(XLSExporter.class, "export", var6);
      }

      TraceHelper.exit(XLSExporter.class, "export");
   }
}
