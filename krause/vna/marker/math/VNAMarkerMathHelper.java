package krause.vna.marker.math;

import krause.util.ras.logging.TraceHelper;

public class VNAMarkerMathHelper {
   private static final double TWO_PI = 6.283185307179586D;

   private VNAMarkerMathHelper() {
   }

   public static VNAMarkerMathResult execute(VNAMarkerMathInput input) {
      VNAMarkerMathResult rc = new VNAMarkerMathResult(input);
      TraceHelper.entry(VNAMarkerMathResult.class, "execute");
      if (input.getHighFrequency() - input.getLowFrequency() > 0L) {
         rc.setBandWidth(input.getHighFrequency() - input.getLowFrequency());
         rc.setQ((double)input.getCenterFrequency() / (double)rc.getBandWidth());
      }

      double kreisFrequenz = 6.283185307179586D * (double)input.getCenterFrequency();
      rc.setSerialCapacity(1.0D / (kreisFrequenz * input.getZ()));
      rc.setSerialInductance(input.getZ() / kreisFrequenz);
      rc.setRp(input.getRs() * (1.0D + input.getXs() / input.getRs() * (input.getXs() / input.getRs())));
      rc.setXp(rc.getRp() * input.getRs() / input.getXs());
      TraceHelper.exit(VNAMarkerMathResult.class, "execute");
      return rc;
   }
}
