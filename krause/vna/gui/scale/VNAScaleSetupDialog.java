package krause.vna.gui.scale;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAScaleSetupDialog extends KrauseDialog implements ActionListener {
   private final JPanel contentPanel;
   private VNAConfig config = VNAConfig.getSingleton();
   private JButton btnSave;
   private JButton btnCancel;
   private transient ArrayList<VNAGenericScale> lstScales = new ArrayList();

   public VNAScaleSetupDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      String methodName = "VNAScaleSetupDialog";
      TraceHelper.entry(this, "VNAScaleSetupDialog");
      this.setTitle(VNAMessages.getString("VNAScaleSetupDialog.Title"));
      this.setDefaultCloseOperation(0);
      this.setModal(true);
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNAScaleSetupDialog");
      this.setMinimumSize(new Dimension(470, 210));
      this.setPreferredSize(new Dimension(470, 260));
      this.contentPanel = new JPanel();
      this.contentPanel.setLayout(new MigLayout("", "[grow,fill][30%][][30%][]", "[grow,fill][][]"));
      this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.getContentPane().add(this.contentPanel);
      JLabel lbl = new JLabel(VNAMessages.getString("VNAScaleSetupDialog.Scale"));
      this.contentPanel.add(lbl, "span 5, wrap");
      lbl = new JLabel();
      this.contentPanel.add(lbl, "");
      lbl = new JLabel(VNAMessages.getString("VNAScaleSetupDialog.Min"));
      this.contentPanel.add(lbl, "span 2");
      lbl = new JLabel(VNAMessages.getString("VNAScaleSetupDialog.Max"));
      this.contentPanel.add(lbl, "span 2, wrap");
      this.lstScales.add((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RS));
      this.lstScales.add((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_XS));
      this.lstScales.add((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS));
      this.lstScales.add((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_RSS));
      this.lstScales.add((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_SWR));
      this.lstScales.add((VNAGenericScale)VNAScaleSymbols.MAP_SCALE_TYPES.get(VNAScaleSymbols.SCALE_TYPE.SCALE_THETA));
      Iterator var5 = this.lstScales.iterator();

      while(var5.hasNext()) {
         VNAGenericScale aScale = (VNAGenericScale)var5.next();
         this.createScaleLine(aScale);
      }

      this.contentPanel.add(new HelpButton(this, "VNAScaleSetupDialog"), "grow");
      this.btnSave = SwingUtil.createJButton("Button.Save", this);
      this.btnCancel = SwingUtil.createJButton("Button.Cancel", this);
      this.contentPanel.add(this.btnCancel, "span 2, grow");
      this.btnSave.setActionCommand("OK");
      this.contentPanel.add(this.btnSave, "span 2, grow");
      this.getRootPane().setDefaultButton(this.btnSave);
      this.doDialogInit();
      TraceHelper.exit(this, "VNAScaleSetupDialog");
   }

   private void createScaleLine(VNAGenericScale aScale) {
      NumberFormat fmt = aScale.getFormat();
      JLabel lbl = new JLabel(aScale.getName());
      this.contentPanel.add(lbl, "");
      VNAScaleTextField txtMin = new VNAScaleTextField(fmt.format(aScale.getDefaultMinValue()), aScale);
      txtMin.setHorizontalAlignment(4);
      txtMin.setBorder(new LineBorder(new Color(171, 173, 179)));
      txtMin.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent arg0) {
            VNAScaleTextField fld = (VNAScaleTextField)arg0.getSource();
            VNAGenericScale s = fld.getScale();
            String txt = fld.getText();
            NumberFormat ft = s.getFormat();

            try {
               Number val = ft.parse(txt);
               double d = val.doubleValue();
               if (d < s.getAbsolutMinValue()) {
                  d = s.getAbsolutMinValue();
               }

               s.setDefaultMinValue(d);
            } catch (ParseException var9) {
            }

            fld.setText(ft.format(s.getDefaultMinValue()));
         }

         public void focusGained(FocusEvent arg0) {
         }
      });
      this.contentPanel.add(txtMin, "grow");
      lbl = new JLabel("(" + fmt.format(aScale.getAbsolutMinValue()) + ")");
      this.contentPanel.add(lbl, "right");
      VNAScaleTextField txtMax = new VNAScaleTextField(fmt.format(aScale.getDefaultMaxValue()), aScale);
      txtMax.setHorizontalAlignment(4);
      txtMax.setBorder(new LineBorder(new Color(171, 173, 179)));
      txtMax.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent arg0) {
            VNAScaleTextField fld = (VNAScaleTextField)arg0.getSource();
            VNAGenericScale s = fld.getScale();
            String txt = fld.getText();
            NumberFormat ft = s.getFormat();

            try {
               Number val = ft.parse(txt);
               double d = val.doubleValue();
               if (d > s.getAbsolutMaxValue()) {
                  d = s.getAbsolutMaxValue();
               }

               s.setDefaultMaxValue(d);
            } catch (ParseException var9) {
            }

            fld.setText(ft.format(s.getDefaultMaxValue()));
         }

         public void focusGained(FocusEvent arg0) {
         }
      });
      this.contentPanel.add(txtMax, "grow");
      lbl = new JLabel("(" + fmt.format(aScale.getAbsolutMaxValue()) + ")");
      this.contentPanel.add(lbl, "right, wrap");
      txtMax.setMinField(txtMin);
      txtMin.setMinField(txtMax);
   }

   protected void doDialogInit() {
      this.addEscapeKey();
      this.doDialogShow();
   }

   protected void doSave() {
      TraceHelper.entry(this, "doSave");
      Iterator var2 = this.lstScales.iterator();

      while(var2.hasNext()) {
         VNAGenericScale aScale = (VNAGenericScale)var2.next();
         this.config.putDouble(aScale.getClass().getSimpleName() + ".defaultMinValue", aScale.getDefaultMinValue());
         this.config.putDouble(aScale.getClass().getSimpleName() + ".defaultMaxValue", aScale.getDefaultMaxValue());
      }

      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doSave");
   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      if (e.getSource() == this.btnCancel) {
         this.doDialogCancel();
      } else if (e.getSource() == this.btnSave) {
         this.doSave();
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }
}
