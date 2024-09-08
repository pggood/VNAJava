package krause.vna.gui;

import java.awt.Window;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import krause.vna.resources.VNAMessages;

public interface OptionDialogHelper {
   static void showExceptionDialog(Window mainFrame, String titleId, String messageId, Exception ex) {
      String msg = MessageFormat.format(VNAMessages.getString(messageId), ex.getLocalizedMessage(), ex.getClass().getName());
      String title = VNAMessages.getString(titleId);
      JOptionPane.showMessageDialog(mainFrame, msg, title, 0);
   }

   static void showInfoDialog(Window mainFrame, String titleId, String messageId) {
      String msg = VNAMessages.getString(messageId);
      String title = VNAMessages.getString(titleId);
      JOptionPane.showMessageDialog(mainFrame, msg, title, 1);
   }
}
