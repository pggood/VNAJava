package krause.vna.data;

import java.io.Serializable;
import krause.common.TypedProperties;
import krause.common.exception.ProcessingException;
import krause.vna.resources.VNAMessages;

public class VNAScanMode implements Serializable {
   public static final int MODENUM_UNKNOWN = -1;
   public static final int MODENUM_TRANSMISSION = 1;
   public static final int MODENUM_REFLECTION = 2;
   public static final int MODENUM_RSS1 = 3;
   public static final int MODENUM_RSS2 = 4;
   public static final int MODENUM_RSS3 = 5;
   public static final int MODENUM_COMBI = 10;
   public static final int MODENUM_TEST = 99;
   private static final long serialVersionUID = 7585259522736849574L;
   public static final VNAScanMode MODE_TRANSMISSION = new VNAScanMode(1);
   public static final VNAScanMode MODE_REFLECTION = new VNAScanMode(2);
   public static final VNAScanMode MODE_RSS1 = new VNAScanMode(3);
   public static final VNAScanMode MODE_RSS2 = new VNAScanMode(4);
   public static final VNAScanMode MODE_RSS3 = new VNAScanMode(5);
   public static final VNAScanMode MODE_TEST = new VNAScanMode(99);
   public static final VNAScanMode MODE_COMBI = new VNAScanMode(10);
   public static final String TEXT_REFLECTION = VNAMessages.getString("VNAScanMode.Reflection");
   public static final String TEXT_TRANSMISSION = VNAMessages.getString("VNAScanMode.Transmission");
   public static final String TEXT_RSS1 = VNAMessages.getString("VNAScanMode.RSS1");
   public static final String TEXT_RSS2 = VNAMessages.getString("VNAScanMode.RSS2");
   public static final String TEXT_RSS3 = VNAMessages.getString("VNAScanMode.RSS3");
   public static final String TEXT_COMBI = VNAMessages.getString("VNAScanMode.COMBI");
   public static final String TEXT_TEST = VNAMessages.getString("VNAScanMode.TEST");
   private int mode = 1;

   public VNAScanMode() {
      this.mode = -1;
   }

   public VNAScanMode(int pMode) {
      this.mode = pMode;
   }

   public void setRssMode1() {
      this.mode = 3;
   }

   public void setTransmissionMode() {
      this.mode = 1;
   }

   public void setReflectionMode() {
      this.mode = 2;
   }

   public boolean isTransmissionMode() {
      return this.mode == 1;
   }

   public boolean isReflectionMode() {
      return this.mode == 2;
   }

   public boolean isRss1Mode() {
      return this.mode == 3;
   }

   public boolean isRss2Mode() {
      return this.mode == 4;
   }

   public boolean isRss3Mode() {
      return this.mode == 5;
   }

   public boolean isTestMode() {
      return this.mode == 99;
   }

   public boolean isCombiMode() {
      return this.mode == 10;
   }

   public int hashCode() {
      return this.mode;
   }

   public boolean equals(Object arg0) {
      boolean rc = false;
      if (arg0 instanceof VNAScanMode) {
         VNAScanMode o = (VNAScanMode)arg0;
         rc = o.getMode() == this.getMode();
      }

      return rc;
   }

   public String toString() {
      if (this.isReflectionMode()) {
         return TEXT_REFLECTION;
      } else if (this.isTransmissionMode()) {
         return TEXT_TRANSMISSION;
      } else if (this.isRss1Mode()) {
         return TEXT_RSS1;
      } else if (this.isRss2Mode()) {
         return TEXT_RSS2;
      } else if (this.isRss3Mode()) {
         return TEXT_RSS3;
      } else if (this.isCombiMode()) {
         return TEXT_COMBI;
      } else {
         return this.isTestMode() ? TEXT_TEST : "???";
      }
   }

   public int getMode() {
      return this.mode;
   }

   public String key() {
      return "" + this.mode;
   }

   public Object shortText() {
      if (this.isReflectionMode()) {
         return "REFL";
      } else if (this.isTransmissionMode()) {
         return "TRAN";
      } else if (this.isRss1Mode()) {
         return "RSS1";
      } else if (this.isRss2Mode()) {
         return "RSS2";
      } else if (this.isRss3Mode()) {
         return "RSS3";
      } else if (this.isCombiMode()) {
         return "COMBI";
      } else {
         return this.isTestMode() ? "TEST" : "XXX";
      }
   }

   public void saveToProperties(TypedProperties props) {
      props.putInteger(this.getClass().getCanonicalName() + ".scanMode", this.mode);
   }

   public void restoreFromProperties(TypedProperties props) {
      this.mode = props.getInteger(this.getClass().getCanonicalName() + ".scanMode", this.mode);
   }

   public static VNAScanMode restoreFromString(String p) throws ProcessingException {
      if ("REFL".equals(p)) {
         return MODE_REFLECTION;
      } else if ("TRAN".equals(p)) {
         return MODE_TRANSMISSION;
      } else {
         throw new ProcessingException("Illegal mode [" + p + "]");
      }
   }
}
