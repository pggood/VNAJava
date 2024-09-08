package krause.vna.gui.scale.values;

import java.awt.Graphics;
import java.text.NumberFormat;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;

public abstract class VNALinearScale extends VNAGenericScale {
   public VNALinearScale(String scaleName, String scaleDescription, VNAScaleSymbols.SCALE_TYPE scaleType, String pUnit, NumberFormat pFormat, double absMinVal, double absMaxVal) {
      super(scaleName, scaleDescription, scaleType, pUnit, pFormat, absMinVal, absMaxVal);
   }

   public void paintScale(int width, int height, Graphics g) {
      g.setColor(this.getFontColor());
      g.setFont(this.getFont());
      --height;
      int nOfTicks = this.getNoOfTicks();
      int[] tickCoordinates = new int[nOfTicks + 1];
      this.setTickCoordinates(tickCoordinates);
      double scale = (double)height * 1.0D / (double)nOfTicks;
      double addi = this.getRange() / (double)nOfTicks;
      double val = this.getCurrentMaxValue();

      for(int i = 0; i <= nOfTicks; ++i) {
         int y = (int)(scale * (double)i);
         tickCoordinates[i] = y;
         g.drawLine(0, y, width, y);
         if (i == 0) {
            g.drawString(this.getFormat().format(val), 1, y + 10);
         } else {
            g.drawString(this.getFormat().format(val), 1, y - 2);
         }

         val -= addi;
      }

   }
}
