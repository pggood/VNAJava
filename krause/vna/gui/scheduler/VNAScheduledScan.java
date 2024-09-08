package krause.vna.gui.scheduler;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.gui.VNAMainFrame;

public class VNAScheduledScan extends Task {
   private VNAMainFrame mainFrame;
   private VNAConfig config = VNAConfig.getSingleton();
   private VNADataPool datapool = VNADataPool.getSingleton();
   private VNASchedulerDialog dialog;

   public VNAScheduledScan(VNAMainFrame pMainFrame, VNASchedulerDialog pDialog) {
      this.mainFrame = pMainFrame;
      this.dialog = pDialog;
   }

   public void execute(TaskExecutionContext arg0) throws RuntimeException {
      TraceHelper.entry(this, "execute");
      if (this.datapool.getResizedCalibrationBlock() == null && this.datapool.getMainCalibrationBlock() != null) {
         VNACalibrationBlock newBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(this.datapool.getMainCalibrationBlock(), this.datapool.getFrequencyRange().getStart(), this.datapool.getFrequencyRange().getStop(), this.config.getNumberOfSamples());
         TraceHelper.text(this, "execute", "Created new resized calibration block id=" + newBlock.hashCode());
         TraceHelper.text(this, "execute", " start  =" + newBlock.getStartFrequency());
         TraceHelper.text(this, "execute", " end    =" + newBlock.getStopFrequency());
         TraceHelper.text(this, "execute", " samples=" + newBlock.getNumberOfSteps());
         this.datapool.setResizedCalibrationBlock(newBlock);
      }

      this.mainFrame.getApplicationState().evtMeasureStarted();
      VNABackgroundJob job = new VNABackgroundJob();
      job.setNumberOfSamples(this.config.getNumberOfSamples());
      job.setFrequencyRange(this.datapool.getFrequencyRange());
      job.setScanMode(this.datapool.getScanMode());
      job.setSpeedup(1);
      VnaBackgroundTask task = new VnaBackgroundTask(this.datapool.getDriver());
      task.addJob(job);
      task.setStatusLabel(this.mainFrame.getStatusBarStatus());
      task.addDataConsumer(this.dialog);
      task.execute();
      TraceHelper.exit(this, "execute");
   }
}
