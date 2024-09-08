package krause.vna.device.serial.pro.lf;

import krause.util.ras.logging.TraceHelper;
import krause.vna.device.serial.pro.VNADriverSerialPro;
import krause.vna.device.serial.pro.VNADriverSerialProMathHelper;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSerialProLf extends VNADriverSerialPro {
   public VNADriverSerialProLf() {
      TraceHelper.entry(this, "VNADriverSerialProLf");
      this.setMathHelper(new VNADriverSerialProMathHelper(this));
      this.setDeviceInfoBlock(new VNADriverSerialProLfDIB());
      this.getDeviceInfoBlock().restore(this.config, this.getDriverConfigPrefix());
      TraceHelper.exit(this, "VNADriverSerialProLf");
   }

   public String getDriverConfigPrefix() {
      return "VNADriver.Serial.Pro.Lf.";
   }

   public VNACalibrationRange[] getSpecificCalibrationRanges() {
      return new VNACalibrationRange[]{new VNACalibrationRange(this.getDeviceInfoBlock().getMinFrequency(), this.getDeviceInfoBlock().getMaxFrequency(), 30000, 1)};
   }
}
