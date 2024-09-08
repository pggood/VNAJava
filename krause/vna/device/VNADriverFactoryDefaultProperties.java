package krause.vna.device;

import java.util.Properties;

public class VNADriverFactoryDefaultProperties extends Properties implements VNADriverFactorySymbols {
   public VNADriverFactoryDefaultProperties() {
      this.put("Drv.0", "krause.vna.device.sample.VNADriverSample");
      this.put("Drv.1", "krause.vna.device.serial.std.VNADriverSerialStd");
      this.put("Drv.51", "krause.vna.device.serial.std2.VNADriverSerialStd2");
      this.put("Drv.2", "krause.vna.device.serial.pro.VNADriverSerialPro");
      this.put("Drv.3", "krause.vna.device.serial.proext.VNADriverSerialProExt");
      this.put("Drv.4", "krause.vna.device.serial.max6.VNADriverSerialMax6");
      this.put("Drv.5", "krause.vna.device.serial.max6_500.VNADriverSerialMax6_500");
      this.put("Drv.10", "krause.vna.device.serial.std.lf.VNADriverSerialStdLf");
      this.put("Drv.12", "krause.vna.device.serial.pro.lf.VNADriverSerialProLf");
      this.put("Drv.20", "krause.vna.device.serial.tiny.VNADriverSerialTiny");
      this.put("Drv.30", "krause.vna.device.serial.metro.VNADriverSerialMetro");
      this.put("Drv.40", "krause.vna.device.serial.vnarduino.VNADriverSerialVNArduino");
      this.put("Drv.50", "krause.vna.device.serial.pro2.VNADriverSerialPro2");
   }
}
