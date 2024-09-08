package krause.vna.gui.tune;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;
import org.jfree.ui.FontChooserDialog;

public class VNATuneDialog extends KrauseDialog {
   private static final int DEF_WIDTH = 750;
   private static final int DEF_HEIGHT = 350;
   private static final int DEF_FNT_SIZE = 80;
   private static final String DEF_FNT_NAME = "MS UI Gothic";
   private Font initialFont = new Font("MS UI Gothic", 0, 80);
   private JTextField txtGreenYellow;
   private JTextField txtYellowRed;
   private VNAMarker marker = null;
   private TypedProperties config = VNAConfig.getSingleton();
   private double limitYellow = 200.0D;
   private double limitRed = 300.0D;
   private JLabel lblSwrVal;
   private JLabel lblFrqVal;
   private JLabel lblSwrTxt;
   private JLabel lblFrqTxt;
   private JButton btFntSelect;

   public void dispose() {
      TraceHelper.entry(this, "dispose");
      String confKey = "BigSWR." + this.marker.getName();
      this.config.storeWindowPosition(confKey, this);
      this.config.storeWindowSize(confKey, this);
      this.config.putDouble(confKey + ".Yellow", this.limitYellow);
      this.config.putDouble(confKey + ".Red", this.limitRed);
      this.config.putInteger(confKey + ".FontSize", this.initialFont.getSize());
      this.config.put(confKey + ".FontName", this.initialFont.getFontName());
      super.dispose();
      TraceHelper.exit(this, "dispose");
   }

   public VNATuneDialog(VNAMarker pMarker) {
      super(false);
      this.setDefaultCloseOperation(0);
      this.marker = pMarker;
      this.getContentPane().setLayout(new MigLayout("", "[left][grow,right]", "[grow][grow][]"));
      this.setTitle(MessageFormat.format(VNAMessages.getString("VNATuneDialog.title"), this.marker.getName()));
      this.getContentPane().setBackground(Color.BLACK);
      this.lblFrqTxt = new JLabel(VNAMessages.getString("VNATuneDialog.frq"));
      this.lblFrqTxt.setForeground(Color.WHITE);
      this.getContentPane().add(this.lblFrqTxt, "");
      this.lblFrqVal = new JLabel("1");
      this.lblFrqVal.setForeground(Color.WHITE);
      this.getContentPane().add(this.lblFrqVal, "wrap");
      this.lblSwrTxt = new JLabel(VNAMessages.getString("VNATuneDialog.swr"));
      this.lblSwrTxt.setForeground(Color.WHITE);
      this.getContentPane().add(this.lblSwrTxt, "");
      this.lblSwrVal = new JLabel("2");
      this.lblSwrVal.setForeground(Color.WHITE);
      this.getContentPane().add(this.lblSwrVal, "wrap");
      JPanel panel_2 = new JPanel();
      this.getContentPane().add(panel_2, "span 2,grow,wrap");
      panel_2.setLayout(new FlowLayout());
      panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNATuneDialog.group"), 4, 2, (Font)null, new Color(0, 0, 0)));
      JLabel lblSwrgreen = new JLabel(VNAMessages.getString("VNATuneDialog.gy"));
      panel_2.add(lblSwrgreen);
      this.txtGreenYellow = new JTextField();
      this.txtGreenYellow.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            VNATuneDialog.this.fields2Limits();
         }
      });
      panel_2.add(this.txtGreenYellow);
      this.txtGreenYellow.setColumns(6);
      JLabel lblYellowred = new JLabel(VNAMessages.getString("VNATuneDialog.yr"));
      panel_2.add(lblYellowred);
      this.txtYellowRed = new JTextField();
      this.txtYellowRed.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            VNATuneDialog.this.fields2Limits();
         }
      });
      panel_2.add(this.txtYellowRed);
      this.txtYellowRed.setColumns(6);
      this.btFntSelect = new JButton(VNAMessages.getString("VNATuneDialog.btFntSelect.text"));
      this.btFntSelect.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0) {
            VNATuneDialog.this.doSelectFont();
         }
      });
      panel_2.add(this.btFntSelect);
      this.getContentPane().addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent arg0) {
            Component c = arg0.getComponent();
            VNATuneDialog.this.doResizeFonts(c);
         }
      });
      this.doDialogInit();
   }

   protected void doResizeFonts(Component c) {
      TraceHelper.entry(this, "doResizeFonts");
      float relX = (float)c.getWidth() / 750.0F;
      float relY = (float)c.getHeight() / 350.0F;
      float fact = Math.min(relX, relY);
      float newFntSize = 80.0F * fact;
      this.lblFrqVal.setFont(this.initialFont.deriveFont(newFntSize));
      this.lblSwrVal.setFont(this.initialFont.deriveFont(newFntSize));
      this.lblFrqTxt.setFont(this.initialFont.deriveFont(newFntSize));
      this.lblSwrTxt.setFont(this.initialFont.deriveFont(newFntSize));
      TraceHelper.exit(this, "doResizeFonts");
   }

   protected void doSelectFont() {
      TraceHelper.entry(this, "doSelectFont");
      FontChooserDialog fcs = new FontChooserDialog(this, VNAMessages.getString("VNATuneDialog.fontSelDialog"), true, this.initialFont);
      fcs.pack();
      fcs.setVisible(true);
      this.initialFont = fcs.getSelectedFont();
      this.doResizeFonts(this.getContentPane());
      TraceHelper.exit(this, "doSelectFont");
   }

   protected void fields2Limits() {
      TraceHelper.entry(this, "fields2Limits");

      try {
         this.limitYellow = VNAFormatFactory.getSwrFormat().parse(this.txtGreenYellow.getText()).doubleValue();
         this.limitRed = VNAFormatFactory.getSwrFormat().parse(this.txtYellowRed.getText()).doubleValue();
         this.limits2Fields();
      } catch (ParseException var2) {
         Toolkit.getDefaultToolkit().beep();
      }

      TraceHelper.exit(this, "fields2Limits");
   }

   protected void doDialogCancel() {
      TraceHelper.entry(this, "doCANCEL");
      TraceHelper.exit(this, "doCANCEL");
   }

   protected void doDialogInit() {
      TraceHelper.entry(this, "doInit");
      String confKey = "BigSWR." + this.marker.getName();
      int fntSize = this.config.getInteger(confKey + ".FontSize", 80);
      String fntName = this.config.getProperty(confKey + ".FontName", "MS UI Gothic");
      this.initialFont = new Font(fntName, 0, fntSize);
      this.config.restoreWindowPosition(confKey, this, new Point(10, 10));
      this.limitYellow = this.config.getDouble(confKey + ".Yellow", 2.0D);
      this.limitRed = this.config.getDouble(confKey + ".Red", 3.0D);
      this.limits2Fields();
      this.update(this.marker.getSample());
      this.pack();
      this.config.restoreWindowSize(confKey, this, new Dimension(810, 345));
      this.setVisible(true);
      TraceHelper.exit(this, "doInit");
   }

   private void limits2Fields() {
      TraceHelper.entry(this, "limits2Fields");
      this.txtGreenYellow.setText(VNAFormatFactory.getSwrFormat().format(this.limitYellow));
      this.txtYellowRed.setText(VNAFormatFactory.getSwrFormat().format(this.limitRed));
      TraceHelper.exit(this, "limits2Fields");
   }

   public void update(VNACalibratedSample markerSample) {
      TraceHelper.entry(this, "update");
      this.lblFrqVal.setText(VNAFormatFactory.getFrequencyFormat().format(markerSample.getFrequency()));
      double swr = markerSample.getSWR();
      this.lblSwrVal.setText(VNAFormatFactory.getSwrFormat().format(swr) + ":1");
      if (swr > this.limitRed) {
         this.lblSwrVal.setForeground(Color.RED);
         this.lblFrqVal.setForeground(Color.RED);
      } else if (swr > this.limitYellow) {
         this.lblSwrVal.setForeground(Color.YELLOW);
         this.lblFrqVal.setForeground(Color.YELLOW);
      } else {
         this.lblSwrVal.setForeground(Color.GREEN);
         this.lblFrqVal.setForeground(Color.GREEN);
      }

      TraceHelper.exit(this, "update");
   }

   public void windowClosing(WindowEvent e) {
      TraceHelper.entry(this, "windowClosing");
      this.marker.doClickOnBigSWRSymbol();
      TraceHelper.exit(this, "windowClosing");
   }
}
