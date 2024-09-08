package krause.vna.data.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrated.VNACalibrationPointHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;

public class VNACalibrationBlockHelper {
   static final VNACalibrationBlockHelper instance = new VNACalibrationBlockHelper();
   static final VNAConfig config = VNAConfig.getSingleton();

   private VNACalibrationBlockHelper() {
   }

public static boolean save(VNACalibrationBlock block, String myFileName) {
    String methodName = "save";
    TraceHelper.entry(instance, methodName);
    boolean result = false;

    if (!myFileName.endsWith(".cal")) {
        myFileName = myFileName + ".cal";
    }

    TraceHelper.text(instance, methodName, "Trying to write to [" + myFileName + "]");

    try (FileOutputStream fos = new FileOutputStream(myFileName);
         ObjectOutputStream encoder = new ObjectOutputStream(fos)) {

        encoder.writeObject("__V5");
        encoder.writeObject(block.getAnalyserType());
        encoder.writeObject(block.getComment());
        encoder.writeObject(block.getStartFrequency());
        encoder.writeObject(block.getStopFrequency());
        encoder.writeObject(block.getNumberOfSteps());
        encoder.writeObject(block.getNumberOfOverscans());
        encoder.writeObject(block.getScanMode());
        encoder.writeObject(block.getCalibrationData4Load());
        encoder.writeObject(block.getCalibrationData4Open());
        encoder.writeObject(block.getCalibrationData4Short());
        encoder.writeObject(block.getCalibrationData4Loop());
        block.setFile(new File(myFileName));
        result = true;

    } catch (IOException e) {
        ErrorLogHelper.exception(instance, methodName, e);
        ErrorLogHelper.text(instance, methodName, e.getMessage());
    }

    TraceHelper.exitWithRC(instance, methodName, result);
    return result;
}


public static VNACalibrationBlock loadCalibrationRAWData(File file, IVNADriver driver) throws ProcessingException {
    String methodName = "loadCalibrationRAWData";
    TraceHelper.entry(instance, methodName);
    VNACalibrationBlock calBlock = null;
    String myFileName = file.getAbsolutePath();
    TraceHelper.text(instance, methodName, "Trying to read from [" + myFileName + "]");

    try (FileInputStream fis = new FileInputStream(myFileName);
         ObjectInputStream decoder = new ObjectInputStream(fis)) {

        calBlock = new VNACalibrationBlock();
        readHeader(decoder, calBlock);
        calBlock.setFile(file);
        calBlock.setMathHelper(driver.getMathHelper());

        VNASampleBlock blkLoad = (VNASampleBlock) decoder.readObject();
        VNASampleBlock blkOpen = (VNASampleBlock) decoder.readObject();
        VNASampleBlock blkShort = (VNASampleBlock) decoder.readObject();
        VNASampleBlock blkLoop = (VNASampleBlock) decoder.readObject();

        if (driver.getDeviceInfoBlock().isPeakSuppression()) {
            long[] switchPoints = driver.getDeviceInfoBlock().getSwitchPoints();
            if (switchPoints != null) {
                if (blkLoad != null) {
                    VNASampleBlockHelper.removeSwitchPoints(blkLoad, switchPoints);
                }
                if (blkOpen != null) {
                    VNASampleBlockHelper.removeSwitchPoints(blkOpen, switchPoints);
                }
                if (blkShort != null) {
                    VNASampleBlockHelper.removeSwitchPoints(blkShort, switchPoints);
                }
                if (blkLoop != null) {
                    VNASampleBlockHelper.removeSwitchPoints(blkLoop, switchPoints);
                }
            }
        }

        calBlock.setCalibrationData4Load(blkLoad);
        calBlock.setCalibrationData4Open(blkOpen);
        calBlock.setCalibrationData4Short(blkShort);
        calBlock.setCalibrationData4Loop(blkLoop);

    } catch (ClassNotFoundException | IOException e) {
        ErrorLogHelper.exception(instance, methodName, e);
        throw new ProcessingException(e);
    }

    TraceHelper.exit(instance, methodName);
    return calBlock;
}


   public static VNACalibrationBlock load(File file, IVNADriver driver, VNACalibrationKit calKit) throws ProcessingException {
      String methodName = "load";
      TraceHelper.entry(instance, "load");
      VNACalibrationBlock calBlock = loadCalibrationRAWData(file, driver);
      calBlock.calculateCalibrationTemperature();
      VNACalibrationContext calContext = calBlock.getMathHelper().createCalibrationContextForCalibrationPoints(calBlock, calKit);
      calBlock.getMathHelper().createCalibrationPoints(calContext, calBlock);
      TraceHelper.exit(instance, "load");
      return calBlock;
   }

   private static void readHeader(ObjectInputStream decoder, VNACalibrationBlock rc) throws IOException, ClassNotFoundException {
      TraceHelper.entry(instance, "readHeader");
      String at = (String)decoder.readObject();
      if (!"__V5".equals(at) && !"__V4".equals(at)) {
         if ("__V3".equals(at)) {
            TraceHelper.text(instance, "readHeader", "New record type [" + at + "] detected");
            rc.setAnalyserType((String)decoder.readObject());
            rc.setComment("");
            rc.setStartFrequency((Long)decoder.readObject());
            rc.setStopFrequency((Long)decoder.readObject());
            rc.setNumberOfSteps((Integer)decoder.readObject());
            rc.setNumberOfOverscans((Integer)decoder.readObject());
            rc.setScanMode((VNAScanMode)decoder.readObject());
         } else if ("__V2".equals(at)) {
            TraceHelper.text(instance, "readHeader", "New record type [" + at + "] detected");
            rc.setAnalyserType((String)decoder.readObject());
            rc.setComment("");
            rc.setStartFrequency((long)(Integer)decoder.readObject());
            rc.setStopFrequency((long)(Integer)decoder.readObject());
            rc.setNumberOfSteps((Integer)decoder.readObject());
            rc.setNumberOfOverscans((Integer)decoder.readObject());
            rc.setScanMode((VNAScanMode)decoder.readObject());
         } else {
            TraceHelper.text(instance, "readHeader", "Old record type [" + at + "] detected");
            rc.setAnalyserType(at);
            rc.setComment("");
            rc.setStartFrequency((long)(Integer)decoder.readObject());
            rc.setStopFrequency((long)(Integer)decoder.readObject());
            rc.setNumberOfSteps((Integer)decoder.readObject());
            rc.setNumberOfOverscans(1);
            Boolean b = (Boolean)decoder.readObject();
            rc.setScanMode(Boolean.TRUE.equals(b) ? VNAScanMode.MODE_TRANSMISSION : VNAScanMode.MODE_REFLECTION);
         }
      } else {
         TraceHelper.text(instance, "readHeader", "New record type [" + at + "] detected");
         rc.setAnalyserType((String)decoder.readObject());
         rc.setComment((String)decoder.readObject());
         rc.setStartFrequency((Long)decoder.readObject());
         rc.setStopFrequency((Long)decoder.readObject());
         rc.setNumberOfSteps((Integer)decoder.readObject());
         rc.setNumberOfOverscans((Integer)decoder.readObject());
         rc.setScanMode((VNAScanMode)decoder.readObject());
      }

      TraceHelper.exit(instance, "readHeader");
   }

 public static VNACalibrationBlock loadHeader(File file) throws ProcessingException {
    TraceHelper.entry(instance, "loadHeader");
    VNACalibrationBlock rc = null;
    String myFileName = file.getAbsolutePath();
    TraceHelper.text(instance, "loadHeader", "Trying to read header from [" + myFileName + "]");

    try (FileInputStream fis = new FileInputStream(myFileName);
         ObjectInputStream decoder = new ObjectInputStream(fis)) {

        rc = new VNACalibrationBlock();
        readHeader(decoder, rc);
        rc.setFile(file);

    } catch (ClassNotFoundException | IOException e) {
        ErrorLogHelper.exception(instance, "loadHeader", e);
        throw new ProcessingException(e);
    }

    TraceHelper.exit(instance, "loadHeader");
    return rc;
}


   public static VNACalibrationBlock createResizedCalibrationBlock(VNACalibrationBlock pMainCalibrationBlock, long pStartFreq, long pStopFreq, int targetSteps) {
      String methodName = "createResizedCalibrationBlock";
      TraceHelper.entry(instance, "createResizedCalibrationBlock");
      TraceHelper.text(instance, "createResizedCalibrationBlock", "start=%10d", pStartFreq);
      TraceHelper.text(instance, "createResizedCalibrationBlock", "stop =%10d", pStopFreq);
      TraceHelper.text(instance, "createResizedCalibrationBlock", "steps=%d", targetSteps);
      VNACalibrationBlock rc = new VNACalibrationBlock();
      if (pStartFreq < pMainCalibrationBlock.getStartFrequency()) {
         ErrorLogHelper.text(instance, "createResizedCalibrationBlock", "frequency [%d] too low for calibration source [%d]", pStartFreq, pMainCalibrationBlock.getStartFrequency());
         return rc;
      } else if (pStopFreq > pMainCalibrationBlock.getStopFrequency()) {
         ErrorLogHelper.text(instance, "createResizedCalibrationBlock", "frequency [%d] too low for calibration source [%d]", pStopFreq, pMainCalibrationBlock.getStopFrequency());
         return rc;
      } else {
         rc.setStartFrequency(pStartFreq);
         rc.setStopFrequency(pStopFreq);
         rc.setNumberOfSteps(targetSteps);
         rc.setAnalyserType(pMainCalibrationBlock.getAnalyserType());
         rc.setMathHelper(pMainCalibrationBlock.getMathHelper());
         rc.setScanMode(pMainCalibrationBlock.getScanMode());
         rc.setTemperature(pMainCalibrationBlock.getTemperature());
         VNACalibrationPoint[] source = pMainCalibrationBlock.getCalibrationPoints();
         VNACalibrationPoint[] target = new VNACalibrationPoint[targetSteps];
         long freqStep = (pStopFreq - pStartFreq) / (long)targetSteps;
         TraceHelper.text(instance, "createResizedCalibrationBlock", "freq step=%d", freqStep);
         long targetFreq = pStartFreq;
         int sourceIndex = 0;
         int sourceSteps = source.length;

         for(int targetIndex = 0; targetIndex < targetSteps; ++targetIndex) {
            while(sourceIndex < sourceSteps && source[sourceIndex].getFrequency() < targetFreq) {
               ++sourceIndex;
            }

            if (sourceIndex >= sourceSteps) {
               sourceIndex = sourceSteps - 1;
            }

            if (source[sourceIndex].getFrequency() == targetFreq) {
               target[targetIndex] = source[sourceIndex];
            } else {
               VNACalibrationPoint p1 = source[sourceIndex - 1];
               VNACalibrationPoint p2 = source[sourceIndex];
               target[targetIndex] = VNACalibrationPointHelper.interpolate(p1, p2, targetFreq);
            }

            targetFreq += freqStep;
         }

         rc.setCalibrationPoints(target);
         TraceHelper.exit(instance, "createResizedCalibrationBlock");
         return rc;
      }
   }
}
