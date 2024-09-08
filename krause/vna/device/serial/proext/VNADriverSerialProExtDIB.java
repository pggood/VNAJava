package krause.vna.device.serial.proext;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;

public class VNADriverSerialProExtDIB extends VNADriverSerialProDIB {
   private final long MIN_FREQ = 10000000L;
   private final long MAX_FREQ = 1500000000L;
   private final double MIN_LOSS = 10.0D;
   private final double MAX_LOSS = -70.0D;
   private String scanCommandReflection;
   private String scanCommandTransmission;
   private int prescaler;

   public VNADriverSerialProExtDIB() {
      TraceHelper.entry(this, "VNADriverSerialProExtDIB");
      this.reset();
      this.clearScanModeParameters();
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_REFLECTION, true, true, true, false, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addScanModeParameter(new VNAScanModeParameter(VNAScanMode.MODE_TRANSMISSION, true, false, false, true, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.setShortName("miniVNA-pro-extender");
      this.setLongName("mini radio solutions - miniVNA pro extender");
      this.setType("3");
      TraceHelper.exit(this, "VNADriverSerialProExtDIB");
   }

   public int getPrescaler() {
      return this.prescaler;
   }

   public String getScanCommandReflection() {
      return this.scanCommandReflection;
   }

   public String getScanCommandTransmission() {
      return this.scanCommandTransmission;
   }

   public void reset() {
      super.reset();
      this.setMinFrequency(10000000L);
      this.setMaxFrequency(1500000000L);
      this.setMinLoss(10.0D);
      this.setMaxLoss(-70.0D);
      this.setMinPhase(-180.0D);
      this.setMaxPhase(180.0D);
      this.setNumberOfSamples4Calibration(10000);
      this.setPrescaler(10);
      this.setScanCommandTransmission("6");
      this.setScanCommandReflection("7");
      this.setAfterCommandDelay(100);
      this.setDdsTicksPerMHz(1000000L);
      this.setReferenceResistance(new Complex(50.0D, 0.0D));
   }

   public void setPrescaler(int prescaler) {
      this.prescaler = prescaler;
   }

   public void setScanCommandReflection(String scanCommandReflection) {
      this.scanCommandReflection = scanCommandReflection;
   }

   public void setScanCommandTransmission(String scanCommandTransmission) {
      this.scanCommandTransmission = scanCommandTransmission;
   }
}
