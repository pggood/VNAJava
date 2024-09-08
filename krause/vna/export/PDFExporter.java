package krause.vna.export;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.resources.VNAMessages;
import org.jfree.chart.JFreeChart;

public class PDFExporter extends VNAExporter {
   private static final int IMAGE_WIDTH = 2000;
   private static final int IMAGE_HEIGHT = 1500;
   private final Font FONT_CELL_VALUE;
   private final Font FONT_CELL_HEADER;
   private final Font FONT_HEADER;
   private final Font FONT_FOOTER;
   private final Font FONT_MARKER;
   private final Font FONT_TITLE;
   private final Font FONT_COMMENT;

   public PDFExporter(VNAMainFrame mainFrame) {
      super(mainFrame);
      if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
         this.FONT_CELL_HEADER = FontFactory.getFont("Times", 9.0F, 1);
         this.FONT_CELL_VALUE = new Font(2, 9.0F, 0);
         this.FONT_HEADER = new Font(2, 8.0F, 0);
         this.FONT_FOOTER = new Font(2, 8.0F, 0);
         this.FONT_MARKER = new Font(2, 10.0F, 1);
         this.FONT_TITLE = new Font(2, (float)this.config.getExportTitleFontSize(), 1);
         this.FONT_COMMENT = FontFactory.getFont("MS UI Gothic", 10.0F, 0);
      } else {
         this.FONT_CELL_HEADER = FontFactory.getFont("Courier", 9.0F, 1);
         this.FONT_CELL_VALUE = new Font(0, 9.0F, 0);
         this.FONT_HEADER = new Font(1, 8.0F, 0);
         this.FONT_FOOTER = new Font(1, 8.0F, 0);
         this.FONT_MARKER = new Font(0, 10.0F, 1);
         this.FONT_TITLE = new Font(1, (float)this.config.getExportTitleFontSize(), 1);
         this.FONT_COMMENT = new Font(0, 10.0F, 0);
      }

   }

   private void createMarkerTable(Document doc) throws DocumentException {
      VNAMarkerPanel mp = this.mainFrame.getMarkerPanel();
      VNAMarker[] markers = mp.getMarkers();
      float[] COL_WIDTH = new float[]{40.0F, 80.0F, 50.0F, 50.0F, 50.0F, 50.0F, 50.0F, 50.0F, 50.0F};
      PdfPTable table = new PdfPTable(COL_WIDTH.length);
      table.setTotalWidth(COL_WIDTH);
      table.setLockedWidth(true);
      this.createMarkerTableHeader(table);
      VNAMarker[] var9 = markers;
      int var8 = markers.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         VNAMarker marker = var9[var7];
         if (marker.isVisible()) {
            this.createMarkerTableRow(marker, table, false);
            if ("2".equals(marker.getShortName()) && mp.getDeltaMarker().getSample() != null) {
               this.createMarkerTableRow(mp.getDeltaMarker(), table, true);
            }
         }
      }

      Paragraph p = new Paragraph();
      p.setSpacingBefore(10.0F);
      p.setSpacingAfter(10.0F);
      p.add(table);
      doc.add(p);
   }

   private void createMarkerTableHeader(PdfPTable table) {
      table.addCell(this.createLeftHeaderCell(VNAMessages.getString("Marker")));
      table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.Frequency")));
      if (VNAScanMode.MODE_REFLECTION.equals(this.datapool.getScanMode())) {
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.RL")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.PhaseRL")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.Z")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.R")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.X")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.Theta")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.SWR")));
      } else {
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.TL")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.PhaseTL")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.Z")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.R")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.X")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.Theta")));
         table.addCell(this.createCenteredHeaderCell(VNAMessages.getString("Marker.GrpDelay")));
      }

   }

   private PdfPCell createLeftHeaderCell(String text) {
      Chunk c = new Chunk(text, this.FONT_CELL_HEADER);
      Paragraph p = new Paragraph(c);
      PdfPCell cell = new PdfPCell(p);
      cell.setHorizontalAlignment(0);
      return cell;
   }

   private PdfPCell createValueCell(String text) {
      Chunk c = new Chunk(text, this.FONT_CELL_VALUE);
      Paragraph p = new Paragraph(c);
      PdfPCell cell = new PdfPCell(p);
      cell.setHorizontalAlignment(2);
      return cell;
   }

   private PdfPCell createCenteredHeaderCell(String text) {
      Chunk c = new Chunk(text, this.FONT_CELL_HEADER);
      Paragraph p = new Paragraph(c);
      PdfPCell cell = new PdfPCell(p);
      cell.setHorizontalAlignment(1);
      return cell;
   }

   private void createMarkerTableRow(VNAMarker marker, PdfPTable table, boolean isDeltaMarker) {
      if (isDeltaMarker) {
         table.addCell(this.createMarkerNameCell("1-2"));
         table.addCell(this.createValueCell(marker.getTxtFrequency().getText()));
         table.addCell(this.createValueCell(marker.getTxtLoss().getText()));
         table.addCell(this.createValueCell(marker.getTxtPhase().getText()));
         table.addCell(this.createValueCell(marker.getTxtZAbsolute().getText()));
         table.addCell(this.createValueCell(marker.getTxtRs().getText()));
         table.addCell(this.createValueCell(marker.getTxtXsAbsolute().getText()));
         table.addCell(this.createValueCell(marker.getTxtTheta().getText()));
         table.addCell(this.createValueCell("---"));
      } else {
         table.addCell(this.createMarkerNameCell(marker.getName()));
         table.addCell(this.createValueCell(marker.getTxtFrequency().getText()));
         table.addCell(this.createValueCell(marker.getTxtLoss().getText()));
         table.addCell(this.createValueCell(marker.getTxtPhase().getText()));
         table.addCell(this.createValueCell(marker.getTxtZAbsolute().getText()));
         table.addCell(this.createValueCell(marker.getTxtRs().getText()));
         table.addCell(this.createValueCell(marker.getTxtXsAbsolute().getText()));
         table.addCell(this.createValueCell(marker.getTxtTheta().getText()));
         table.addCell(this.createValueCell(marker.getTxtSwrGrpDelay().getText()));
      }

   }

   private PdfPCell createMarkerNameCell(String text) {
      Chunk c = new Chunk(text, this.FONT_MARKER);
      Paragraph p = new Paragraph(c);
      PdfPCell cell = new PdfPCell(p);
      cell.setHorizontalAlignment(0);
      return cell;
   }

   private Document createDocument(String filename) throws ProcessingException, FileNotFoundException, DocumentException {
      Document document = new Document(PageSize.A4);
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
      PdfPageEventHelper pageHandler = new PdfPageEventHelper() {
         public void onEndPage(PdfWriter writer, Document document) {
            Rectangle page = document.getPageSize();
            PdfPTable table = new PdfPTable(3);
            PdfPCell cell = new PdfPCell(new Paragraph(VNAMessages.getString("Application.copyright"), PDFExporter.this.FONT_FOOTER));
            cell.setBorder(1);
            cell.setHorizontalAlignment(0);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(MessageFormat.format(VNAMessages.getString("Application.header"), VNAMessages.getString("Application.version")), PDFExporter.this.FONT_FOOTER));
            cell.setBorder(1);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(VNAMessages.getString("Application.URL"), PDFExporter.this.FONT_FOOTER));
            cell.setBorder(1);
            cell.setHorizontalAlignment(2);
            table.addCell(cell);
            table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            table.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
         }

         public void onStartPage(PdfWriter writer, Document document) {
            Rectangle page = document.getPageSize();
            PdfPTable table = new PdfPTable(1);
            Object[] parms = new Object[]{new Date(), System.getProperty("user.name")};
            String title = MessageFormat.format(VNAMessages.getString("Export.PDF.Title"), parms);
            PdfPCell cell = new PdfPCell(new Paragraph(title, PDFExporter.this.FONT_HEADER));
            cell.setBorder(2);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);
            table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            table.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + table.getTotalHeight(), writer.getDirectContent());
         }
      };
      writer.setPageEvent(pageHandler);
      document.open();
      return document;
   }

   public String export(String fnp, boolean overwrite) throws ProcessingException {
      String methodName = "export";
      TraceHelper.entry(this, "export");
      String currFilename = null;
      VNACalibratedSampleBlock blk = this.datapool.getCalibratedData();
      VNACalibratedSample[] pDataList = blk.getCalibratedSamples();

      try {
         currFilename = this.check4FileToDelete(fnp, overwrite);
         if (currFilename != null) {
            JFreeChart chart = this.createChart(pDataList);
            chart.setTitle("");
            Document doc = this.createDocument(currFilename);
            this.createTitle(doc);
            this.createImage(chart, doc);
            this.createMarkerTable(doc);
            this.createComment(doc);
            doc.close();
         }
      } catch (Exception var9) {
         ErrorLogHelper.exception(this, "export", var9);
         throw new ProcessingException(var9);
      }

      TraceHelper.entry(this, "export");
      return currFilename;
   }

   private void createTitle(Document doc) throws DocumentException {
      Paragraph p = new Paragraph();
      p.setSpacingAfter(10.0F);
      Chunk c = new Chunk("");
      p.add(c);
      doc.add(p);
      c = new Chunk(this.replaceParameters(this.config.getExportTitle()), this.FONT_TITLE);
      p = new Paragraph();
      p.setSpacingBefore(15.0F);
      p.setAlignment(1);
      p.setSpacingAfter(5.0F);
      p.add(c);
      doc.add(p);
   }

   private void createImage(JFreeChart chart, Document doc) throws IOException, DocumentException {
      Image awtImg = chart.createBufferedImage(2000, 1500);
      com.lowagie.text.Image itImg = com.lowagie.text.Image.getInstance(awtImg, (Color)null);
      float w = PageSize.A4.getWidth() - 100.0F;
      float h = w / 1.33F;
      itImg.scaleAbsolute(w, h);
      itImg.setAlignment(1);
      Paragraph p = new Paragraph();
      doc.add(new Paragraph(" "));
      p.add(itImg);
      doc.add(p);
   }

   private void createComment(Document doc) throws DocumentException {
      Paragraph p = new Paragraph();
      p.setSpacingAfter(10.0F);
      Chunk c = new Chunk(VNAMessages.getString("Export.PDF.Comment"));
      c.setUnderline(0.2F, -2.0F);
      p.add(c);
      doc.add(p);
      c = new Chunk(this.replaceParameters(this.config.getExportComment()), this.FONT_COMMENT);
      p = new Paragraph();
      p.setIndentationLeft(10.0F);
      p.setIndentationRight(10.0F);
      p.setSpacingAfter(10.0F);
      p.add(c);
      doc.add(p);
   }

   public String getExtension() {
      return ".pdf";
   }
}
