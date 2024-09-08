package krause.vna.gui.scale;

import java.util.HashMap;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.scale.values.VNAGroupDelayScale;
import krause.vna.gui.scale.values.VNARSSScale;
import krause.vna.gui.scale.values.VNAReturnLossScale;
import krause.vna.gui.scale.values.VNAReturnPhaseScale;
import krause.vna.gui.scale.values.VNARsScale;
import krause.vna.gui.scale.values.VNASWRScale;
import krause.vna.gui.scale.values.VNAThetaScale;
import krause.vna.gui.scale.values.VNATransmissionLossScale;
import krause.vna.gui.scale.values.VNATransmissionPhaseScale;
import krause.vna.gui.scale.values.VNAXsScale;
import krause.vna.gui.scale.values.VNAZAbsScale;

public interface VNAScaleSymbols {
   int NUM_SCALE_TICKS = 10;
   HashMap<VNAScaleSymbols.SCALE_TYPE, VNAGenericScale> MAP_SCALE_TYPES = new HashMap<VNAScaleSymbols.SCALE_TYPE, VNAGenericScale>() {
      {
         TraceHelper.entry(this, "static init");
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_NONE, new VNANoneScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS, new VNAReturnLossScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS, new VNATransmissionLossScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE, new VNAReturnPhaseScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE, new VNATransmissionPhaseScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RSS, new VNARSSScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RS, new VNARsScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_SWR, new VNASWRScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_XS, new VNAXsScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_RS, new VNARsScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS, new VNAZAbsScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_THETA, new VNAThetaScale());
         this.put(VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY, new VNAGroupDelayScale());
         TraceHelper.exit(this, "static init");
      }
   };

   public static enum SCALE_TYPE {
      SCALE_NONE,
      SCALE_TRANSMISSIONLOSS,
      SCALE_RETURNLOSS,
      SCALE_Z_ABS,
      SCALE_RETURNPHASE,
      SCALE_TRANSMISSIONPHASE,
      SCALE_RS,
      SCALE_XS,
      SCALE_RSS,
      SCALE_SWR,
      SCALE_THETA,
      SCALE_GRPDLY;
   }
}
