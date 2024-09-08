package krause.vna.gui.cable;

import com.l2fprod.common.swing.StatusBar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACableLengthDialog extends KrauseDialog implements IVNADataConsumer {
   private JButton btMeasure;
   private JButton btOK;
   private VNAVelocityFactorTable tblVelocity;
   private JLabel lblVelocityFactor1;
   private JTextField txtLength1;
   private transient VNAMainFrame mainFrame;
   private JTextField txtLength2;
   private JTextField txtVelocityFactor2;
   private JPanel pnlVariableVelocity;
   private JLabel lblVelocityFactor3;
   private JTextField txtVelocityFactor3;
   private JLabel lblLength3;
   private JTextField txtLength3;
   private JPanel pnlKnownCableLength;
   private JPanel pnlKnownVelocity;
   private VNADeviceInfoBlock dib;
   private final VNAConfig config = VNAConfig.getSingleton();
   private final transient VNADataPool datapool = VNADataPool.getSingleton();
   private JRadioButton rdbtnM;
   private JRadioButton rdbtnFeet;
   private JLabel lblUnit;
   private long startFreq;
   private long stopFreq;
   private int numIterations;
   private StatusBar statusBar;

   public VNACableLengthDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      this.mainFrame = pMainFrame;
      this.dib = this.datapool.getDriver().getDeviceInfoBlock();
      this.setConfigurationPrefix("CableLength");
      this.setProperties(this.config);
      this.setTitle(VNAMessages.getString("VNACableLengthDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(850, 600));
      this.getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[][][][grow,fill]"));
      this.pnlKnownVelocity = new JPanel();
      this.getContentPane().add(this.pnlKnownVelocity, "wrap");
      this.pnlKnownVelocity.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACableLengthDialog.rdbtnKnowVelocityFactor.text"), 4, 2, (Font)null, (Color)null));
      this.pnlKnownVelocity.setLayout(new MigLayout("", "[][grow,fill]", "[]"));
      this.lblVelocityFactor1 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text"));
      this.pnlKnownVelocity.add(this.lblVelocityFactor1, "");
      this.tblVelocity = new VNAVelocityFactorTable();
      JScrollPane scrollPane = new JScrollPane(this.tblVelocity);
      scrollPane.setViewportBorder((Border)null);
      this.pnlKnownVelocity.add(scrollPane, "wrap");
      JLabel lblLength1 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblLength.text"));
      this.pnlKnownVelocity.add(lblLength1, "");
      this.txtLength1 = new JTextField();
      this.txtLength1.setFocusable(false);
      this.txtLength1.setFocusTraversalKeysEnabled(false);
      this.pnlKnownVelocity.add(this.txtLength1, "");
      this.txtLength1.setHorizontalAlignment(4);
      this.txtLength1.setEditable(false);
      this.txtLength1.setColumns(6);
      this.pnlKnownCableLength = new JPanel();
      this.getContentPane().add(this.pnlKnownCableLength, "wrap");
      this.pnlKnownCableLength.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACableLengthDialog.rdbtnKnownCableLength.text"), 4, 2, (Font)null, (Color)null));
      this.pnlKnownCableLength.setLayout(new MigLayout("", "[][][][grow,fill]", "[]"));
      JLabel lblLength2 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblKnownLength.text"));
      this.pnlKnownCableLength.add(lblLength2, "");
      this.txtLength2 = new JTextField();
      this.txtLength2.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            try {
               String t = VNACableLengthDialog.this.txtLength2.getText();
               if (t != null && t.length() > 0) {
                  double clen = VNAFormatFactory.getLengthFormat().parse(VNACableLengthDialog.this.txtLength2.getText()).doubleValue();
                  VNACableLengthDialog.this.txtLength2.setText(VNAFormatFactory.getLengthFormat().format(clen));
               }
            } catch (ParseException var5) {
               String m = MessageFormat.format(VNAMessages.getString("VNACableLengthDialog.Err.1"), VNACableLengthDialog.this.txtLength2.getText());
               JOptionPane.showMessageDialog(VNACableLengthDialog.this.mainFrame.getJFrame(), m, VNAMessages.getString("VNACableLengthDialog.Err.2"), 0);
            }

         }
      });
      this.txtLength2.setHorizontalAlignment(4);
      this.pnlKnownCableLength.add(this.txtLength2, "");
      this.txtLength2.setColumns(6);
      JLabel lblVelocityFactor2 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text"));
      this.pnlKnownCableLength.add(lblVelocityFactor2, "");
      this.txtVelocityFactor2 = new JTextField();
      this.txtVelocityFactor2.setFocusable(false);
      this.txtVelocityFactor2.setFocusTraversalKeysEnabled(false);
      this.txtVelocityFactor2.setHorizontalAlignment(4);
      this.txtVelocityFactor2.setEditable(false);
      this.txtVelocityFactor2.setColumns(6);
      this.pnlKnownCableLength.add(this.txtVelocityFactor2, "");
      this.pnlVariableVelocity = new JPanel();
      this.getContentPane().add(this.pnlVariableVelocity, "wrap");
      this.pnlVariableVelocity.setLayout(new MigLayout("", "[][][][grow,fill]", "[]"));
      this.pnlVariableVelocity.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACableLengthDialog.rdbtnVariableVelocityFactor.text"), 4, 2, (Font)null, (Color)null));
      this.lblVelocityFactor3 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text"));
      this.pnlVariableVelocity.add(this.lblVelocityFactor3, "");
      this.txtVelocityFactor3 = new JTextField();
      this.txtVelocityFactor3.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            try {
               String t = VNACableLengthDialog.this.txtVelocityFactor3.getText();
               if (t != null && t.length() > 0) {
                  double clen = VNAFormatFactory.getVelocityFormat().parse(VNACableLengthDialog.this.txtVelocityFactor3.getText()).doubleValue();
                  VNACableLengthDialog.this.txtVelocityFactor3.setText(VNAFormatFactory.getVelocityFormat().format(clen));
               }
            } catch (ParseException var5) {
               String m = MessageFormat.format(VNAMessages.getString("VNACableLengthDialog.Err.1"), VNACableLengthDialog.this.txtVelocityFactor3.getText());
               JOptionPane.showMessageDialog(VNACableLengthDialog.this.mainFrame.getJFrame(), m, VNAMessages.getString("VNACableLengthDialog.Err.2"), 0);
            }

         }
      });
      this.txtVelocityFactor3.setHorizontalAlignment(4);
      this.txtVelocityFactor3.setColumns(6);
      this.pnlVariableVelocity.add(this.txtVelocityFactor3, "");
      this.lblLength3 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblLength.text"));
      this.pnlVariableVelocity.add(this.lblLength3, "");
      this.txtLength3 = new JTextField();
      this.txtLength3.setFocusTraversalKeysEnabled(false);
      this.txtLength3.setFocusable(false);
      this.txtLength3.setHorizontalAlignment(4);
      this.txtLength3.setEditable(false);
      this.txtLength3.setColumns(6);
      this.pnlVariableVelocity.add(this.txtLength3, "");
      ButtonGroup bg = new ButtonGroup();
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "wrap");
      pnlButtons.setLayout(new MigLayout("", "[grow,fill][grow][grow][grow][grow,fill]", "[]"));
      this.btMeasure = new JButton(VNAMessages.getString("VNACableLengthDialog.btMeasure.text"));
      pnlButtons.add(this.btMeasure, "cell 0 0,alignx left,aligny center");
      this.btMeasure.addActionListener((e) -> {
         this.doMeasure();
      });
      this.lblUnit = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblUnit.text"));
      pnlButtons.add(this.lblUnit, "");
      this.rdbtnM = new JRadioButton(VNAMessages.getString("VNACableLengthDialog.rdbtnM.text"));
      pnlButtons.add(this.rdbtnM, "");
      bg.add(this.rdbtnM);
      this.rdbtnFeet = new JRadioButton(VNAMessages.getString("VNACableLengthDialog.rdbtnFeet.text"));
      pnlButtons.add(this.rdbtnFeet, "");
      bg.add(this.rdbtnFeet);
      this.btOK = new JButton(VNAMessages.getString("Button.Close"));
      pnlButtons.add(this.btOK, "");
      this.btOK.addActionListener((e) -> {
         this.doDialogCancel();
      });
      JPanel pnlStatus = new JPanel();
      pnlStatus.setLayout(new MigLayout("", "[][grow,fill]", "[]"));
      this.getContentPane().add(pnlStatus, "wrap");
      pnlStatus.add(new JLabel(VNAMessages.getString("VNACableLengthDialog.lblStatus")), "");
      this.statusBar = new StatusBar();
      pnlStatus.add(this.statusBar, "");
      JLabel lbl = new JLabel(VNAMessages.getString("Message.Ready"));
      lbl.setOpaque(true);
      this.statusBar.addZone("status", lbl, "*");
      this.doDialogInit();
   }

   private void initiateScan() {
      this.setCursor(Cursor.getPredefinedCursor(3));
      VNABackgroundJob job = new VNABackgroundJob();
      job.setNumberOfSamples(1000);
      job.setFrequencyRange(new VNAFrequencyRange(this.startFreq, this.stopFreq));
      job.setScanMode(VNAScanMode.MODE_REFLECTION);
      job.setSpeedup(1);
      VnaBackgroundTask backgroundTask = new VnaBackgroundTask(this.datapool.getDriver());
      backgroundTask.addJob(job);
      backgroundTask.setStatusLabel((JLabel)this.statusBar.getZone("status"));
      backgroundTask.addDataConsumer(this);
      backgroundTask.execute();
   }

   protected void doMeasure() {
      this.btMeasure.setEnabled(false);
      this.btOK.setEnabled(false);
      this.startFreq = this.datapool.getMainCalibrationBlock().getStartFrequency();
      this.stopFreq = this.datapool.getMainCalibrationBlock().getStopFrequency();
      this.numIterations = 1;
      this.initiateScan();
   }

   public void consumeDataBlock(List<VNABackgroundJob> jobs) {
      String methodName = "consumeDataBlock";
      TraceHelper.entry(this, "consumeDataBlock");
      boolean enableButtons = true;
      if (jobs.size() == 1) {
         VNASampleBlock rawData = ((VNABackgroundJob)jobs.get(0)).getResult();
         if (rawData != null) {
            IVNADriverMathHelper mathHelper = rawData.getMathHelper();
            if (mathHelper != null) {
               VNACalibrationBlock mainCalibrationBlock = this.datapool.getMainCalibrationBlock();
               if (mainCalibrationBlock != null) {
                  VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mainCalibrationBlock, this.startFreq, this.stopFreq, rawData.getNumberOfSteps());
                  if (rawData.getScanMode().isReflectionMode()) {
                     VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
                     context.setConversionTemperature(rawData.getDeviceTemperature());
                     VNACalibratedSampleBlock samples = mathHelper.createCalibratedSamples(context, rawData);
                     VNACableMeasurementHelper helper = new VNACableMeasurementHelper(this.dib.getMinPhase() < 0.0D, this.rdbtnM.isSelected());
                     List<VNACalibratedSample> allPoints = helper.findAllCrossingPoints(samples);
                     Iterator var13 = allPoints.iterator();

                     while(var13.hasNext()) {
                        VNACalibratedSample aSample = (VNACalibratedSample)var13.next();
                        TraceHelper.text(this, "consumeDataBlock", "crossingPoint f=" + aSample.getFrequency() + " rl=" + aSample.getReflectionLoss());
                     }

                     List<VNACalibratedSample> points = helper.findTwoCrossingPoints(samples);
                     if (points.size() == 2) {
                        TraceHelper.text(this, "consumeDataBlock", "Point1=" + ((VNACalibratedSample)points.get(0)).getFrequency());
                        TraceHelper.text(this, "consumeDataBlock", "Point2=" + ((VNACalibratedSample)points.get(1)).getFrequency());
                        if (this.numIterations > 0) {
                           this.startFreq = (long)((double)((VNACalibratedSample)points.get(0)).getFrequency() * 0.9D);
                           this.startFreq = Math.max(this.startFreq, this.dib.getMinFrequency());
                           this.stopFreq = (long)((double)((VNACalibratedSample)points.get(1)).getFrequency() * 1.1D);
                           this.stopFreq = Math.min(this.stopFreq, this.dib.getMaxFrequency());
                           --this.numIterations;
                           this.initiateScan();
                           enableButtons = false;
                        } else {
                           this.updateFields(points, helper);
                        }
                     } else {
                        this.clearFields();
                     }
                  } else {
                     this.clearFields();
                  }
               } else {
                  this.clearFields();
               }
            } else {
               this.clearFields();
            }
         } else {
            this.clearFields();
         }
      } else {
         this.clearFields();
      }

      if (enableButtons) {
         this.btMeasure.setEnabled(true);
         this.btOK.setEnabled(true);
         this.setCursor(Cursor.getPredefinedCursor(0));
      }

      TraceHelper.exit(this, "consumeDataBlock");
   }

   private void clearFields() {
      this.txtLength1.setText("???");
      this.txtVelocityFactor2.setText("???");
      this.txtLength3.setText("???");
   }

   private void updateFields(List<VNACalibratedSample> points, VNACableMeasurementHelper helper) {
      TraceHelper.entry(this, "updateFields");
      double velocityEntered = this.tblVelocity.getSelectedItem().getVf();
      VNACableMeasurementPoint result = helper.calculateLength(points, velocityEntered);
      this.txtLength1.setBackground(Color.BLACK);
      this.txtLength1.setForeground(Color.YELLOW);
      this.txtLength1.setText(VNAFormatFactory.getLengthFormat().format(result.getLength()) + (this.rdbtnM.isSelected() ? " m" : " ft"));

      try {
         double clen = VNAFormatFactory.getLengthFormat().parse(this.txtLength2.getText()).doubleValue();
         result = helper.calculateVelocityFactor(points, clen);
         this.txtVelocityFactor2.setBackground(Color.BLACK);
         this.txtVelocityFactor2.setForeground(Color.YELLOW);
         this.txtVelocityFactor2.setText(VNAFormatFactory.getVelocityFormat().format(result.getVelocityFactor()));
      } catch (ParseException var9) {
      }

      try {
         velocityEntered = VNAFormatFactory.getVelocityFormat().parse(this.txtVelocityFactor3.getText()).doubleValue();
         result = helper.calculateLength(points, velocityEntered);
         this.txtLength3.setBackground(Color.BLACK);
         this.txtLength3.setForeground(Color.YELLOW);
         this.txtLength3.setText(VNAFormatFactory.getLengthFormat().format(result.getLength()) + (this.rdbtnM.isSelected() ? " m" : " ft"));
      } catch (ParseException var8) {
      }

      TraceHelper.exit(this, "updateFields");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.config.putInteger(this.getConfigurationPrefix() + ".selIdx", this.tblVelocity.getSelectedRow());
      this.config.put(this.getConfigurationPrefix() + ".userLength", this.txtLength2.getText());
      this.config.put(this.getConfigurationPrefix() + ".userVelFactor", this.txtVelocityFactor3.getText());
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      int selIdx = this.config.getInteger(this.getConfigurationPrefix() + ".selIdx", 0);
      this.tblVelocity.selectRow(selIdx);
      this.txtLength2.setText(this.config.getProperty(this.getConfigurationPrefix() + ".userLength", ""));
      this.txtVelocityFactor3.setText(this.config.getProperty(this.getConfigurationPrefix() + ".userVelFactor", ""));
      this.rdbtnM.setSelected(true);
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }
}
