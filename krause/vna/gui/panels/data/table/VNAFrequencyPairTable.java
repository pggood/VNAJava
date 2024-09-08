package krause.vna.gui.panels.data.table;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JTable;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.comparators.VNAFrequencyPairComparator;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.gui.util.tables.VNAFrequencyRenderer;

public class VNAFrequencyPairTable extends JTable {
   public void addFrequencyPair(VNAFrequencyPair pair) {
      this.getModel().addElement(pair);
      Collections.sort(this.getModel().getData(), new VNAFrequencyPairComparator());
   }

   public VNAFrequencyPairTableModel getModel() {
      return (VNAFrequencyPairTableModel)super.getModel();
   }

   public void loadDefaults() {
   }

   public boolean load(String myFileName) {
      TraceHelper.entry(this, "load");
      boolean result = false;
      XMLDecoder dec = null;
      FileInputStream fis = null;
      TraceHelper.text(this, "load", "Trying to read from [" + myFileName + "]");

      try {
         fis = new FileInputStream(myFileName);
         dec = new XMLDecoder(fis);
         this.getModel().clear();

         while(true) {
            this.getModel().addElement((VNAFrequencyPair)dec.readObject());
         }
      } catch (ArrayIndexOutOfBoundsException var17) {
         result = true;
      } catch (FileNotFoundException var18) {
         TraceHelper.text(this, "load", "file [" + myFileName + "] not found. Using defaults.");
      } catch (Exception var19) {
         ErrorLogHelper.exception(this, "load", var19);
      } finally {
         if (dec != null) {
            dec.close();
         }

         if (fis != null) {
            try {
               fis.close();
            } catch (IOException var16) {
               ErrorLogHelper.exception(this, "load", var16);
            }
         }

      }

      if (this.getModel().getData().size() == 0) {
         this.loadDefaults();
      }

      TraceHelper.exitWithRC(this, "load", result);
      return result;
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
         Iterator it = this.getModel().getData().iterator();

         while(it.hasNext()) {
            VNAFrequencyPair obj = (VNAFrequencyPair)it.next();
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

   public VNAFrequencyPairTable() {
      super(new VNAFrequencyPairTableModel());
      TraceHelper.entry(this, "VNAFrequencyPairTable");
      this.setDefaultRenderer(Long.class, new VNAFrequencyRenderer());
      this.setSelectionMode(0);
      TraceHelper.exit(this, "VNAFrequencyPairTable");
   }

   public Class getColumnClass(int c) {
      return this.getValueAt(0, c).getClass();
   }
}
