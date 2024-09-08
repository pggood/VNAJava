package krause.vna.device.serial.tiny.calibration;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNAScanRange;
import krause.vna.device.serial.tiny.VNADriverSerialTinyDIB;
import krause.vna.device.serial.tiny.VNADriverSerialTinyMessages;
import krause.vna.resources.VNAMessages;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class PhaseCalibrationHelper implements IVNADataConsumer {
   private final VNADataPool datapool = VNADataPool.getSingleton();
   private final VNADriverSerialTinyDIB dib;
   private final Component parent;
   private static final double STEP_GAIN = 0.03D;
   private static final double STEP_PHASE = 0.2D;

   public PhaseCalibrationHelper(Component pComp, VNADriverSerialTinyDIB pDib) {
      String methodName = "PhaseCalibrationHelper";
      TraceHelper.entry(this, "PhaseCalibrationHelper");
      this.dib = pDib;
      this.parent = pComp;
      TraceHelper.exit(this, "PhaseCalibrationHelper");
   }

   public void calculateFrequencies(VNABaseSample[] pSamples) {
      String methodName = "calculateFrequencies";
      TraceHelper.entry(this, "calculateFrequencies");
      int lastVal = pSamples[0].getP1();
      int lastDirection = 0;
      int newDirection = 0;
      int peakIndex = 1;
      int dipIndex = 1;

      for(int i = 1; i < pSamples.length; ++i) {
         int currVal = pSamples[i].getP1();
         if (lastVal > currVal) {
            newDirection = -1;
         } else if (lastVal < currVal) {
            newDirection = 1;
         } else {
            newDirection = 0;
         }

         lastVal = currVal;
         if (newDirection != 0) {
            if (lastDirection == 1 && newDirection == -1) {
               TraceHelper.text(this, "calculateFrequencies", "peak=" + i + " f=" + pSamples[i].getFrequency());
            } else if (lastDirection == -1 && newDirection == 1) {
               TraceHelper.text(this, "calculateFrequencies", "dip=" + i + " f=" + pSamples[i].getFrequency());
            }

            lastDirection = newDirection;
         }
      }

      TraceHelper.exit(this, "calculateFrequencies");
   }

   public void calculateBestFit(VNABaseSample[] pSamples) {
      String methodName = "calculateBestFit";
      TraceHelper.entry(this, "calculateBestFit");
      WeightedObservedPoints obsP1 = new WeightedObservedPoints();
      WeightedObservedPoints obsP2 = new WeightedObservedPoints();
      WeightedObservedPoints obsP3 = new WeightedObservedPoints();
      WeightedObservedPoints obsP4 = new WeightedObservedPoints();

      for(int x = 0; x < pSamples.length; ++x) {
         VNABaseSample aSample = pSamples[x];
         obsP1.add((double)x, (double)aSample.getP1());
         obsP2.add((double)x, (double)aSample.getP2());
         obsP3.add((double)x, (double)aSample.getP3());
         obsP4.add((double)x, (double)aSample.getP4());
      }

      double[] coeffP1 = PolynomialCurveFitter.create(3).fit(obsP1.toList());
      double[] coeffP2 = PolynomialCurveFitter.create(3).fit(obsP2.toList());
      double[] coeffP3 = PolynomialCurveFitter.create(3).fit(obsP3.toList());
      double[] coeffP4 = PolynomialCurveFitter.create(3).fit(obsP4.toList());

      for(int x = 0; x < pSamples.length; ++x) {
         long i = 1L;
         double p1 = coeffP1[0];
         double p2 = coeffP2[0];
         double p3 = coeffP3[0];
         double p4 = coeffP4[0];

         for(int coeffIndex = 1; coeffIndex < coeffP1.length; ++coeffIndex) {
            i *= (long)x;
            p1 += (double)i * coeffP1[coeffIndex];
            p2 += (double)i * coeffP2[coeffIndex];
            p3 += (double)i * coeffP3[coeffIndex];
            p4 += (double)i * coeffP4[coeffIndex];
         }

         VNABaseSample aSample = pSamples[x];
         aSample.setP1((int)p1);
         aSample.setP2((int)p2);
         aSample.setP3((int)p3);
         aSample.setP4((int)p4);
      }

      TraceHelper.exit(this, "calculateBestFit");
   }

   public void autoCalOnMagnitude(VNASampleBlock rawData) {
      TraceHelper.entry(this, "autoCalOnMagnitude");
      double minMagnitudeGain = Double.MAX_VALUE;
      double minMagnitudePhase = Double.MAX_VALUE;
      double minDiffMagnitude = Double.MAX_VALUE;

      for(double curPhase = -20.0D; curPhase < 20.0D; curPhase += 0.2D) {
         for(double curGain = 0.5D; curGain < 2.0D; curGain += 0.03D) {
            TraceHelper.text(this, "autoCalOnMagnitude", "p=" + curPhase + " g=" + curGain);
            this.dib.setGainCorrection(curGain);
            this.dib.setPhaseCorrection(curPhase);
            Tuple[] transformedData = this.transformData(rawData);
            double minMagnitude = Double.MAX_VALUE;
            double maxMagnitude = Double.MIN_VALUE;
            Tuple[] var20 = transformedData;
            int var19 = transformedData.length;

            for(int var18 = 0; var18 < var19; ++var18) {
               Tuple data = var20[var18];
               double mag = data.getVal();
               if (mag < minMagnitude) {
                  minMagnitude = mag;
               }

               if (mag > maxMagnitude) {
                  maxMagnitude = mag;
               }
            }

            double diffMagnitude = maxMagnitude - minMagnitude;
            if (diffMagnitude < minDiffMagnitude) {
               minDiffMagnitude = diffMagnitude;
               minMagnitudeGain = curGain;
               minMagnitudePhase = curPhase;
            }
         }
      }

      TraceHelper.text(this, "doAutoCal", "minMagnitude     =" + minDiffMagnitude);
      TraceHelper.text(this, "doAutoCal", "minMagnitudeGain =" + minMagnitudeGain);
      TraceHelper.text(this, "doAutoCal", "minMagnitudePhase=" + minMagnitudePhase);
      this.dib.setGainCorrection(minMagnitudeGain);
      this.dib.setPhaseCorrection(minMagnitudePhase);
      TraceHelper.exit(this, "autoCalOnMagnitude");
   }

   private Tuple[] transformData(VNASampleBlock rawScanData) {
      IVNADriverMathHelper mathHelper = this.datapool.getDriver().getMathHelper();
      VNACalibrationBlock mcb = this.datapool.getMainCalibrationBlock();
      VNACalibrationKit kit = this.datapool.getCalibrationKit();
      VNACalibrationContext ctxCalPoints = mathHelper.createCalibrationContextForCalibrationPoints(mcb, kit);
      mathHelper.createCalibrationPoints(ctxCalPoints, mcb);
      VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mcb, 100000000L, 200000000L, 800);
      VNACalibrationContext ctxCalSamples = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
      VNACalibratedSampleBlock calibratedData = mathHelper.createCalibratedSamples(ctxCalSamples, rawScanData);
      Tuple[] rc = new Tuple[calibratedData.getCalibratedSamples().length];

      for(int i = 0; i < rc.length; ++i) {
         VNACalibratedSample data = calibratedData.getCalibratedSamples()[i];
         Tuple at = new Tuple();
         at.setFrq(data.getFrequency());
         at.setVal(data.getMag());
         rc[i] = at;
      }

      return rc;
   }

   public void doCalibrate() {
      Object[] options = new Object[]{VNAMessages.getString("Button.Continue"), VNAMessages.getString("Button.Cancel")};
      int n = JOptionPane.showOptionDialog(this.parent, VNADriverSerialTinyMessages.getString("msgExit"), VNADriverSerialTinyMessages.getString("calTitle"), 0, 3, (Icon)null, options, options[0]);
      if (n == 0) {
         VNAScanRange range = new VNAScanRange(100000000L, 200000000L, 800);
         VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
         backgroundTask.addDataConsumer(this);
         backgroundTask.setStatusLabel((JLabel)null);

         for(int i = 0; i < 4; ++i) {
            VNABackgroundJob job = new VNABackgroundJob();
            job.setSpeedup(1);
            job.setNumberOfSamples(800);
            job.setFrequencyRange((VNAFrequencyRange)range);
            job.setScanMode(VNAScanMode.MODE_REFLECTION);
            backgroundTask.addJob(job);
         }

         backgroundTask.execute();
      }

   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      TraceHelper.entry(this, "consumeDataBlock");
      Object[] options = new Object[]{VNAMessages.getString("Button.Continue"), VNAMessages.getString("Button.Cancel")};
      int n = JOptionPane.showOptionDialog(this.parent, VNADriverSerialTinyMessages.getString("msgCalculate"), VNADriverSerialTinyMessages.getString("calTitle"), 0, 3, (Icon)null, options, options[0]);
      if (n == 0) {
         VNASampleBlock rawScanData = null;
         if (jobs.size() <= 1) {
            if (jobs.size() == 1) {
               rawScanData = ((VNABackgroundJob)jobs.get(0)).getResult();
            }
         } else {
            List<VNASampleBlock> blocks = new ArrayList();
            Iterator var7 = jobs.iterator();

            while(var7.hasNext()) {
               VNABackgroundJob job = (VNABackgroundJob)var7.next();
               blocks.add(job.getResult());
            }

            rawScanData = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);
         }

         this.autoCalOnMagnitude(rawScanData);
         JOptionPane.showMessageDialog(this.parent, VNADriverSerialTinyMessages.getString("msgFinish"), VNADriverSerialTinyMessages.getString("calTitle"), 1, (Icon)null);
      }

      TraceHelper.exit(this, "consumeDataBlock");
   }
}
