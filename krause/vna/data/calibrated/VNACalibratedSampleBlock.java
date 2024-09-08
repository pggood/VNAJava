package krause.vna.data.calibrated;

import java.io.File;
import java.util.List;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.jdom.Element;

public class VNACalibratedSampleBlock {
   public static final String XML_NAME_COMMENT = "comment";
   public static final String XML_NAME_FREQ = "frequency-range";
   public static final String XML_NAME_MAX = "max";
   public static final String XML_NAME_MIN = "min";
   public static final String XML_NAME_MINMAX = "min-max-values";
   public static final String XML_NAME_ROOT = "vna-j-scandata";
   public static final String XML_NAME_SAMPLES = "samples";
   private String blockComment;
   private VNACalibratedSample[] calibratedSamples;
   private File file = null;
   private VNAMinMaxPair mmGroupDelay;
   private VNAMinMaxPair mmRL;
   private VNAMinMaxPair mmRLPHASE;
   private VNAMinMaxPair mmRS;
   private VNAMinMaxPair mmRSS;
   private VNAMinMaxPair mmSWR;
   private VNAMinMaxPair mmTheta;
   private VNAMinMaxPair mmTL;
   private VNAMinMaxPair mmTLPHASE;
   private VNAMinMaxPair mmXS;
   private VNAMinMaxPair mmZABS;

   public static VNACalibratedSampleBlock fromElement(Element root) {
      VNACalibratedSampleBlock rc = null;
      VNACalibratedSample[] samples = readDataElements(root);
      if (samples != null) {
         rc = new VNACalibratedSampleBlock(samples.length);
         rc.setComment(root.getChildText("comment"));
         rc.setCalibratedSamples(samples);
         readMinMaxValueElements(rc, root);
      }

      return rc;
   }

   private static VNACalibratedSample[] readDataElements(Element root) {
      VNACalibratedSample[] rc = null;
      Element e = root.getChild("samples");
      if (e != null) {
         List<Element> samples = e.getChildren("sample");
         if (samples != null) {
            int len = samples.size();
            if (len > 0) {
               rc = new VNACalibratedSample[len];

               for(int i = 0; i < len; ++i) {
                  rc[i] = VNACalibratedSample.fromElement((Element)samples.get(i));
               }
            }
         }
      }

      return rc;
   }

   private static void readMinMaxValueElements(VNACalibratedSampleBlock sb, Element root) {
      Element mms = root.getChild("min-max-values");
      sb.setMmRP(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE.toString()));
      if (sb.getMmRP().getMinIndex() == -1) {
         sb.setMmRP(VNAMinMaxPair.fromElement(mms, "SCALE_PHASE"));
      }

      sb.setMmTP(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE.toString()));
      if (sb.getMmTP().getMinIndex() == -1) {
         sb.setMmTP(VNAMinMaxPair.fromElement(mms, "SCALE_PHASE"));
      }

      sb.setMmRL(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS.toString()));
      sb.setMmTL(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS.toString()));
      sb.setMmRS(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_RS.toString()));
      sb.setMmRSS(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_RSS.toString()));
      sb.setMmSWR(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_SWR.toString()));
      sb.setMmTheta(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_THETA.toString()));
      sb.setMmXS(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_XS.toString()));
      sb.setMmZABS(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS.toString()));
      sb.setMmGRPDLY(VNAMinMaxPair.fromElement(mms, VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY.toString()));
   }

   public VNACalibratedSampleBlock(int listLength) {
      this.mmGroupDelay = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY);
      this.mmRL = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS);
      this.mmRLPHASE = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE);
      this.mmRS = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RS);
      this.mmRSS = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_RSS);
      this.mmSWR = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_SWR);
      this.mmTheta = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_THETA);
      this.mmTL = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS);
      this.mmTLPHASE = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE);
      this.mmXS = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_XS);
      this.mmZABS = new VNAMinMaxPair(VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS);
      this.calibratedSamples = new VNACalibratedSample[listLength];
   }

   public Element asElement() {
      Element rc = new Element("vna-j-scandata");
      Element minmax = new Element("min-max-values");
      minmax.addContent(this.getMmGRPDLY().asElement());
      minmax.addContent(this.getMmRL().asElement());
      minmax.addContent(this.getMmRP().asElement());
      minmax.addContent(this.getMmTL().asElement());
      minmax.addContent(this.getMmTP().asElement());
      minmax.addContent(this.getMmRS().asElement());
      minmax.addContent(this.getMmRSS().asElement());
      minmax.addContent(this.getMmSWR().asElement());
      minmax.addContent(this.getMmTheta().asElement());
      minmax.addContent(this.getMmXS().asElement());
      minmax.addContent(this.getMmZABS().asElement());
      rc.addContent(minmax);
      Element fr = new Element("frequency-range");
      VNACalibratedSample[] samples = this.getCalibratedSamples();
      if (samples != null && samples.length > 0) {
         fr.addContent((new Element("min")).setText("" + samples[0].getFrequency()));
         fr.addContent((new Element("max")).setText("" + samples[samples.length - 1].getFrequency()));
      }

      rc.addContent(fr);
      if (this.getComment() != null) {
         rc.addContent((new Element("comment")).setText(this.getComment()));
      }

      Element data = new Element("samples");
      VNACalibratedSample[] ss = this.getCalibratedSamples();

      for(int i = 0; i < ss.length; ++i) {
         VNACalibratedSample s = ss[i];
         data.addContent(s.asElement(i));
      }

      rc.addContent(data);
      return rc;
   }

   public void consumeCalibratedSample(VNACalibratedSample sample, int index) {
      this.mmRL.consume(sample.getReflectionLoss(), index);
      this.mmTL.consume(sample.getTransmissionLoss(), index);
      this.mmRLPHASE.consume(sample.getReflectionPhase(), index);
      this.mmTLPHASE.consume(sample.getTransmissionPhase(), index);
      this.mmXS.consume(sample.getX(), index);
      this.mmRS.consume(sample.getR(), index);
      this.mmZABS.consume(sample.getZ(), index);
      this.mmSWR.consume(sample.getSWR(), index);
      this.mmRSS.consume(sample.getRelativeSignalStrength1(), index);
      this.mmTheta.consume(sample.getTheta(), index);
      this.mmGroupDelay.consume(sample.getGroupDelay(), index);
      this.calibratedSamples[index] = sample;
   }

   public VNACalibratedSample[] getCalibratedSamples() {
      return this.calibratedSamples;
   }

   public String getComment() {
      return this.blockComment;
   }

   public File getFile() {
      return this.file;
   }

   public VNAMinMaxPair getMinMaxPair(VNAScaleSymbols.SCALE_TYPE key) {
      if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE) {
         return this.mmRLPHASE;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE) {
         return this.mmTLPHASE;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS) {
         return this.mmRL;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS) {
         return this.mmTL;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_RS) {
         return this.mmRS;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_RSS) {
         return this.mmRSS;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_SWR) {
         return this.mmSWR;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_THETA) {
         return this.mmTheta;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_XS) {
         return this.mmXS;
      } else if (key == VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS) {
         return this.mmZABS;
      } else {
         return key == VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY ? this.mmGroupDelay : null;
      }
   }

   public VNAMinMaxPair getMmGRPDLY() {
      return this.mmGroupDelay;
   }

   public VNAMinMaxPair getMmRL() {
      return this.mmRL;
   }

   public VNAMinMaxPair getMmRP() {
      return this.mmRLPHASE;
   }

   public VNAMinMaxPair getMmRS() {
      return this.mmRS;
   }

   public VNAMinMaxPair getMmRSS() {
      return this.mmRSS;
   }

   public VNAMinMaxPair getMmSWR() {
      return this.mmSWR;
   }

   public VNAMinMaxPair getMmTheta() {
      return this.mmTheta;
   }

   public VNAMinMaxPair getMmTL() {
      return this.mmTL;
   }

   public VNAMinMaxPair getMmTP() {
      return this.mmTLPHASE;
   }

   public VNAMinMaxPair getMmXS() {
      return this.mmXS;
   }

   public VNAMinMaxPair getMmZABS() {
      return this.mmZABS;
   }

   public void setCalibratedSamples(VNACalibratedSample[] calibratedSamples) {
      this.calibratedSamples = calibratedSamples;
   }

   public void setComment(String blockComment) {
      this.blockComment = blockComment;
   }

   public void setFile(File file) {
      this.file = file;
   }

   public void setMmGRPDLY(VNAMinMaxPair mmGRPDLY) {
      this.mmGroupDelay = mmGRPDLY;
   }

   public void setMmRL(VNAMinMaxPair mmRL) {
      this.mmRL = mmRL;
   }

   public void setMmRP(VNAMinMaxPair mmPHASE) {
      this.mmRLPHASE = mmPHASE;
   }

   public void setMmRS(VNAMinMaxPair mmRS) {
      this.mmRS = mmRS;
   }

   public void setMmRSS(VNAMinMaxPair mmRSS) {
      this.mmRSS = mmRSS;
   }

   public void setMmSWR(VNAMinMaxPair mmSWR) {
      this.mmSWR = mmSWR;
   }

   public void setMmTheta(VNAMinMaxPair mmTheta) {
      this.mmTheta = mmTheta;
   }

   public void setMmTL(VNAMinMaxPair mmTL) {
      this.mmTL = mmTL;
   }

   public void setMmTP(VNAMinMaxPair mmTLPHASE) {
      this.mmTLPHASE = mmTLPHASE;
   }

   public void setMmXS(VNAMinMaxPair mmXS) {
      this.mmXS = mmXS;
   }

   public void setMmZABS(VNAMinMaxPair mmZABS) {
      this.mmZABS = mmZABS;
   }
}
