package krause.vna.gui.raw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import krause.common.exception.ProcessingException;
import krause.common.gui.DocumentSizeFilter;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNARawCommentField extends JPanel implements PropertyChangeListener, ActionListener {
   private JTextArea txtComment;
   private Window owner = null;

   public VNARawCommentField(Window owner, boolean saveMode) {
      this.setLayout(new MigLayout("", "[grow,fill]", "[]"));
      this.setBorder(new TitledBorder(VNAMessages.getString("VNARawCommentField.title")));
      if (saveMode) {
         this.add(new JLabel(VNAMessages.getString("VNARawCommentField.hint")), "wrap");
      }

      this.txtComment = new JTextArea(10, 40);
      this.txtComment.setFont(new Font("Courier New", 0, 12));
      this.txtComment.setLineWrap(true);
      this.txtComment.setWrapStyleWord(true);
      this.txtComment.setEditable(saveMode);
      this.txtComment.setVisible(saveMode);
      this.txtComment.setOpaque(true);
      AbstractDocument pDoc = (AbstractDocument)this.txtComment.getDocument();
      pDoc.setDocumentFilter(new DocumentSizeFilter(512));
      JScrollPane scrollPane = new JScrollPane(this.txtComment);
      this.add(scrollPane, "growy");
   }

   public void setText(String string) {
      this.txtComment.setText(string);
   }

   public String getText() {
      return this.txtComment.getText();
   }

   public void propertyChange(PropertyChangeEvent e) {
      String pname = e.getPropertyName();
      if ("SelectedFileChangedProperty".equals(pname)) {
         File file = (File)e.getNewValue();

         try {
            String cmt = (new VNARawHandler(this.owner)).readComment(file);
            this.txtComment.setBackground(this.getBackground());
            if (cmt != null) {
               this.txtComment.setText(cmt);
               this.txtComment.setVisible(true);
            } else {
               this.txtComment.setText("");
               this.txtComment.setVisible(false);
            }
         } catch (ProcessingException var6) {
            this.txtComment.setBackground(Color.RED);
            this.txtComment.setText(var6.getLocalizedMessage());
         }
      }

   }

   public void actionPerformed(ActionEvent e) {
   }
}
