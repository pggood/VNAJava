package krause.vna.gui.scale;

import javax.swing.JTextField;

public class VNAScaleTextField extends JTextField {
   private transient VNAGenericScale scale = null;
   private VNAScaleTextField minField = null;
   private VNAScaleTextField maxField = null;

   public VNAGenericScale getScale() {
      return this.scale;
   }

   public void setScale(VNAGenericScale scale) {
      this.scale = scale;
   }

   public VNAScaleTextField(String format, VNAGenericScale aScale) {
      super(format);
      this.setScale(aScale);
   }

   public VNAScaleTextField getMinField() {
      return this.minField;
   }

   public void setMinField(VNAScaleTextField minField) {
      this.minField = minField;
   }

   public VNAScaleTextField getMaxField() {
      return this.maxField;
   }

   public void setMaxField(VNAScaleTextField maxField) {
      this.maxField = maxField;
   }
}
