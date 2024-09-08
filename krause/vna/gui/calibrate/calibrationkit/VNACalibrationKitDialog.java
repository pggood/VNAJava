package krause.vna.gui.calibrate.calibrationkit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.common.validation.DoubleValidator;
import krause.common.validation.StringValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalSetHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationKitDialog extends KrauseDialog implements AdjustmentListener, ListSelectionListener, ActionListener, IVNABackgroundTaskStatusListener {
   private final VNAConfig config = VNAConfig.getSingleton();
   private JButton btOK;
   private VNACalibrationKitTable lbCalibrationSets;
   private JTextField txtKpOpenOffset;
   private JTextField txtKpOpenLoss;
   private JTextField txtKpShortOffset;
   private JTextField txtKpShortLoss;
   private JTextField txtShortInd;
   private JTextField txtC0;
   private JTextField txtC1;
   private JTextField txtC2;
   private JTextField txtC3;
   private JTextField txtThruLen;
   private JTextField txtName;
   private JButton btCalSetAdd;
   private JButton btCalSetEdit;
   private JButton btCalSetDelete;
   private JButton btCalSetSave;
   private JButton btCalSetAbort;
   private VNACalibrationKitDialog.EDIT_MODES currentEditMode;
   private transient VNACalibrationKit selectedCalibrationKit;
   private NumberFormat formatCx;
   private NumberFormat formatLength;
   private transient VNAMainFrame mainFrame;
   private JScrollBar sbOpenOffset;
   private JScrollBar sbShortOffset;
   private JToggleButton cbSmith;
   private VNACalibrationKitSmithDiagramDialog smithDialog;
   private static final int NUM_SAMPLES = 200;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$krause$vna$gui$calibrate$calibrationkit$VNACalibrationKitDialog$EDIT_MODES;

   public VNACalibrationKitDialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      this.currentEditMode = VNACalibrationKitDialog.EDIT_MODES.NONE;
      this.mainFrame = pMainFrame;
      TraceHelper.entry(this, "VNACalSetDialog");
      this.setTitle(VNAMessages.getString("VNACalSetDialog.title"));
      this.setDefaultCloseOperation(0);
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNACalSetDialog");
      this.setPreferredSize(new Dimension(580, 450));
      JPanel panel = new JPanel();
      this.getContentPane().add(panel, "Center");
      panel.setLayout(new MigLayout("", "[][grow,fill]", "[grow,fill]"));
      panel.add(this.createCalSetList(), "");
      panel.add(this.createCalSetDetail(), "wrap");
      JPanel buttonPanel = new JPanel(new MigLayout("", "[left][grow,fill][right]", ""));
      panel.add(buttonPanel, "span 2,grow");
      buttonPanel.add(new HelpButton(this, "VNACalSetDialog"), "wmin 100px");
      this.btOK = SwingUtil.createJButton("Button.OK", (e) -> {
         this.doDialogOK();
      });
      buttonPanel.add(new JLabel(VNAMessages.getString("VNACalSetDialog.selectedCalSet")), "");
      buttonPanel.add(this.btOK, "wmin 100px");
      this.initFormatters();
      this.getRootPane().setDefaultButton(this.btOK);
      this.doDialogInit();
      TraceHelper.exit(this, "VNACalSetDialog");
   }

   private Component createCalSetDetail() {
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill][][]", "[]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.calSetDetail"), 4, 2, (Font)null, (Color)null));
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.name")), "");
      this.txtName = new JTextField();
      this.txtName.setColumns(30);
      rc.add(this.txtName, "wrap");
      rc.add(this.createKitParameters(), "span 3, wrap");
      rc.add(this.createOpenCapacitanceCoefficients(), "span 3,wrap");
      rc.add(this.createShortInductance(), "span 2,grow");
      rc.add(this.createThruLength(), "wrap");
      this.btCalSetAbort = SwingUtil.createJButton("Button.Abort", (e) -> {
         this.doCalSetAbortEditOrAdd();
      });
      this.btCalSetAbort.setEnabled(false);
      rc.add(this.btCalSetAbort, "wmin 100px");
      this.cbSmith = SwingUtil.createToggleButton("Panel.Scale.Smith", this);
      this.cbSmith.setEnabled(false);
      rc.add(this.cbSmith, "center");
      this.btCalSetSave = SwingUtil.createJButton("Button.Save", (e) -> {
         this.doSaveCalSet();
      });
      this.btCalSetSave.setEnabled(false);
      rc.add(this.btCalSetSave, "wmin 100px,right");
      return rc;
   }

   private Component createCalSetList() {
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill][][]", "[grow,fill][]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.calSetList"), 4, 2, (Font)null, (Color)null));
      this.lbCalibrationSets = new VNACalibrationKitTable();
      this.lbCalibrationSets.setSelectionMode(0);
      this.lbCalibrationSets.addListSelectionListener(this);
      this.lbCalibrationSets.addMouseListener(new MouseListener() {
         public void mouseReleased(MouseEvent e) {
         }

         public void mousePressed(MouseEvent e) {
         }

         public void mouseExited(MouseEvent e) {
         }

         public void mouseEntered(MouseEvent e) {
         }

         public void mouseClicked(MouseEvent evt) {
         }
      });
      JScrollPane sp = new JScrollPane(this.lbCalibrationSets);
      rc.add(sp, "span 3,wrap");
      this.btCalSetAdd = SwingUtil.createJButton("Button.Add", (e) -> {
         this.doCalSetAdd();
      });
      rc.add(this.btCalSetAdd, "");
      this.btCalSetEdit = SwingUtil.createJButton("Button.Edit", (e) -> {
         this.doCalSetEdit();
      });
      this.btCalSetEdit.setEnabled(false);
      rc.add(this.btCalSetEdit, "");
      this.btCalSetDelete = SwingUtil.createJButton("Button.Delete", (e) -> {
         this.doCalSetDelete();
      });
      this.btCalSetDelete.setEnabled(false);
      rc.add(this.btCalSetDelete, "");
      return rc;
   }

   private Component createKitParameters() {
      JPanel pnlKitParms = new JPanel(new MigLayout("", "[20%][][]", "[][][]"));
      pnlKitParms.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.kitParms"), 4, 2, (Font)null, (Color)null));
      pnlKitParms.add(new JLabel());
      pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpOffset")), "right");
      pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpLoss")), "right, wrap");
      pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpOpen")), "");
      this.txtKpOpenOffset = new JTextField();
      this.txtKpOpenOffset.setHorizontalAlignment(4);
      this.txtKpOpenOffset.setColumns(10);
      pnlKitParms.add(this.txtKpOpenOffset, "");
      this.txtKpOpenLoss = new JTextField();
      this.txtKpOpenLoss.setHorizontalAlignment(4);
      this.txtKpOpenLoss.setColumns(10);
      pnlKitParms.add(this.txtKpOpenLoss, "wrap");
      pnlKitParms.add(new JLabel());
      this.sbOpenOffset = new JScrollBar(0, 0, 1, -300, 300);
      this.sbOpenOffset.addAdjustmentListener(this);
      pnlKitParms.add(this.sbOpenOffset, "grow,wrap");
      pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpShort")), "");
      this.txtKpShortOffset = new JTextField();
      this.txtKpShortOffset.setHorizontalAlignment(4);
      this.txtKpShortOffset.setColumns(10);
      pnlKitParms.add(this.txtKpShortOffset, "");
      this.txtKpShortLoss = new JTextField();
      this.txtKpShortLoss.setHorizontalAlignment(4);
      this.txtKpShortLoss.setColumns(10);
      pnlKitParms.add(this.txtKpShortLoss, "wrap");
      pnlKitParms.add(new JLabel());
      this.sbShortOffset = new JScrollBar(0, 0, 1, -300, 300);
      this.sbShortOffset.addAdjustmentListener(this);
      pnlKitParms.add(this.sbShortOffset, "grow,wrap");
      return pnlKitParms;
   }

   private Component createOpenCapacitanceCoefficients() {
      JPanel rc = new JPanel(new MigLayout("", "[][][]", "[][][]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.openCapCoeff"), 4, 2, (Font)null, (Color)null));
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC0")), "right");
      this.txtC0 = new JTextField();
      this.txtC0.setHorizontalAlignment(4);
      this.txtC0.setColumns(10);
      rc.add(this.txtC0, "");
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC1")), "right");
      this.txtC1 = new JTextField();
      this.txtC1.setHorizontalAlignment(4);
      this.txtC1.setColumns(10);
      rc.add(this.txtC1, "wrap");
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC2")), "right");
      this.txtC2 = new JTextField();
      this.txtC2.setHorizontalAlignment(4);
      this.txtC2.setColumns(10);
      rc.add(this.txtC2, "");
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblC3")), "right");
      this.txtC3 = new JTextField();
      this.txtC3.setHorizontalAlignment(4);
      this.txtC3.setColumns(10);
      rc.add(this.txtC3, "wrap");
      return rc;
   }

   private Component createShortInductance() {
      JPanel rc = new JPanel(new MigLayout("", "[][]", "[]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.shortInductance"), 4, 2, (Font)null, (Color)null));
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblL")), "right");
      this.txtShortInd = new JTextField();
      this.txtShortInd.setHorizontalAlignment(4);
      this.txtShortInd.setColumns(10);
      rc.add(this.txtShortInd, "");
      return rc;
   }

   private Component createThruLength() {
      JPanel rc = new JPanel(new MigLayout("", "[][]", "[]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.thruLength"), 4, 2, (Font)null, (Color)null));
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.lblLen")), "right");
      this.txtThruLen = new JTextField();
      this.txtThruLen.setHorizontalAlignment(4);
      this.txtThruLen.setColumns(10);
      rc.add(this.txtThruLen, "");
      return rc;
   }

   protected void doCalSetAbortEditOrAdd() {
      TraceHelper.entry(this, "doCalSetAbortEditOrAdd");
      this.enableFieldsAndButtons();
      this.valueChanged(new ListSelectionEvent(this.lbCalibrationSets, 0, 0, false));
      this.currentEditMode = VNACalibrationKitDialog.EDIT_MODES.NONE;
      if (this.smithDialog != null) {
         this.removeSmithDialog();
         this.cbSmith.setSelected(false);
      }

      TraceHelper.exit(this, "doCalSetAbortEditOrAdd");
   }

   protected void doCalSetAdd() {
      TraceHelper.entry(this, "doCalSetAdd");
      this.selectedCalibrationKit = new VNACalibrationKit("");
      this.transferDataToFields(this.selectedCalibrationKit);
      this.currentEditMode = VNACalibrationKitDialog.EDIT_MODES.ADD;
      this.enableFieldsAndButtons();
      TraceHelper.exit(this, "doCalSetAdd");
   }

   protected void doCalSetEdit() {
      TraceHelper.entry(this, "doCalSetEdit");
      this.currentEditMode = VNACalibrationKitDialog.EDIT_MODES.EDIT;
      this.selectedCalibrationKit = (VNACalibrationKit)this.lbCalibrationSets.getSelectedValue();
      this.transferDataToFields(this.selectedCalibrationKit);
      this.enableFieldsAndButtons();
      TraceHelper.exit(this, "doCalSetEdit");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doDialogCancel");
      TraceHelper.exit(this, "doDialogCancel");
   }

   protected void doCalSetDelete() {
      TraceHelper.entry(this, "doCalSetDelete");
      Object[] options = new Object[]{VNAMessages.getString("Button.Delete"), VNAMessages.getString("Button.Cancel")};
      int n = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), VNAMessages.getString("VNACalSetDialog.Delete.1"), VNAMessages.getString("VNACalSetDialog.Delete.2"), 0, 3, (Icon)null, options, options[0]);
      if (n == 0) {
         this.selectedCalibrationKit = null;
         VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.lbCalibrationSets.getModel();
         model.removeElement(this.lbCalibrationSets.getSelectedValue());
      }

      TraceHelper.exit(this, "doCalSetDelete");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doDialogInit");
      this.currentEditMode = VNACalibrationKitDialog.EDIT_MODES.NONE;
      this.enableFieldsAndButtons();
      VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.lbCalibrationSets.getModel();
      List<VNACalibrationKit> calSets = (new VNACalSetHelper()).load(this.config.getCalibrationKitFilename());
      Iterator var4 = calSets.iterator();

      VNACalibrationKit calSet;
      while(var4.hasNext()) {
         calSet = (VNACalibrationKit)var4.next();
         model.addElement(calSet);
      }

      var4 = calSets.iterator();

      while(var4.hasNext()) {
         calSet = (VNACalibrationKit)var4.next();
         if (calSet.getId().equals(this.config.getCurrentCalSetID())) {
            this.lbCalibrationSets.setSelectedValue(calSet, true);
         }
      }

      this.doDialogShow();
      TraceHelper.exit(this, "doDialogInit");
   }

   protected void doDialogOK() {
      String methodName = "doDialogOK";
      TraceHelper.entry(this, "doDialogOK");
      VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.lbCalibrationSets.getModel();
      (new VNACalSetHelper()).save(model.getData(), this.config.getCalibrationKitFilename());
      if (this.selectedCalibrationKit != null) {
         VNADataPool dataPool = VNADataPool.getSingleton();
         TraceHelper.text(this, "doDialogOK", "Setting calset [" + this.selectedCalibrationKit + "] into datapool");
         dataPool.setCalibrationKit(this.selectedCalibrationKit);

         try {
            VNACalibrationBlock reloadedBlock = VNACalibrationBlockHelper.load(dataPool.getMainCalibrationBlock().getFile(), dataPool.getDriver(), this.selectedCalibrationKit);
            this.mainFrame.setMainCalibrationBlock(reloadedBlock);
         } catch (ProcessingException var6) {
            ErrorLogHelper.exception(this, "doDialogOK", var6);
         }

         TraceHelper.text(this, "doDialogOK", "Setting calset [" + this.selectedCalibrationKit + "] as default calset");
         this.config.setCurrentCalSetID(this.selectedCalibrationKit.getId());
      }

      this.setVisible(false);
      this.dispose();
      TraceHelper.entry(this, "doDialogOK");
   }

   protected void doSaveCalSet() {
      TraceHelper.entry(this, "doSaveCalSet");
      this.selectedCalibrationKit = this.transferFieldsToData(this.selectedCalibrationKit.getId());
      if (this.selectedCalibrationKit != null) {
         if (this.currentEditMode == VNACalibrationKitDialog.EDIT_MODES.EDIT) {
            this.lbCalibrationSets.updateCalSet(this.selectedCalibrationKit);
         } else {
            this.lbCalibrationSets.addCalSet(this.selectedCalibrationKit);
         }

         this.currentEditMode = VNACalibrationKitDialog.EDIT_MODES.NONE;
         this.enableFieldsAndButtons();
      }

      TraceHelper.exit(this, "doSaveCalSet");
   }

   private void transferDataToFields(VNACalibrationKit calSet) {
      TraceHelper.entry(this, "transferDataToFields");
      if (calSet == null) {
         this.txtC0.setText("");
         this.txtC1.setText("");
         this.txtC2.setText("");
         this.txtC3.setText("");
         this.txtKpOpenOffset.setText("");
         this.txtKpShortOffset.setText("");
         this.txtThruLen.setText("");
         this.txtName.setText("");
         this.txtKpOpenLoss.setText("");
         this.txtKpShortLoss.setText("");
         this.txtThruLen.setText("");
      } else {
         this.txtC0.setText(this.formatCx.format(calSet.getOpenCapCoeffC0()));
         this.txtC1.setText(this.formatCx.format(calSet.getOpenCapCoeffC1()));
         this.txtC2.setText(this.formatCx.format(calSet.getOpenCapCoeffC2()));
         this.txtC3.setText(this.formatCx.format(calSet.getOpenCapCoeffC3()));
         this.txtKpOpenOffset.setText(this.formatLength.format(calSet.getOpenOffset()));
         this.txtKpOpenLoss.setText(this.formatLength.format(calSet.getOpenLoss()));
         this.txtKpShortOffset.setText(this.formatLength.format(calSet.getShortOffset()));
         this.txtKpShortLoss.setText(this.formatLength.format(calSet.getShortLoss()));
         this.txtName.setText(calSet.getName());
         this.txtThruLen.setText(this.formatLength.format(calSet.getThruLength()));
         this.txtShortInd.setText(this.formatLength.format(calSet.getShortInductance()));
         this.sbOpenOffset.setValue((int)(calSet.getOpenOffset() * 10.0D));
         this.sbShortOffset.setValue((int)(calSet.getShortOffset() * 10.0D));
      }

      TraceHelper.exit(this, "transferDataToFields");
   }

   private VNACalibrationKit transferFieldsToData(String id) {
      TraceHelper.entry(this, "transferFieldsToData");
      VNACalibrationKit rc = null;
      ValidationResults results = new ValidationResults();
      String name = StringValidator.parse(this.txtName.getText(), 1L, 20L, VNAMessages.getString("VNACalSetDialog.name"), results);
      double c0 = DoubleValidator.parse(this.txtC0.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.lblC0"), results);
      double c1 = DoubleValidator.parse(this.txtC1.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.lblC1"), results);
      double c2 = DoubleValidator.parse(this.txtC2.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.lblC2"), results);
      double c3 = DoubleValidator.parse(this.txtC3.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.lblC3"), results);
      double kpOpenLoss = DoubleValidator.parse(this.txtKpOpenLoss.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.kpOpen"), results);
      double kpShortLoss = DoubleValidator.parse(this.txtKpShortLoss.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.kpLoss"), results);
      double shortInductance = DoubleValidator.parse(this.txtShortInd.getText(), 0.0D, 10000.0D, VNAMessages.getString("VNACalSetDialog.shortInductance"), results);
      double kpShortOffset = DoubleValidator.parse(this.txtKpShortOffset.getText(), -100.0D, 100.0D, VNAMessages.getString("VNACalSetDialog.kpShortOffset"), results);
      double kpOpenOffset = DoubleValidator.parse(this.txtKpOpenOffset.getText(), -100.0D, 100.0D, VNAMessages.getString("VNACalSetDialog.kpOpenOffset"), results);
      double thruLength = DoubleValidator.parse(this.txtThruLen.getText(), -100.0D, 100.0D, VNAMessages.getString("VNACalSetDialog.thruLength"), results);
      if (results.isEmpty()) {
         rc = new VNACalibrationKit(name);
         rc.setId(id);
         rc.setOpenCapCoeffC0(c0);
         rc.setOpenCapCoeffC1(c1);
         rc.setOpenCapCoeffC2(c2);
         rc.setOpenCapCoeffC3(c3);
         rc.setOpenOffset(kpOpenOffset);
         rc.setOpenLoss(kpOpenLoss);
         rc.setShortOffset(kpShortOffset);
         rc.setShortLoss(kpShortLoss);
         rc.setThruLength(thruLength);
         rc.setShortInductance(shortInductance);
      } else {
         new ValidationResultsDialog(this.getOwner(), results, this.getTitle());
      }

      TraceHelper.exit(this, "transferFieldsToData");
      return rc;
   }

   private void enableFieldsAndButtons() {
      switch($SWITCH_TABLE$krause$vna$gui$calibrate$calibrationkit$VNACalibrationKitDialog$EDIT_MODES()[this.currentEditMode.ordinal()]) {
      case 1:
         this.txtC0.setEnabled(false);
         this.txtC1.setEnabled(false);
         this.txtC2.setEnabled(false);
         this.txtC3.setEnabled(false);
         this.txtKpOpenLoss.setEnabled(false);
         this.txtKpShortLoss.setEnabled(false);
         this.txtKpOpenOffset.setEnabled(false);
         this.txtKpShortOffset.setEnabled(false);
         this.txtName.setEnabled(false);
         this.txtShortInd.setEnabled(false);
         this.txtThruLen.setEnabled(false);
         this.sbOpenOffset.setEnabled(false);
         this.sbShortOffset.setEnabled(false);
         this.lbCalibrationSets.setEnabled(true);
         this.btCalSetAdd.setEnabled(true);
         this.btCalSetDelete.setEnabled(false);
         this.btCalSetAbort.setEnabled(false);
         this.btCalSetSave.setEnabled(false);
         this.cbSmith.setEnabled(false);
         this.btOK.setEnabled(true);
         break;
      case 2:
      case 3:
         this.txtC0.setEnabled(true);
         this.txtC1.setEnabled(true);
         this.txtC2.setEnabled(true);
         this.txtC3.setEnabled(true);
         this.txtKpOpenLoss.setEnabled(true);
         this.txtKpShortLoss.setEnabled(true);
         this.txtKpOpenOffset.setEnabled(true);
         this.txtKpShortOffset.setEnabled(true);
         this.txtName.setEnabled(true);
         this.txtShortInd.setEnabled(true);
         this.txtThruLen.setEnabled(true);
         this.sbOpenOffset.setEnabled(true);
         this.sbShortOffset.setEnabled(true);
         this.lbCalibrationSets.setEnabled(false);
         this.btCalSetAdd.setEnabled(false);
         this.btCalSetDelete.setEnabled(false);
         this.btCalSetEdit.setEnabled(false);
         this.btCalSetAbort.setEnabled(true);
         this.btCalSetSave.setEnabled(true);
         this.cbSmith.setEnabled(true);
         this.btOK.setEnabled(false);
      }

   }

   private void initFormatters() {
      this.formatCx = NumberFormat.getNumberInstance();
      this.formatCx.setGroupingUsed(false);
      this.formatCx.setMaximumFractionDigits(2);
      this.formatCx.setMinimumFractionDigits(2);
      this.formatCx.setMaximumIntegerDigits(4);
      this.formatCx.setMinimumIntegerDigits(1);
      this.formatLength = NumberFormat.getNumberInstance();
      this.formatLength.setGroupingUsed(false);
      this.formatLength.setMaximumFractionDigits(1);
      this.formatLength.setMinimumFractionDigits(1);
      this.formatLength.setMaximumIntegerDigits(4);
      this.formatLength.setMinimumIntegerDigits(1);
   }

   public void adjustmentValueChanged(AdjustmentEvent e) {
      boolean updateDiagram = false;
      if (e.getSource() == this.sbOpenOffset) {
         this.txtKpOpenOffset.setText(this.formatLength.format((double)this.sbOpenOffset.getValue() / 10.0D));
         updateDiagram = true;
      } else if (e.getSource() == this.sbShortOffset) {
         this.txtKpShortOffset.setText(this.formatLength.format((double)this.sbShortOffset.getValue() / 10.0D));
         updateDiagram = true;
      }

      if (!e.getValueIsAdjusting() && updateDiagram && this.smithDialog != null) {
         this.selectedCalibrationKit = this.transferFieldsToData(this.selectedCalibrationKit.getId());
         if (this.selectedCalibrationKit != null) {
            VNACalibratedSampleBlock calibratedSamples = this.doExecuteOneScan();
            this.smithDialog.consumeCalibratedData(calibratedSamples);
         }
      }

   }

   public void valueChanged(ListSelectionEvent e) {
      boolean isAdjusting = e.getValueIsAdjusting();
      String methodName = "valueChanged";
      TraceHelper.entry(this, "valueChanged", "adj=%b", isAdjusting);
      if (!isAdjusting) {
         this.enableFieldsAndButtons();
         int selIdx = this.lbCalibrationSets.getSelectedIndex();
         if (selIdx == -1) {
            this.selectedCalibrationKit = null;
            this.btCalSetDelete.setEnabled(false);
            this.btCalSetEdit.setEnabled(false);
            this.btCalSetAdd.setEnabled(true);
         } else {
            this.selectedCalibrationKit = (VNACalibrationKit)this.lbCalibrationSets.getSelectedValue();
            this.btCalSetDelete.setEnabled(true);
            this.btCalSetEdit.setEnabled(true);
            this.btCalSetAdd.setEnabled(true);
         }

         this.transferDataToFields(this.selectedCalibrationKit);
      }

      TraceHelper.exit(this, "valueChanged");
   }

   public void actionPerformed(ActionEvent e) {
      String methodName = "actionPerformed";
      TraceHelper.entry(this, "actionPerformed", "enabled=%b", this.cbSmith.isSelected());
      if (this.cbSmith.isSelected()) {
         if (this.smithDialog == null) {
            this.setupSmithDialog();
         }
      } else if (this.smithDialog != null) {
         this.removeSmithDialog();
         this.cbSmith.setSelected(false);
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   private void removeSmithDialog() {
      String methodName = "removeSmithDialog";
      TraceHelper.entry(this, "removeSmithDialog");
      this.smithDialog.setVisible(false);
      this.smithDialog.dispose();
      this.smithDialog = null;
      TraceHelper.exit(this, "removeSmithDialog");
   }

   private void setupSmithDialog() {
      String methodName = "setupSmithDialog";
      TraceHelper.entry(this, "setupSmithDialog");
      this.smithDialog = new VNACalibrationKitSmithDiagramDialog(this);
      this.smithDialog.setLocation(this.getX() + this.getWidth(), this.getY());
      this.smithDialog.setVisible(true);
      VNACalibratedSampleBlock calibratedSamples = this.doExecuteOneScan();
      this.smithDialog.consumeCalibratedData(calibratedSamples);
      TraceHelper.exit(this, "setupSmithDialog");
   }

   private VNACalibratedSampleBlock doExecuteOneScan() {
      String methodName = "doExecuteOneScan";
      VNACalibratedSampleBlock calibratedSamples = null;
      TraceHelper.entry(this, "doExecuteOneScan");
      VNADataPool pool = VNADataPool.getSingleton();
      IVNADriver driver = pool.getDriver();
      IVNADriverMathHelper mathHelper = driver.getMathHelper();
      VNADeviceInfoBlock dib = driver.getDeviceInfoBlock();
      long startFreq = 1600000000L;
      long stopFreq = dib.getMaxFrequency();
      File file = pool.getMainCalibrationBlock().getFile();

      try {
         VNACalibrationBlock mcb = VNACalibrationBlockHelper.load(file, driver, this.selectedCalibrationKit);
         VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mcb, 1600000000L, stopFreq, 200);
         VNASampleBlock rawData = driver.scan(VNAScanMode.MODE_REFLECTION, 1600000000L, stopFreq, 200, this);
         VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
         context.setConversionTemperature(rawData.getDeviceTemperature());
         calibratedSamples = mathHelper.createCalibratedSamples(context, rawData);
      } catch (ProcessingException var16) {
         ErrorLogHelper.exception(this, "doExecuteOneScan", var16);
      }

      TraceHelper.exit(this, "doExecuteOneScan");
      return calibratedSamples;
   }

   public void publishProgress(int percentage) {
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$krause$vna$gui$calibrate$calibrationkit$VNACalibrationKitDialog$EDIT_MODES() {
      int[] var10000 = $SWITCH_TABLE$krause$vna$gui$calibrate$calibrationkit$VNACalibrationKitDialog$EDIT_MODES;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[VNACalibrationKitDialog.EDIT_MODES.values().length];

         try {
            var0[VNACalibrationKitDialog.EDIT_MODES.ADD.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[VNACalibrationKitDialog.EDIT_MODES.EDIT.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[VNACalibrationKitDialog.EDIT_MODES.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$krause$vna$gui$calibrate$calibrationkit$VNACalibrationKitDialog$EDIT_MODES = var0;
         return var0;
      }
   }

   private static enum EDIT_MODES {
      NONE,
      EDIT,
      ADD;
   }
}
