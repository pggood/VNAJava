package krause.vna.gui.panels.marker;

public class VNAMarkerSearchMode {
   private VNAMarkerSearchMode.MARKERFIELDTYPE field;
   private boolean minimum;
   private boolean maximum;

   public boolean isMinimum() {
      return this.minimum;
   }

   public VNAMarkerSearchMode(VNAMarkerSearchMode.MARKERFIELDTYPE field) {
      this.field = VNAMarkerSearchMode.MARKERFIELDTYPE.RL;
      this.field = field;
      this.minimum = false;
      this.maximum = false;
   }

   public void setMinimum(boolean minimum) {
      this.minimum = minimum;
   }

   public VNAMarkerSearchMode.MARKERFIELDTYPE getField() {
      return this.field;
   }

   public void setField(VNAMarkerSearchMode.MARKERFIELDTYPE field) {
      this.field = field;
   }

   public boolean isMaximum() {
      return this.maximum;
   }

   public void setMaximum(boolean maximum) {
      this.maximum = maximum;
   }

   public boolean toggle() {
      if (this.maximum) {
         this.maximum = false;
         this.minimum = false;
         return false;
      } else if (this.minimum) {
         this.maximum = true;
         this.minimum = false;
         return true;
      } else {
         this.maximum = false;
         this.minimum = true;
         return true;
      }
   }

   public void clearSearchMode() {
      this.maximum = false;
      this.minimum = false;
   }

   public static enum MARKERFIELDTYPE {
      RL,
      TL,
      PHASE,
      SWR,
      Z,
      R,
      X;
   }
}
