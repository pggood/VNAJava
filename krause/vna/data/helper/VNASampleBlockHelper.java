package krause.vna.data.helper;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.gui.format.VNAFormatFactory;

public final class VNASampleBlockHelper {
   static final VNASampleBlockHelper instance = new VNASampleBlockHelper();

   private VNASampleBlockHelper() {
   }

   public static VNASampleBlock calculateAverageSampleBlock(List<VNASampleBlock> blocks) {
      String methodName = "calculateAverageSampleBlock";
      TraceHelper.entry(instance, "calculateAverageSampleBlock");
      VNASampleBlock rc = null;
      int numBlocks = blocks.size();
      TraceHelper.text(instance, "calculateAverageSampleBlock", "  blocks=%d", numBlocks);
      if (numBlocks > 1) {
         rc = new VNASampleBlock();
         VNASampleBlock firstBlock = (VNASampleBlock)blocks.get(0);
         int blockLen = firstBlock.getSamples().length;
         VNABaseSample[] samples = new VNABaseSample[blockLen];
         double deviceTemp = 0.0D;
         Iterator var10 = blocks.iterator();

         while(var10.hasNext()) {
            VNASampleBlock block = (VNASampleBlock)var10.next();
            if (block.getDeviceTemperature() != null) {
               deviceTemp += block.getDeviceTemperature();
            }
         }

         deviceTemp /= (double)numBlocks;

         for(int i = 0; i < blockLen; ++i) {
            VNABaseSample sumSample = new VNABaseSample();
            Iterator var12 = blocks.iterator();

            while(var12.hasNext()) {
               VNASampleBlock block = (VNASampleBlock)var12.next();
               VNABaseSample curSample = block.getSamples()[i];
               sumSample.setFrequency(curSample.getFrequency());
               sumSample.setAngle(sumSample.getAngle() + curSample.getAngle());
               sumSample.setLoss(sumSample.getLoss() + curSample.getLoss());
               sumSample.setP1(sumSample.getP1() + curSample.getP1());
               sumSample.setP2(sumSample.getP2() + curSample.getP2());
               sumSample.setP3(sumSample.getP3() + curSample.getP3());
               sumSample.setP4(sumSample.getP4() + curSample.getP4());
               sumSample.setP1Ref(sumSample.getP1Ref() + curSample.getP1Ref());
               sumSample.setP2Ref(sumSample.getP2Ref() + curSample.getP2Ref());
               sumSample.setP3Ref(sumSample.getP3Ref() + curSample.getP3Ref());
               sumSample.setP4Ref(sumSample.getP3Ref() + curSample.getP4Ref());
               sumSample.setRss1(sumSample.getRss1() + curSample.getRss1());
               sumSample.setRss2(sumSample.getRss2() + curSample.getRss2());
               sumSample.setRss3(sumSample.getRss3() + curSample.getRss3());
            }

            sumSample.setAngle(sumSample.getAngle() / (double)numBlocks);
            sumSample.setLoss(sumSample.getLoss() / (double)numBlocks);
            sumSample.setP1(sumSample.getP1() / numBlocks);
            sumSample.setP2(sumSample.getP2() / numBlocks);
            sumSample.setP3(sumSample.getP3() / numBlocks);
            sumSample.setP4(sumSample.getP4() / numBlocks);
            sumSample.setP1Ref(sumSample.getP1Ref() / numBlocks);
            sumSample.setP2Ref(sumSample.getP2Ref() / numBlocks);
            sumSample.setP3Ref(sumSample.getP3Ref() / numBlocks);
            sumSample.setP4Ref(sumSample.getP4Ref() / numBlocks);
            sumSample.setRss1(sumSample.getRss1() / numBlocks);
            sumSample.setRss2(sumSample.getRss2() / numBlocks);
            sumSample.setRss3(sumSample.getRss3() / numBlocks);
            samples[i] = sumSample;
         }

         rc.setAnalyserType(firstBlock.getAnalyserType());
         rc.setMathHelper(firstBlock.getMathHelper());
         rc.setNumberOfSteps(firstBlock.getNumberOfSteps());
         rc.setSamples(samples);
         rc.setStartFrequency(firstBlock.getStartFrequency());
         rc.setStopFrequency(firstBlock.getStopFrequency());
         rc.setScanMode(firstBlock.getScanMode());
         rc.setDeviceTemperature(deviceTemp);
         rc.setNumberOfOverscans(numBlocks);
      } else {
         rc = (VNASampleBlock)blocks.get(0);
      }

      TraceHelper.exit(instance, "calculateAverageSampleBlock");
      return rc;
   }

   public static void removeSwitchPoints(VNASampleBlock pBlock, long[] pSwitchPoints) {
      String methodName = "removeSwitchPoints";
      TraceHelper.entry(instance, "removeSwitchPoints", "SwitchPoints=%s", Arrays.toString(pSwitchPoints));
      VNABaseSample[] pSamples = pBlock.getSamples();
      long[] var8 = pSwitchPoints;
      int var7 = pSwitchPoints.length;

      for(int var6 = 0; var6 < var7; ++var6) {
         long currSwitchPointFreq = var8[var6];
         VNABaseSample lastSample = pSamples[0];
         if (LogManager.getSingleton().isTracingEnabled()) {
            TraceHelper.text(instance, "removeSwitchPoints", "processing switch frequency: " + currSwitchPointFreq);
         }

         for(int i = 2; i < pSamples.length - 1; ++i) {
            VNABaseSample currSample = pSamples[i];
            if (lastSample.getFrequency() <= currSwitchPointFreq && currSample.getFrequency() > currSwitchPointFreq) {
               if (LogManager.getSingleton().isTracingEnabled()) {
                  NumberFormat nf = VNAFormatFactory.getFrequencyFormat();
                  TraceHelper.text(instance, "removeSwitchPoints", "switch point between " + i + " (" + nf.format(currSample.getFrequency()) + ") and " + (i - 1) + " (" + nf.format(lastSample.getFrequency()) + ")");
               }

               currSample.copy(lastSample);
               break;
            }

            lastSample = currSample;
         }
      }

      TraceHelper.exit(instance, "removeSwitchPoints");
   }
}
