package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XLSExporter extends VNAExporter {
   public XLSExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
   }

   private void dumpData(VNACalibratedSample[] dataList, HSSFWorkbook wb) throws IOException {
      int idx = wb.getSheetIndex("vnaJ");
      if (idx >= 0) {
         wb.removeSheetAt(idx);
      }

      HSSFSheet sheet = wb.createSheet("vnaJ");
      int rowNum = 1;
      int cell = 0;
      HSSFRow row = sheet.createRow(rowNum);
      int var13 = cell + 1;
      row.createCell(cell).setCellValue(new HSSFRichTextString("Frequency (Hz)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Returnloss (dB)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Returnphase (°)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Transmissionloss (dB)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Transmissionphase (°)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Rs (Ohm)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Xs (Ohm)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("|Z| (Ohm)"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Magnitude"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Rho real"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Rho imag"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("SWR"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("Theta"));
      row.createCell(var13++).setCellValue(new HSSFRichTextString("GroupDelay (nS)"));
      VNACalibratedSample[] var11 = dataList;
      int var10 = dataList.length;

      for(int var9 = 0; var9 < var10; ++var9) {
         VNACalibratedSample data = var11[var9];
         cell = 0;
         row = sheet.createRow(rowNum);
         var13 = cell + 1;
         row.createCell(cell).setCellValue((double)data.getFrequency());
         row.createCell(var13++).setCellValue(data.getReflectionLoss());
         row.createCell(var13++).setCellValue(data.getReflectionPhase());
         row.createCell(var13++).setCellValue(data.getTransmissionLoss());
         row.createCell(var13++).setCellValue(data.getTransmissionPhase());
         row.createCell(var13++).setCellValue(data.getR());
         row.createCell(var13++).setCellValue(data.getX());
         row.createCell(var13++).setCellValue(data.getZ());
         row.createCell(var13++).setCellValue(data.getMag());
         if (data.getRHO() != null) {
            row.createCell(var13++).setCellValue(data.getRHO().getReal());
            row.createCell(var13++).setCellValue(data.getRHO().getImaginary());
         } else {
            row.createCell(var13++);
            row.createCell(var13++);
         }

         row.createCell(var13++).setCellValue(data.getSWR());
         row.createCell(var13++).setCellValue(data.getTheta());
         row.createCell(var13++).setCellValue(data.getGroupDelay());
         ++rowNum;
      }

      sheet.autoSizeColumn(0);
      sheet.autoSizeColumn(1);
      sheet.autoSizeColumn(2);
   }

   public String export(String fnp, boolean overwrite) throws ProcessingException {
      TraceHelper.entry(this, "export");
      String currFilename = null;
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      VNACalibratedSample[] pDataList = blk.getCalibratedSamples();

      try {
         currFilename = this.check4FileToDelete(fnp, overwrite);
         if (currFilename != null) {
            HSSFWorkbook wb = new HSSFWorkbook();
            this.dumpData(pDataList, wb);
            FileOutputStream fileOut = new FileOutputStream(currFilename);
            wb.write(fileOut);
            fileOut.close();
         }
      } catch (IOException var8) {
         ErrorLogHelper.exception(this, "export", var8);
         throw new ProcessingException(var8);
      }

      TraceHelper.exit(this, "export");
      return currFilename;
   }

   public String getExtension() {
      return ".xls";
   }
}
