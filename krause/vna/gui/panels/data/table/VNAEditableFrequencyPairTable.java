package krause.vna.gui.panels.data.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.resources.VNAMessages;

public class VNAEditableFrequencyPairTable extends JPanel implements ActionListener, MouseListener {
   private VNAFrequencyPairTable tblFrequencies;
   private JButton buttonDelete;
   private JButton buttonAdd;
   private JButton buttonUse;
   private EventListenerList listenerList = new EventListenerList();

   public VNAEditableFrequencyPairTable() {
      TraceHelper.entry(this, "VNAEditableFrequencyPairTable");
      this.createComponents();
      TraceHelper.exit(this, "VNAEditableFrequencyPairTable");
   }

   private void createComponents() {
      TraceHelper.entry(this, "createComponents");
      this.setLayout(new BorderLayout());
      this.tblFrequencies = new VNAFrequencyPairTable();
      this.tblFrequencies.addMouseListener(this);
      this.tblFrequencies.setToolTipText(VNAMessages.getString("Panel.Data.FrequencyList.Tooltip"));
      JScrollPane tablePane = new JScrollPane(this.tblFrequencies);
      tablePane.setPreferredSize(new Dimension(150, 100));
      tablePane.setMinimumSize(tablePane.getPreferredSize());
      tablePane.setAlignmentX(0.0F);
      this.add(tablePane, "Center");
      JPanel panel1 = new JPanel(new FlowLayout());
      this.buttonAdd = SwingUtil.createToolbarButton("Button.Icon.Add", this);
      this.buttonDelete = SwingUtil.createToolbarButton("Button.Icon.Delete", this);
      this.buttonUse = SwingUtil.createToolbarButton("Button.Icon.Use", this);
      panel1.add(this.buttonAdd);
      panel1.add(this.buttonDelete);
      panel1.add(this.buttonUse);
      this.add(panel1, "South");
      this.buttonUse.setEnabled(false);
      this.buttonDelete.setEnabled(false);
      TraceHelper.exit(this, "createComponents");
   }

   public void addActionListener(ActionListener l) {
      this.listenerList.add(ActionListener.class, l);
   }

   public void removeActionListener(ActionListener l) {
      this.listenerList.remove(ActionListener.class, l);
   }

   protected void fireAction(String command, VNAFrequencyPair fp) {
      Object[] listeners = this.listenerList.getListenerList();
      if (fp != null) {
         command = command + ";";
         command = command + fp.getStartFrequency() + ";";
         command = command + fp.getStopFrequency() + ";";
      }

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ActionListener.class) {
            ActionEvent actionEvent = new ActionEvent(this, 123, command, 0);
            ((ActionListener)listeners[i + 1]).actionPerformed(actionEvent);
         }
      }

   }

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      int row = this.tblFrequencies.getSelectedRow();
      if (e.getSource() == this.buttonAdd) {
         this.fireAction("ADD", (VNAFrequencyPair)null);
      } else {
         VNAFrequencyPair fp;
         if (e.getSource() == this.buttonDelete) {
            if (row != -1) {
               fp = (VNAFrequencyPair)this.tblFrequencies.getModel().getData().get(row);
               this.tblFrequencies.getModel().getData().remove(row);
               this.tblFrequencies.getModel().fireTableDataChanged();
               this.fireAction("DEL", fp);
               this.tblFrequencies.getSelectionModel().setSelectionInterval(-1, -1);
               this.buttonDelete.setEnabled(false);
               this.buttonUse.setEnabled(false);
            }
         } else if (e.getSource() == this.buttonUse && row != -1) {
            fp = (VNAFrequencyPair)this.tblFrequencies.getModel().getData().get(row);
            this.fireAction("USE", fp);
         }
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void mouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "mouseClicked");
      VNAFrequencyPairTable tbl = (VNAFrequencyPairTable)e.getSource();
      if (tbl.isEnabled()) {
         int row = tbl.getSelectedRow();
         if (e.getButton() == 1) {
            if (e.getClickCount() == 1) {
               this.buttonDelete.setEnabled(row != -1);
               this.buttonUse.setEnabled(row != -1);
            } else if (e.getClickCount() > 1) {
               VNAFrequencyPair fp = (VNAFrequencyPair)tbl.getModel().getData().get(row);
               this.fireAction("USE", fp);
            }
         }
      }

      TraceHelper.exit(this, "mouseClicked");
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void addFrequency(VNAFrequencyPair pair) {
      this.tblFrequencies.addFrequencyPair(pair);
   }

   public void save(String string) {
      TraceHelper.entry(this, "save");
      this.tblFrequencies.save(string);
      TraceHelper.exit(this, "save");
   }

   public void load(String string) {
      TraceHelper.entry(this, "load");
      this.tblFrequencies.load(string);
      TraceHelper.exit(this, "load");
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      this.tblFrequencies.setEnabled(enabled);
      this.buttonAdd.setEnabled(enabled);
      this.buttonDelete.setEnabled(enabled);
      this.buttonUse.setEnabled(enabled);
   }

   public JButton getButtonUse() {
      return this.buttonUse;
   }

   public List<VNAFrequencyPair> getFrequencyPairs() {
      return this.tblFrequencies.getModel().getData();
   }
}
