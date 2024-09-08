package krause.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Properties;

public class TypedProperties extends Properties {
   public boolean getBoolean(String key, boolean defValue) {
      boolean rc = defValue;
      String val = this.getProperty(key);
      if (val != null) {
         try {
            rc = Boolean.parseBoolean(val);
         } catch (NumberFormatException var6) {
         }
      }

      return rc;
   }

   public Color getColor(String prefix, Color colDef) {
      return new Color(Integer.parseInt(this.getProperty(prefix, Integer.toString(colDef.getRGB()))));
   }

   public double getDouble(String key, double defValue) {
      double rc = defValue;
      String val = this.getProperty(key);

      try {
         rc = Double.parseDouble(val);
      } catch (Exception var8) {
      }

      return rc;
   }

   public float getFloat(String key, float defValue) {
      float rc = defValue;
      String val = this.getProperty(key);

      try {
         rc = Float.parseFloat(val);
      } catch (Exception var6) {
      }

      return rc;
   }

   public int getInteger(String key, int defValue) {
      int rc = defValue;
      String val = this.getProperty(key);

      try {
         rc = Integer.parseInt(val);
      } catch (NumberFormatException var6) {
      }

      return rc;
   }

   public Integer getInteger(String key, Integer defValue) {
      Integer rc = defValue;
      String val = this.getProperty(key);

      try {
         rc = Integer.valueOf(val);
      } catch (NumberFormatException var6) {
      }

      return rc;
   }

   public long getLong(String key, long defValue) {
      long rc = defValue;
      String val = this.getProperty(key);

      try {
         rc = Long.parseLong(val);
      } catch (NumberFormatException var8) {
      }

      return rc;
   }

   public Long getInteger(String key, Long defValue) {
      Long rc = defValue;
      String val = this.getProperty(key);

      try {
         rc = Long.valueOf(val);
      } catch (NumberFormatException var6) {
      }

      return rc;
   }

   public void putBoolean(String key, boolean value) {
      this.setProperty(key, Boolean.toString(value));
   }

   public void putColor(String prefix, Color color) {
      this.setProperty(prefix, Integer.toString(color.getRGB()));
   }

   public void putDouble(String key, double value) {
      this.setProperty(key, Double.toString(value));
   }

   public void putInteger(String key, int value) {
      this.setProperty(key, Integer.toString(value));
   }

   public void putLong(String key, long value) {
      this.setProperty(key, Long.toString(value));
   }

   public void restoreWindowPosition(String prefix, Component wnd, Point point) {
      int x = this.getInteger(prefix + ".X", (int)point.getX());
      int y = this.getInteger(prefix + ".Y", (int)point.getY());
      Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
      if (x + wnd.getWidth() > sz.width) {
         x = sz.width - wnd.getWidth();
      }

      if (y + wnd.getHeight() > sz.height) {
         y = sz.height - wnd.getHeight();
      }

      wnd.setLocation(x, y);
   }

   public void restoreWindowSize(String prefix, Component wnd, Dimension sz) {
      int w = this.getInteger(prefix + ".Width", (int)sz.getWidth());
      int h = this.getInteger(prefix + ".Height", (int)sz.getHeight());
      wnd.setSize(w, h);
      wnd.setPreferredSize(wnd.getSize());
   }

   public void storeWindowPosition(String prefix, Component wnd) {
      this.putInteger(prefix + ".X", wnd.getX());
      this.putInteger(prefix + ".Y", wnd.getY());
   }

   public void storeWindowSize(String prefix, Component wnd) {
      this.putInteger(prefix + ".Width", wnd.getWidth());
      this.putInteger(prefix + ".Height", wnd.getHeight());
   }
}
