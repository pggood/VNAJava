package krause.vna.device.serial.max6_500;

import krause.util.ras.logging.TraceHelper;
import krause.vna.device.serial.max6.VNADriverSerialMax6;
import krause.vna.device.serial.max6.VNADriverSerialMax6MathHelper;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSerialMax6_500 extends VNADriverSerialMax6 {
   public VNADriverSerialMax6_500() {
      String methodName = "VNADriverSerialMax6_500";
      TraceHelper.entry(this, "VNADriverSerialMax6_500");
      this.setMathHelper(new VNADriverSerialMax6MathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialMax6_500_DIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialMax6_500");
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.MAX6_500.";
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), this.getDeviceInfoBlock().getMaxFrequency(), 30000, 1)};
   }
}
