package krause.vna.gui.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class VNAGridBagConstraints extends GridBagConstraints {
   private static Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);

   public VNAGridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
      super(gridx, gridy, gridwidth, gridheight, weightx, weighty, 23, 0, DEFAULT_INSETS, 0, 0);
   }
}
