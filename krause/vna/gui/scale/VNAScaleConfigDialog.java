package krause.vna.gui.scale;

import java.awt.Frame;
import java.awt.Window;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.RangeCheckedTextField;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAScaleConfigDialog extends KrauseDialog {
   private VNAMeasurementScale scale;
   private boolean exitWithOK = false;
   private JButton btnOK;
   private JButton btCancel;
   private JButton btnReset;
   private RangeCheckedTextField txtCurrMin;
   private RangeCheckedTextField txtCurrMax;
   private RangeCheckedTextField txtGuideLineLine;

   public VNAScaleConfigDialog(Frame aFrame, VNAMeasurementScale pScale) {
      super((Window)aFrame, true);
      this.scale = pScale;
      this.setTitle(VNAMessages.getString("VNAScaleConfigDialog.title") + "-" + this.scale.getScale().getName());
      this.setResizable(true);
      this.setDefaultCloseOperation(2);
      JPanel panel = new JPanel();
      this.getContentPane().add(panel, "Center");
      panel.setLayout(new MigLayout("", "[][][grow]", ""));
      NumberFormat fmt = NumberFormat.getNumberInstance();
      fmt.setGroupingUsed(false);
      fmt.setMaximumFractionDigits(3);
      fmt.setMinimumFractionDigits(0);
      fmt.setMaximumIntegerDigits(10);
      fmt.setMinimumIntegerDigits(1);
      panel.add(new JLabel(VNAMessages.getString("VNAScaleConfigDialog.max")), "span 2");
      this.txtCurrMax = new RangeCheckedTextField(fmt);
      this.txtCurrMax.setColumns(7);
      panel.add(this.txtCurrMax, "wrap");
      panel.add(new JLabel(VNAMessages.getString("VNAScaleConfigDialog.min")), "span 2");
      this.txtCurrMin = new RangeCheckedTextField(fmt);
      this.txtCurrMin.setColumns(7);
      panel.add(this.txtCurrMin, "wrap");
      panel.add(new JLabel(VNAMessages.getString("VNAScaleConfigDialog.assistive")), "span 2");
      this.txtGuideLineLine = new RangeCheckedTextField(fmt, true);
      this.txtGuideLineLine.setColumns(7);
      panel.add(this.txtGuideLineLine, "wrap");
      this.btnReset = SwingUtil.createJButton("Button.RESET", (e) -> {
         this.txtCurrMax.setValue(this.txtCurrMax.getUpperLimit());
         this.txtCurrMin.setValue(this.txtCurrMin.getLowerLimit());
         this.txtGuideLineLine.setValue((Double)null);
      });
      panel.add(this.btnReset, "");
      this.btCancel = SwingUtil.createJButton("Button.Cancel", (e) -> {
         this.doDialogCancel();
      });
      panel.add(this.btCancel, "");
      this.btnOK = SwingUtil.createJButton("Button.OK", (e) -> {
         this.doOK();
      });
      panel.add(this.btnOK, "grow");
      this.doDialogInit();
   }

   protected void doOK() {
      TraceHelper.entry(this, "doOK");
      this.scale.getScale().setCurrentMaxValue(Math.max(this.txtCurrMax.getValue(), this.txtCurrMin.getValue()));
      this.scale.getScale().setCurrentMinValue(Math.min(this.txtCurrMax.getValue(), this.txtCurrMin.getValue()));
      this.scale.getScale().setGuideLineValue(this.txtGuideLineLine.getValue());
      this.exitWithOK = true;
      this.dispose();
      TraceHelper.exit(this, "doOK");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doDialogCancel");
      this.dispose();
      TraceHelper.exit(this, "doDialogCancel");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doDialogInit");
      this.txtCurrMax.setUpperLimit(this.scale.getScale().getDefaultMaxValue());
      this.txtCurrMax.setLowerLimit(this.scale.getScale().getDefaultMinValue());
      this.txtCurrMax.setValue(this.scale.getScale().getCurrentMaxValue());
      this.txtCurrMin.setUpperLimit(this.scale.getScale().getDefaultMaxValue());
      this.txtCurrMin.setLowerLimit(this.scale.getScale().getDefaultMinValue());
      this.txtCurrMin.setValue(this.scale.getScale().getCurrentMinValue());
      this.txtGuideLineLine.setUpperLimit(this.scale.getScale().getDefaultMaxValue());
      this.txtGuideLineLine.setLowerLimit(this.scale.getScale().getDefaultMinValue());
      this.txtGuideLineLine.setValue(this.scale.getScale().getGuideLineValue());
      this.addEscapeKey();
      this.showCentered(this.getOwner());
      TraceHelper.exit(this, "doDialogInit");
   }

   public boolean isExitWithOK() {
      return this.exitWithOK;
   }
}
