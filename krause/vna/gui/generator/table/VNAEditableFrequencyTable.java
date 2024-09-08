package krause.vna.gui.generator.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNAEditableFrequencyTable extends JPanel implements ActionListener, MouseListener {
   private VNAFrequencyTable tblFrequencies;
   private JButton buttonDelete;
   private JButton buttonAdd;
   private JButton buttonUse;
   protected EventListenerList listenerList = new EventListenerList();

   public VNAEditableFrequencyTable() {
      TraceHelper.exit(this, "VNAEditableFrequencyTable");
      this.createComponents();
      TraceHelper.exit(this, "VNAEditableFrequencyTable");
   }

   private void createComponents() {
      TraceHelper.entry(this, "createComponents");
      this.setLayout(new BorderLayout());
      this.tblFrequencies = new VNAFrequencyTable();
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

   public void actionPerformed(ActionEvent e) {
      TraceHelper.entry(this, "actionPerformed");
      TraceHelper.text(this, "actionPerformed", e.toString());
      int row = this.tblFrequencies.getSelectedRow();
      if (e.getSource() == this.buttonAdd) {
         this.fireAction("ADD", 0L);
      } else if (e.getSource() == this.buttonDelete) {
         if (row != -1) {
            this.tblFrequencies.getModel().getData().remove(row);
            this.tblFrequencies.getModel().fireTableDataChanged();
            this.fireAction("DEL", 0L);
            this.tblFrequencies.getSelectionModel().setSelectionInterval(-1, -1);
            this.buttonDelete.setEnabled(false);
            this.buttonUse.setEnabled(false);
         }
      } else if (e.getSource() == this.buttonUse && row != -1) {
         this.fireAction("USE", (Long)this.tblFrequencies.getModel().getData().get(row));
      }

      TraceHelper.exit(this, "actionPerformed");
   }

   public void mouseClicked(MouseEvent e) {
      TraceHelper.entry(this, "mouseClicked");
      VNAFrequencyTable tbl = (VNAFrequencyTable)e.getSource();
      int row = tbl.getSelectedRow();
      if (e.getButton() == 1) {
         if (e.getClickCount() == 1) {
            this.buttonDelete.setEnabled(row != -1);
            this.buttonUse.setEnabled(row != -1);
         } else if (e.getClickCount() > 1) {
            Long freq = (Long)tbl.getModel().getData().get(row);
            this.fireAction("FRQ", freq);
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

   public void addActionListener(ActionListener l) {
      this.listenerList.add(ActionListener.class, l);
   }

   public void removeActionListener(ActionListener l) {
      this.listenerList.remove(ActionListener.class, l);
   }

   protected void fireAction(String command, long value) {
      Object[] listeners = this.listenerList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ActionListener.class) {
            ActionEvent actionEvent = new ActionEvent(this, -1, command, value, 0);
            ((ActionListener)listeners[i + 1]).actionPerformed(actionEvent);
         }
      }

   }

   public void addFrequency(Long pair) {
      this.tblFrequencies.addFrequency(pair);
   }

   public void load(String fn) {
      this.tblFrequencies.load(fn);
   }

   public void save(String fn) {
      this.tblFrequencies.save(fn);
   }
}
