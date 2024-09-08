package com.l2fprod.common.swing;

import java.awt.Component;
import java.util.Hashtable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

public class StatusBar extends JComponent {
   public static final String DEFAULT_ZONE = "default";
   private Hashtable<String, Component> idToZones;
   private transient Border zoneBorder;

   public StatusBar() {
      this.setLayout(LookAndFeelTweaks.createHorizontalPercentLayout());
      this.idToZones = new Hashtable();
   }

   public void setZoneBorder(Border border) {
      this.zoneBorder = border;
   }

   public void addZone(String id, Component zone, String constraints) {
      Component previousZone = this.getZone(id);
      if (previousZone != null) {
         this.remove(previousZone);
         this.idToZones.remove(id);
      }

      if (zone instanceof JComponent) {
         JComponent jc = (JComponent)zone;
         if (jc.getBorder() == null || jc.getBorder() instanceof UIResource) {
            if (jc instanceof JLabel) {
               jc.setBorder(new CompoundBorder(this.zoneBorder, new EmptyBorder(0, 2, 0, 2)));
               ((JLabel)jc).setText(" ");
            } else {
               jc.setBorder(this.zoneBorder);
            }
         }
      }

      this.add(zone, constraints);
      this.idToZones.put(id, zone);
   }

   public Component getZone(String id) {
      return (Component)this.idToZones.get(id);
   }

   public void setZones(String[] ids, Component[] zones, String[] constraints) {
      this.removeAll();
      this.idToZones.clear();
      int i = 0;

      for(int c = zones.length; i < c; ++i) {
         this.addZone(ids[i], zones[i], constraints[i]);
      }

   }
}
