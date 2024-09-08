package krause.vna.gui.importers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.util.SwingUtil;
import krause.vna.importers.SnPImporter;
import krause.vna.importers.SnPInfoBlock;
import krause.vna.importers.SnPRecord;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNASnPImportDialog extends KrauseDialog {
   private JButton btOK;
   private JTextField txtFilename;
   private String filename;
   private VNASnPDataTable lstData;
   private SnPImporter importer;
   private SnPInfoBlock infoBlock;
   private JComboBox cbRL;
   private JComboBox cbTL;
   private JComboBox cbRP;
   private VNACalibratedSampleBlock csb = null;
   private JTextField txtFormat;
   private JTextField txtParameter;
   private JTextField txtReference;
   private JComboBox cbTP;

   public VNASnPImportDialog(Window aFrame, String pFilename) {
      super(aFrame, true);
      TraceHelper.entry(this, "VNASnPImportDialog");
      this.filename = pFilename;
      this.importer = new SnPImporter();
      this.setProperties(VNAConfig.getSingleton());
      this.setConfigurationPrefix("VNASnPImportDialog");
      this.setTitle(VNAMessages.getString("VNASnPImportDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(900, 600));
      this.setLayout(new MigLayout("", "[][][][][][grow,fill]", ""));
      this.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.headline")), "span 6,grow,wrap");
      this.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblFN")), "");
      this.txtFilename = new JTextField(this.filename);
      this.txtFilename.setEditable(false);
      this.add(this.txtFilename, "span 5,grow,wrap");
      this.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblFormat")), "");
      this.txtFormat = new JTextField();
      this.txtFormat.setColumns(10);
      this.txtFormat.setEditable(false);
      this.add(this.txtFormat, "");
      this.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblReference")), "");
      this.txtReference = new JTextField();
      this.txtReference.setColumns(10);
      this.txtReference.setEditable(false);
      this.add(this.txtReference, "");
      this.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblParameter")), "");
      this.txtParameter = new JTextField();
      this.txtParameter.setEditable(false);
      this.add(this.txtParameter, "grow,wrap");
      this.lstData = new VNASnPDataTable();
      JScrollPane scrollPane = new JScrollPane(this.lstData);
      scrollPane.setViewportBorder((Border)null);
      this.add(scrollPane, "span 6,grow,wrap");
      JPanel pnl1 = new JPanel(new MigLayout("", "[][10%][][10%][][10%][][10%][grow,fill]", ""));
      pnl1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNASnPImportDialog.lblAssign"), 4, 2, (Font)null, new Color(0, 0, 0)));
      pnl1.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblAssign2")), "span 8, wrap");
      pnl1.add(new JLabel(VNAMessages.getString("Marker.RL") + " - "), "");
      this.cbRL = new JComboBox(new String[]{"", "S11", "S21", "S12", "S22"});
      pnl1.add(this.cbRL, "left");
      pnl1.add(new JLabel(VNAMessages.getString("Marker.PhaseRL") + " - "), "");
      this.cbRP = new JComboBox(new String[]{"", "S11", "S21", "S12", "S22"});
      pnl1.add(this.cbRP, "left");
      pnl1.add(new JLabel(VNAMessages.getString("Marker.TL") + " - "), "");
      this.cbTL = new JComboBox(new String[]{"", "S11", "S21", "S12", "S22"});
      pnl1.add(this.cbTL, "left");
      pnl1.add(new JLabel(VNAMessages.getString("Marker.PhaseTL") + " - "), "");
      this.cbTP = new JComboBox(new String[]{"", "S11", "S21", "S12", "S22"});
      pnl1.add(this.cbTP, "left");
      this.add(pnl1, "grow, wrap, span 6");
      this.add(SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNASnPImportDialog.this.doDialogCancel();
         }
      }), "center,span 2");
      this.add(new HelpButton(this, "VNASnPImportDialog"), "");
      this.btOK = SwingUtil.createJButton("Button.Load", new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNASnPImportDialog.this.doLoad();
         }
      });
      this.add(this.btOK, "right, span 3");
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNASnPImportDialog");
   }

   protected void doReadFile() {
      TraceHelper.entry(this, "doReadFile");

      try {
         this.infoBlock = this.importer.readFile(this.filename, "US-ASCII");
         VNASnPDataTableModel model = this.lstData.getModel();
         model.getData().clear();
         model.getData().addAll(this.infoBlock.getRecords());
         model.fireTableDataChanged();
         this.analyseData();
      } catch (ProcessingException var2) {
         ErrorLogHelper.exception(this, "doReadFile", var2);
      }

      TraceHelper.exit(this, "doReadFile");
   }

   private void analyseData() {
      TraceHelper.entry(this, "analyseData");
      List<SnPRecord> records = this.infoBlock.getRecords();
      if (records.size() > 0) {
         boolean[] hasLossData = new boolean[4];
         boolean[] hasPhaseData = new boolean[4];
         Iterator var5 = records.iterator();

         while(var5.hasNext()) {
            SnPRecord record = (SnPRecord)var5.next();

            for(int i = 0; i < 4; ++i) {
               double loss = record.getLoss()[i];
               if (!Double.isNaN(loss) && loss != 0.0D) {
                  hasLossData[i] = true;
               }

               double phase = record.getPhase()[i];
               if (!Double.isNaN(phase) && phase != 0.0D) {
                  hasPhaseData[i] = true;
               }
            }
         }

         if (hasLossData[0] && hasPhaseData[0] && !hasLossData[1] && !hasPhaseData[1] && (!hasLossData[2] || hasPhaseData[2]) && !hasLossData[3] && !hasPhaseData[3]) {
            this.cbRL.setSelectedIndex(1);
            this.cbTL.setSelectedIndex(0);
            this.cbRP.setSelectedIndex(1);
         } else if (!hasLossData[0] && !hasPhaseData[0] && hasLossData[1] && hasPhaseData[1] && (!hasLossData[2] || hasPhaseData[2]) && !hasLossData[3] && !hasPhaseData[3]) {
            this.cbRL.setSelectedIndex(0);
            this.cbTL.setSelectedIndex(2);
            this.cbRP.setSelectedIndex(2);
         } else if (hasLossData[0] && hasPhaseData[0] && hasLossData[1] && hasPhaseData[1] && (!hasLossData[2] || hasPhaseData[2]) && !hasLossData[3] && !hasPhaseData[3]) {
            this.cbRL.setSelectedIndex(1);
            this.cbTL.setSelectedIndex(2);
            this.cbRP.setSelectedIndex(1);
         }

         if (this.infoBlock.getFormat() != SnPInfoBlock.FORMAT.DB || this.infoBlock.getParameter() != SnPInfoBlock.PARAMETER.S) {
            JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASnPImportDialog.notSupportedFormat"), this.getTitle(), 0);
            this.btOK.setEnabled(false);
         }
      } else {
         JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASnPImportDialog.notDataFound"), this.getTitle(), 0);
         this.btOK.setEnabled(false);
      }

      TraceHelper.exit(this, "analyseData");
   }

   protected void doLoad() {
      TraceHelper.entry(this, "doOK");
      List<SnPRecord> records = this.infoBlock.getRecords();
      this.csb = new VNACalibratedSampleBlock(records.size());
      this.csb.setFile(new File(this.filename));
      int rpIndex = this.cbRP.getSelectedIndex();
      int tpIndex = this.cbTP.getSelectedIndex();
      int rlIndex = this.cbRL.getSelectedIndex();
      int tlIndex = this.cbTL.getSelectedIndex();
      int index = 0;
      Iterator var8 = records.iterator();

      while(var8.hasNext()) {
         SnPRecord record = (SnPRecord)var8.next();
         VNACalibratedSample cs = new VNACalibratedSample();
         cs.setFrequency(record.getFrequency());
         if (rpIndex != 0) {
            cs.setReflectionPhase(record.getPhase()[rpIndex - 1]);
         }

         if (tpIndex != 0) {
            cs.setTransmissionPhase(record.getPhase()[tpIndex - 1]);
         }

         if (rlIndex != 0) {
            cs.setReflectionLoss(record.getLoss()[rlIndex - 1]);
         }

         if (tlIndex != 0) {
            cs.setTransmissionLoss(record.getLoss()[tlIndex - 1]);
         }

         this.calculateDerivedValues(cs);
         this.csb.consumeCalibratedSample(cs, index++);
      }

      this.setVisible(false);
      TraceHelper.exit(this, "doOK");
   }

   private void calculateDerivedValues(VNACalibratedSample cs) {
      TraceHelper.entry(this, "calculateDerivedValues");
      double RAD2DEG = 57.29577951308232D;
      double referenceRes = this.infoBlock.getReference().getReal();
      double mag = Math.pow(10.0D, cs.getReflectionLoss() / 20.0D);
      double swr = Math.abs((1.0D + mag) / (1.0D - mag));
      double f = Math.cos(cs.getReflectionPhase() / 57.29577951308232D);
      double g = Math.sin(cs.getReflectionPhase() / 57.29577951308232D);
      double rr = f * mag;
      double ss = g * mag;
      double x_imp = 2.0D * ss / ((1.0D - rr) * (1.0D - rr) + ss * ss) * referenceRes;
      double r_imp = (1.0D - rr * rr - ss * ss) / ((1.0D - rr) * (1.0D - rr) + ss * ss) * referenceRes;
      double z_imp = Math.sqrt(r_imp * r_imp + x_imp * x_imp);
      cs.setMag(mag);
      cs.setSWR(swr);
      cs.setX(x_imp);
      cs.setR(r_imp);
      cs.setZ(z_imp);
      TraceHelper.exit(this, "calculateDerivedValues");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.doReadFile();
      this.txtFormat.setText("" + this.infoBlock.getFormat());
      this.txtParameter.setText("" + this.infoBlock.getParameter());
      this.txtReference.setText("" + this.infoBlock.getReference().getReal());
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   public VNACalibratedSampleBlock getData() {
      return this.csb;
   }
}
