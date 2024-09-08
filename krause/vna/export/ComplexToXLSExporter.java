package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import org.apache.commons.math3.complex.Complex;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ComplexToXLSExporter {
   private ComplexToXLSExporter() {
   }

   private static void dumpData(Complex[] input, Complex[] output, HSSFWorkbook wb) {
      int idx = wb.getSheetIndex("vnaJ");
      if (idx >= 0) {
         wb.removeSheetAt(idx);
      }

      HSSFSheet sheet = wb.createSheet("vnaJ");
      int rowNum = 2;
      int cell = 2;
      HSSFRow row = sheet.createRow(rowNum);
      row.createCell(cell).setCellValue(new HSSFRichTextString("C1.real"));
      row.createCell(cell++).setCellValue(new HSSFRichTextString("C1.imag"));
      row.createCell(cell++).setCellValue(new HSSFRichTextString("C2.real"));
      row.createCell(cell).setCellValue(new HSSFRichTextString("C2.imag"));

      for(int i = 0; i < input.length; ++i) {
         cell = 0;
         row = sheet.createRow(rowNum);
         cell = cell + 1;
         row.createCell(cell).setCellValue(input[i].getReal());
         row.createCell(cell++).setCellValue(input[i].getImaginary());
         row.createCell(cell++).setCellValue(output[i].getReal());
         row.createCell(cell).setCellValue(output[i].getImaginary());
         ++rowNum;
      }

      sheet.autoSizeColumn(0);
      sheet.autoSizeColumn(1);
      sheet.autoSizeColumn(2);
      sheet.autoSizeColumn(3);
   }

   public static void export(String fnp, Complex[] input, Complex[] output) {
      String methodName = "export";
      TraceHelper.entry(ComplexToXLSExporter.class, "export");

      try {
         HSSFWorkbook wb = new HSSFWorkbook();
         dumpData(input, output, wb);
         FileOutputStream fileOut = new FileOutputStream(fnp);
         wb.write(fileOut);
         fileOut.close();
      } catch (IOException var6) {
         ErrorLogHelper.exception(ComplexToXLSExporter.class, "export", var6);
      }

      TraceHelper.exit(ComplexToXLSExporter.class, "export");
   }
}
