package krause.vna.gui.smith;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.smith.data.SmithDiagramCurve;
import krause.vna.resources.VNAMessages;
import org.apache.commons.math3.complex.Complex;

public class SmithPanel extends JPanel {
   public static final Complex PLUS_1 = new Complex(1.0D, 0.0D);
   public static final Complex PLUS_50 = new Complex(50.0D, 0.0D);
   public static final int SCALING_FACTOR = 1000;
   public static final String VERSION_INFO = VNAMessages.getString("Application.version");
   public static final String COPYRIGHT_INFO = VNAMessages.getString("Application.copyright");
   public static final int TEXT_TOP_OFFSET = 15;
   public static final int TEXT_LEFT_OFFSET = 15;
   private final Font textFont = new Font("Courier", 1, 15);
   private Complex referenceResistance;
   private transient SmithPanelDataSupplier dataSupplier;
   private VNAConfig config;
   private List<SmithDiagramCurve> admittanceRealCircles;
   private List<SmithDiagramCurve> admittanceImaginaryCircles;
   private List<SmithDiagramCurve> impedanceRealCircles;
   private List<SmithDiagramCurve> impedanceImaginaryCircles;
   private double[] swrCircles;
   private Color colText;
   private Color colData;
   private Color colBackground;
   private Color colMarker;
   private Color colSWR;
   private boolean showPhase;
   private boolean showRL;
   private boolean showRS;
   private boolean showXS;
   private boolean showZ;
   private boolean showMag;
   private boolean showSwr;
   private Color colImpedance;
   private Color colAdmittance;

   private static Complex calculateReflectionCoefficient(Complex z) {
      Complex rc = null;
      rc = z.subtract(PLUS_1).divide(z.add(PLUS_1));
      return rc;
   }

   private static SmithDiagramCurve createCircleAdmittanceImaginary(double imaginary) {
      SmithDiagramCurve rc = new SmithDiagramCurve();
      rc.setLabel(NumberFormat.getNumberInstance().format(-imaginary));
      rc.setRealCurve(false);

      double x;
      Complex comp;
      Complex gamma;
      int px;
      int py;
      for(x = 0.0D; x < 10.0D; x += 0.1D) {
         comp = new Complex(x, imaginary);
         gamma = calculateReflectionCoefficient(comp);
         px = -((int)(gamma.getReal() * 1000.0D));
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      for(x = 10.0D; x < 100.0D; x += 0.5D) {
         comp = new Complex(x, imaginary);
         gamma = calculateReflectionCoefficient(comp);
         px = -((int)(gamma.getReal() * 1000.0D));
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      return rc;
   }

   private static SmithDiagramCurve createCircleAdmittanceReal(double real) {
      SmithDiagramCurve rc = new SmithDiagramCurve();
      rc.setLabel(NumberFormat.getNumberInstance().format(real));
      rc.setRealCurve(false);

      double x;
      Complex comp;
      Complex gamma;
      int px;
      int py;
      for(x = -100.0D; x < -10.0D; x += 0.5D) {
         comp = new Complex(real, x);
         gamma = calculateReflectionCoefficient(comp);
         px = -((int)(gamma.getReal() * 1000.0D));
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      for(x = -10.0D; x < 10.0D; x += 0.1D) {
         comp = new Complex(real, x);
         gamma = calculateReflectionCoefficient(comp);
         px = -((int)(gamma.getReal() * 1000.0D));
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      for(x = 10.0D; x < 100.0D; x += 0.5D) {
         comp = new Complex(real, x);
         gamma = calculateReflectionCoefficient(comp);
         px = -((int)(gamma.getReal() * 1000.0D));
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      return rc;
   }

   private static SmithDiagramCurve createCircleImpedanceImaginary(double imaginary) {
      SmithDiagramCurve rc = new SmithDiagramCurve();
      rc.setLabel(NumberFormat.getNumberInstance().format(imaginary));
      rc.setRealCurve(false);

      double x;
      Complex comp;
      Complex gamma;
      int px;
      int py;
      for(x = 0.0D; x < 10.0D; x += 0.1D) {
         comp = new Complex(x, imaginary);
         gamma = calculateReflectionCoefficient(comp);
         px = (int)(gamma.getReal() * 1000.0D);
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      for(x = 10.0D; x < 100.0D; x += 0.5D) {
         comp = new Complex(x, imaginary);
         gamma = calculateReflectionCoefficient(comp);
         px = (int)(gamma.getReal() * 1000.0D);
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      return rc;
   }

   private static SmithDiagramCurve createCircleImpedanceReal(double real) {
      SmithDiagramCurve rc = new SmithDiagramCurve();
      rc.setLabel(NumberFormat.getNumberInstance().format(real));
      rc.setRealCurve(false);

      double x;
      Complex comp;
      Complex gamma;
      int px;
      int py;
      for(x = -100.0D; x < -10.0D; x += 0.5D) {
         comp = new Complex(real, x);
         gamma = calculateReflectionCoefficient(comp);
         px = (int)(gamma.getReal() * 1000.0D);
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      for(x = -10.0D; x < 10.0D; x += 0.1D) {
         comp = new Complex(real, x);
         gamma = calculateReflectionCoefficient(comp);
         px = (int)(gamma.getReal() * 1000.0D);
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      for(x = 10.0D; x < 100.0D; x += 0.5D) {
         comp = new Complex(real, x);
         gamma = calculateReflectionCoefficient(comp);
         px = (int)(gamma.getReal() * 1000.0D);
         py = (int)(gamma.getImaginary() * 1000.0D);
         rc.addPoint(px, -py);
      }

      return rc;
   }

   private static double[] readDoubleList(String line) {
      ArrayList<Double> al = new ArrayList();
      String[] doubles = line.split(" ");
      String[] var6 = doubles;
      int var5 = doubles.length;

      int i;
      for(i = 0; i < var5; ++i) {
         String aDoubleString = var6[i];
         if (aDoubleString.trim().length() > 0) {
            al.add(Double.parseDouble(aDoubleString));
         }
      }

      double[] target = new double[al.size()];

      for(i = 0; i < target.length; ++i) {
         target[i] = (Double)al.get(i);
      }

      return target;
   }

   public SmithPanel(SmithPanelDataSupplier pDataSupplier) {
      this.referenceResistance = PLUS_50;
      this.config = VNAConfig.getSingleton();
      this.admittanceRealCircles = new ArrayList();
      this.admittanceImaginaryCircles = new ArrayList();
      this.impedanceRealCircles = new ArrayList();
      this.impedanceImaginaryCircles = new ArrayList();
      this.dataSupplier = pDataSupplier;
      this.calculateSmithChart();
      this.setBounds(0, 0, 250, 250);
      this.readConfig();
      this.setBackground(this.colBackground);
      this.setToolTipText(VNAMessages.getString("SmithPanel.tooltip"));
      this.addMouseListener(new MouseListener() {
         public void mouseClicked(MouseEvent e) {
            SmithPanel.this.doConfig();
         }

         public void mouseEntered(MouseEvent e) {
         }

         public void mouseExited(MouseEvent e) {
         }

         public void mousePressed(MouseEvent e) {
         }

         public void mouseReleased(MouseEvent e) {
         }
      });
   }

   private void calculateSmithChart() {
      TraceHelper.entry(this, "calculateSmithChart");
      this.admittanceRealCircles.clear();
      double[] var5;
      int var4 = (var5 = readDoubleList(this.config.getProperty("SmithPanel.circlesAdmittanceReal", ""))).length;

      double cond;
      int var3;
      for(var3 = 0; var3 < var4; ++var3) {
         cond = var5[var3];
         this.admittanceRealCircles.add(createCircleAdmittanceReal(cond));
      }

      this.admittanceImaginaryCircles.clear();
      var4 = (var5 = readDoubleList(this.config.getProperty("SmithPanel.circlesAdmittanceImaginary", ""))).length;

      for(var3 = 0; var3 < var4; ++var3) {
         cond = var5[var3];
         this.admittanceImaginaryCircles.add(createCircleAdmittanceImaginary(cond));
      }

      this.impedanceRealCircles.clear();
      var4 = (var5 = readDoubleList(this.config.getProperty("SmithPanel.circlesImpedanceReal", "0.0 0.2 0.5 1.0 2.0 5.0"))).length;

      for(var3 = 0; var3 < var4; ++var3) {
         cond = var5[var3];
         this.impedanceRealCircles.add(createCircleImpedanceReal(cond));
      }

      this.impedanceImaginaryCircles.clear();
      var4 = (var5 = readDoubleList(this.config.getProperty("SmithPanel.circlesImpedanceImaginary", "-5.0 -2.0 -1.0 -0.5 -0.2 0.0 0.2 0.5 1.0 2.0 5.0"))).length;

      for(var3 = 0; var3 < var4; ++var3) {
         cond = var5[var3];
         this.impedanceImaginaryCircles.add(createCircleImpedanceImaginary(cond));
      }

      this.swrCircles = readDoubleList(this.config.getProperty("SmithPanel.circlesSWR", "2.0 3.0"));
      TraceHelper.exit(this, "calculateSmithChart");
   }

   public SmithDataCurve createDataCurve(VNACalibratedSample[] samples) {
      TraceHelper.entry(this, "createDataCurve");
      Point2D[] points = new Point2D[samples.length];

      for(int i = 0; i < samples.length; ++i) {
         VNACalibratedSample sample = samples[i];
         double real = sample.getR();
         if (real < 0.0D) {
            real = 0.0D;
         }

         double imag = sample.getX();
         Complex compSample = new Complex(real, imag);
         Complex gamma = compSample.subtract(this.referenceResistance).divide(compSample.add(this.referenceResistance));
         Point2D aPoint = new java.awt.geom.Point2D.Double(gamma.getReal() * 1000.0D, -gamma.getImaginary() * 1000.0D);
         points[i] = aPoint;
      }

      SmithDataCurve rc = new SmithDataCurve(samples.length);
      rc.setSamples(samples);
      rc.addPoints(points);
      TraceHelper.exit(this, "createDataCurve");
      return rc;
   }

   protected void doConfig() {
      TraceHelper.entry(this, "doConfig");
      new VNASmithDiagramConfigDialog();
      this.readConfig();
      this.calculateSmithChart();
      this.repaint();
      TraceHelper.exit(this, "doConfig");
   }

   private void drawInfoFrequencyRange(Graphics g, VNACalibratedSample[] samples) {
      long startFrq = samples[0].getFrequency();
      long stopFrq = samples[samples.length - 1].getFrequency();
      int x = this.getWidth() - g.getFontMetrics().stringWidth(String.format("Start %,12dHz  ", 1));
      g.drawString(String.format("Start %,13dHz", startFrq), x, this.getHeight() - 25);
      g.drawString(String.format("Stop  %,13dHz", stopFrq), x, this.getHeight() - 10);
   }

   private void drawInfoMarkers(Graphics g, VNACalibratedSample[] samples, SelectedSampleTuple[] tuples) {
      String txtMarker = String.format("%s: %,12dHz  ", "1", 100);
      Rectangle2D textDimension = g.getFontMetrics().getStringBounds(txtMarker, g);
      int x = (int)((double)this.getWidth() - textDimension.getWidth());
      int yStep = (int)textDimension.getHeight();
      int index = 0;
      SelectedSampleTuple[] var13 = tuples;
      int var12 = tuples.length;

      for(int var11 = 0; var11 < var12; ++var11) {
         SelectedSampleTuple tuple = var13[var11];
         VNACalibratedSample sample = samples[tuple.getIndex()];
         g.drawString(String.format("%s: %,12dHz", tuple.getName(), sample.getFrequency()), x, 15 + yStep * index++);
         if (this.showRL) {
            g.drawString(String.format("    RL %7.2fdB", sample.getReflectionLoss()), x, 15 + yStep * index++);
         }

         if (this.showPhase) {
            g.drawString(String.format("    RP %7.2f°", sample.getReflectionPhase()), x, 15 + yStep * index++);
         }

         if (this.showZ) {
            g.drawString(String.format("    Z  %6.1fΩ", sample.getZ()), x, 15 + yStep * index++);
         }

         if (this.showRS) {
            g.drawString(String.format("    Rs %6.1fΩ", sample.getR()), x, 15 + yStep * index++);
         }

         if (this.showXS) {
            g.drawString(String.format("    Xs %6.1fΩ", sample.getX()), x, 15 + yStep * index++);
         }

         if (this.showSwr) {
            g.drawString(String.format("    Swr %5.1f:1", sample.getSWR()), x, 15 + yStep * index++);
         }

         if (this.showMag) {
            g.drawString(String.format("    Mag %7.3f", sample.getMag()), x, 15 + yStep * index++);
         }
      }

   }

   private void drawInfoDecoration(Graphics g) {
      g.setColor(this.colText);
      g.setFont(this.textFont);
      g.drawString("L    vna/J " + VERSION_INFO + "  " + COPYRIGHT_INFO, 15, 15);
      g.drawString("C    Ref=" + this.referenceResistance.getReal() + "+" + this.referenceResistance.getImaginary() + "i", 15, this.getHeight() - 10);
   }

   public void paint(Graphics g) {
      super.paint(g);
      Graphics2D grafObject = (Graphics2D)g.create();
      g.setColor(this.colBackground);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      this.drawInfoDecoration(g);
      SmithDataCurve curve = this.dataSupplier.getDataCurve();
      SelectedSampleTuple[] tuples = this.dataSupplier.getSelectedTuples();
      double minVal = (double)Math.min(this.getWidth(), this.getHeight()) * 0.93D;
      double scale = minVal / 2000.0D;
      Font f = grafObject.getFont().deriveFont((float)(12.0D / scale)).deriveFont(1);
      grafObject.setFont(f);
      AffineTransform transformTranslate = AffineTransform.getTranslateInstance((double)this.getWidth() / 2.0D, (double)this.getHeight() / 2.0D);
      AffineTransform transformScale = AffineTransform.getScaleInstance(scale, scale);
      grafObject.transform(transformTranslate);
      grafObject.transform(transformScale);
      grafObject.setColor(this.colAdmittance);
      Iterator var14 = this.admittanceRealCircles.iterator();

      SmithDiagramCurve polygon;
      while(var14.hasNext()) {
         polygon = (SmithDiagramCurve)var14.next();
         grafObject.drawString(polygon.getLabel(), polygon.xpoints[polygon.npoints / 2], polygon.ypoints[polygon.npoints / 2] + 40);
         grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
      }

      var14 = this.admittanceImaginaryCircles.iterator();

      int x;
      while(var14.hasNext()) {
         polygon = (SmithDiagramCurve)var14.next();
         x = polygon.xpoints[0];
         if (x < 0) {
            x -= 40;
         } else if (x > 0) {
            x += 40;
         }

         grafObject.drawString(polygon.getLabel(), x, polygon.ypoints[0]);
         grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
      }

      grafObject.setColor(this.colImpedance);
      var14 = this.impedanceRealCircles.iterator();

      while(var14.hasNext()) {
         polygon = (SmithDiagramCurve)var14.next();
         grafObject.drawString(polygon.getLabel(), polygon.xpoints[polygon.npoints / 2] + 10, polygon.ypoints[polygon.npoints / 2] - 10);
         grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
      }

      var14 = this.impedanceImaginaryCircles.iterator();

      while(var14.hasNext()) {
         polygon = (SmithDiagramCurve)var14.next();
         x = polygon.xpoints[0];
         if (x < 0) {
            x += 40;
         } else if (x > 0) {
            x -= 40;
         }

         grafObject.drawString(polygon.getLabel(), x, polygon.ypoints[0]);
         grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
      }

      double[] var17;
      int var16 = (var17 = this.swrCircles).length;

      for(x = 0; x < var16; ++x) {
         double aSWR = var17[x];
         double mag = (aSWR - 1.0D) / (aSWR + 1.0D);
         int ac = (int)(mag * 2000.0D);
         grafObject.setColor(this.colSWR);
         grafObject.drawOval(-ac / 2, -ac / 2, ac, ac);
      }

      grafObject.setColor(this.colData);
      if (curve != null) {
         Line2D[] lines = curve.getLines();
         int i = 0;
         Line2D[] var25 = lines;
         int var24 = lines.length;

         for(var16 = 0; var16 < var24; ++var16) {
            Line2D line = var25[var16];
            grafObject.draw(line);
            if (tuples != null) {
               for(int s = 0; s < tuples.length; ++s) {
                  if (tuples[s].getIndex() == i) {
                     grafObject.setColor(this.colMarker);
                     grafObject.drawRect((int)line.getX1() - 15, (int)line.getY1() - 15, 30, 30);
                     grafObject.drawString(tuples[s].getName(), (int)line.getX1() - 7, (int)line.getY1() - 20);
                     grafObject.setColor(this.colData);
                  }
               }
            }

            ++i;
         }

         g.setColor(this.colText);
         g.setFont(this.textFont);
         VNACalibratedSample[] samples = curve.getSamples();
         if (samples != null) {
            this.drawInfoFrequencyRange(g, samples);
            if (tuples != null) {
               this.drawInfoMarkers(g, samples, tuples);
            }
         }
      }

   }

   private void readConfig() {
      TraceHelper.entry(this, "readConfig");
      this.colText = this.config.getColor("SmithPanel.colText", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_TEXT);
      this.colData = this.config.getColor("SmithPanel.colLines", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_DATA);
      this.colBackground = this.config.getColor("SmithPanel.colBackground", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_BACKGROUND);
      this.colMarker = this.config.getColor("SmithPanel.colMarker", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_MARKER);
      this.colSWR = this.config.getColor("SmithPanel.colSWR", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_SWR);
      this.colImpedance = this.config.getColor("SmithPanel.colImpedance", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_IMPEDANCE);
      this.colAdmittance = this.config.getColor("SmithPanel.colInductance", VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_ADMITTANCE);
      this.referenceResistance = this.config.getSmithReference();
      this.showPhase = this.config.getBoolean("SmithPanel.showMarker.Phase", true);
      this.showRL = this.config.getBoolean("SmithPanel.showMarker.RL", true);
      this.showRS = this.config.getBoolean("SmithPanel.showMarker.RS", true);
      this.showXS = this.config.getBoolean("SmithPanel.showMarker.XS", true);
      this.showZ = this.config.getBoolean("SmithPanel.showMarker.Z", true);
      this.showMag = this.config.getBoolean("SmithPanel.showMarker.Mag", true);
      this.showSwr = this.config.getBoolean("SmithPanel.showMarker.SWR", true);
      TraceHelper.exit(this, "readConfig");
   }
}
