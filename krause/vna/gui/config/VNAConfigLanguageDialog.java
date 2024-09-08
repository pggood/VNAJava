package krause.vna.gui.config;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAConfigLanguageDialog extends KrauseDialog {
   private VNAConfig config = VNAConfig.getSingleton();
   private JRadioButton rbCS;
   private JRadioButton rbDE;
   private JRadioButton rbEN;
   private JRadioButton rbES;
   private JRadioButton rbFR;
   private JRadioButton rbHU;
   private JRadioButton rbIT;
   private JRadioButton rbJP;
   private JRadioButton rbNL;
   private JRadioButton rbPL;
   private JRadioButton rbSE;
   private JRadioButton rbRU;
   private JRadioButton rbSYS;

   public VNAConfigLanguageDialog(Frame aFrame) {
      super((Window)aFrame, true);
      TraceHelper.entry(this, "VNAConfigLanguageDialog");
      this.setTitle(VNAMessages.getString("VNAConfigLanguageDialog.title"));
      this.setProperties(this.config);
      this.setConfigurationPrefix("VNAConfigLanguageDialog");
      this.setDefaultCloseOperation(0);
      this.setPreferredSize(new Dimension(300, 220));
      JPanel pnlButtons = new JPanel();
      this.getContentPane().add(pnlButtons, "South");
      pnlButtons.add(new HelpButton(this, "VNAConfigLanguageDialog"));
      JButton btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAConfigLanguageDialog.this.doDialogCancel();
         }
      });
      pnlButtons.add(btCancel);
      JButton btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            VNAConfigLanguageDialog.this.doOK();
         }
      });
      pnlButtons.add(btOK);
      JPanel pnlCenter = new JPanel();
      this.getContentPane().add(pnlCenter, "Center");
      pnlCenter.setLayout(new MigLayout("", "", ""));
      this.rbSYS = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbSYS"));
      pnlCenter.add(this.rbSYS, "wrap");
      this.rbCS = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbCS"));
      pnlCenter.add(this.rbCS, "");
      this.rbDE = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbDE"));
      pnlCenter.add(this.rbDE, "");
      this.rbEN = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbEN"));
      pnlCenter.add(this.rbEN, "wrap");
      this.rbHU = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbHU"));
      pnlCenter.add(this.rbHU, "");
      this.rbPL = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbPL"));
      pnlCenter.add(this.rbPL, "");
      this.rbSE = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbSV"));
      pnlCenter.add(this.rbSE, "wrap");
      this.rbIT = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbIT"));
      pnlCenter.add(this.rbIT, "");
      this.rbES = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbES"));
      pnlCenter.add(this.rbES, "");
      this.rbNL = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbNL"));
      pnlCenter.add(this.rbNL, "wrap");
      this.rbFR = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbFR"));
      pnlCenter.add(this.rbFR, "");
      this.rbJP = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbJP"));
      pnlCenter.add(this.rbJP, "");
      this.rbRU = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbRU"));
      pnlCenter.add(this.rbRU, "");
      ButtonGroup bg = new ButtonGroup();
      bg.add(this.rbDE);
      bg.add(this.rbEN);
      bg.add(this.rbHU);
      bg.add(this.rbPL);
      bg.add(this.rbSE);
      bg.add(this.rbSYS);
      bg.add(this.rbCS);
      bg.add(this.rbIT);
      bg.add(this.rbES);
      bg.add(this.rbNL);
      bg.add(this.rbFR);
      bg.add(this.rbJP);
      bg.add(this.rbRU);
      this.doDialogInit();
      TraceHelper.exit(this, "VNAConfigLanguageDialog");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      Locale loc = this.config.getLocale();
      this.rbSYS.setSelected(loc == null);
      this.rbDE.setSelected((new Locale("de", "DE")).equals(loc));
      this.rbEN.setSelected((new Locale("en", "US")).equals(loc));
      this.rbHU.setSelected((new Locale("hu", "HU")).equals(loc));
      this.rbIT.setSelected((new Locale("it", "IT")).equals(loc));
      this.rbPL.setSelected((new Locale("pl", "PL")).equals(loc));
      this.rbSE.setSelected((new Locale("sv", "SE")).equals(loc));
      this.rbNL.setSelected((new Locale("nl", "NL")).equals(loc));
      this.rbES.setSelected((new Locale("es", "ES")).equals(loc));
      this.rbCS.setSelected((new Locale("cs", "CZ")).equals(loc));
      this.rbFR.setSelected((new Locale("fr", "FR")).equals(loc));
      this.rbJP.setSelected((new Locale("ja", "JP")).equals(loc));
      this.rbRU.setSelected((new Locale("ru", "RUS")).equals(loc));
      this.addEscapeKey();
      this.doDialogShow();
      TraceHelper.exit(this, "doInit");
   }

   private void doOK() {
      TraceHelper.entry(this, "doOK");
      Locale loc = null;
      if (this.rbDE.isSelected()) {
         loc = new Locale("de", "DE");
      } else if (this.rbEN.isSelected()) {
         loc = new Locale("en", "US");
      } else if (this.rbHU.isSelected()) {
         loc = new Locale("hu", "HU");
      } else if (this.rbIT.isSelected()) {
         loc = new Locale("it", "IT");
      } else if (this.rbPL.isSelected()) {
         loc = new Locale("pl", "PL");
      } else if (this.rbSE.isSelected()) {
         loc = new Locale("sv", "SE");
      } else if (this.rbNL.isSelected()) {
         loc = new Locale("nl", "NL");
      } else if (this.rbES.isSelected()) {
         loc = new Locale("es", "ES");
      } else if (this.rbCS.isSelected()) {
         loc = new Locale("cs", "CZ");
      } else if (this.rbFR.isSelected()) {
         loc = new Locale("fr", "FR");
      } else if (this.rbJP.isSelected()) {
         loc = new Locale("ja", "JP");
      } else if (this.rbRU.isSelected()) {
         loc = new Locale("ru", "RUS");
      }

      JOptionPane.showMessageDialog(this, VNAMessages.getString("VNAConfigLanguageDialog.msg.1"), this.getTitle(), 1);
      this.config.setLocale(loc);
      this.setVisible(false);
      this.dispose();
      TraceHelper.exit(this, "doOK");
   }
}
