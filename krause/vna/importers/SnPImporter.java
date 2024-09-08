package krause.vna.importers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import org.apache.commons.math3.complex.Complex;

public class SnPImporter {
   private Map<String, SnPInfoBlock.PARAMETER> PARAMETER_MAP = new HashMap();
   private Map<String, SnPInfoBlock.FORMAT> FORMAT_MAP = new HashMap();

   public SnPImporter() {
      this.PARAMETER_MAP.put("S", SnPInfoBlock.PARAMETER.S);
      this.PARAMETER_MAP.put("Y", SnPInfoBlock.PARAMETER.Y);
      this.PARAMETER_MAP.put("Z", SnPInfoBlock.PARAMETER.Z);
      this.PARAMETER_MAP.put("H", SnPInfoBlock.PARAMETER.H);
      this.PARAMETER_MAP.put("G", SnPInfoBlock.PARAMETER.G);
      this.FORMAT_MAP.put("DB", SnPInfoBlock.FORMAT.DB);
      this.FORMAT_MAP.put("MA", SnPInfoBlock.FORMAT.MA);
      this.FORMAT_MAP.put("RI", SnPInfoBlock.FORMAT.RI);
   }

   private SnPRecord analyseDataLine(SnPInfoBlock infoblock, String line) {
      SnPRecord rc = null;
      TraceHelper.entry(this, "analyseDataLine");
      String[] parts = line.toUpperCase().split("\\s+");
      if (parts.length > 1) {
         rc = new SnPRecord();
         double freq = Double.parseDouble(parts[0]);
         rc.setFrequency((long)(freq * (double)infoblock.getFrequencyMultiplier()));
         int pairs = (parts.length - 1) / 2;
         int i;
         double real;
         double imag;
         if (infoblock.getFormat() == SnPInfoBlock.FORMAT.DB) {
            for(i = 0; i < pairs; ++i) {
               real = Double.parseDouble(parts[1 + 2 * i]);
               imag = Double.parseDouble(parts[2 + 2 * i]);
               rc.getLoss()[i] = real;
               rc.getPhase()[i] = imag;
            }
         } else if (infoblock.getFormat() == SnPInfoBlock.FORMAT.RI) {
            for(i = 0; i < pairs; ++i) {
               real = Double.parseDouble(parts[1 + 2 * i]);
               imag = Double.parseDouble(parts[2 + 2 * i]);
               rc.getLoss()[i] = 20.0D * Math.log(Math.sqrt(real * real + imag * imag));
               rc.getPhase()[i] = Math.atan(imag / real);
            }

            infoblock.setFormat(SnPInfoBlock.FORMAT.DB);
         }

         for(i = pairs; i < 4; ++i) {
            rc.getLoss()[i] = Double.NaN;
            rc.getPhase()[i] = Double.NaN;
         }
      }

      TraceHelper.exit(this, "analyseDataLine");
      return rc;
   }

   private int analyseOptionLine(SnPInfoBlock infoBlock, String line) {
      int rc = 99;
      TraceHelper.entry(this, "analyseOptionLine");
      String[] parts = line.toUpperCase().split("\\s+");
      if (parts.length == 6) {
         if ("GHZ".equals(parts[1])) {
            infoBlock.setFrequencyMultiplier(1000000000L);
         } else if ("MHZ".equals(parts[1])) {
            infoBlock.setFrequencyMultiplier(1000000L);
         } else if ("KHZ".equals(parts[1])) {
            infoBlock.setFrequencyMultiplier(1000L);
         } else {
            infoBlock.setFrequencyMultiplier(1L);
         }

         infoBlock.setParameter((SnPInfoBlock.PARAMETER)this.PARAMETER_MAP.get(parts[2]));
         infoBlock.setFormat((SnPInfoBlock.FORMAT)this.FORMAT_MAP.get(parts[3]));
         infoBlock.setReference(new Complex(Double.parseDouble(parts[5]), 0.0D));
         rc = 1;
      }

      TraceHelper.exit(this, "analyseOptionLine");
      return rc;
   }

   public SnPInfoBlock readFile(String filename, String encoding) throws ProcessingException {
      SnPInfoBlock rc = null;
      TraceHelper.entry(this, "readFile");
      Scanner scanner = null;
      FileInputStream fis = null;

      try {
         fis = new FileInputStream(filename);
         scanner = new Scanner(fis, encoding);
         rc = new SnPInfoBlock();
         rc.setFilename(filename);
         int state = 0;

         while(scanner.hasNextLine() && state != 99) {
            String line = scanner.nextLine();
            TraceHelper.text(this, "readFile", state + "::" + line);
            line = line.trim();
            if (line.length() != 0) {
               switch(state) {
               case 0:
                  if (line.startsWith("!")) {
                     state = 0;
                     if (rc.getComment() == null) {
                        rc.setComment(line);
                     } else {
                        rc.setComment(rc.getComment() + line);
                     }
                  } else if (line.startsWith("#")) {
                     state = this.analyseOptionLine(rc, line);
                  }
                  break;
               case 1:
                  SnPRecord inpRec = this.analyseDataLine(rc, line);
                  if (inpRec != null) {
                     rc.getRecords().add(inpRec);
                  } else {
                     state = 99;
                  }
                  break;
               case 99:
                  throw new ProcessingException("Illegal file format");
               }
            }
         }
      } catch (FileNotFoundException var16) {
         ErrorLogHelper.exception(this, "readFile", var16);
         throw new ProcessingException(var16);
      } finally {
         if (scanner != null) {
            scanner.close();
         }

         if (fis != null) {
            try {
               fis.close();
            } catch (IOException var15) {
               ErrorLogHelper.exception(this, "readFile", var15);
            }
         }

      }

      TraceHelper.exit(this, "readFile");
      return rc;
   }
}
