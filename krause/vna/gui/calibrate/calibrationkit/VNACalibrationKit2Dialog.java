package krause.vna.gui.calibrate.calibrationkit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.common.validation.DoubleValidator;
import krause.common.validation.StringValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalSetHelper;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACalibrationKit2Dialog extends KrauseDialog {
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
   private VNACalibrationKit2Dialog.EDIT_MODES currentEditMode;
   private transient VNACalibrationKit currentSelectedCalSet;
   private NumberFormat formatCx;
   private NumberFormat formatLength;
   private transient VNAMainFrame mainFrame;

   public VNACalibrationKit2Dialog(VNAMainFrame pMainFrame) {
      super((Window)pMainFrame.getJFrame(), true);
      this.currentEditMode = VNACalibrationKit2Dialog.EDIT_MODES.NONE;
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
      JPanel rc = new JPanel(new MigLayout("", "[50%][]", "[][][]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.calSetDetail"), 4, 2, (Font)null, (Color)null));
      rc.add(new JLabel(VNAMessages.getString("VNACalSetDialog.name")), "");
      this.txtName = new JTextField();
      this.txtName.setColumns(30);
      rc.add(this.txtName, "wrap");
      rc.add(this.createKitParameters(), "span 2,wrap");
      rc.add(this.createOpenCapacitanceCoefficients(), "span 2,wrap");
      rc.add(this.createShortInductance(), "grow");
      rc.add(this.createThruLength(), "wrap");
      this.btCalSetSave = SwingUtil.createJButton("Button.Save", (e) -> {
         this.doSaveCalSet();
      });
      this.btCalSetSave.setEnabled(false);
      rc.add(this.btCalSetSave, "wmin 100px");
      this.btCalSetAbort = SwingUtil.createJButton("Button.Abort", (e) -> {
         this.doCalSetAbortEditOrAdd();
      });
      this.btCalSetAbort.setEnabled(false);
      rc.add(this.btCalSetAbort, "wmin 100px,right");
      return rc;
   }

   private Component createCalSetList() {
      JPanel rc = new JPanel(new MigLayout("", "[grow,fill][][]", "[grow,fill][]"));
      rc.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.calSetList"), 4, 2, (Font)null, (Color)null));
      this.lbCalibrationSets = new VNACalibrationKitTable();
      this.lbCalibrationSets.setSelectionMode(0);
      this.lbCalibrationSets.addListSelectionListener((e) -> {
         this.handleCalSetListSelection(e);
      });
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
            JList list = (JList)evt.getSource();
            if (evt.getClickCount() == 2) {
               int index = list.locationToIndex(evt.getPoint());
               System.out.println("index: " + index);
            }

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
      JPanel pnlKitParms = new JPanel(new MigLayout("", "[][][]", "[][][]"));
      pnlKitParms.setBorder(new TitledBorder((Border)null, VNAMessages.getString("VNACalSetDialog.kitParms"), 4, 2, (Font)null, (Color)null));
      pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpOffset")), "right, span 2");
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
      pnlKitParms.add(new JLabel(VNAMessages.getString("VNACalSetDialog.kpShort")), "");
      this.txtKpShortOffset = new JTextField();
      this.txtKpShortOffset.setHorizontalAlignment(4);
      this.txtKpShortOffset.setColumns(10);
      pnlKitParms.add(this.txtKpShortOffset, "");
      this.txtKpShortLoss = new JTextField();
      this.txtKpShortLoss.setHorizontalAlignment(4);
      this.txtKpShortLoss.setColumns(10);
      pnlKitParms.add(this.txtKpShortLoss, "wrap");
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
      this.enableEditFields(false);
      this.btCalSetAdd.setEnabled(true);
      this.btCalSetDelete.setEnabled(false);
      this.btCalSetAbort.setEnabled(false);
      this.btCalSetSave.setEnabled(false);
      this.currentEditMode = VNACalibrationKit2Dialog.EDIT_MODES.NONE;
      TraceHelper.exit(this, "doCalSetAbortEditOrAdd");
   }

   protected void doCalSetAdd() {
      TraceHelper.entry(this, "doCalSetAdd");
      this.btCalSetAdd.setEnabled(false);
      this.btCalSetDelete.setEnabled(false);
      this.btCalSetEdit.setEnabled(false);
      this.btCalSetAbort.setEnabled(true);
      this.btCalSetSave.setEnabled(true);
      this.currentSelectedCalSet = new VNACalibrationKit("");
      this.transferDataToFields(this.currentSelectedCalSet);
      this.currentEditMode = VNACalibrationKit2Dialog.EDIT_MODES.ADD;
      this.enableEditFields(true);
      TraceHelper.exit(this, "doCalSetAdd");
   }

   protected void doCalSetEdit() {
      TraceHelper.entry(this, "doCalSetEdit");
      this.btCalSetAdd.setEnabled(false);
      this.btCalSetDelete.setEnabled(false);
      this.btCalSetEdit.setEnabled(false);
      this.btCalSetAbort.setEnabled(true);
      this.btCalSetSave.setEnabled(true);
      this.currentEditMode = VNACalibrationKit2Dialog.EDIT_MODES.EDIT;
      this.currentSelectedCalSet = (VNACalibrationKit)this.lbCalibrationSets.getSelectedValue();
      this.transferDataToFields(this.currentSelectedCalSet);
      this.enableEditFields(true);
      TraceHelper.exit(this, "doCalSetEdit");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doDialogCancel");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doDialogCancel");
   }

   protected void doCalSetDelete() {
      TraceHelper.entry(this, "doCalSetDelete");
      Object[] options = new Object[]{VNAMessages.getString("Button.Delete"), VNAMessages.getString("Button.Cancel")};
      int n = JOptionPane.showOptionDialog(this.mainFrame.getJFrame(), VNAMessages.getString("VNACalSetDialog.Delete.1"), VNAMessages.getString("VNACalSetDialog.Delete.2"), 0, 3, (Icon)null, options, options[0]);
      if (n == 0) {
         this.currentSelectedCalSet = null;
         VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.lbCalibrationSets.getModel();
         model.removeElement(this.lbCalibrationSets.getSelectedValue());
      }

      TraceHelper.exit(this, "doCalSetDelete");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doDialogInit");
      this.enableEditFields(false);
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
      TraceHelper.entry(this, "doDialogOK");
      VNACalibrationKitTableListModel model = (VNACalibrationKitTableListModel)this.lbCalibrationSets.getModel();
      (new VNACalSetHelper()).save(model.getData(), this.config.getCalibrationKitFilename());
      if (this.currentSelectedCalSet != null) {
         VNADataPool dataPool = VNADataPool.getSingleton();
         TraceHelper.text(this, "doDialogOK", "Setting calset [" + this.currentSelectedCalSet + "] into datapool");
         dataPool.setCalibrationKit(this.currentSelectedCalSet);
         TraceHelper.text(this, "doDialogOK", "Clearing pre-calculated calibrationblocks in datapool");

         try {
            VNACalibrationBlock reloadedBlock = VNACalibrationBlockHelper.load(dataPool.getMainCalibrationBlock().getFile(), dataPool.getDriver(), this.currentSelectedCalSet);
            this.mainFrame.setMainCalibrationBlock(reloadedBlock);
         } catch (ProcessingException var5) {
            ErrorLogHelper.exception(this, "doDialogOK", var5);
         }

         TraceHelper.text(this, "doDialogOK", "Setting calset [" + this.currentSelectedCalSet + "] as default calset");
         this.config.setCurrentCalSetID(this.currentSelectedCalSet.getId());
      }

      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doDialogOK");
   }

   protected void doSaveCalSet() {
      TraceHelper.entry(this, "doSaveCalSet");
      if (this.transferFieldsToData(this.currentSelectedCalSet)) {
         this.enableEditFields(false);
         this.btCalSetAdd.setEnabled(true);
         this.btCalSetDelete.setEnabled(true);
         this.btCalSetAbort.setEnabled(false);
         this.btCalSetSave.setEnabled(false);
         if (this.currentEditMode == VNACalibrationKit2Dialog.EDIT_MODES.EDIT) {
            this.lbCalibrationSets.updateCalSet(this.currentSelectedCalSet);
         } else {
            this.lbCalibrationSets.addCalSet(this.currentSelectedCalSet);
         }

         this.currentEditMode = VNACalibrationKit2Dialog.EDIT_MODES.NONE;
      }

      TraceHelper.exit(this, "doSaveCalSet");
   }

   protected void handleCalSetListSelection(ListSelectionEvent e) {
      TraceHelper.entry(this, "handleCalSetListSelection");
      if (!e.getValueIsAdjusting()) {
         int selIdx = this.lbCalibrationSets.getSelectedIndex();
         if (selIdx == -1) {
            this.currentSelectedCalSet = null;
            this.btCalSetDelete.setEnabled(false);
            this.btCalSetEdit.setEnabled(false);
            this.btCalSetAdd.setEnabled(true);
         } else {
            this.currentSelectedCalSet = (VNACalibrationKit)this.lbCalibrationSets.getSelectedValue();
            this.enableEditFields(false);
            this.btCalSetDelete.setEnabled(true);
            this.btCalSetEdit.setEnabled(true);
            this.btCalSetAdd.setEnabled(true);
         }
      }

      this.transferDataToFields(this.currentSelectedCalSet);
      TraceHelper.exit(this, "handleCalSetListSelection");
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
      }

      TraceHelper.exit(this, "transferDataToFields");
   }

   private boolean transferFieldsToData(VNACalibrationKit calSetToUpdate) {
      TraceHelper.entry(this, "transferFieldsToData");
      boolean rc = false;
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
         calSetToUpdate.setName(name);
         calSetToUpdate.setOpenCapCoeffC0(c0);
         calSetToUpdate.setOpenCapCoeffC1(c1);
         calSetToUpdate.setOpenCapCoeffC2(c2);
         calSetToUpdate.setOpenCapCoeffC3(c3);
         calSetToUpdate.setOpenOffset(kpOpenOffset);
         calSetToUpdate.setOpenLoss(kpOpenLoss);
         calSetToUpdate.setShortOffset(kpShortOffset);
         calSetToUpdate.setShortLoss(kpShortLoss);
         calSetToUpdate.setThruLength(thruLength);
         calSetToUpdate.setShortInductance(shortInductance);
         rc = true;
      } else {
         new ValidationResultsDialog(this.getOwner(), results, this.getTitle());
      }

      TraceHelper.exit(this, "transferFieldsToData");
      return rc;
   }

   private void enableEditFields(boolean enabled) {
      this.txtC0.setEnabled(enabled);
      this.txtC1.setEnabled(enabled);
      this.txtC2.setEnabled(enabled);
      this.txtC3.setEnabled(enabled);
      this.txtKpOpenLoss.setEnabled(enabled);
      this.txtKpShortLoss.setEnabled(enabled);
      this.txtKpOpenOffset.setEnabled(enabled);
      this.txtKpShortOffset.setEnabled(enabled);
      this.txtName.setEnabled(enabled);
      this.txtShortInd.setEnabled(enabled);
      this.txtThruLen.setEnabled(enabled);
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

   private static enum EDIT_MODES {
      NONE,
      EDIT,
      ADD;
   }
}
