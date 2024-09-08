package krause.vna.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;

public class VNATemperatureButton extends JButton implements ActionListener {
   private VNAMainFrame mainFrame;
   private static final int MAX_LIST = 1000;
   private double[] tempList = new double[1000];
   private boolean firstTemp = true;

   public VNATemperatureButton(VNAMainFrame pMainFrame, String pResPrefix, ActionListener pListener) {
      this.mainFrame = pMainFrame;
      String command = VNAMessages.getString(pResPrefix + ".Command");
      String tooltip = VNAMessages.getString(pResPrefix + ".Tooltip");
      this.setActionCommand(command);
      this.setToolTipText(tooltip);
      if (pListener != null) {
         this.addActionListener(pListener);
      }

      this.addActionListener(this);

      for(int i = 0; i < 1000; ++i) {
         this.tempList[i] = 0.0D;
      }

      this.setTemperature((Double)null);
   }

   public void setTemperature(Double temp) {
      TraceHelper.entry(this, "setTemperature");
      if (temp != null) {
         this.setText(VNAFormatFactory.getTemperatureFormat().format(temp) + "°C");
         int i;
         if (this.firstTemp) {
            for(i = 0; i < 1000; ++i) {
               this.tempList[i] = temp;
            }

            this.firstTemp = false;
         } else {
            for(i = 1; i < 1000; ++i) {
               this.tempList[i - 1] = this.tempList[i];
            }

            this.tempList[999] = temp;
         }
      } else {
         this.setToolTipText("");
      }

      TraceHelper.exit(this, "setTemperature");
   }

   public void actionPerformed(ActionEvent arg0) {
      new VNATemperatureDetailsDialog(this.mainFrame.getJFrame(), this.tempList);
   }
}
