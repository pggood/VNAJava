package krause.vna.gui.panels.marker;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.marker.math.VNAMarkerMathHelper;
import krause.vna.marker.math.VNAMarkerMathInput;
import krause.vna.marker.math.VNAMarkerMathResult;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAMarkerMathDialog extends KrauseDialog {
   private final transient TypedProperties config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();
   private String confPrefix;
   VNAMarker marker = null;
   private JTextField txtLoss;
   private JTextField txtFrq;
   private JTextField txtLeftLowFrq;
   private JTextField txtRightLowFrq;
   private JTextField txtLeftLowLoss;
   private JTextField txtRightLowLoss;
   private JTextField txtLossTarget;
   private JLabel lblFrequency;
   private JLabel lblTarget;
   private JLabel lblBandwidth;
   private JTextField txtBW;
   private JTextField txtQ;
   private JLabel lblLow;
   private JLabel lblMode;
   private JTextField txtMODE;
   NumberFormat returnLossFormat = VNAFormatFactory.getReflectionLossFormat();
   private JLabel lblC;
   private JLabel lblL;
   private JTextField txtCSer;
   private JTextField txtLSer;
   private JLabel lblRs;
   private JLabel lblRp;
   private JTextField txtRS;
   private JTextField txtRP;
   private JLabel lblXs;
   private JTextField txtXS;
   private JLabel lblXp;
   private JTextField txtXP;
   private JRadioButton rbTL;
   private JRadioButton rbRL;

   public VNAMarkerMathDialog(VNAMarker pMarker) {
      super((Dialog)null, false);
      this.setDefaultCloseOperation(0);
      this.setBounds(new Rectangle(0, 0, 440, 330));
      this.setTitle(MessageFormat.format(VNAMessages.getString("VNAMarkerMathDialog.title"), pMarker.getName()));
      this.marker = pMarker;
      this.confPrefix = "MarkerMath." + this.marker.getName();
      this.setResizable(true);
      this.getContentPane().setLayout(new BorderLayout(0, 0));
      JPanel panel2 = new JPanel();
      panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.getContentPane().add(panel2);
      panel2.setLayout(new MigLayout("", "", "grow"));
      panel2.add(new JLabel(), "");
      this.lblLow = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.low"));
      panel2.add(this.lblLow, "");
      JLabel lblLoss = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.marker"));
      panel2.add(lblLoss, "");
      JLabel lblFreq = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.high"));
      panel2.add(lblFreq, "wrap");
      this.lblFrequency = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.frequency"));
      panel2.add(this.lblFrequency, "");
      this.txtLeftLowFrq = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
      this.txtLeftLowFrq.setHorizontalAlignment(4);
      this.txtLeftLowFrq.setEditable(false);
      this.txtLeftLowFrq.setColumns(9);
      panel2.add(this.txtLeftLowFrq, "");
      this.txtFrq = new JTextField();
      this.txtFrq.setHorizontalAlignment(4);
      panel2.add(this.txtFrq, "");
      this.txtFrq.setEditable(false);
      this.txtFrq.setColumns(9);
      this.txtRightLowFrq = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
      this.txtRightLowFrq.setHorizontalAlignment(4);
      this.txtRightLowFrq.setEditable(false);
      this.txtRightLowFrq.setColumns(9);
      panel2.add(this.txtRightLowFrq, "wrap");
      JLabel lblLossdb = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.loss"));
      panel2.add(lblLossdb);
      this.txtLeftLowLoss = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
      this.txtLeftLowLoss.setHorizontalAlignment(4);
      this.txtLeftLowLoss.setEditable(false);
      this.txtLeftLowLoss.setColumns(9);
      panel2.add(this.txtLeftLowLoss, "");
      this.txtLoss = new JTextField();
      this.txtLoss.setHorizontalAlignment(4);
      this.txtLoss.setEditable(false);
      this.txtLoss.setColumns(9);
      panel2.add(this.txtLoss, "");
      this.txtRightLowLoss = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
      this.txtRightLowLoss.setHorizontalAlignment(4);
      this.txtRightLowLoss.setEditable(false);
      this.txtRightLowLoss.setColumns(9);
      panel2.add(this.txtRightLowLoss, "wrap");
      this.lblTarget = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.target"));
      panel2.add(this.lblTarget);
      this.txtLossTarget = new JTextField();
      this.txtLossTarget.setHorizontalAlignment(4);
      this.txtLossTarget.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            try {
               VNAMarkerMathDialog.this.txtLossTarget.setText(VNAMarkerMathDialog.this.returnLossFormat.format(VNAMarkerMathDialog.this.returnLossFormat.parse(VNAMarkerMathDialog.this.txtLossTarget.getText())));
            } catch (ParseException var3) {
               Toolkit.getDefaultToolkit().beep();
            }

            VNAMarkerMathDialog.this.update();
         }
      });
      this.txtLossTarget.setText("6,0");
      this.txtLossTarget.setColumns(4);
      panel2.add(this.txtLossTarget, "wrap");
      this.lblBandwidth = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.bandwidth"));
      panel2.add(this.lblBandwidth, "");
      this.txtBW = new JTextField();
      this.txtBW.setHorizontalAlignment(4);
      this.txtBW.setEditable(false);
      this.txtBW.setColumns(9);
      panel2.add(this.txtBW, "");
      JLabel lblQ1 = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.q"));
      panel2.add(lblQ1, "right");
      this.txtQ = new JTextField();
      this.txtQ.setHorizontalAlignment(4);
      this.txtQ.setEditable(false);
      this.txtQ.setColumns(9);
      panel2.add(this.txtQ, "wrap");
      this.lblC = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.c"));
      panel2.add(this.lblC, "");
      this.txtCSer = new JTextField();
      this.txtCSer.setHorizontalAlignment(4);
      this.txtCSer.setEditable(false);
      this.txtCSer.setColumns(9);
      panel2.add(this.txtCSer, "");
      this.lblL = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.l"));
      panel2.add(this.lblL, "right");
      this.txtLSer = new JTextField();
      this.txtLSer.setHorizontalAlignment(4);
      this.txtLSer.setEditable(false);
      this.txtLSer.setColumns(9);
      panel2.add(this.txtLSer, "wrap");
      this.lblRs = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblRs.text"));
      panel2.add(this.lblRs, "");
      this.txtRS = new JTextField();
      this.txtRS.setHorizontalAlignment(4);
      this.txtRS.setEditable(false);
      this.txtRS.setColumns(9);
      panel2.add(this.txtRS, "");
      this.lblXs = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblXs.text"));
      panel2.add(this.lblXs, "right");
      this.txtXS = new JTextField();
      this.txtXS.setHorizontalAlignment(4);
      this.txtXS.setEditable(false);
      this.txtXS.setColumns(9);
      panel2.add(this.txtXS, "wrap");
      this.lblRp = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblRp.text"));
      panel2.add(this.lblRp, "");
      this.txtRP = new JTextField();
      this.txtRP.setHorizontalAlignment(4);
      this.txtRP.setEditable(false);
      this.txtRP.setColumns(9);
      panel2.add(this.txtRP, "");
      this.lblXp = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblXp.text"));
      panel2.add(this.lblXp, "right");
      this.txtXP = new JTextField();
      this.txtXP.setHorizontalAlignment(4);
      this.txtXP.setEditable(false);
      this.txtXP.setColumns(9);
      panel2.add(this.txtXP, "wrap");
      this.lblMode = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.mode"));
      panel2.add(this.lblMode, "");
      this.txtMODE = new JTextField();
      this.txtMODE.setHorizontalAlignment(4);
      this.txtMODE.setEditable(false);
      this.txtMODE.setColumns(9);
      panel2.add(this.txtMODE, "wrap");
      panel2.add(new JLabel(VNAMessages.getString("VNAMarkerMathDialog.use")), "");
      panel2.add(this.rbRL = SwingUtil.createJRadioButton("VNAMarkerMathDialog.rl", (ActionListener)null), "");
      panel2.add(this.rbTL = SwingUtil.createJRadioButton("VNAMarkerMathDialog.tl", (ActionListener)null), "");
      panel2.add(new HelpButton(this, "VNAMarkerMathDialog"), "wrap");
      ButtonGroup bg = new ButtonGroup();
      bg.add(this.rbRL);
      bg.add(this.rbTL);
      this.rbRL.setSelected(this.datapool.getScanMode().isReflectionMode());
      this.rbTL.setSelected(this.datapool.getScanMode().isTransmissionMode());
      this.rbTL.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAMarkerMathDialog.this.update();
         }
      });
      this.rbRL.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNAMarkerMathDialog.this.update();
         }
      });
      this.doDialogInit();
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      this.config.storeWindowPosition(this.confPrefix, this);
      this.config.storeWindowSize(this.confPrefix, this);
      this.config.put(this.confPrefix + ".Loss", this.txtLossTarget.getText());
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.txtLossTarget.setText(this.config.getProperty(this.confPrefix + ".Loss", "6"));
      TraceHelper.exit(this, "doInit");
   }

   public void doDialogShow() {
      this.config.restoreWindowPosition(this.confPrefix, this, new Point(100, 100));
      this.pack();
      this.config.restoreWindowSize(this.confPrefix, this, new Dimension(440, 330));
      this.setVisible(true);
   }

   public void update() {
      TraceHelper.entry(this, "update");
      String lossString = this.txtLossTarget.getText();

      try {
         double delta = VNAFormatFactory.getReflectionLossFormat().parse(lossString).doubleValue();
         VNACalibratedSample markerSample = this.marker.getSample();
         VNACalibratedSampleBlock currentData = this.datapool.getCalibratedData();
         VNACalibratedSample[] allSamples = currentData.getCalibratedSamples();
         int markerSampleIndex = -1;

         for(int i = 0; i < allSamples.length; ++i) {
            if (allSamples[i] == markerSample) {
               markerSampleIndex = i;
            }
         }

         boolean useRL = this.rbRL.isSelected();
         if (markerSampleIndex != -1) {
            double lowDelta = (useRL ? markerSample.getReflectionLoss() : markerSample.getTransmissionLoss()) - delta;
            double highDelta = (useRL ? markerSample.getReflectionLoss() : markerSample.getTransmissionLoss()) + delta;
            VNACalibratedSample leftLowDeltaSample = null;
            VNACalibratedSample leftHighDeltaSample = null;
            VNACalibratedSample rightLowDeltaSample = null;
            VNACalibratedSample rightHighDeltaSample = null;

            int i;
            VNACalibratedSample sample;
            double loss;
            for(i = markerSampleIndex - 1; i > 0; --i) {
               sample = allSamples[i];
               loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
               if (loss < lowDelta) {
                  leftLowDeltaSample = sample;
                  break;
               }
            }

            for(i = markerSampleIndex + 1; i < allSamples.length; ++i) {
               sample = allSamples[i];
               loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
               if (loss < lowDelta) {
                  rightLowDeltaSample = sample;
                  break;
               }
            }

            boolean peakMode = leftLowDeltaSample != null && rightLowDeltaSample != null;

            //double loss;
            //int i;
            //VNACalibratedSample sample;
            for(i = markerSampleIndex - 1; i > 0; --i) {
               sample = allSamples[i];
               loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
               if (loss > highDelta) {
                  leftHighDeltaSample = sample;
                  break;
               }
            }

            for(i = markerSampleIndex + 1; i < allSamples.length; ++i) {
               sample = allSamples[i];
               loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
               if (loss > highDelta) {
                  rightHighDeltaSample = sample;
                  break;
               }
            }

            boolean notchMode = leftHighDeltaSample != null && rightHighDeltaSample != null;
            this.txtFrq.setText(VNAFormatFactory.getFrequencyFormat().format(markerSample.getFrequency()));
            this.txtLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? markerSample.getReflectionLoss() : markerSample.getTransmissionLoss()));
            VNAMarkerMathInput mmInput = new VNAMarkerMathInput(markerSample);
            if (peakMode) {
               this.txtMODE.setText(VNAMessages.getString("VNAMarkerMathDialog.notch"));
               this.txtLeftLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(leftLowDeltaSample.getFrequency()));
               this.txtLeftLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? leftLowDeltaSample.getReflectionLoss() : leftLowDeltaSample.getTransmissionLoss()));
               this.txtRightLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(rightLowDeltaSample.getFrequency()));
               this.txtRightLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? rightLowDeltaSample.getReflectionLoss() : rightLowDeltaSample.getTransmissionLoss()));
               mmInput.setHighFrequency(rightLowDeltaSample.getFrequency());
               mmInput.setLowFrequency(leftLowDeltaSample.getFrequency());
            } else if (notchMode) {
               this.txtMODE.setText(VNAMessages.getString("VNAMarkerMathDialog.peak"));
               this.txtLeftLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(leftHighDeltaSample.getFrequency()));
               this.txtLeftLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? leftHighDeltaSample.getReflectionLoss() : leftHighDeltaSample.getTransmissionLoss()));
               this.txtRightLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(rightHighDeltaSample.getFrequency()));
               this.txtRightLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? rightHighDeltaSample.getReflectionLoss() : rightHighDeltaSample.getTransmissionLoss()));
               mmInput.setHighFrequency(rightHighDeltaSample.getFrequency());
               mmInput.setLowFrequency(leftHighDeltaSample.getFrequency());
            } else {
               this.txtLeftLowFrq.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtLeftLowLoss.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtRightLowFrq.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtRightLowLoss.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtBW.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtQ.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtMODE.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtCSer.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtLSer.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtRS.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtRP.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtXS.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
               this.txtXP.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
            }

            VNAMarkerMathResult mmResult = VNAMarkerMathHelper.execute(mmInput);
            this.txtBW.setText(VNAFormatFactory.getFrequencyFormat().format(mmResult.getBandWidth()));
            this.txtQ.setText(VNAFormatFactory.getQFormat().format(mmResult.getQ()));
            this.txtCSer.setText(VNAFormatFactory.getCapacityFormat().format(mmResult.getSerialCapacity()));
            this.txtLSer.setText(VNAFormatFactory.getInductivityFormat().format(mmResult.getSerialInductance()));
            this.txtRS.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getRs()));
            this.txtRP.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getRp()));
            this.txtXS.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getXs()));
            this.txtXP.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getXp()));
         }
      } catch (ParseException var22) {
         ErrorLogHelper.exception(this, "update", var22);
      }

      TraceHelper.exit(this, "update");
   }

   public void windowClosing(WindowEvent e) {
      TraceHelper.entry(this, "windowClosing");
      this.marker.doClickOnMathSymbol();
      TraceHelper.exit(this, "windowClosing");
   }
}
