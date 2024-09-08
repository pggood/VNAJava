package krause.vna.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.help.VNAHelpDialog;
import krause.vna.resources.VNAMessages;

public class HelpButton extends JButton {
   private String helpID;

   public HelpButton(final Frame owner, String pHelpID) {
      super(VNAMessages.getString("Button.Help"));
      this.setToolTipText(VNAMessages.getString("Button.Help.Tooltip"));
      this.setActionCommand(VNAMessages.getString("Button.Help.Command"));
      this.setMnemonic(VNAMessages.getString("Button.Help.Key").charAt(0));
      this.setHelpID(pHelpID);
      this.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            new VNAHelpDialog(owner, HelpButton.this.getHelpID());
            TraceHelper.exit(this, "actionPerformed");
         }
      });
   }

   public HelpButton(final Dialog owner, String pHelpID) {
      super(VNAMessages.getString("Button.Help"));
      this.setHelpID(pHelpID);
      this.setToolTipText(VNAMessages.getString("Button.Help.Tooltip"));
      this.setActionCommand(VNAMessages.getString("Button.Help.Command"));
      this.setMnemonic(VNAMessages.getString("Button.Help.Key").charAt(0));
      this.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            TraceHelper.entry(this, "actionPerformed");
            new VNAHelpDialog(owner, HelpButton.this.getHelpID());
            TraceHelper.exit(this, "actionPerformed");
         }
      });
   }

   public String getHelpID() {
      return this.helpID;
   }

   public void setHelpID(String helpID) {
      this.helpID = helpID;
   }
}
