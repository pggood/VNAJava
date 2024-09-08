package krause.vna.gui.scale;

import java.awt.Graphics;
import java.text.NumberFormat;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.resources.VNAMessages;

public class VNANoneScale extends VNAGenericScale {
   public VNANoneScale() {
      super(VNAMessages.getString("Scale.None"), VNAMessages.getString("Scale.None.Description"), VNAScaleSymbols.SCALE_TYPE.SCALE_NONE, (String)null, NumberFormat.getNumberInstance(), 0.0D, 0.0D);
   }

   public int getScaledSampleValue(VNACalibratedSample sample, int height) {
      return -100;
   }

   public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
   }

   public void paintScale(int width, int height, Graphics g) {
   }

   public int getScaledSampleValue(double value, int height) {
      return 50;
   }
}
