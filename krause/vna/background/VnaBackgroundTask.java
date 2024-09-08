package krause.vna.background;

import java.awt.Color;
import java.net.ConnectException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JLabel;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNABaseSampleHelper;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.message.ErrorMessage;
import krause.vna.message.GenericMessage;
import krause.vna.message.InfoMessage;
import krause.vna.resources.VNAMessages;
import org.jdesktop.swingworker.SwingWorker;

public class VnaBackgroundTask extends SwingWorker<List<VNABackgroundJob>, GenericMessage> implements IVNABackgroundTaskStatusListener {
   private final VNAConfig config = VNAConfig.getSingleton();
   private List<IVNADataConsumer> consumers = new ArrayList();
   private long currJob = 0L;
   private long currOver = 0L;
   private IVNADriver driver;
   private List<VNABackgroundJob> jobs = new ArrayList();
   private long maxJobs = 0L;
   private JLabel statusLabel = null;

   public VnaBackgroundTask(IVNADriver pDriver) {
      this.driver = pDriver;
   }

   public void addDataConsumer(IVNADataConsumer pConsumer) {
      TraceHelper.entry(this, "addConsumer");
      this.consumers.add(pConsumer);
      TraceHelper.exit(this, "addConsumer");
   }

   public void addJob(VNABackgroundJob job) {
      this.getJobs().add(job);
   }

   private static int createIntermediateSamples(int idx, VNABaseSample currentSample, VNABaseSample nextSample, VNABaseSample[] newSamples, int numberIntermediateSamples) {
      VNABaseSample deltaSample = VNABaseSampleHelper.createDeltaSample(currentSample, nextSample, numberIntermediateSamples);
      newSamples[idx] = currentSample;
      ++idx;

      for(int j = 1; j < numberIntermediateSamples; ++j) {
         VNABaseSample newSample = VNABaseSampleHelper.createNewSampleWithDelta(newSamples[idx - 1], deltaSample);
         newSamples[idx] = newSample;
         ++idx;
      }

      return idx;
   }

   public List<VNABackgroundJob> doInBackground() {
      String methodName = "doInBackground";
      TraceHelper.entry(this, "doInBackground");

      try {
         this.publish(new GenericMessage[]{new InfoMessage("Background.1")});
         this.publish(new GenericMessage[]{new InfoMessage("Background.2")});
         this.maxJobs = (long)this.jobs.size();
         this.currJob = 0L;
         Iterator var18 = this.jobs.iterator();

         while(var18.hasNext()) {
            VNABackgroundJob job = (VNABackgroundJob)var18.next();
            ++this.currJob;
            int numTargetSamples = job.getNumberOfSamples();
            int speedup = job.getSpeedup();
            int overscan = job.getOverScan();
            int numSamplesToScan = numTargetSamples / speedup;
            if (speedup > 1) {
               if (numTargetSamples % speedup > 0) {
                  numSamplesToScan += 2;
               } else {
                  ++numSamplesToScan;
               }
            }

            TraceHelper.text(this, "doInBackground", "Speedup=%d", speedup);
            TraceHelper.text(this, "doInBackground", "Overscan=%d", overscan);
            TraceHelper.text(this, "doInBackground", "numTargetSamples=%d", numTargetSamples);
            TraceHelper.text(this, "doInBackground", "numSamplesToScan=%d", numSamplesToScan);
            VNASampleBlock scannedSamples = null;
            if (overscan <= 1) {
               this.currOver = 0L;
               scannedSamples = this.driver.scan(job.getScanMode(), job.getFrequencyRange().getStart(), job.getFrequencyRange().getStop(), numSamplesToScan, this);
            } else {
               List<VNASampleBlock> blocks = new ArrayList();

               for(int i = 0; i < overscan; ++i) {
                  this.currOver = (long)i;
                  VNASampleBlock aBlock = this.driver.scan(job.getScanMode(), job.getFrequencyRange().getStart(), job.getFrequencyRange().getStop(), numSamplesToScan, this);
                  blocks.add(aBlock);
               }

               scannedSamples = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);
            }

            if (speedup > 1) {
               VNABaseSample[] targetSamples = new VNABaseSample[numTargetSamples];
               VNABaseSample[] sourceSamples = scannedSamples.getSamples();
               int idx = 0;
               int max = numSamplesToScan - 1;
               int numIntermediateSamples = speedup;

               for(int i = 0; i < max; ++i) {
                  VNABaseSample currentSample = sourceSamples[i];
                  VNABaseSample nextSample = sourceSamples[i + 1];
                  if (i + 1 >= max) {
                     numIntermediateSamples = numTargetSamples - numTargetSamples / speedup * speedup;
                     if (numIntermediateSamples == 0) {
                        numIntermediateSamples = speedup;
                     }
                  }

                  if (numIntermediateSamples > 0) {
                     idx = createIntermediateSamples(idx, currentSample, nextSample, targetSamples, numIntermediateSamples);
                  }
               }

               scannedSamples.setSamples(targetSamples);
               scannedSamples.setNumberOfSteps(numTargetSamples);
            }

            job.setResult(scannedSamples);
            if (this.config.isTurnOffGenAfterScan()) {
               this.driver.stopGenerator();
            }
         }
      } catch (ProcessingException var17) {
         ErrorLogHelper.exception(this, "doInBackground", var17);
         Throwable root = var17;
         if (var17.getCause() != null) {
            root = var17.getCause();
         }

         if (root instanceof ConnectException) {
            String msg = VNAMessages.getString("VnaBackgroundTask.ConnectException");
            this.publish(new GenericMessage[]{new ErrorMessage(MessageFormat.format(msg, var17.getMessage()))});
         } else {
            this.publish(new GenericMessage[]{new ErrorMessage(var17.getMessage())});
         }
      }

      TraceHelper.exit(this, "doInBackground");
      return this.getJobs();
   }

   protected void done() {
      String methodName = "done";
      TraceHelper.entry(this, "done");

      try {
         List<VNABackgroundJob> data = (List)this.get();
         Iterator var4 = this.consumers.iterator();

         while(var4.hasNext()) {
            IVNADataConsumer consumer = (IVNADataConsumer)var4.next();
            consumer.consumeDataBlock(data);
         }
      } catch (ExecutionException | InterruptedException var5) {
         ErrorLogHelper.exception(this, "done", var5);
      }

      TraceHelper.exit(this, "done");
   }

   public List<VNABackgroundJob> getJobs() {
      return this.jobs;
   }

   public JLabel getStatusLabel() {
      return this.statusLabel;
   }

   protected void process(List<GenericMessage> chunks) {
      if (this.statusLabel != null) {
         Iterator var3 = chunks.iterator();

         while(var3.hasNext()) {
            GenericMessage message = (GenericMessage)var3.next();
            this.statusLabel.setOpaque(true);
            if (message instanceof ErrorMessage) {
               this.statusLabel.setForeground(Color.RED);
               this.statusLabel.setBackground(Color.BLACK);
            } else {
               this.statusLabel.setForeground(Color.BLACK);
               this.statusLabel.setBackground(Color.GREEN);
            }

            this.statusLabel.setText(message.getMessage());
            TraceHelper.text(this, "process", message.getMessage());
         }
      }

   }

   public void publishProgress(int percentage) {
      this.publish(new GenericMessage[]{new InfoMessage("Background.3", this.currOver, this.currJob, this.maxJobs, (long)percentage)});
   }

   public void setStatusLabel(JLabel statusLabel) {
      this.statusLabel = statusLabel;
   }
}
