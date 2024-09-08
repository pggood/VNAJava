package krause.vna.gui.panels.marker;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JPanel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.VNAScaleSelectPanel;
import net.miginfocom.swing.MigLayout;

public class VNAMarkerPanel extends JPanel implements ActionListener, VNAApplicationStateObserver, WindowListener {
   private final VNAConfig config = VNAConfig.getSingleton();
   private final VNADataPool datapool = VNADataPool.getSingleton();
   private VNAMainFrame mainFrame;
   public static final int MARKER_0 = 0;
   public static final int MARKER_1 = 1;
   public static final int MARKER_2 = 2;
   public static final int MARKER_3 = 3;
   public static final int NUM_MARKERS = 4;
   private VNAMarker[] markers = new VNAMarker[4];
   private VNAMarker mouseMarker;
   private VNAMarker deltaMarker;
   private VNAMarkerHeader markerHeader;

   public VNAMarkerPanel(VNAMainFrame pMainFrame) {
      TraceHelper.entry(this, "VNAMarkerPanel");
      this.setFont(new Font("Tahoma", 0, 8));
      this.mainFrame = pMainFrame;
      this.setLayout(new MigLayout("", "[][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][][][]", "[]"));
      int line = 2;
      //int line = line + 1;
      this.markerHeader = new VNAMarkerHeader(this, line);
      ++line;
      this.mouseMarker = new VNAMarker("Maus", this.mainFrame, this, this, line, 1, Color.BLACK);
      ++line;
      this.markers[0] = new VNAMarker(0, this.mainFrame, this, this, line, 1, this.config.getColorMarker(0));
      ++line;
      this.deltaMarker = new VNAMarker("Delta", this.mainFrame, this, this, line, 1, Color.BLACK);
      ++line;
      this.markers[1] = new VNAMarker(1, this.mainFrame, this, this, line, 1, this.config.getColorMarker(1));
      ++line;
      this.markers[2] = new VNAMarker(2, this.mainFrame, this, this, line, 1, this.config.getColorMarker(2));
      ++line;
      this.markers[3] = new VNAMarker(3, this.mainFrame, this, this, line, 1, this.config.getColorMarker(3));
      this.markers[0].setEventEvaluator(new Marker0EventEvaluator());
      this.markers[1].setEventEvaluator(new Marker1EventEvaluator());
      this.markers[2].setEventEvaluator(new Marker2EventEvaluator());
      this.markers[3].setEventEvaluator(new Marker3EventEvaluator());
      TraceHelper.exit(this, "VNAMarkerPanel");
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      TraceHelper.entry(this, "actionPerformed", cmd);
      VNAMarker marker = null;

      for(int i = 0; i < 4; ++i) {
         if (this.markers[i].getName().equals(cmd)) {
            marker = this.markers[i];
            break;
         }
      }

      if (marker != null && marker.isVisible()) {
         marker.clearFields();
         this.mainFrame.getDiagramPanel().repaint();
         VNAScaleSelectPanel ssp = this.mainFrame.getDiagramPanel().getScaleSelectPanel();
         if (ssp.getSmithDialog() != null) {
            ssp.getSmithDialog().consumeCalibratedData(this.datapool.getCalibratedData());
         }
      }

      TraceHelper.entry(this, "actionPerformed");
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      VNAMarker[] var6;
      int var5 = (var6 = this.markers).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         VNAMarker oneMarker = var6[var4];
         oneMarker.changeState(oldState, newState);
      }

      this.markerHeader.changeState(oldState, newState);
      this.mouseMarker.changeState(oldState, newState);
      this.deltaMarker.changeState(oldState, newState);
   }

   public void consumeMouseWheelEvent(MouseWheelEvent e) {
      boolean oneMarkerVisible = false;

      for(int i = 0; i < 4; ++i) {
         if (this.markers[i].isMyMouseWheelEvent(e)) {
            oneMarkerVisible = true;
            this.markers[i].mouseWheelMoved(e);
         }
      }

      if (oneMarkerVisible) {
         VNAScaleSelectPanel ssp = this.mainFrame.getDiagramPanel().getScaleSelectPanel();
         if (ssp.getSmithDialog() != null) {
            ssp.getSmithDialog().consumeCalibratedData(this.datapool.getCalibratedData());
         }
      }

   }

   public VNAMainFrame getMainFrame() {
      return this.mainFrame;
   }

   public VNAMarker getMarker(int i) {
      return this.markers[i];
   }

   public VNAMarker getMarkerForMouseEvent(MouseEvent e) {
      VNAMarker rc = null;
      TraceHelper.entry(this, "getMarkerForMouseEvent");

      for(int i = 0; i < 4; ++i) {
         if (this.markers[i].isMyMouseEvent(e)) {
            rc = this.markers[i];
            break;
         }
      }

      TraceHelper.exit(this, "getMarkerForMouseEvent");
      return rc;
   }

   public void setupColors() {
      TraceHelper.entry(this, "setupColors");

      for(int i = 0; i < 4; ++i) {
         this.markers[i].setMarkerColor(this.config.getColorMarker(i));
      }

      TraceHelper.exit(this, "setupColors");
   }

   public void windowActivated(WindowEvent e) {
      TraceHelper.entry(this, "windowActivated");
      TraceHelper.exit(this, "windowActivated");
   }

   public void windowClosed(WindowEvent e) {
      TraceHelper.entry(this, "windowClosed");
      TraceHelper.exit(this, "windowClosed");
   }

   public void windowClosing(WindowEvent e) {
      TraceHelper.entry(this, "windowClosing");
      TraceHelper.exit(this, "windowClosing");
   }

   public void windowDeactivated(WindowEvent e) {
      TraceHelper.entry(this, "windowDeactivated");
      TraceHelper.exit(this, "windowDeactivated");
   }

   public void windowDeiconified(WindowEvent e) {
      TraceHelper.entry(this, "windowDeiconified");
      TraceHelper.exit(this, "windowDeiconified");
   }

   public void windowIconified(WindowEvent e) {
      TraceHelper.entry(this, "windowIconified");
      TraceHelper.exit(this, "windowIconified");
   }

   public void windowOpened(WindowEvent e) {
      TraceHelper.entry(this, "windowOpened");
      TraceHelper.exit(this, "windowOpened");
   }

   public VNAMarker[] getMarkers() {
      return this.markers;
   }

   public VNAMarker getMouseMarker() {
      return this.mouseMarker;
   }

   public VNAMarker getDeltaMarker() {
      return this.deltaMarker;
   }
}
