package krause.vna.gui.smith;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.input.ComplexInputFieldValueChangeListener;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.ExtensionFileFilter;

public class VNASyncedSmithDiagramDialog extends KrauseDialog implements WindowListener, ActionListener, SmithPanelDataSupplier, ComplexInputFieldValueChangeListener {
   private VNAConfig config = VNAConfig.getSingleton();
   private static final String RAW_EXTENSION = "gif";
   private static final String RAW_DESCRIPTION = "GIF images (*.gif)";
   private SmithDataCurve dataCurve = null;
   private SmithPanel smithDiagram;
   private VNAMainFrame mainFrame = null;
   private VNACalibratedSampleBlock lastDataReceived = null;

   public VNASyncedSmithDiagramDialog(VNAMainFrame pMainFrame) {
      super(false);
      String methodName = "VNASyncedSmithDiagramDialog";
      TraceHelper.entry(this, "VNASyncedSmithDiagramDialog");
      this.setConfigurationPrefix("VNASyncedSmithDiagramDialog");
      this.setProperties(VNAConfig.getSingleton());
      this.setDefaultCloseOperation(0);
      this.mainFrame = pMainFrame;
      this.setTitle(VNAMessages.getString("Dlg.SyncedSmith.Title"));
      this.setResizable(true);
      this.setPreferredSize(new Dimension(600, 600));
      this.getContentPane().add(this.createButtonPanel(), "South");
      this.getContentPane().add(this.createSmithPanel(), "Center");
      this.doDialogInit();
      TraceHelper.entry(this, "VNASyncedSmithDiagramDialog");
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      TraceHelper.entry(this, "actionPerformed", cmd);
      if (VNAMessages.getString("Button.Save.GIF.Command").equals(cmd)) {
         this.doExportToImage();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void consumeCalibratedData(VNACalibratedSampleBlock currentData) {
      TraceHelper.entry(this, "consumeCalibratedData");
      this.lastDataReceived = currentData;
      if (this.lastDataReceived != null && this.lastDataReceived.getCalibratedSamples().length > 0) {
         this.dataCurve = this.smithDiagram.createDataCurve(this.lastDataReceived.getCalibratedSamples());
         this.smithDiagram.repaint();
      }

      TraceHelper.exit(this, "consumeCalibratedData");
   }

   private Component createButtonPanel() {
      TraceHelper.entry(this, "createButtonPanel");
      JPanel pnlButton = new JPanel();
      pnlButton.add(SwingUtil.createJButton("Button.Save.GIF", this));
      TraceHelper.exit(this, "createButtonPanel");
      return pnlButton;
   }

   private JPanel createSmithPanel() {
      TraceHelper.entry(this, "createSmithPanel");
      JPanel rc = new JPanel();
      rc.setLayout(new BorderLayout());
      rc.add(this.smithDiagram = new SmithPanel(this), "Center");
      TraceHelper.exit(this, "createSmithPanel");
      return rc;
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      TraceHelper.exit(this, "doCANCEL");
   }

   private void doExportToImage() {
      JFileChooser fc = new JFileChooser();
      fc.setFileSelectionMode(0);
      fc.setFileFilter(new ExtensionFileFilter("GIF images (*.gif)", "gif"));
      fc.setSelectedFile(new File(this.config.getExportDirectory() + "/."));
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
         } catch (Exception var17) {
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
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   public SmithDataCurve getDataCurve() {
      return this.dataCurve;
   }

   public SelectedSampleTuple[] getSelectedTuples() {
      TraceHelper.entry(this, "getSelectedTuples");
      ArrayList<SelectedSampleTuple> tuples = new ArrayList();
      VNAMarkerPanel mp = this.mainFrame.getMarkerPanel();
      VNAMarker[] markers = mp.getMarkers();
      VNAMarker[] var7 = markers;
      int var6 = markers.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         VNAMarker marker = var7[var5];
         if (marker.isVisible()) {
            SelectedSampleTuple t = new SelectedSampleTuple(marker.getSample().getDiagramX(), this.config.getColorMarker(1), marker.getShortName());
            tuples.add(t);
         }
      }

      TraceHelper.exit(this, "getSelectedTuples");
      return (SelectedSampleTuple[])tuples.toArray(new SelectedSampleTuple[tuples.size()]);
   }

   public void valueChanged(Complex oldValue, Complex newValue) {
      TraceHelper.entry(this, "valueChanged");
      this.consumeCalibratedData(this.lastDataReceived);
      TraceHelper.exit(this, "valueChanged");
   }

   public void windowClosing(WindowEvent e) {
      TraceHelper.entry(this, "windowClosing");
      this.mainFrame.getDiagramPanel().getScaleSelectPanel().doHandleSmithDiagram();
      TraceHelper.exit(this, "windowClosing");
   }
}
