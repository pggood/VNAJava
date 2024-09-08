package krause.vna.data.reference;

import java.io.File;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;

public class VNAReferenceDataBlock {
   private VNACalibratedSample[] samples;
   private VNACalibratedSample[] resizedSamples;
   private long minFrequency = Long.MAX_VALUE;
   private long maxFrequency = Long.MIN_VALUE;
   private File file = null;
   private String comment = null;

   public VNAReferenceDataBlock(VNACalibratedSampleBlock refData) {
      TraceHelper.entry(this, "VNAReferenceSampleBlock");
      if (refData != null) {
         this.samples = refData.getCalibratedSamples();
         int len = this.samples.length;
         if (len > 0) {
            this.setMinFrequency(this.samples[0].getFrequency());
            this.setMaxFrequency(this.samples[len - 1].getFrequency());
         }

         this.setComment(refData.getComment());
      }

      TraceHelper.exit(this, "VNAReferenceSampleBlock");
   }

   public VNACalibratedSample[] getSamples() {
      return this.samples;
   }

   public void setSamples(VNACalibratedSample[] samples) {
      this.samples = samples;
   }

   public String toString() {
      return "VNAReferenceSampleBlock [maxFrequency=" + this.maxFrequency + ", minFrequency=" + this.minFrequency;
   }

   public void setFile(File file) {
      this.file = file;
   }

   public File getFile() {
      return this.file;
   }

   public void prepare(VNACalibratedSample[] scanSamples, long startFreq, long stopFreq) {
      TraceHelper.entry(this, "prepare");
      int numScanSamples = scanSamples.length;
      int numRefSamples = this.samples.length;
      if (this.resizedSamples == null) {
         this.resizedSamples = new VNACalibratedSample[numScanSamples];
      } else if (this.resizedSamples.length != numScanSamples) {
         this.resizedSamples = new VNACalibratedSample[numScanSamples];
      }

      if (scanSamples[0].getFrequency() <= this.samples[numRefSamples - 1].getFrequency()) {
         if (scanSamples[numScanSamples - 1].getFrequency() >= this.samples[0].getFrequency()) {
            int refIndex = 0;

            for(int x = 0; x < numScanSamples; ++x) {
               VNACalibratedSample scanSample = scanSamples[x];
               if (refIndex >= numRefSamples) {
                  this.resizedSamples[x] = null;
               } else {
                  VNACalibratedSample refSample;
                  for(refSample = this.samples[refIndex]; refSample.getFrequency() < scanSample.getFrequency(); refSample = this.samples[refIndex]) {
                     ++refIndex;
                     if (refIndex >= numRefSamples) {
                        break;
                     }
                  }

                  if (refIndex < numRefSamples) {
                     this.resizedSamples[x] = refSample;
                  } else {
                     this.resizedSamples[x] = null;
                  }
               }
            }

            TraceHelper.exit(this, "prepare");
         }
      }
   }

   public VNACalibratedSample[] getResizedSamples() {
      return this.resizedSamples;
   }

   public long getMinFrequency() {
      return this.minFrequency;
   }

   public void setMinFrequency(long minFrequency) {
      this.minFrequency = minFrequency;
   }

   public long getMaxFrequency() {
      return this.maxFrequency;
   }

   public void setMaxFrequency(long maxFrequency) {
      this.maxFrequency = maxFrequency;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public String getComment() {
      return this.comment;
   }
}
