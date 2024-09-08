package krause.vna.device.serial.std.lf;

import krause.vna.device.serial.std.VNADriverSerialStd;
import krause.vna.device.serial.std.VNADriverSerialStdMathHelper;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSerialStdLf extends VNADriverSerialStd {
   public VNADriverSerialStdLf() {
      this.setMathHelper(new VNADriverSerialStdMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialStdLfDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.Std.Lf.";
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), this.getDeviceInfoBlock().getMaxFrequency(), 30000, 1)};
   }
}
