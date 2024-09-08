package krause.vna.gui.cable;

import java.util.ArrayList;
import java.util.List;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;

public class VNACableMeasurementHelper {
   private boolean metricMode;
   private boolean scale360;

   public VNACableMeasurementHelper(boolean scale, boolean meter) {
      TraceHelper.exit(this, "VNACableMeasurementHelper");
      this.scale360 = scale;
      this.metricMode = meter;
      TraceHelper.exit(this, "VNACableMeasurementHelper");
   }

   public VNACableMeasurementPoint calculateLength(List<VNACalibratedSample> points, double pVelocity) {
      TraceHelper.entry(this, "calculateLength");
      VNACableMeasurementPoint rc = null;
      if (points != null && points.size() == 2) {
         VNACableMeasurementPoint p1 = new VNACableMeasurementPoint(this.metricMode, this.scale360);
         p1.setStart((VNACalibratedSample)points.get(0));
         p1.setStop((VNACalibratedSample)points.get(1));
         p1.calculateLength(pVelocity);
         rc = p1;
      }

      TraceHelper.exitWithRC(this, "calculateLength", rc);
      return rc;
   }

   public VNACableMeasurementPoint calculateVelocityFactor(List<VNACalibratedSample> points, double pCableLength) {
      String methodName = "calculateVelocityFactor";
      TraceHelper.entry(this, "calculateVelocityFactor");
      VNACableMeasurementPoint rc = null;
      if (points != null && points.size() == 2) {
         VNACableMeasurementPoint p1 = new VNACableMeasurementPoint(this.metricMode, this.scale360);
         p1.setStart((VNACalibratedSample)points.get(0));
         p1.setStop((VNACalibratedSample)points.get(1));
         p1.calculateVelocityFactor(pCableLength);
         rc = p1;
      }

      TraceHelper.exitWithRC(this, "calculateVelocityFactor", rc);
      return rc;
   }

   public List<VNACalibratedSample> findAllCrossingPoints(VNACalibratedSampleBlock samples) {
      return this.scale360 ? this.findAllCrossingPointsWithSign(samples) : this.findAllCrossingPointsWithoutSign(samples);
   }

   private List<VNACalibratedSample> findAllCrossingPointsWithoutSign(VNACalibratedSampleBlock samples) {
      TraceHelper.entry(this, "findAllCrossingPointsWithoutSign");
      List<VNACalibratedSample> rc = new ArrayList();
      VNACalibratedSample[] calibratedSample = samples.getCalibratedSamples();
      double lastPhase = calibratedSample[10].getReflectionPhase();
      int state = 0;
      int i = 10;

      for(int max = calibratedSample.length; i < max && state != 99; ++i) {
         VNACalibratedSample currentSample = calibratedSample[i];
         double phase = currentSample.getReflectionPhase();
         switch(state) {
         case 0:
            if (phase >= 90.0D && lastPhase < 90.0D) {
               rc.add(currentSample);
               state = 1;
            } else if (phase < 90.0D && lastPhase >= 90.0D) {
               rc.add(currentSample);
               state = 0;
            }
            break;
         case 1:
            if (phase < 90.0D && lastPhase >= 90.0D) {
               rc.add(currentSample);
               state = 0;
            }
         }

         lastPhase = phase;
      }

      TraceHelper.exitWithRC(this, "findAllCrossingPointsWithoutSign", "#=" + rc.size());
      return rc;
   }

   public List<VNACalibratedSample> findAllCrossingPointsWithSign(VNACalibratedSampleBlock samples) {
      String methodName = "findAllCrossingPointsWithSign";
      TraceHelper.entry(this, "findAllCrossingPointsWithSign");
      List<VNACalibratedSample> rc = new ArrayList();
      VNACalibratedSample[] calibratedSample = samples.getCalibratedSamples();
      double lastPhase = calibratedSample[10].getReflectionPhase();
      int state = 0;
      int i = 10;

      for(int max = calibratedSample.length; i < max && state != 99; ++i) {
         VNACalibratedSample currentSample = calibratedSample[i];
         double phase = currentSample.getReflectionPhase();
         if (phase > 0.0D && lastPhase < 0.0D) {
            rc.add(currentSample);
            TraceHelper.text(this, "findAllCrossingPointsWithSign", "state=" + state + " a f=" + currentSample.getFrequency() + " °=" + phase + " last°=" + lastPhase);
            state = 1;
         } else if (phase < 0.0D && lastPhase >= 0.0D) {
            TraceHelper.text(this, "findAllCrossingPointsWithSign", "state=" + state + " b f=" + currentSample.getFrequency() + " °=" + phase + " last°=" + lastPhase);
            rc.add(currentSample);
            state = 0;
         }

         lastPhase = phase;
      }

      TraceHelper.exitWithRC(this, "findAllCrossingPointsWithSign", "#=" + rc.size());
      return rc;
   }

   public List<VNACalibratedSample> findTwoCrossingPoints(VNACalibratedSampleBlock samples) {
      return this.scale360 ? this.findTwoCrossingPointsWithSign(samples) : this.findTwoCrossingPointsWithoutSign(samples);
   }

   public List<VNACalibratedSample> findTwoCrossingPointsWithoutSign(VNACalibratedSampleBlock samples) {
      TraceHelper.entry(this, "findTwoCrossingPointsWithoutSign");
      List<VNACalibratedSample> rc = new ArrayList();
      VNACalibratedSample firstPoint = null;
      VNACalibratedSample secondPoint = null;
      int startIndex = 1;
      VNACalibratedSample sample1 = samples.getCalibratedSamples()[10];
      VNACalibratedSample sample2 = samples.getCalibratedSamples()[11];
      boolean fromLow = sample1.getReflectionPhase() < sample2.getReflectionPhase();
      int state = 0;
      int i = 10;

      for(int max = samples.getCalibratedSamples().length; i < max && state != 99; ++i) {
         VNACalibratedSample currentSample = samples.getCalibratedSamples()[i];
         double phase = currentSample.getReflectionPhase();
         switch(state) {
         case 0:
            if (fromLow) {
               if (phase > 90.0D) {
                  rc.add(currentSample);
                  state = 1;
                  i += 10;
               }
            } else if (phase < 90.0D) {
               rc.add(currentSample);
               state = 1;
               i += 10;
            }
            break;
         case 1:
            if (fromLow) {
               if (phase < 90.0D) {
                  state = 2;
                  i += 10;
               }
            } else if (phase > 90.0D) {
               state = 2;
               i += 10;
            }
            break;
         case 2:
            if (fromLow) {
               if (phase > 90.0D) {
                  rc.add(currentSample);
                  state = 99;
               }
            } else if (phase < 90.0D) {
               rc.add(currentSample);
               state = 99;
            }
         }
      }

      TraceHelper.exit(this, "findTwoCrossingPointsWithoutSign");
      return rc;
   }

   public List<VNACalibratedSample> findTwoCrossingPointsWithSign(VNACalibratedSampleBlock samples) {
      List<VNACalibratedSample> rc = new ArrayList();
      VNACalibratedSample firstPoint = null;
      VNACalibratedSample secondPoint = null;
      VNACalibratedSample[] calibratedSample = samples.getCalibratedSamples();
      double lastPhase = calibratedSample[10].getReflectionPhase();
      int state = 0;
      int i = 10;

      for(int max = calibratedSample.length; i < max && state != 99; ++i) {
         VNACalibratedSample currentSample = calibratedSample[i];
         double phase = currentSample.getReflectionPhase();
         switch(state) {
         case 0:
            if (phase > 0.0D && lastPhase < 0.0D) {
               rc.add(currentSample);
               state = 1;
            }
            break;
         case 1:
            if (phase > 0.0D && lastPhase < 0.0D) {
               rc.add(currentSample);
               state = 99;
            }
         }

         lastPhase = phase;
      }

      return rc;
   }
}
