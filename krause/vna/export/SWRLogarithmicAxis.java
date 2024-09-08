package krause.vna.export;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

public class SWRLogarithmicAxis extends LogarithmicAxis {
   public SWRLogarithmicAxis(String string) {
      super(string);
   }

   protected List<NumberTick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
      List<NumberTick> ticks = new ArrayList();
      double lowerBoundVal = this.getRange().getLowerBound();
      if (this.smallLogFlag && lowerBoundVal < 1.0E-100D) {
         lowerBoundVal = 1.0E-100D;
      }

      double upperBoundVal = this.getRange().getUpperBound();
      int iBegCount = (int)Math.rint(this.switchedLog10(lowerBoundVal));
      int iEndCount = (int)Math.ceil(this.switchedLog10(upperBoundVal));
      if (iBegCount == iEndCount && iBegCount > 0 && Math.pow(10.0D, (double)iBegCount) > lowerBoundVal) {
         --iBegCount;
      }

      boolean zeroTickFlag = false;

      for(int i = iBegCount; i <= iEndCount; ++i) {
         int jEndCount = 10;
         if (i == iEndCount) {
            jEndCount = 1;
         }

         for(int j = 0; j < jEndCount; ++j) {
            double tickVal;
            String tickLabel;
            NumberFormat format;
            if (this.smallLogFlag) {
               tickVal = Math.pow(10.0D, (double)i) + Math.pow(10.0D, (double)i) * (double)j;
               if (j == 0) {
                  if (this.log10TickLabelsFlag) {
                     tickLabel = "10^" + i;
                  } else if (this.expTickLabelsFlag) {
                     tickLabel = "1e" + i;
                  } else if (i >= 0) {
                     format = this.getNumberFormatOverride();
                     if (format != null) {
                        tickLabel = format.format(tickVal);
                     } else {
                        tickLabel = Long.toString((long)Math.rint(tickVal));
                     }
                  } else {
                     this.numberFormatterObj.setMaximumFractionDigits(-i);
                     tickLabel = this.numberFormatterObj.format(tickVal);
                  }
               } else {
                  format = this.getNumberFormatOverride();
                  if (format != null) {
                     tickLabel = format.format(tickVal);
                  } else {
                     tickLabel = Long.toString((long)Math.rint(tickVal));
                  }
               }
            } else {
               if (zeroTickFlag) {
                  --j;
               }

               tickVal = i >= 0 ? Math.pow(10.0D, (double)i) + Math.pow(10.0D, (double)i) * (double)j : -(Math.pow(10.0D, (double)(-i)) - Math.pow(10.0D, (double)(-i - 1)) * (double)j);
               if (j == 0) {
                  if (!zeroTickFlag) {
                     if (i > iBegCount && i < iEndCount && Math.abs(tickVal - 1.0D) < 1.0E-4D) {
                        tickVal = 0.0D;
                        zeroTickFlag = true;
                        tickLabel = "0";
                     } else if (this.log10TickLabelsFlag) {
                        tickLabel = (i < 0 ? "-" : "") + "10^" + Math.abs(i);
                     } else if (this.expTickLabelsFlag) {
                        tickLabel = (i < 0 ? "-" : "") + "1e" + Math.abs(i);
                     } else {
                        format = this.getNumberFormatOverride();
                        if (format != null) {
                           tickLabel = format.format(tickVal);
                        } else {
                           tickLabel = Long.toString((long)Math.rint(tickVal));
                        }
                     }
                  } else {
                     tickLabel = "";
                     zeroTickFlag = false;
                  }
               } else {
                  tickLabel = "";
                  zeroTickFlag = false;
               }
            }

            if (tickVal > upperBoundVal) {
               return ticks;
            }

            if (tickVal >= lowerBoundVal - 1.0E-100D) {
               format = null;
               TextAnchor rotationAnchor = null;
               double angle = 0.0D;
               TextAnchor anchor;
               if (this.isVerticalTickLabels()) {
                  if (edge == RectangleEdge.LEFT) {
                     anchor = TextAnchor.BOTTOM_CENTER;
                     rotationAnchor = TextAnchor.BOTTOM_CENTER;
                     angle = -1.5707963267948966D;
                  } else {
                     anchor = TextAnchor.BOTTOM_CENTER;
                     rotationAnchor = TextAnchor.BOTTOM_CENTER;
                     angle = 1.5707963267948966D;
                  }
               } else if (edge == RectangleEdge.LEFT) {
                  anchor = TextAnchor.CENTER_RIGHT;
                  rotationAnchor = TextAnchor.CENTER_RIGHT;
               } else {
                  anchor = TextAnchor.CENTER_LEFT;
                  rotationAnchor = TextAnchor.CENTER_LEFT;
               }

               ticks.add(new NumberTick(tickVal, tickLabel, anchor, rotationAnchor, angle));
            }
         }
      }

      return ticks;
   }
}
