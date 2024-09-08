package krause.vna.gui.padcalc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAPadCalculatorDialog extends KrauseDialog {
   private JTextField txtR1;
   private JTextField txtR2;
   private JTextField txtR3;
   private JTextField txtR4;
   private JTextField txtR5;
   private JTextField txtAtten;
   private JTextField txtR3_E24;
   private JTextField txtR4_E24;
   private JTextField txtR5_E24;
   private JTextField txtR3_E48;
   private JTextField txtR4_E48;
   private JTextField txtR5_E48;
   private JTextField txtR1_E24;
   private JTextField txtR2_E24;
   private JTextField txtR1_E48;
   private JTextField txtR2_E48;
   private JTextField txtR1_E12;
   private JTextField txtR2_E12;
   private JTextField txtR3_E12;
   private JTextField txtR4_E12;
   private JTextField txtR5_E12;
   private JTextField txtNumRes;
   private JRadioButton rbPi;
   private JRadioButton rbT;
   private JLabel lblImage;
   NumberFormat realNumberFormat = NumberFormat.getNumberInstance();
   NumberFormat intNumberFormat = NumberFormat.getNumberInstance();

   public VNAPadCalculatorDialog(Frame aFrame) {
      super((Window)aFrame, true);
      TraceHelper.entry(this, "VNAPadCalculatorDialog");
      this.setTitle(VNAMessages.getString("VNAPadCalculatorDialog.title"));
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNAPadCalculatorDialog");
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(600, 600));
      this.realNumberFormat.setGroupingUsed(false);
      this.realNumberFormat.setMaximumFractionDigits(2);
      this.realNumberFormat.setMinimumFractionDigits(2);
      this.realNumberFormat.setMaximumIntegerDigits(4);
      this.realNumberFormat.setMinimumIntegerDigits(1);
      this.intNumberFormat.setGroupingUsed(false);
      this.intNumberFormat.setMaximumFractionDigits(0);
      this.intNumberFormat.setMinimumFractionDigits(0);
      this.intNumberFormat.setMaximumIntegerDigits(1);
      this.intNumberFormat.setMinimumIntegerDigits(1);
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.add(new HelpButton(this, "VNAPadCalculatorDialog"));
      this.rbPi = new JRadioButton(VNAMessages.getString("VNAPadCalculatorDialog.piPad"));
      this.rbPi.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNAPadCalculatorDialog.this.doSwitchPadType();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      this.rbT = new JRadioButton(VNAMessages.getString("VNAPadCalculatorDialog.tPad"));
      this.rbT.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            VNAPadCalculatorDialog.this.doSwitchPadType();
            TraceHelper.exit(this, "actionPerformed");
         }
      });
      ButtonGroup bg = new ButtonGroup();
      bg.add(this.rbPi);
      bg.add(this.rbT);
      pnlButtons.add(this.rbPi);
      pnlButtons.add(this.rbT);
      pnlButtons.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.numRes")), "");
      this.txtNumRes = new JTextField("2");
      this.txtNumRes.setColumns(3);
      this.txtNumRes.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
            VNAPadCalculatorDialog.this.doCalculate();
         }

         public void focusGained(FocusEvent e) {
            VNAPadCalculatorDialog.this.txtNumRes.select(0, 99);
         }
      });
      pnlButtons.add(this.txtNumRes, "");
      JButton btOK = SwingUtil.createJButton("Button.Close", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAPadCalculatorDialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(btOK);
      JPanel pnlCenter = new JPanel();
      this.getContentPane().add(pnlCenter, "Center");
      pnlCenter.setLayout(new MigLayout("", "[]", "[][][]"));
      this.lblImage = new JLabel("");
      this.lblImage.setBorder(new BevelBorder(1, (Color)null, (Color)null, (Color)null, (Color)null));
      pnlCenter.add(this.lblImage, "spanx 6, spany 1, center, wrap");
      pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.atten")), "");
      pnlCenter.add(new JLabel("R1"), "");
      pnlCenter.add(new JLabel("R2"), "");
      pnlCenter.add(new JLabel("R3"), "");
      pnlCenter.add(new JLabel("R4"), "");
      pnlCenter.add(new JLabel("R5"), "wrap");
      int colWidth = 7;
      this.txtAtten = new JTextField("6");
      this.txtAtten.setColumns(colWidth);
      this.txtAtten.setHorizontalAlignment(4);
      this.txtAtten.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
            VNAPadCalculatorDialog.this.doCalculate();
         }

         public void focusGained(FocusEvent e) {
            VNAPadCalculatorDialog.this.txtAtten.select(0, 99);
         }
      });
      pnlCenter.add(this.txtAtten, "");
      this.txtR1 = new JTextField("50");
      this.txtR1.setColumns(colWidth);
      this.txtR1.setHorizontalAlignment(4);
      this.txtR1.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
            VNAPadCalculatorDialog.this.doCalculate();
         }

         public void focusGained(FocusEvent e) {
            VNAPadCalculatorDialog.this.txtR1.select(0, 99);
         }
      });
      pnlCenter.add(this.txtR1, "");
      this.txtR2 = new JTextField("50");
      this.txtR2.setColumns(colWidth);
      this.txtR2.setHorizontalAlignment(4);
      this.txtR2.addFocusListener(new FocusListener() {
         public void focusLost(FocusEvent e) {
            VNAPadCalculatorDialog.this.doCalculate();
         }

         public void focusGained(FocusEvent e) {
            VNAPadCalculatorDialog.this.txtR2.select(0, 99);
         }
      });
      pnlCenter.add(this.txtR2, "");
      this.txtR3 = new JTextField();
      this.txtR3.setColumns(colWidth);
      this.txtR3.setEditable(false);
      this.txtR3.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR3, "");
      this.txtR4 = new JTextField();
      this.txtR4.setEditable(false);
      this.txtR4.setColumns(colWidth);
      this.txtR4.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR4, "");
      this.txtR5 = new JTextField();
      this.txtR5.setEditable(false);
      this.txtR5.setColumns(colWidth);
      this.txtR5.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR5, "grow,wrap");
      pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.E12")), "right");
      this.txtR1_E12 = new JTextField();
      this.txtR1_E12.setColumns(colWidth);
      this.txtR1_E12.setEditable(false);
      this.txtR1_E12.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR1_E12, "");
      this.txtR2_E12 = new JTextField();
      this.txtR2_E12.setColumns(colWidth);
      this.txtR2_E12.setEditable(false);
      this.txtR2_E12.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR2_E12, "");
      this.txtR3_E12 = new JTextField();
      this.txtR3_E12.setColumns(colWidth);
      this.txtR3_E12.setEditable(false);
      this.txtR3_E12.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR3_E12, "");
      this.txtR4_E12 = new JTextField();
      this.txtR4_E12.setEditable(false);
      this.txtR4_E12.setColumns(colWidth);
      this.txtR4_E12.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR4_E12, "");
      this.txtR5_E12 = new JTextField();
      this.txtR5_E12.setEditable(false);
      this.txtR5_E12.setColumns(colWidth);
      this.txtR5_E12.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR5_E12, "grow,wrap");
      pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.E24")), "right");
      this.txtR1_E24 = new JTextField();
      this.txtR1_E24.setColumns(colWidth);
      this.txtR1_E24.setEditable(false);
      this.txtR1_E24.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR1_E24, "");
      this.txtR2_E24 = new JTextField();
      this.txtR2_E24.setColumns(colWidth);
      this.txtR2_E24.setEditable(false);
      this.txtR2_E24.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR2_E24, "");
      this.txtR3_E24 = new JTextField();
      this.txtR3_E24.setColumns(colWidth);
      this.txtR3_E24.setEditable(false);
      this.txtR3_E24.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR3_E24, "");
      this.txtR4_E24 = new JTextField();
      this.txtR4_E24.setEditable(false);
      this.txtR4_E24.setColumns(colWidth);
      this.txtR4_E24.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR4_E24, "");
      this.txtR5_E24 = new JTextField();
      this.txtR5_E24.setEditable(false);
      this.txtR5_E24.setColumns(colWidth);
      this.txtR5_E24.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR5_E24, "grow,wrap");
      pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.E48")), "right");
      this.txtR1_E48 = new JTextField();
      this.txtR1_E48.setColumns(colWidth);
      this.txtR1_E48.setEditable(false);
      this.txtR1_E48.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR1_E48, "");
      this.txtR2_E48 = new JTextField();
      this.txtR2_E48.setColumns(colWidth);
      this.txtR2_E48.setEditable(false);
      this.txtR2_E48.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR2_E48, "");
      this.txtR3_E48 = new JTextField();
      this.txtR3_E48.setColumns(colWidth);
      this.txtR3_E48.setEditable(false);
      this.txtR3_E48.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR3_E48, "");
      this.txtR4_E48 = new JTextField();
      this.txtR4_E48.setEditable(false);
      this.txtR4_E48.setColumns(colWidth);
      this.txtR4_E48.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR4_E48, "");
      this.txtR5_E48 = new JTextField();
      this.txtR5_E48.setEditable(false);
      this.txtR5_E48.setColumns(colWidth);
      this.txtR5_E48.setHorizontalAlignment(4);
      pnlCenter.add(this.txtR5_E48, "grow,wrap");
      pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.hint1")), "right");
      JLabel lblHint = new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.hint2"));
      lblHint.setForeground(Color.BLUE);
      pnlCenter.add(lblHint, "center,span 5, grow, wrap");
      this.doDialogInit();
      TraceHelper.exit(this, "VNAPadCalculatorDialog");
   }

   protected void doSwitchPadType() {
      TraceHelper.entry(this, "doSwitchPadType");
      if (this.rbPi.isSelected()) {
         this.lblImage.setIcon(new ImageIcon(VNAPadCalculatorDialog.class.getResource("/images/PiGlied.gif")));
      } else {
         this.lblImage.setIcon(new ImageIcon(VNAPadCalculatorDialog.class.getResource("/images/TGlied.gif")));
      }

      this.doCalculate();
      TraceHelper.exit(this, "doSwitchPadType");
   }

   protected void doCalculate() {
      TraceHelper.entry(this, "doCalculate");

      double r1;
      double r2;
      double atten;
      int numResistors;
      try {
         r1 = this.realNumberFormat.parse(this.txtR1.getText()).doubleValue();
         r2 = this.realNumberFormat.parse(this.txtR2.getText()).doubleValue();
         atten = this.realNumberFormat.parse(this.txtAtten.getText()).doubleValue();
         numResistors = this.intNumberFormat.parse(this.txtNumRes.getText()).intValue();
         this.txtAtten.setText(this.realNumberFormat.format(atten));
         this.txtR1.setText(this.realNumberFormat.format(r1));
         this.txtR2.setText(this.realNumberFormat.format(r2));
         this.txtNumRes.setText(this.intNumberFormat.format((long)numResistors));
      } catch (Exception var23) {
         this.txtR3.setText("");
         this.txtR4.setText("");
         this.txtR5.setText("");
         this.txtR1_E12.setText("");
         this.txtR2_E12.setText("");
         this.txtR3_E12.setText("");
         this.txtR4_E12.setText("");
         this.txtR5_E12.setText("");
         this.txtR1_E24.setText("");
         this.txtR2_E24.setText("");
         this.txtR3_E24.setText("");
         this.txtR4_E24.setText("");
         this.txtR5_E24.setText("");
         this.txtR1_E48.setText("");
         this.txtR2_E48.setText("");
         this.txtR3_E48.setText("");
         this.txtR4_E48.setText("");
         this.txtR5_E48.setText("");
         return;
      }

      Object pad;
      if (this.rbPi.isSelected()) {
         pad = new VNAPiPad();
      } else {
         pad = new VNATPad();
      }

      ((VNAGenericPad)pad).setR1(r1);
      ((VNAGenericPad)pad).setR2(r2);
      VNAPadCalculator pc = new VNAPadCalculator();
      pc.setPad((VNAGenericPad)pad);
      pc.calculatePad(atten);
      double r3 = ((VNAGenericPad)pad).getR3();
      double r4 = ((VNAGenericPad)pad).getR4();
      double r5 = ((VNAGenericPad)pad).getR5();
      this.txtR3.setText(VNAFormatFactory.getResistanceFormat().format(r3));
      this.txtR4.setText(VNAFormatFactory.getResistanceFormat().format(r4));
      this.txtR5.setText(VNAFormatFactory.getResistanceFormat().format(r5));
      if (r3 > 0.0D && r4 > 0.0D && r5 > 0.0D) {
         double[] fsE12 = pc.createFullSeries(VNAPadConstants.E12Factors, 6);
         List<Double> r3s = pc.calculateSeriesCircuit(fsE12, r3, numResistors, 0.01D);
         List<Double> r4s = pc.calculateSeriesCircuit(fsE12, r4, numResistors, 0.01D);
         List<Double> r5s = pc.calculateSeriesCircuit(fsE12, r5, numResistors, 0.01D);
         VNAPiPad pp = new VNAPiPad();
         pp.setR1(r1);
         pp.setR2(r2);
         pp.setR3(this.getResistorSum(r3s));
         pp.setR4(this.getResistorSum(r4s));
         pp.setR5(this.getResistorSum(r5s));
         pc.setPad(pp);
         pc.reverseCalcPad(atten);
         this.txtR1_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR1()));
         this.txtR2_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR2()));
         this.txtR3_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR3()));
         this.txtR4_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR4()));
         this.txtR5_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR5()));
         this.txtR3_E12.setToolTipText(this.formatResistorList(r3s));
         this.txtR4_E12.setToolTipText(this.formatResistorList(r4s));
         this.txtR5_E12.setToolTipText(this.formatResistorList(r5s));
         double[] fsE24 = pc.createFullSeries(VNAPadConstants.E24Factors, 6);
         r3s = pc.calculateSeriesCircuit(fsE24, r3, numResistors, 0.01D);
         r4s = pc.calculateSeriesCircuit(fsE24, r4, numResistors, 0.01D);
         r5s = pc.calculateSeriesCircuit(fsE24, r5, numResistors, 0.01D);
         pp = new VNAPiPad();
         pp.setR1(r1);
         pp.setR2(r2);
         pp.setR3(this.getResistorSum(r3s));
         pp.setR4(this.getResistorSum(r4s));
         pp.setR5(this.getResistorSum(r5s));
         pc.setPad(pp);
         pc.reverseCalcPad(atten);
         this.txtR1_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR1()));
         this.txtR2_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR2()));
         this.txtR3_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR3()));
         this.txtR4_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR4()));
         this.txtR5_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR5()));
         this.txtR3_E24.setToolTipText(this.formatResistorList(r3s));
         this.txtR4_E24.setToolTipText(this.formatResistorList(r4s));
         this.txtR5_E24.setToolTipText(this.formatResistorList(r5s));
         double[] fsE48 = pc.createFullSeries(VNAPadConstants.E48Factors, 6);
         r3s = pc.calculateSeriesCircuit(fsE48, r3, numResistors, 0.001D);
         r4s = pc.calculateSeriesCircuit(fsE48, r4, numResistors, 0.001D);
         r5s = pc.calculateSeriesCircuit(fsE48, r5, numResistors, 0.001D);
         pp = new VNAPiPad();
         pp.setR1(r1);
         pp.setR2(r2);
         pp.setR3(this.getResistorSum(r3s));
         pp.setR4(this.getResistorSum(r4s));
         pp.setR5(this.getResistorSum(r5s));
         pc.setPad(pp);
         pc.reverseCalcPad(atten);
         this.txtR1_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR1()));
         this.txtR2_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR2()));
         this.txtR3_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR3()));
         this.txtR4_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR4()));
         this.txtR5_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR5()));
         this.txtR3_E48.setToolTipText(this.formatResistorList(r3s));
         this.txtR4_E48.setToolTipText(this.formatResistorList(r4s));
         this.txtR5_E48.setToolTipText(this.formatResistorList(r5s));
      } else {
         this.txtR1_E12.setText("");
         this.txtR2_E12.setText("");
         this.txtR3_E12.setText("");
         this.txtR4_E12.setText("");
         this.txtR5_E12.setText("");
         this.txtR1_E24.setText("");
         this.txtR2_E24.setText("");
         this.txtR3_E24.setText("");
         this.txtR4_E24.setText("");
         this.txtR5_E24.setText("");
         this.txtR1_E48.setText("");
         this.txtR2_E48.setText("");
         this.txtR3_E48.setText("");
         this.txtR4_E48.setText("");
         this.txtR5_E48.setText("");
      }

      TraceHelper.exit(this, "doCalculate");
   }

   private String formatResistorList(List<Double> rs) {
      String rc = "";
      Iterator it = rs.iterator();

      while(it.hasNext()) {
         Double r = (Double)it.next();
         rc = rc + VNAFormatFactory.getResistanceFormat().format(r);
         if (it.hasNext()) {
            rc = rc + " + ";
         }
      }

      return rc;
   }

   private double getResistorSum(List<Double> rs) {
      double rc = 0.0D;
      TraceHelper.entry(this, "getResistorSum");

      Double r;
      for(Iterator it = rs.iterator(); it.hasNext(); rc += r) {
         r = (Double)it.next();
      }

      TraceHelper.exit(this, "getResistorSum");
      return rc;
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.rbPi.setSelected(true);
      this.doSwitchPadType();
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }
}
