package krause.vna.gui.generator.table;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JTable;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNAFrequencyTable extends JTable {
   public VNAFrequencyTable() {
      super(new VNAFrequencyTableModel());
      TraceHelper.entry(this, "VNAFrequencyTable");
      this.setDefaultRenderer(Long.class, new VNAFrequencyRenderer());
      this.setSelectionMode(0);
      TraceHelper.exit(this, "VNAFrequencyTable");
   }

   public void addFrequency(Long pair) {
      this.getModel().addElement(pair);
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }

   public VNAFrequencyTableModel getModel() {
      return (VNAFrequencyTableModel)super.getModel();
   }

   public boolean load(String myFileName) {
      TraceHelper.entry(this, "load");
      boolean result = false;
      XMLDecoder dec = null;
      FileInputStream fis = null;
      TraceHelper.text(this, "save", "Trying to read from [" + myFileName + "]");

      try {
         fis = new FileInputStream(myFileName);
         dec = new XMLDecoder(fis);
         this.getModel().clear();

         while(true) {
            this.getModel().addElement((Long)dec.readObject());
         }
      } catch (Exception var13) {
         result = true;
      } finally {
         if (dec != null) {
            dec.close();
         }

         if (fis != null) {
            try {
               fis.close();
            } catch (IOException var12) {
               ErrorLogHelper.exception(this, "load", var12);
            }
         }

      }

      if (this.getModel().getData().size() == 0) {
         this.loadDefaults();
      }

      TraceHelper.exitWithRC(this, "load", result);
      return result;
   }

   public void loadDefaults() {
   }

   public boolean save(String myFileName) {
      TraceHelper.entry(this, "save");
      boolean result = false;
      TraceHelper.text(this, "save", "Trying to write to [" + myFileName + "]");
      XMLEncoder enc = null;
      FileOutputStream fos = null;

      try {
         fos = new FileOutputStream(myFileName);
         enc = new XMLEncoder(fos);
         Iterator var6 = this.getModel().getData().iterator();

         while(var6.hasNext()) {
            Long obj = (Long)var6.next();
            enc.writeObject(obj);
         }

         result = true;
      } catch (Exception var15) {
         ErrorLogHelper.exception(this, "save", var15);
         ErrorLogHelper.text(this, "save", var15.getMessage());
      } finally {
         if (enc != null) {
            enc.flush();
            enc.close();
         }

         if (fos != null) {
            try {
               fos.close();
            } catch (IOException var14) {
               ErrorLogHelper.exception(this, "save", var14);
            }
         }

      }

      TraceHelper.exitWithRC(this, "save", result);
      return result;
   }
}
