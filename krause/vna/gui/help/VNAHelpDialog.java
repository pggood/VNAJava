package krause.vna.gui.help;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.resources.VNAMessages;

public class VNAHelpDialog extends JDialog implements HyperlinkListener {
   public static final String HELP_HOME = "krause/vna/resources/help/";
   public static final String HELP_IMAGES = "krause/vna/resources/help/images";
   private JEditorPane htmlPane;

   public VNAHelpDialog(Dialog owner, String helpID) {
      super(owner);
      TraceHelper.entry(this, "VNAHelpDialog", helpID);
      this.internal(helpID);
      TraceHelper.exit(this, "VNAHelpDialog");
   }

   public VNAHelpDialog(Frame owner, String helpID) {
      super(owner);
      TraceHelper.entry(this, "VNAHelpDialog", helpID);
      this.internal(helpID);
      TraceHelper.exit(this, "VNAHelpDialog");
   }

   private void addEscapeKey() {
      Action actionListener = new AbstractAction() {
         public void actionPerformed(ActionEvent actionEvent) {
            VNAHelpDialog.this.dispose();
         }
      };
      KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
      InputMap inputMap = this.rootPane.getInputMap(2);
      inputMap.put(stroke, "ESCAPE");
      this.rootPane.getActionMap().put("ESCAPE", actionListener);
   }

   private String buildDefaultResourceName(String helpID) {
      String rc = "krause/vna/resources/help/";
      TraceHelper.entry(this, "buildDefaultResourceName", helpID);
      String language = Locale.ENGLISH.getLanguage();
      rc = rc + language;
      rc = rc + "/";
      rc = rc + helpID;
      rc = rc + ".html";
      TraceHelper.exitWithRC(this, "buildDefaultResourceName", rc);
      return rc;
   }

   private String buildResourceName(String helpID) {
      String rc = "krause/vna/resources/help/";
      TraceHelper.entry(this, "buildResourceName", helpID);
      String language = Locale.getDefault().getLanguage();
      rc = rc + language;
      rc = rc + "/";
      rc = rc + helpID;
      rc = rc + ".html";
      TraceHelper.exitWithRC(this, "buildResourceName", rc);
      return rc;
   }

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      VNAConfig.getSingleton().storeWindowPosition("VNAHelpDialog", this);
      VNAConfig.getSingleton().storeWindowSize("VNAHelpDialog", this);
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   private void internal(String helpID) {
      TraceHelper.entry(this, "internal", helpID);
      this.setDefaultCloseOperation(2);

      try {
         String resourceName = this.buildResourceName(helpID);
         TraceHelper.text(this, "internal", "resourcename=[" + resourceName + "]");
         URL url = ClassLoader.getSystemResource(resourceName);
         if (url != null) {
            TraceHelper.text(this, "internal", "URL build=[" + url.toString() + "]");
            InputStream s = ClassLoader.getSystemResourceAsStream(resourceName);
            if (s == null) {
               TraceHelper.text(this, "internal", "language resource not found");
               resourceName = this.buildDefaultResourceName(helpID);
               url = ClassLoader.getSystemResource(resourceName);
            } else {
               s.close();
            }
         } else {
            resourceName = this.buildDefaultResourceName(helpID);
            url = ClassLoader.getSystemResource(resourceName);
         }

         TraceHelper.text(this, "internal", "try to load from [" + url + "]");
         this.htmlPane = new JEditorPane(url);
         this.htmlPane.setContentType("text/html;charset=iso8859-1");
         this.htmlPane.putClientProperty("html.base", ClassLoader.getSystemResource("/"));
         this.htmlPane.addHyperlinkListener(this);
         HTMLEditorKit kit = new HTMLEditorKit();
         StyleSheet styleSheet = kit.getStyleSheet();
         styleSheet.addRule("h1 {margin-bottom: 0px; margin-top: 5px;}");
         styleSheet.addRule("h2 {margin-bottom: 0px; margin-top: 5px;}");
         styleSheet.addRule("h3 {margin-bottom: 0px; margin-top: 5px; }");
         styleSheet.addRule("p  {margin-top: 5px; margin-left: 10px; }");
         styleSheet.addRule("ol {font-weight: bold; font-size:larger; }");
         styleSheet.addRule("ol p {font-weight: normal; font-size:smaller; }");
         JPanel panel = new JPanel();
         this.getContentPane().add(panel, "South");
         this.htmlPane.setEditable(false);
         JScrollPane scrollPane = new JScrollPane(this.htmlPane);
         this.getContentPane().add(scrollPane, "Center");
         this.setTitle(VNAMessages.getString("Dlg.Help.title"));
         this.htmlPane.setSelectionStart(0);
         this.htmlPane.setSelectionEnd(0);
      } catch (IOException var8) {
         ErrorLogHelper.exception(this, "internal", var8);
      }

      this.addEscapeKey();
      VNAConfig.getSingleton().restoreWindowPosition("VNAHelpDialog", this, new Point(100, 100));
      this.pack();
      VNAConfig.getSingleton().restoreWindowSize("VNAHelpDialog", this, new Dimension(400, 400));
      this.setVisible(true);
      TraceHelper.exit(this, "internal");
   }

   public void hyperlinkUpdate(HyperlinkEvent event) {
      TraceHelper.entry(this, "hyperlinkUpdate");
      if (event.getEventType() == EventType.ACTIVATED) {
         try {
            this.htmlPane.setPage(event.getURL());
         } catch (IOException var3) {
            ErrorLogHelper.exception(this, "hyperlinkUpdate", var3);
         }
      }

      TraceHelper.exit(this, "hyperlinkUpdate");
   }
}
