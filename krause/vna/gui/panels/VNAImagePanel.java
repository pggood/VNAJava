package krause.vna.gui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNABandMap;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.helper.VNACalibratedSampleHelper;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.data.reference.VNAReferenceDataHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.util.VNAFrequencyPair;

public class VNAImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, VNAApplicationStateObserver {
   private final VNAConfig config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();
   private static final int SAMPLE_HISTORY_DEPTH = 8;
   private static final int RED_MASK = 16711680;
   private static final int GREEN_MASK = 65280;
   private static final int BLUE_MASK = 255;
   private static final float BRIGHT_SCALE = 0.8F;
   private final Font textFont = new Font("Dialog", 0, 17);
   private final transient VNABandMap bandMap = new VNABandMap();
   private int lastMouseX = -1;
   private transient VNAMainFrame mainFrame;
   private int lastMouseY = -1;
   private Cursor lastCursor = null;
   private List<VNACalibratedSample[]> sampleHistory = new ArrayList();
   private Color[] historyColorsLeft = new Color[8];
   private Color[] historyColorsRight = new Color[8];

   public VNAImagePanel(VNAMainFrame pMainFrame) {
      this.setBorder(new BevelBorder(0, (Color)null, (Color)null, (Color)null, (Color)null));
      this.mainFrame = pMainFrame;
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      this.addMouseWheelListener(this);
      this.addComponentListener(this);
      TraceHelper.exit(this, "componentResized");
   }

   public void componentHidden(ComponentEvent arg0) {
   }

   public void componentMoved(ComponentEvent arg0) {
   }

   public void componentResized(ComponentEvent arg0) {
      Dimension dim = this.getSize();
      int numberOfSamples = (int)dim.getWidth();
      VNAConfig.getSingleton().setNumberOfSamples(numberOfSamples);
      this.datapool.clearResizedCalibrationBlock();
      this.datapool.clearCalibratedData();
      VNAMarkerPanel markerPanel = this.mainFrame.getMarkerPanel();
      VNAMarker[] markers = markerPanel.getMarkers();
      VNAMarker[] var9 = markers;
      int var8 = markers.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         VNAMarker marker = var9[var7];
         if (marker.isVisible() && marker.getDiagramX() > numberOfSamples) {
            marker.setVisible(false);
            marker.clearFields();
         }
      }

      markerPanel.repaint();
      this.mainFrame.getDiagramPanel().repaint();
   }

   public void componentShown(ComponentEvent arg0) {
   }

   public VNACalibratedSample getSampleAtMousePosition(int mouseX) {
      VNACalibratedSample rc = null;
      if (this.datapool.getCalibratedData() != null) {
         try {
            rc = this.datapool.getCalibratedData().getCalibratedSamples()[mouseX];
            rc.setDiagramX(mouseX);
         } catch (IndexOutOfBoundsException var4) {
         }
      }

      return rc;
   }

   public void mouseClicked(MouseEvent e) {
      String methodName = "mouseClicked";
      TraceHelper.entry(this, "mouseClicked");
      VNAMarker marker = this.mainFrame.getMarkerPanel().getMarkerForMouseEvent(e);
      if (marker != null) {
         marker.update(this.getSampleAtMousePosition(e.getX()));
         this.repaint();
         VNAScaleSelectPanel ssp = this.mainFrame.getDiagramPanel().getScaleSelectPanel();
         if (ssp.getSmithDialog() != null) {
            ssp.getSmithDialog().consumeCalibratedData(this.datapool.getCalibratedData());
         }
      } else if (e.getButton() == 3 && (e.getModifiersEx() & 704) == 0) {
         TraceHelper.text(this, "mouseClicked", "right-button clicked");
         this.mainFrame.getMenuAndToolbarHandler().doExportJPGClipboard();
      }

      TraceHelper.exit(this, "mouseClicked");
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
      this.lastCursor = this.getCursor();
      this.setCursor(Cursor.getPredefinedCursor(1));
   }

   public void mouseExited(MouseEvent e) {
      this.lastMouseX = this.lastMouseY = -1;
      if (this.lastCursor != null) {
         this.setCursor(this.lastCursor);
      }

      this.mainFrame.getMarkerPanel().getMouseMarker().update((VNACalibratedSample)null);
   }

   public void mouseMoved(MouseEvent e) {
      this.lastMouseX = e.getX();
      this.lastMouseY = e.getY();
      if (this.datapool.getCalibratedData() != null) {
         this.mainFrame.getMarkerPanel().getMouseMarker().update(this.getSampleAtMousePosition(e.getX()));
      }

   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      this.mainFrame.getMarkerPanel().consumeMouseWheelEvent(e);
   }

   public void paint(Graphics g) {
      String methodName = "paint";
      TraceHelper.entry(this, "paint");
      super.paint(g);
      int width = this.getWidth();
      int height = this.getHeight();
      g.setColor(this.config.getColorDiagram());
      g.fillRect(0, 0, width, height);
      VNACalibratedSampleBlock calData = this.datapool.getCalibratedData();
      if (calData != null) {
         Color colLeft = this.config.getColorScaleLeft();
         Color colRight = this.config.getColorScaleRight();
         Color colDiagramLine = this.config.getColorDiagramLines();
         VNAMarker[] markers = this.mainFrame.getMarkerPanel().getMarkers();
         VNADiagramPanel diagPanel = this.mainFrame.getDiagramPanel();
         VNACalibratedSample[] samples = calData.getCalibratedSamples();
         int numSamples = samples.length;
         if (numSamples > 1) {
            TraceHelper.text(this, "paint", "#samples %d", numSamples);
            TraceHelper.text(this, "paint", "first    %d", samples[0].getFrequency());
            TraceHelper.text(this, "paint", "last     %d", samples[numSamples - 1].getFrequency());
            VNAGenericScale scaleLeft = diagPanel.getScaleLeft().getScale();
            VNAGenericScale scaleRight = diagPanel.getScaleRight().getScale();
            VNACalibratedSample sample = samples[0];
            int lastY1 = scaleLeft.getScaledSampleValue(sample, height);
            int lastY2 = scaleRight.getScaledSampleValue(sample, height);
            int lastX = 0;
            this.drawBandMap(g, samples);
            VNAReferenceDataBlock refBlock = this.datapool.getReferenceData();
            if (refBlock != null) {
               long startFreq = samples[0].getFrequency();
               long stopFreq = samples[numSamples - 1].getFrequency();
               refBlock.prepare(samples, startFreq, stopFreq);
               VNAReferenceDataHelper.paint(g, scaleLeft, scaleRight, numSamples, height, refBlock.getResizedSamples());
            }

            if (this.config.isPhosphor()) {
               this.drawHistorySample(g, scaleLeft, scaleRight, height);
               this.sampleHistory.add(0, samples);
               if (this.sampleHistory.size() > 8) {
                  this.sampleHistory.remove(8);
               }

               this.setupPhosphorColors(colLeft, colRight);
            }

            Graphics2D g2 = (Graphics2D)g.create();
            Stroke dashed = new BasicStroke(1.0F, 0, 2, 0.0F, new float[]{9.0F}, 0.0F);
            g2.setStroke(dashed);
            Double assVal = scaleLeft.getGuideLineValue();
            int x;
            String valStr;
            if (assVal != null) {
               x = scaleLeft.getScaledSampleValue(assVal, height);
               g2.setColor(colLeft);
               g2.drawLine(0, x, width, x);
               valStr = scaleLeft.getFormattedValueAsStringWithUnit(assVal);
               g2.drawString(valStr, 5, x - 2);
            }

            assVal = scaleRight.getGuideLineValue();
            if (assVal != null) {
               x = scaleRight.getScaledSampleValue(assVal, height);
               g2.setColor(colRight);
               g2.drawLine(0, x, width, x);
               valStr = scaleRight.getFormattedValueAsStringWithUnit(assVal);
               g2.drawString(valStr, width - g2.getFontMetrics().stringWidth(valStr) - 5, x - 2);
            }

            for(x = 1; x < numSamples; ++x) {
               sample = samples[x];
               sample.setDiagramX(x);
               int rY1 = scaleLeft.getScaledSampleValue(sample, height);
               int rY2 = scaleRight.getScaledSampleValue(sample, height);
               g.setColor(colLeft);
               g.drawLine(lastX, lastY1, x, rY1);
               g.setColor(colRight);
               g.drawLine(lastX, lastY2, x, rY2);
               lastX = x;
               lastY1 = rY1;
               lastY2 = rY2;
               int[] polyX = new int[]{x, x - 5, x + 5};
               VNAMarker[] var30 = markers;
               int var29 = markers.length;

               int t;
               for(t = 0; t < var29; ++t) {
                  VNAMarker marker = var30[t];
                  if (marker.isVisible() && x == marker.getDiagramX()) {
                     marker.update(sample);
                     g.setColor(marker.getMarkerColor());
                     if (this.config.isMarkerModeLine()) {
                        g.drawLine(x, 1, x, height - 2);
                        if (x > width - 20) {
                           g.drawString(marker.getShortName(), x - 15, 20);
                        } else {
                           g.drawString(marker.getShortName(), x + 2, 20);
                        }
                     } else {
                        int[] polyY;
                        if (rY1 > 20) {
                           polyY = new int[]{rY1, rY1 - 7, rY1 - 7};
                           g.drawString(marker.getShortName(), x - 3, rY1 - 7);
                           g.drawPolygon(polyX, polyY, 3);
                        } else {
                           polyY = new int[]{rY1, rY1 + 7, rY1 + 7};
                           g.drawString(marker.getShortName(), x - 3, rY1 + 20);
                           g.drawPolygon(polyX, polyY, 3);
                        }

                        if (rY2 > 20) {
                           polyY = new int[]{rY2, rY2 - 7, rY2 - 7};
                           g.drawString(marker.getShortName(), x - 3, rY2 - 7);
                           g.drawPolygon(polyX, polyY, 3);
                        } else {
                           polyY = new int[]{rY2, rY2 + 7, rY2 + 7};
                           g.drawString(marker.getShortName(), x - 3, rY2 + 20);
                           g.drawPolygon(polyX, polyY, 3);
                        }
                     }
                  }
               }

               if (this.lastMouseX == x) {
                  this.mainFrame.getMarkerPanel().getMouseMarker().update(sample);
               }

               int[] ticks = scaleLeft.getTickCoordinates();
               if ((x & 7) == 1) {
                  g.setColor(colDiagramLine);

                  for(t = 0; t < ticks.length; ++t) {
                     g.drawLine(x, ticks[t], x, ticks[t]);
                  }
               }
            }

            if (markers[0].isVisible() && markers[1].isVisible()) {
               VNACalibratedSample s1 = this.mainFrame.getMarkerPanel().getMarker(0).getSample();
               VNACalibratedSample s2 = this.mainFrame.getMarkerPanel().getMarker(1).getSample();
               this.mainFrame.getMarkerPanel().getDeltaMarker().update(VNACalibratedSampleHelper.delta(s1, s2));
            } else {
               this.mainFrame.getMarkerPanel().getDeltaMarker().update((VNACalibratedSample)null);
            }
         }
      } else {
         this.sampleHistory.clear();
      }

      TraceHelper.exit(this, "paint");
   }

   private static final Color getDarkerColor(Color col) {
      int value = col.getRGB();
      int r = (int)((float)((value & 16711680) >> 16) * 0.8F);
      int g = (int)((float)((value & '\uff00') >> 8) * 0.8F);
      int b = (int)((float)(value & 255) * 0.8F);
      return new Color(r, g, b, 255);
   }

   private void setupPhosphorColors(Color colLeft, Color colRight) {
      this.historyColorsLeft[0] = getDarkerColor(colLeft);
      this.historyColorsRight[0] = getDarkerColor(colRight);

      for(int i = 1; i < 8; ++i) {
         this.historyColorsLeft[i] = getDarkerColor(this.historyColorsLeft[i - 1]);
         this.historyColorsRight[i] = getDarkerColor(this.historyColorsRight[i - 1]);
      }

   }

   private void drawHistorySample(Graphics g, VNAGenericScale scaleLeft, VNAGenericScale scaleRight, int height) {
      String methodName = "drawHistorySample";
      TraceHelper.entry(this, "drawHistorySample");
      int historySize = this.sampleHistory.size();
      TraceHelper.text(this, "drawHistorySample", "length=" + historySize);

      for(int historyIndex = historySize - 1; historyIndex >= 0; --historyIndex) {
         VNACalibratedSample[] samples = (VNACalibratedSample[])this.sampleHistory.get(historyIndex);
         int numSamples = samples.length;
         VNACalibratedSample sample = samples[0];
         int lastY1 = scaleLeft.getScaledSampleValue(sample, height);
         int lastY2 = scaleRight.getScaledSampleValue(sample, height);
         int lastX = 0;

         for(int x = 1; x < numSamples; ++x) {
            sample = samples[x];
            sample.setDiagramX(x);
            int rY1 = scaleLeft.getScaledSampleValue(sample, height);
            int rY2 = scaleRight.getScaledSampleValue(sample, height);
            g.setColor(this.historyColorsLeft[historyIndex]);
            g.drawLine(lastX, lastY1, x, rY1);
            g.setColor(this.historyColorsRight[historyIndex]);
            g.drawLine(lastX, lastY2, x, rY2);
            lastX = x;
            lastY1 = rY1;
            lastY2 = rY2;
         }
      }

      TraceHelper.exit(this, "drawHistorySample");
   }

   private void drawBandMap(Graphics g, VNACalibratedSample[] samples) {
      if (this.config.isShowBandmap()) {
         List<VNAFrequencyPair> map = this.bandMap.getList();
         Color col = this.config.getColorBandmap();
         int numSamples = samples.length;
         int height = this.getHeight();

         for(int x = 1; x < numSamples; ++x) {
            VNACalibratedSample sample = samples[x];
            long frq = sample.getFrequency();
            Iterator var12 = map.iterator();

            while(var12.hasNext()) {
               VNAFrequencyPair pair = (VNAFrequencyPair)var12.next();
               if (pair.isWithinPair(frq)) {
                  g.setColor(col);
                  g.drawLine(x, 1, x, height - 2);
                  break;
               }
            }
         }
      }

   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      if (newState == VNAApplicationState.INNERSTATE.DRIVERLOADED) {
         this.sampleHistory.clear();
      } else if (newState != VNAApplicationState.INNERSTATE.CALIBRATED) {
         VNAApplicationState.INNERSTATE var10000 = VNAApplicationState.INNERSTATE.RUNNING;
      }

   }
}
