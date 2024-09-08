package krause.vna.gui.panels.marker;

import javax.swing.JPanel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAApplicationState;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAScanMode;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.resources.VNAMessages;

public class VNAMarkerHeader implements VNAApplicationStateObserver {
   private final VNADataPool datapool = VNADataPool.getSingleton();
   private VNAMarkerLabel lblLoss;
   private VNAMarkerLabel lblPhase;
   private VNAMarkerLabel lblSwrGrpDelay;

   public VNAMarkerHeader(JPanel panel, int line) {
      TraceHelper.entry(this, "VNAMarkerHeader");
      panel.add(new VNAMarkerLabel(""), "");
      panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.Frequency")), "");
      panel.add(this.lblLoss = new VNAMarkerLabel(VNAMessages.getString("Marker.RL")), "");
      panel.add(this.lblPhase = new VNAMarkerLabel(VNAMessages.getString("Marker.PhaseRL")), "");
      panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.Z")), "");
      panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.R")), "");
      panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.X")), "");
      panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.Theta")), "");
      panel.add(this.lblSwrGrpDelay = new VNAMarkerLabel(VNAMessages.getString("Marker.SWR")), "wrap");
      TraceHelper.exit(this, "VNAMarkerHeader");
   }

   public void changeState(VNAApplicationState.INNERSTATE oldState, VNAApplicationState.INNERSTATE newState) {
      if (oldState == VNAApplicationState.INNERSTATE.CALIBRATED && newState == VNAApplicationState.INNERSTATE.CALIBRATED) {
         if (this.datapool.getScanMode() == VNAScanMode.MODE_REFLECTION) {
            this.lblLoss.setText(VNAMessages.getString("Marker.RL"));
            this.lblPhase.setText(VNAMessages.getString("Marker.PhaseRL"));
            this.lblSwrGrpDelay.setText(VNAMessages.getString("Marker.SWR"));
         } else if (this.datapool.getScanMode() == VNAScanMode.MODE_TRANSMISSION) {
            this.lblLoss.setText(VNAMessages.getString("Marker.TL"));
            this.lblPhase.setText(VNAMessages.getString("Marker.PhaseTL"));
            this.lblSwrGrpDelay.setText(VNAMessages.getString("Marker.GrpDelay"));
         }
      }

   }
}
