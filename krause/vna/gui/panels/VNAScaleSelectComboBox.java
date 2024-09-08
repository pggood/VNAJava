package krause.vna.gui.panels;

import javax.swing.JComboBox;
import krause.vna.gui.scale.VNAScaleSymbols;

public class VNAScaleSelectComboBox extends JComboBox {
   public VNAScaleSelectComboBox() {
      this.setEditable(false);
      this.setMaximumRowCount(15);
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_NONE));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_SWR));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RSS));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RS));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_THETA));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_XS));
      this.addItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS));
   }
}
