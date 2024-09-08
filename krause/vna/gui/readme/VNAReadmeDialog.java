package krause.vna.gui.readme;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;

public class VNAReadmeDialog extends KrauseDialog implements ActionListener {
   public VNAReadmeDialog(VNAMainFrame f) {
      super((Window)f.getJFrame(), true);
      this.setDefaultCloseOperation(2);
      StringBuffer t = new StringBuffer(32000);

      try {
         JEditorPane htmlPane = new JTextPane();
         htmlPane.setEditorKit(new HTMLEditorKit());
         InputStream fr = this.getClass().getResourceAsStream("/krause/vna/resources/readme.html");
         BufferedReader d = new BufferedReader(new InputStreamReader(fr));

         String line;
         while((line = d.readLine()) != null) {
            t.append(line);
         }

         d.close();
         fr.close();
         t.append("<h1>System properties</h1>");
         t.append("<div style=\"margin-left: 30px;\"><table>");
         Properties props = System.getProperties();
         Set<Object> keys = props.keySet();
         Iterator var11 = keys.iterator();

         while(var11.hasNext()) {
            Object key = var11.next();
            t.append("<tr><td>");
            t.append(key);
            t.append("=");
            t.append(props.getProperty((String)key));
            t.append("</td></tr>");
         }

         t.append("</table></div>");
         t.append("</body></html>");
         htmlPane.setText(t.toString());
         JPanel panel = new JPanel();
         this.getContentPane().add(panel, "South");
         JButton button = new JButton(VNAMessages.getString("Button.Close"));
         panel.add(button);
         button.addActionListener(this);
         this.getRootPane().setDefaultButton(button);
         htmlPane.setEditable(false);
         JScrollPane scrollPane = new JScrollPane(htmlPane);
         this.getContentPane().add(scrollPane, "Center");
         this.setTitle(VNAMessages.getString("Dlg.Readme.1"));
         htmlPane.setSelectionStart(0);
         htmlPane.setSelectionEnd(0);
         this.doDialogInit();
      } catch (IOException var12) {
         ErrorLogHelper.exception(this, "VNAReadmeDialog", var12);
         this.setVisible(false);
         this.dispose();
      }

   }

   public void actionPerformed(ActionEvent e) {
      this.doDialogCancel();
   }

   protected void doDialogCancel() {
      this.setVisible(false);
      this.dispose();
   }

   protected void doDialogInit() {
      this.addEscapeKey();
      this.showCentered(800, 480);
   }
}
