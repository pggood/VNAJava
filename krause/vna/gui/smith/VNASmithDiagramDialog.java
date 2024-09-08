package krause.vna.gui.smith;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.input.ComplexInputFieldValueChangeListener;
import krause.vna.gui.panels.marker.VNAMarkerTextField;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.smith.data.SmithDiagramCurve;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAGridBagConstraints;
import krause.vna.resources.VNAMessages;
import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.ExtensionFileFilter;

public class VNASmithDiagramDialog extends KrauseDialog implements ActionListener, AdjustmentListener, MouseWheelListener, SmithPanelDataSupplier, ComplexInputFieldValueChangeListener {
   private static final String RAW_EXTENSION = "gif";
   private static final String RAW_DESCRIPTION = "GIF images (*.gif)";
   private List<SmithDiagramCurve> realCurves = new ArrayList();
   private List<SmithDiagramCurve> imaginaryCurves = new ArrayList();
   private SmithDataCurve dataCurve = null;
   private SmithPanel smithDiagram;
   private VNACalibratedSampleBlock dataBlock = null;
   private JScrollBar sbMarker;
   private VNAMarkerTextField txtFRQ;
   private VNAMarkerTextField txtLOSS;
   private VNAMarkerTextField txtPHASE;
   private VNAMarkerTextField txtZ;
   private VNAMarkerTextField txtR;
   private VNAMarkerTextField txtX;
   private VNAMarkerTextField txtSWR;
   private ComplexInputField referenceResistance;
   private VNAConfig config = VNAConfig.getSingleton();
   private int selectedSampleIndex;

   public VNASmithDiagramDialog(VNACalibratedSampleBlock blk, String titleInsert) {
      super(false);
      TraceHelper.entry(this, "VNASmithDiagramDialog");
      String msg = VNAMessages.getString("Dlg.Smith.Title");
      this.setConfigurationPrefix("VNASmithDiagramDialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setTitle(MessageFormat.format(msg, titleInsert));
      this.setResizable(true);
      this.dataBlock = blk;
      this.setPreferredSize(new Dimension(720, 600));
      this.getContentPane().add(this.createButtonPanel(), "South");
      this.getContentPane().add(this.createSmithPanel(), "Center");
      this.calculateSmithChart();
      this.valueChanged((Complex)null, (Complex)null);
      this.doDialogInit();
      TraceHelper.exit(this, "VNASmithDiagramDialog");
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.config.setSmithReference(this.referenceResistance.getComplexValue());
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   public void valueChanged(Complex oldValue, Complex newValue) {
      TraceHelper.entry(this, "valueChanged");
      VNACalibratedSample[] samples = this.dataBlock.getCalibratedSamples();
      this.dataCurve = this.smithDiagram.createDataCurve(samples);
      this.sbMarker.setValue(0);
      this.updateMarker(this.dataBlock.getCalibratedSamples()[0]);
      this.smithDiagram.repaint();
      TraceHelper.exit(this, "valueChanged");
   }

   private Component createButtonPanel() {
      TraceHelper.entry(this, "createButtonPanel");
      JPanel pnlButton = new JPanel();
      this.referenceResistance = new ComplexInputField(this.config.getSmithReference());
      this.referenceResistance.setMaximum(new Complex(5000.0D, 5000.0D));
      this.referenceResistance.setMinimum(new Complex(-5000.0D, -5000.0D));
      this.referenceResistance.setListener(this);
      pnlButton.add(this.referenceResistance);
      pnlButton.add(this.createMarkerPanel());
      pnlButton.add(SwingUtil.createJButton("Button.Save.GIF", this));
      pnlButton.add(SwingUtil.createJButton("Button.Close", this));
      TraceHelper.exit(this, "createButtonPanel");
      return pnlButton;
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      TraceHelper.entry(this, "actionPerformed", cmd);
      if (VNAMessages.getString("Button.Close.Command").equals(cmd)) {
         this.doDialogCancel();
      } else if (VNAMessages.getString("Button.Save.GIF.Command").equals(cmd)) {
         this.doExportToImage();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void adjustmentValueChanged(AdjustmentEvent e) {
      this.selectedSampleIndex = e.getValue();
      this.updateMarker(this.dataBlock.getCalibratedSamples()[e.getValue()]);
      this.smithDiagram.repaint();
   }

   private void calculateSmithChart() {
      TraceHelper.entry(this, "calculateSmithChart");
      this.realCurves.add(this.createCircle4Real(0.0D));
      this.realCurves.add(this.createCircle4Real(0.2D));
      this.realCurves.add(this.createCircle4Real(0.5D));
      this.realCurves.add(this.createCircle4Real(1.0D));
      this.realCurves.add(this.createCircle4Real(2.0D));
      this.realCurves.add(this.createCircle4Real(5.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(-5.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(-2.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(-1.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(-0.5D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(-0.2D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(0.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(0.2D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(0.5D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(1.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(2.0D));
      this.imaginaryCurves.add(this.createCircle4Imaginary(5.0D));
      TraceHelper.exit(this, "calculateSmithChart");
   }

   private SmithDiagramCurve createCircle4Imaginary(double imaginary) {
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
         gamma = mGamma(comp);
         px = (int)(gamma.getReal() * (double)this.getFactor());
         py = (int)(gamma.getImaginary() * (double)this.getFactor());
         rc.addPoint(px, -py);
      }

      for(x = 10.0D; x < 100.0D; x += 0.5D) {
         comp = new Complex(x, imaginary);
         gamma = mGamma(comp);
         px = (int)(gamma.getReal() * (double)this.getFactor());
         py = (int)(gamma.getImaginary() * (double)this.getFactor());
         rc.addPoint(px, -py);
      }

      return rc;
   }

   private SmithDiagramCurve createCircle4Real(double real) {
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
         gamma = mGamma(comp);
         px = (int)(gamma.getReal() * (double)this.getFactor());
         py = (int)(gamma.getImaginary() * (double)this.getFactor());
         rc.addPoint(px, -py);
      }

      for(x = -10.0D; x < 10.0D; x += 0.1D) {
         comp = new Complex(real, x);
         gamma = mGamma(comp);
         px = (int)(gamma.getReal() * (double)this.getFactor());
         py = (int)(gamma.getImaginary() * (double)this.getFactor());
         rc.addPoint(px, -py);
      }

      for(x = 10.0D; x < 100.0D; x += 0.5D) {
         comp = new Complex(real, x);
         gamma = mGamma(comp);
         px = (int)(gamma.getReal() * (double)this.getFactor());
         py = (int)(gamma.getImaginary() * (double)this.getFactor());
         rc.addPoint(px, -py);
      }

      return rc;
   }

   private JPanel createMarkerPanel() {
      JPanel rc = new JPanel();
      rc.setLayout(new GridBagLayout());
      rc.setBorder((Border)null);
      int x = 0;
      int line = 0;
      JLabel var10001 = new JLabel(VNAMessages.getString("Marker.Frequency"));
      int var4 = x + 1;
      rc.add(var10001, new VNAGridBagConstraints(x, line, 1, 1, 0.0D, 0.0D));
      rc.add(new JLabel(VNAMessages.getString("Marker.RL")), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(new JLabel(VNAMessages.getString("Marker.PhaseRL")), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(new JLabel(VNAMessages.getString("Marker.Z")), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(new JLabel(VNAMessages.getString("Marker.R")), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(new JLabel(VNAMessages.getString("Marker.X")), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(new JLabel(VNAMessages.getString("Marker.SWR")), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      line = 1;
      x = 0;
      VNAMarkerTextField var5 = this.txtFRQ = new VNAMarkerTextField(9, false);
      var4 = x + 1;
      rc.add(var5, new VNAGridBagConstraints(x, line, 1, 1, 0.0D, 0.0D));
      rc.add(this.txtLOSS = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(this.txtPHASE = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(this.txtZ = new VNAMarkerTextField(5, false), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(this.txtR = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(this.txtX = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      rc.add(this.txtSWR = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(var4++, line, 1, 1, 0.0D, 0.0D));
      return rc;
   }

   private JPanel createSmithPanel() {
      TraceHelper.entry(this, "createSmithPanel");
      JPanel rc = new JPanel();
      rc.setLayout(new BorderLayout());
      rc.add(this.smithDiagram = new SmithPanel(this), "Center");
      this.sbMarker = new JScrollBar(0, 0, 1, 0, this.dataBlock.getCalibratedSamples().length - 1);
      this.sbMarker.addAdjustmentListener(this);
      this.sbMarker.addMouseWheelListener(this);
      this.sbMarker.setToolTipText(VNAMessages.getString("Dlg.Smith.Scrollbar"));
      rc.add(this.sbMarker, "South");
      TraceHelper.exit(this, "createSmithPanel");
      return rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   private void doExportToImage() {
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new ExtensionFileFilter("GIF images (*.gif)", "gif"));
      fc.setSelectedFile(new File(VNAConfig.getSingleton().getExportDirectory() + "/."));
      int returnVal = fc.showSaveDialog(this);
      if (returnVal == 0) {
         File file = fc.getSelectedFile();
         if (!file.getName().endsWith(".gif")) {
            file = new File(file.getAbsolutePath() + "." + "gif");
         }

         if (file.exists()) {
            String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), file.getName());
            int response = JOptionPane.showOptionDialog(this, msg, VNAMessages.getString("Message.Export.2"), 0, 3, (Icon)null, (Object[])null, (Object)null);
            if (response == 2) {
               return;
            }
         }

         Dimension size = this.smithDiagram.getSize();
         BufferedImage smithImage = new BufferedImage(size.width, size.height, 1);
         Graphics2D g2 = smithImage.createGraphics();
         this.smithDiagram.paint(g2);
         FileOutputStream outputStream = null;

         try {
            outputStream = new FileOutputStream(file.getAbsolutePath());
            ImageIO.write(smithImage, "gif", outputStream);
         } catch (IOException var17) {
            JOptionPane.showMessageDialog(this, var17.getMessage(), VNAMessages.getString("Message.Export.2"), 0);
            ErrorLogHelper.exception(this, "doExportToJPG", var17);
         } finally {
            if (outputStream != null) {
               try {
                  outputStream.close();
               } catch (IOException var16) {
                  ErrorLogHelper.exception(this, "doExportToJPG", var16);
               }
            }

         }
      }

   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   public SmithDataCurve getDataCurve() {
      return this.dataCurve;
   }

   public int getFactor() {
      return 1000;
   }

   public List<SmithDiagramCurve> getImaginaryCurves() {
      return this.imaginaryCurves;
   }

   public List<SmithDiagramCurve> getRealCurves() {
      return this.realCurves;
   }

   public int getSelectedSampleIndex() {
      return this.selectedSampleIndex;
   }

   private static Complex mGamma(Complex z) {
      Complex rc = null;
      rc = z.subtract(SmithPanel.PLUS_1).divide(z.add(SmithPanel.PLUS_1));
      return rc;
   }

   private void updateMarker(VNACalibratedSample s) {
      if (s != null) {
         this.txtFRQ.setText(VNAFormatFactory.getFrequencyFormat().format(s.getFrequency()));
         this.txtSWR.setText(VNAFormatFactory.getSwrFormat().format(s.getSWR()));
         this.txtLOSS.setText(VNAFormatFactory.getReflectionLossFormat().format(s.getReflectionLoss()));
         this.txtPHASE.setText(VNAFormatFactory.getPhaseFormat().format(s.getReflectionPhase()));
         this.txtZ.setText(VNAFormatFactory.getZFormat().format(s.getZ()));
         this.txtR.setText(VNAFormatFactory.getRsFormat().format(s.getR()));
         this.txtX.setText(VNAFormatFactory.getXsFormat().format(s.getX()));
      } else {
         this.txtFRQ.setText("");
         this.txtSWR.setText("");
         this.txtZ.setText("");
         this.txtLOSS.setText("");
         this.txtPHASE.setText("");
         this.txtR.setText("");
         this.txtX.setText("");
      }

   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      TraceHelper.entry(this, "mouseWheelMoved");
      JScrollBar source = (JScrollBar)e.getSource();
      if (e.getScrollType() == 0) {
         int totalScrollAmount = e.getUnitsToScroll() * source.getUnitIncrement();
         source.setValue(source.getValue() + totalScrollAmount);
      }

      TraceHelper.exit(this, "mouseWheelMoved");
   }

   public SelectedSampleTuple[] getSelectedTuples() {
      return new SelectedSampleTuple[]{new SelectedSampleTuple(this.selectedSampleIndex, Color.red, "M")};
   }
}
