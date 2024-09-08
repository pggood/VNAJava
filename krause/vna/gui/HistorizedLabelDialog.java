package krause.vna.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;

public class HistorizedLabelDialog extends KrauseDialog {
   private HistorizedLabelTable tblStatus;
   private JScrollPane tablePane;
   private JPanel pnlStatus;

   public HistorizedLabelDialog(Frame aFrame, List<HistorizedLabelEntry> data) {
      super((Window)aFrame, true);
      TraceHelper.entry(this, "HistorizedLabelDialog");
      JPanel pnlButton = new JPanel();
      this.getContentPane().add(pnlButton, "South");
      JButton btOK = new JButton("OK");
      btOK.addActionListener((e) -> {
         this.doDialogCancel();
      });
      pnlButton.add(btOK);
      this.pnlStatus = new JPanel();
      this.getContentPane().add(this.pnlStatus, "Center");
      this.tblStatus = new HistorizedLabelTable(data);
      this.tablePane = new JScrollPane(this.tblStatus);
      this.tablePane.setPreferredSize(new Dimension(500, 300));
      this.tablePane.setMinimumSize(this.tablePane.getPreferredSize());
      this.tablePane.setAlignmentX(0.0F);
      this.pnlStatus.add(this.tablePane);
      this.doDialogInit();
      TraceHelper.exit(this, "HistorizedLabelDialog");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      this.showCenteredOnScreen();
      TraceHelper.exit(this, "doInit");
   }
}
