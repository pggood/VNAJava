package krause.vna.gui.panels.marker;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import krause.util.GlobalSymbols;
import krause.util.StringHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.tune.VNATuneDialog;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNAMarker implements ClipboardOwner, MouseWheelListener, MouseListener, ActionListener, VNAApplicationStateObserver {
   private VNATuneDialog bigSWRDialog;
   private JCheckBox cbVisible;
   private final VNADataPool datapool;
   private int diagramX;
   private IMarkerEventEvaluator eventEvaluator;
   private boolean iAmVisible;
   private boolean isDynamicMarker;
   private boolean isMouseMarker;
   private JToggleButton labelBIGSWR;
   private JToggleButton labelMath;
   private JLabel lblName;
   private VNAMainFrame mainFrame;
   private Color markerColor;
   private VNAMarkerMathDialog mathDialog;
   private String name;
   private VNACalibratedSample sample;
   private String shortName;
   private VNAMarkerTextField txtFRQ;
   private VNAMarkerTextField txtLoss;
   private VNAMarkerTextField txtPhase;
   private VNAMarkerTextField txtR;
   private VNAMarkerTextField txtSwrGrpDelay;
   private VNAMarkerTextField txtTheta;
   private VNAMarkerTextField txtX;
   private VNAMarkerTextField txtZ;

   public VNAMarker(int id, VNAMainFrame pMF, VNAMarkerPanel pMP, ActionListener pLis, int pLine, int pStartCol, Color pColor) {
      this("" + id, pMF, pMP, pLis, pLine, pStartCol, pColor);
   }

   public VNAMarker(String pKey, VNAMainFrame pMainFrame, JPanel panel, ActionListener listener, int line, int startCol, Color color) {
      this.cbVisible = null;
      this.datapool = VNADataPool.getSingleton();
      this.diagramX = -1;
      this.eventEvaluator = null;
      this.iAmVisible = false;
      this.isDynamicMarker = false;
      this.isMouseMarker = false;
      this.labelBIGSWR = null;
      this.labelMath = null;
      this.lblName = null;
      this.markerColor = null;
      this.mathDialog = null;
      this.name = null;
      this.sample = null;
      this.txtFRQ = null;
      this.txtLoss = null;
      this.txtPhase = null;
      this.txtR = null;
      this.txtSwrGrpDelay = null;
      this.txtTheta = null;
      this.txtX = null;
      this.txtZ = null;
      TraceHelper.entry(this, "VNAMarker", pKey);
      this.mainFrame = pMainFrame;
      this.isDynamicMarker = line == 4;
      this.isMouseMarker = line == 2;
      this.markerColor = color;
      this.name = VNAMessages.getString("Marker." + pKey);
      this.shortName = VNAMessages.getString("Marker." + pKey + ".short");
      this.lblName = new VNAMarkerLabel(this.name);
      this.lblName.setToolTipText(VNAMessages.getString("Marker.Name.Tooltip"));
      panel.add(this.lblName, "");
      panel.add(this.txtFRQ = new VNAMarkerTextField(8), "");
      panel.add(this.txtLoss = new VNAMarkerTextField(4), "");
      panel.add(this.txtPhase = new VNAMarkerTextField(4), "");
      panel.add(this.txtZ = new VNAMarkerTextField(4), "");
      panel.add(this.txtR = new VNAMarkerTextField(4), "");
      panel.add(this.txtX = new VNAMarkerTextField(4), "");
      panel.add(this.txtTheta = new VNAMarkerTextField(4), "");
      panel.add(this.txtSwrGrpDelay = new VNAMarkerTextField(4), "");
      panel.add(this.cbVisible = new JCheckBox("", false), "");
      this.cbVisible.addActionListener(listener);
      this.cbVisible.setActionCommand(this.name);
      this.cbVisible.setToolTipText(VNAMessages.getString("Marker.Checkbox.Tooltip"));
      panel.add(this.labelMath = SwingUtil.createToggleButton("Marker.Math", this), "");
      panel.add(this.labelBIGSWR = SwingUtil.createToggleButton("Marker.BigSWR", this), "wrap");
      this.labelMath.setBorder((Border)null);
      this.labelBIGSWR.setBorder((Border)null);
      this.cbVisible.setBorder((Border)null);
      this.lblName.setBorder((Border)null);
      if (!this.isMouseMarker && !this.isDynamicMarker) {
         this.lblName.addMouseListener(this);
         this.txtPhase.setMarkerSearchMode(new VNAMarkerSearchMode(VNAMarkerSearchMode.MARKERFIELDTYPE.PHASE));
         this.txtPhase.addMouseListener(this);
         this.txtPhase.setToolTipText(VNAMessages.getString("Marker.Phase.Tooltip"));
         this.txtLoss.setMarkerSearchMode(new VNAMarkerSearchMode(VNAMarkerSearchMode.MARKERFIELDTYPE.RL));
         this.txtLoss.addMouseListener(this);
         this.txtLoss.setToolTipText(VNAMessages.getString("Marker.Loss.Tooltip"));
         this.txtSwrGrpDelay.setMarkerSearchMode(new VNAMarkerSearchMode(VNAMarkerSearchMode.MARKERFIELDTYPE.SWR));
         this.txtSwrGrpDelay.addMouseListener(this);
         this.txtSwrGrpDelay.setToolTipText(VNAMessages.getString("Marker.SWR.Tooltip"));
      }

      if (this.isMouseMarker) {
         this.cbVisible.setVisible(false);
         this.labelMath.setVisible(false);
         this.labelBIGSWR.setVisible(false);
      }

      if (this.isDynamicMarker) {
         this.txtSwrGrpDelay.setVisible(false);
         this.cbVisible.setVisible(false);
         this.labelMath.setVisible(false);
         this.labelBIGSWR.setVisible(false);
      }

      this.setVisible(false);
      TraceHelper.exit(this, "VNAMarker");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      if (e.getSource() == this.labelMath) {
         this.doClickOnMathSymbol();
      } else if (e.getSource() == this.labelBIGSWR) {
         this.doClickOnBigSWRSymbol();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
   }

   public void clearFields() {
      this.txtFRQ.setText("");
      this.txtLoss.setText("");
      this.txtPhase.setText("");
      this.txtZ.setText("");
      this.txtR.setText("");
      this.txtX.setText("");
      this.txtTheta.setText("");
      this.txtSwrGrpDelay.setText("");
      this.setVisible(false);
   }

   protected void copyMarkerData2Clipboard(MouseEvent e) {
      TraceHelper.entry(this, "copyMarkerData2Clipboard");
      String rc = new String();
      String[] values;
      if (e.getButton() == 3) {
         values = new String[]{VNAMessages.getString("Marker.Frequency"), VNAMessages.getString("Marker.RL"), VNAMessages.getString("Marker.TL"), VNAMessages.getString("Marker.Phase"), VNAMessages.getString("Marker.Z"), VNAMessages.getString("Marker.R"), VNAMessages.getString("Marker.X"), VNAMessages.getString("Marker.SWR"), VNAMessages.getString("Marker.Theta"), VNAMessages.getString("Marker.Magnitude")};
         rc = StringHelper.array2String(values, "\t");
         rc = rc + GlobalSymbols.LINE_SEPARATOR;
      }

      if (e.getButton() == 1 || e.getButton() == 3) {
         values = new String[]{VNAFormatFactory.getFrequencyCalibrationFormat().format(this.getSample().getFrequency()), VNAFormatFactory.getReflectionLossFormat().format(this.getSample().getReflectionLoss()), VNAFormatFactory.getReflectionLossFormat().format(this.getSample().getTransmissionLoss()), VNAFormatFactory.getPhaseFormat().format(this.getSample().getReflectionPhase()), VNAFormatFactory.getZFormat().format(this.getSample().getZ()), VNAFormatFactory.getRsFormat().format(this.getSample().getR()), VNAFormatFactory.getXsFormat().format(this.getSample().getX()), VNAFormatFactory.getSwrFormat().format(this.getSample().getSWR()), VNAFormatFactory.getThetaFormat().format(this.getSample().getTheta()), VNAFormatFactory.getMagFormat().format(this.getSample().getMag())};
         rc = rc + StringHelper.array2String(values, "\t");
         StringSelection str = new StringSelection(rc);
         Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
         cb.setContents(str, this);
      }

      TraceHelper.exit(this, "copyMarkerData2Clipboard");
   }

   public void doClickOnBigSWRSymbol() {
      TraceHelper.entry(this, "doClickOnBigSWRSymbol");
      if (this.bigSWRDialog == null) {
         this.bigSWRDialog = new VNATuneDialog(this);
         this.labelBIGSWR.setSelected(true);
      } else {
         this.bigSWRDialog.setVisible(false);
         this.bigSWRDialog.dispose();
         this.bigSWRDialog = null;
         this.labelBIGSWR.setSelected(false);
      }

      TraceHelper.exit(this, "doClickOnBigSWRSymbol");
   }

   public void doClickOnMathSymbol() {
      TraceHelper.entry(this, "doClickOnMathSymbol");
      if (this.mathDialog == null) {
         this.mathDialog = new VNAMarkerMathDialog(this);
         this.mathDialog.doDialogShow();
         this.mathDialog.update();
         this.labelMath.setSelected(true);
      } else {
         this.mathDialog.setVisible(false);
         this.mathDialog.dispose();
         this.mathDialog = null;
         this.labelMath.setSelected(false);
      }

      TraceHelper.exit(this, "doClickOnMathSymbol");
   }

   public int getDiagramX() {
      return this.diagramX;
   }

   public long getFrequency() {
      return this.sample.getFrequency();
   }

   public Color getMarkerColor() {
      return this.markerColor;
   }

   public String getName() {
      return this.name;
   }

   public VNACalibratedSample getSample() {
      return this.sample;
   }

   public String getShortName() {
      return this.shortName;
   }

   public VNAMarkerTextField getTxtFrequency() {
      return this.txtFRQ;
   }

   public VNAMarkerTextField getTxtFRQ() {
      return this.txtFRQ;
   }

   public VNAMarkerTextField getTxtLoss() {
      return this.txtLoss;
   }

   public VNAMarkerTextField getTxtPhase() {
      return this.txtPhase;
   }

   public VNAMarkerTextField getTxtR() {
      return this.txtR;
   }

   public VNAMarkerTextField getTxtRs() {
      return this.txtR;
   }

   public VNAMarkerTextField getTxtSwrGrpDelay() {
      return this.txtSwrGrpDelay;
   }

   public VNAMarkerTextField getTxtTheta() {
      return this.txtTheta;
   }

   public VNAMarkerTextField getTxtX() {
      return this.txtX;
   }

   public VNAMarkerTextField getTxtXsAbsolute() {
      return this.txtX;
   }

   public VNAMarkerTextField getTxtZ() {
      return this.txtZ;
   }

   public VNAMarkerTextField getTxtZAbsolute() {
      return this.txtZ;
   }

   public boolean isMyMouseEvent(MouseEvent e) {
      boolean rc = false;
      if (this.eventEvaluator != null) {
         rc = this.eventEvaluator.isMyMouseEvent(e);
      }

      return rc;
   }

   public boolean isMyMouseWheelEvent(MouseWheelEvent e) {
      boolean rc = false;
      if (this.eventEvaluator != null) {
         rc = this.eventEvaluator.isMyMouseWheelEvent(e);
      }

      return rc;
   }

   public boolean isVisible() {
      return this.iAmVisible;
   }

   public void lostOwnership(Clipboard clipboard, Transferable contents) {
   }

   public void mouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "mouseClicked");
      if (!this.isVisible()) {
         Toolkit.getDefaultToolkit().beep();
      } else {
         if (e.getSource() == this.lblName) {
            this.copyMarkerData2Clipboard(e);
         } else if (e.getSource() == this.txtPhase) {
            if (this.txtPhase.toggleSearchMode()) {
               this.txtSwrGrpDelay.clearSearchMode();
               this.txtLoss.clearSearchMode();
            }

            this.moveMarkerToData(this.datapool.getCalibratedData());
            this.mainFrame.getDiagramPanel().getImagePanel().repaint();
         } else if (e.getSource() == this.txtSwrGrpDelay) {
            if (this.txtSwrGrpDelay.toggleSearchMode()) {
               this.txtPhase.clearSearchMode();
               this.txtLoss.clearSearchMode();
            }

            this.moveMarkerToData(this.datapool.getCalibratedData());
            this.mainFrame.getDiagramPanel().getImagePanel().repaint();
         } else if (e.getSource() == this.txtLoss) {
            if (this.txtLoss.toggleSearchMode()) {
               this.txtPhase.clearSearchMode();
               this.txtSwrGrpDelay.clearSearchMode();
            }

            this.moveMarkerToData(this.datapool.getCalibratedData());
            this.mainFrame.getDiagramPanel().getImagePanel().repaint();
         }

         TraceHelper.exit(this, "mouseClicked");
      }
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      if (this.isVisible()) {
         int delta = 0;
         if (e.getWheelRotation() < 0) {
            delta = -1;
         } else if (e.getWheelRotation() > 0) {
            delta = 1;
         }

         VNACalibratedSample sample = null;
         sample = this.mainFrame.getDiagramPanel().getImagePanel().getSampleAtMousePosition(this.getSample().getDiagramX() + delta);
         if (sample != null) {
            this.update(sample);
         } else {
            Toolkit.getDefaultToolkit().beep();
         }

         this.mainFrame.getDiagramPanel().getImagePanel().repaint();
      }

   }

   public void moveMarkerToData(VNACalibratedSampleBlock data) {
      VNACalibratedSample foundSample = null;
      int idx = -1;
      if (this.txtLoss.getMarkerSearchMode().isMaximum()) {
         if (this.datapool.getScanMode().isReflectionMode()) {
            idx = data.getMmRL().getMaxIndex();
         } else {
            idx = data.getMmTL().getMaxIndex();
         }
      } else if (this.txtLoss.getMarkerSearchMode().isMinimum()) {
         if (this.datapool.getScanMode().isReflectionMode()) {
            idx = data.getMmRL().getMinIndex();
         } else {
            idx = data.getMmTL().getMinIndex();
         }
      } else if (this.txtPhase.getMarkerSearchMode().isMaximum()) {
         if (this.datapool.getScanMode().isReflectionMode()) {
            idx = data.getMmRP().getMaxIndex();
         } else {
            idx = data.getMmTP().getMaxIndex();
         }
      } else if (this.txtPhase.getMarkerSearchMode().isMinimum()) {
         if (this.datapool.getScanMode().isReflectionMode()) {
            idx = data.getMmRP().getMinIndex();
         } else {
            idx = data.getMmTP().getMinIndex();
         }
      } else if (this.txtSwrGrpDelay.getMarkerSearchMode().isMaximum()) {
         if (this.datapool.getScanMode().isReflectionMode()) {
            idx = data.getMmSWR().getMaxIndex();
         } else {
            idx = data.getMmGRPDLY().getMaxIndex();
         }
      } else if (this.txtSwrGrpDelay.getMarkerSearchMode().isMinimum()) {
         if (this.datapool.getScanMode().isReflectionMode()) {
            idx = data.getMmSWR().getMinIndex();
         } else {
            idx = data.getMmGRPDLY().getMinIndex();
         }
      }

      if (idx != -1) {
         foundSample = data.getCalibratedSamples()[idx];
         foundSample.setDiagramX(idx);
         this.update(foundSample);
      }

   }

   public void moveMarkerToFrequency(long targetFrq) {
      TraceHelper.entry(this, "moveMarkerToFrequency");
      VNACalibratedSampleBlock cd = VNADataPool.getSingleton().getCalibratedData();
      if (cd != null) {
         VNACalibratedSample[] var7;
         int var6 = (var7 = cd.getCalibratedSamples()).length;

         for(int var5 = 0; var5 < var6; ++var5) {
            VNACalibratedSample cs = var7[var5];
            if (cs.getFrequency() >= targetFrq) {
               this.update(cs);
               break;
            }
         }
      }

      TraceHelper.exit(this, "moveMarkerToFrequency");
   }

   public void setDiagramX(int diagramX) {
      this.diagramX = diagramX;
   }

   public void setEventEvaluator(IMarkerEventEvaluator eventEvaluator) {
      this.eventEvaluator = eventEvaluator;
   }

   public void setMarkerColor(Color markerColor) {
      this.markerColor = markerColor;
   }

   public void setVisible(boolean v) {
      this.iAmVisible = v;
      this.cbVisible.setSelected(v);
      this.cbVisible.setEnabled(v);
      this.labelMath.setEnabled(v);
      this.labelBIGSWR.setEnabled(v);
   }

   public void update(VNACalibratedSample s) {
      this.sample = s;
      if (s != null) {
         if (!this.isVisible()) {
            this.setVisible(true);
         }

         this.setDiagramX(s.getDiagramX());
         this.txtFRQ.setText(VNAFormatFactory.formatFrequency(s.getFrequency()));
         if (this.datapool.getScanMode().isReflectionMode()) {
            this.txtLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(s.getReflectionLoss()));
            this.txtPhase.setText(VNAFormatFactory.getPhaseFormat().format(s.getReflectionPhase()));
            this.txtSwrGrpDelay.setText(VNAFormatFactory.getSwrFormat().format(s.getSWR()) + ":1");
         } else if (this.datapool.getScanMode().isTransmissionMode()) {
            this.txtLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(s.getTransmissionLoss()));
            this.txtPhase.setText(VNAFormatFactory.getPhaseFormat().format(s.getTransmissionPhase()));
            this.txtSwrGrpDelay.setText(VNAFormatFactory.getGroupDelayFormat().format(s.getGroupDelay()));
         }

         this.txtTheta.setText(VNAFormatFactory.getThetaFormat().format(s.getTheta()));
         this.txtZ.setText(VNAFormatFactory.getZFormat().format(s.getZ()));
         this.txtR.setText(VNAFormatFactory.getRsFormat().format(s.getR()));
         this.txtX.setText(VNAFormatFactory.getXsFormat().format(s.getX()));
         if (this.mathDialog != null) {
            this.mathDialog.update();
         }

         if (this.bigSWRDialog != null) {
            this.bigSWRDialog.update(s);
         }
      } else {
         this.clearFields();
      }

   }
}
