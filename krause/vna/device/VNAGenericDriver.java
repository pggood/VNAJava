package krause.vna.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import krause.common.exception.InitializationException;
import krause.common.validation.ValidationResult;
import krause.common.validation.ValidationResults;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAScanMode;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.calibrate.mode1.VNACalibrationRangeComparator;
import krause.vna.resources.VNAMessages;

public abstract class VNAGenericDriver implements IVNADriver {
   public static final int MINIMUM_SCAN_POINTS = 1000;
   public static final int MAXIMUM_SCAN_POINTS = 30000;
   protected VNAConfig config = VNAConfig.getSingleton();
   private VNADeviceInfoBlock deviceInfoBlock = null;
   private IVNADriverMathHelper mathHelper = null;
   private String portname = null;

   protected String generateScanRangeFilename() {
      String fName = VNAConfig.getSingleton().getPresetsDirectory();
      fName = fName + "/CalRanges_";
      fName = fName + this.getDeviceInfoBlock().getShortName();
      fName = fName + ".txt";
      return fName;
   }

   public final VNACalibrationRange[] getCalibrationRanges() {
      VNACalibrationRange[] rc = null;
      TraceHelper.entry(this, "getCalibrationRanges");
      rc = this.loadCalibrationRanges();
      if (rc == null) {
         rc = this.getSpecificCalibrationRanges();
      }

      this.saveCalibrationRanges(rc);
      TraceHelper.entry(this, "getCalibrationRanges");
      return rc;
   }

   public VNAScanMode getDefaultMode() {
      return VNAScanMode.MODE_REFLECTION;
   }

   public String getDeviceFirmwareInfo() {
      String methodName = "getDeviceFirmwareInfo";
      TraceHelper.entry(this, "getDeviceFirmwareInfo");
      TraceHelper.exitWithRC(this, "getDeviceFirmwareInfo", "null");
      return null;
   }

   public VNADeviceInfoBlock getDeviceInfoBlock() {
      return this.deviceInfoBlock;
   }

   public Double getDeviceSupply() {
      String methodName = "getDevicePowerStatus";
      TraceHelper.entry(this, "getDevicePowerStatus");
      TraceHelper.exitWithRC(this, "getDevicePowerStatus", "null");
      return null;
   }

   public Double getDeviceTemperature() {
      String methodName = "getDeviceTemperature";
      TraceHelper.entry(this, "getDeviceTemperature");
      TraceHelper.exitWithRC(this, "getDeviceTemperature", "null");
      return null;
   }

   public final IVNADriverMathHelper getMathHelper() {
      return this.mathHelper;
   }

   public String getPortname() {
      return this.portname;
   }

   public void init() throws InitializationException {
      TraceHelper.entry(this, "init");
      this.setPortname(this.config.getPortName(this));
      TraceHelper.exit(this, "init");
   }

   protected VNACalibrationRange[] loadCalibrationRanges() {
      String methodeName = "loadCalibrationRanges";
      VNACalibrationRange[] rc = null;
      TraceHelper.entry(this, "loadCalibrationRanges");
      long min = this.getDeviceInfoBlock().getMinFrequency();
      long max = this.getDeviceInfoBlock().getMaxFrequency();
      String fName = this.generateScanRangeFilename();
      FileInputStream fstream = null;
      DataInputStream dis = null;
      BufferedReader br = null;
      ArrayList listRanges = new ArrayList();

      try {
         fstream = new FileInputStream(fName);
         dis = new DataInputStream(fstream);
         br = new BufferedReader(new InputStreamReader(dis));

         String line;
         while((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0) {
               String[] parts = line.split("[\t ]");
               if (parts.length == 4) {
                  long start = Long.parseLong(parts[0]);
                  long stop = Long.parseLong(parts[1]);
                  int steps = Integer.parseInt(parts[2]);
                  int overscans = Integer.parseInt(parts[3]);
                  VNACalibrationRange sr = new VNACalibrationRange(start, stop, steps, overscans);
                  listRanges.add(sr);
               } else {
                  ErrorLogHelper.text(this, "loadCalibrationRanges", "Line [" + line + "] ignored");
               }
            } else {
               ErrorLogHelper.text(this, "loadCalibrationRanges", "Empty line ignored");
            }
         }
      } catch (FileNotFoundException var41) {
      } catch (IOException var42) {
         ErrorLogHelper.exception(this, "loadCalibrationRanges", var42);
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException var40) {
               ErrorLogHelper.exception(this, "loadCalibrationRanges", var40);
            }
         }

         if (dis != null) {
            try {
               dis.close();
            } catch (IOException var39) {
               ErrorLogHelper.exception(this, "loadCalibrationRanges", var39);
            }
         }

         if (fstream != null) {
            try {
               fstream.close();
            } catch (IOException var38) {
               ErrorLogHelper.exception(this, "loadCalibrationRanges", var38);
            }
         }

      }

      TraceHelper.text(this, "loadCalibrationRanges", "File read");
      Collections.sort(listRanges, new VNACalibrationRangeComparator());
      TraceHelper.text(this, "loadCalibrationRanges", "Ranges sorted");
      boolean ok = true;
      if (listRanges.size() > 0) {
         if (((VNACalibrationRange)listRanges.get(0)).getStart() != min) {
            ErrorLogHelper.text(this, "loadCalibrationRanges", "First range must start at [" + min + "]");
            ok = false;
         }

         if (((VNACalibrationRange)listRanges.get(listRanges.size() - 1)).getStop() != max) {
            ErrorLogHelper.text(this, "loadCalibrationRanges", "Last range must end at [" + max + "]");
            ok = false;
         }

         for(int i = 1; i < listRanges.size(); ++i) {
            VNAScanRange asr = (VNAScanRange)listRanges.get(i);
            if (asr.getStart() < min || asr.getStart() >= asr.getStop() || asr.getStop() > max || asr.getNumScanPoints() < 10 || asr.getNumScanPoints() > 30000) {
               ErrorLogHelper.text(this, "loadCalibrationRanges", "Range [" + asr + "] not valid");
               ok = false;
            }

            VNAScanRange prevSr = (VNAScanRange)listRanges.get(i - 1);
            if (prevSr.getStop() + 1L != asr.getStart()) {
               ErrorLogHelper.text(this, "loadCalibrationRanges", "Range [" + prevSr + "] and [" + asr + "] are not not consecutively");
               ok = false;
            }
         }
      }

      TraceHelper.text(this, "loadCalibrationRanges", "Validation result=" + ok);
      if (ok && listRanges.size() > 0) {
         rc = (VNACalibrationRange[])listRanges.toArray(new VNACalibrationRange[listRanges.size()]);
      } else {
         File f = new File(fName);
         String fNameNew = fName + ".bak";
         f.renameTo(new File(fNameNew));
         ErrorLogHelper.text(this, "loadCalibrationRanges", "Old rangefile renamed from [" + fName + "] to [" + fNameNew + "]");
      }

      TraceHelper.entry(this, "loadCalibrationRanges");
      return rc;
   }

   public void saveCalibrationRanges(VNACalibrationRange[] ranges) {
      TraceHelper.entry(this, "saveCalibrationRanges");
      BufferedWriter bw = null;
      FileWriter fw = null;
      File fi = null;

      try {
         fi = new File(this.generateScanRangeFilename());
         fw = new FileWriter(fi, false);
         bw = new BufferedWriter(fw);

         for(int i = 0; i < ranges.length; ++i) {
            bw.write("" + ranges[i].getStart());
            bw.write(" ");
            bw.write("" + ranges[i].getStop());
            bw.write(" ");
            bw.write("" + ranges[i].getNumScanPoints());
            bw.write(" ");
            bw.write("" + ranges[i].getNumOverScans());
            bw.newLine();
         }
      } catch (Exception var18) {
      } finally {
         if (bw != null) {
            try {
               bw.close();
            } catch (IOException var17) {
               ErrorLogHelper.exception(this, "saveCalibrationRanges", var17);
            }
         }

         if (fw != null) {
            try {
               fw.close();
            } catch (IOException var16) {
               ErrorLogHelper.exception(this, "saveCalibrationRanges", var16);
            }
         }

      }

      TraceHelper.entry(this, "saveCalibrationRanges");
   }

   public void setDeviceInfoBlock(VNADeviceInfoBlock deviceInfoBlock) {
      this.deviceInfoBlock = deviceInfoBlock;
   }

   public void setMathHelper(IVNADriverMathHelper mathHelper) {
      this.mathHelper = mathHelper;
   }

   public void setPortname(String portname) {
      this.portname = portname;
   }

   public ValidationResults validateScanRange(VNAScanRange range) {
      ValidationResults results = new ValidationResults();
      if (range.getStop() - range.getStart() < 1000L) {
         String msg = VNAMessages.getString("VNAGenericDriver.ScanRange.tooSmall");
         ValidationResult res = new ValidationResult(MessageFormat.format(msg, 1000));
         res.setType(ValidationResult.ValidationType.ERROR);
         res.setErrorObject((Object)null);
         results.add(res);
      } else {
         int samples = range.getNumScanPoints();
         long frequencyStep = (range.getStop() - range.getStart()) / (long)samples;
         long newStop = range.getStart() + frequencyStep * (long)samples;
         long newStart = range.getStart();
         if (newStop > this.getDeviceInfoBlock().getMaxFrequency()) {
            newStop = range.getStop();
            newStart = newStop - frequencyStep * (long)samples;
         }

         range.setStart(newStart);
         range.setStop(newStop);
      }

      return results;
   }
}
