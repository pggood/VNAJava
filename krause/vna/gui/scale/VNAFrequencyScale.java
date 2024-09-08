package krause.vna.gui.scale;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.NumberFormat;
import javax.swing.JPanel;
import javax.swing.UIManager;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.format.VNAFormatFactory;

public class VNAFrequencyScale extends VNADiagramScale {
   private transient VNADataPool datapool = VNADataPool.getSingleton();
   private final Color fontColor = UIManager.getColor("Panel.foreground");
   private final Font myFont = new Font("Dialog", 0, 10);
   private static final long[][] SCALER = new long[][]{{5000000000L, 1000000000L}, {2500000000L, 500000000L}, {1000000000L, 200000000L}, {500000000L, 100000000L}, {250000000L, 50000000L}, {100000000L, 20000000L}, {50000000L, 10000000L}, {25000000L, 5000000L}, {10000000L, 2500000L}, {5000000L, 1000000L}, {2500000L, 500000L}, {1000000L, 250000L}, {500000L, 100000L}, {250000L, 50000L}, {100000L, 25000L}, {50000L, 10000L}, {25000L, 5000L}, {10000L, 2500L}, {5000L, 1000L}, {2500L, 500L}, {1000L, 250L}};
   private JPanel leftPanelObject;
   private JPanel rightPanelObject;

   public VNAFrequencyScale(JPanel leftObject, JPanel rightObject) {
      this.leftPanelObject = leftObject;
      this.rightPanelObject = rightObject;
   }

   public void paint(Graphics g) {
      super.paint(g);
      int realWidth = this.getParent().getWidth();
      int xOffset = 0;
      if (this.leftPanelObject != null) {
         realWidth -= this.leftPanelObject.getWidth();
         xOffset = this.leftPanelObject.getWidth();
      }

      if (this.rightPanelObject != null) {
         realWidth -= this.rightPanelObject.getWidth();
      }

      VNACalibratedSampleBlock lastBlock = this.datapool.getCalibratedData();
      if (lastBlock != null) {
         VNACalibratedSample[] lastVnaData = lastBlock.getCalibratedSamples();
         if (lastVnaData.length > 0) {
            long min = lastVnaData[0].getFrequency();
            long max = lastVnaData[lastVnaData.length - 1].getFrequency();
            long diff = max - min;
            if (diff != 0L) {
               if (diff < 0L) {
                  diff = -diff;
               }

               long ticker = 0L;

               int scalerIdx;
               for(scalerIdx = 0; diff < SCALER[scalerIdx][0]; ++scalerIdx) {
               }

               ticker = SCALER[scalerIdx][1];
               int ticks = (int)Math.round((double)diff * 1.0D / (double)ticker);
               long lowFrq = min / ticker * ticker;
               if (lowFrq < min) {
                  lowFrq += ticker;
               }

               int divisor = 1000;
               String unit = "kHz";
               if (scalerIdx < 1) {
                  divisor = 1000000;
                  unit = "MHz";
               }

               g.setColor(this.fontColor);
               g.setFont(this.myFont);
               g.setClip(0, 0, this.getParent().getWidth(), this.getParent().getHeight());
               g.drawString(unit, this.getWidth() - 30, 10);
               g.setClip(xOffset, 0, realWidth, this.getHeight());
               float part = (float)diff / ((float)realWidth * 1.0F);
               NumberFormat ff = VNAFormatFactory.getFrequencyFormat();

               for(scalerIdx = 0; scalerIdx < ticks; ++scalerIdx) {
                  int x = xOffset + (int)((float)(lowFrq - min) / part);
                  g.drawLine(x, 0, x, x + this.getParent().getHeight());
                  g.drawString(ff.format(lowFrq / (long)divisor), x + 3, 10);
                  lowFrq += ticker;
               }
            }
         }
      }

   }
}
