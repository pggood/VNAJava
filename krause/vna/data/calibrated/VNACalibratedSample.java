package krause.vna.data.calibrated;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashSet;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.scale.VNAScaleSymbols;
import org.apache.commons.math3.complex.Complex;
import org.jdom.Element;

public class VNACalibratedSample implements Serializable {
   private static HashSet<String> beanNames = new HashSet<String>() {
   };
   private static final long serialVersionUID = -231006451669990001L;
   public static final String XML_NAME_SAMPLE = "sample";
   private static final String XML_NAME_SAMPLE_FREQ = "frequency";
   private static final String XML_NAME_SAMPLE_GRPDEL = "Tgr";
   private static final String XML_NAME_SAMPLE_INDEX = "index";
   private static final String XML_NAME_SAMPLE_MAG = "magnitude";
   private static final String XML_NAME_SAMPLE_R = "r";
   private static final String XML_NAME_SAMPLE_RL = "reflectionloss";
   private static final String XML_NAME_SAMPLE_RP = "reflectionphase";
   private static final String XML_NAME_SAMPLE_RSS1 = "rss1";
   private static final String XML_NAME_SAMPLE_RSS2 = "rss2";
   private static final String XML_NAME_SAMPLE_RSS3 = "rss3";
   private static final String XML_NAME_SAMPLE_SWR = "swr";
   private static final String XML_NAME_SAMPLE_THETA = "theta";
   private static final String XML_NAME_SAMPLE_TL = "transmissionloss";
   private static final String XML_NAME_SAMPLE_TP = "transmissionphase";
   private static final String XML_NAME_SAMPLE_X = "x";
   private static final String XML_NAME_SAMPLE_Z = "z";
   private transient int diagramX = 0;
   private long frequency = 0L;
   private double groupDelay = 0.0D;
   private double mag = 0.0D;
   private double R = 0.0D;
   private double reflectionLoss = 0.0D;
   private double reflectionPhase = 0.0D;
   private double RelativeSignalStrength1 = 0.0D;
   private double RelativeSignalStrength2 = 0.0D;
   private double RelativeSignalStrength3 = 0.0D;
   private transient Complex RHO = null;
   private double swr = 0.0D;
   private double theta = 0.0D;
   private double transmissionLoss = 0.0D;
   private double transmissionPhase = 0.0D;
   private double X = 0.0D;
   private double z = 0.0D;
   private transient Complex zComplex50Ohms = null;

   public static VNACalibratedSample fromElement(Element e) {
      VNACalibratedSample rc = new VNACalibratedSample();
      rc.setFrequency(getLongFromElement(e, "frequency"));
      rc.setMag(getDoubleFromElement(e, "magnitude"));
      rc.setReflectionLoss(getDoubleFromElement(e, "reflectionloss"));
      rc.setReflectionPhase(getDoubleFromElement(e, "reflectionphase"));
      rc.setTransmissionLoss(getDoubleFromElement(e, "transmissionloss"));
      rc.setTransmissionPhase(getDoubleFromElement(e, "transmissionphase"));
      rc.setR(getDoubleFromElement(e, "r"));
      rc.setRelativeSignalStrength1(getDoubleFromElement(e, "rss1"));
      rc.setRelativeSignalStrength2(getDoubleFromElement(e, "rss2"));
      rc.setRelativeSignalStrength3(getDoubleFromElement(e, "rss3"));
      rc.setSWR(getDoubleFromElement(e, "swr"));
      rc.setTheta(getDoubleFromElement(e, "theta"));
      rc.setX(getDoubleFromElement(e, "x"));
      rc.setZ(getDoubleFromElement(e, "z"));
      return rc;
   }

   private static double getDoubleFromElement(Element e, String name) {
      double rc = 0.0D;
      if (e != null) {
         String t = e.getChildText(name);
         if (t != null && !"".equals(t)) {
            rc = Double.parseDouble(t);
         }
      }

      return rc;
   }

   private static long getLongFromElement(Element e, String name) {
      long rc = 0L;
      if (e != null) {
         String t = e.getChildText(name);
         if (t != null && !"".equals(t)) {
            rc = Long.parseLong(t);
         }
      }

      return rc;
   }

   private static void setupBeanInfo() {
      BeanInfo info = null;

      try {
         info = Introspector.getBeanInfo(VNACalibratedSample.class);
         PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();

         for(int i = 0; i < propertyDescriptors.length; ++i) {
            PropertyDescriptor pd = propertyDescriptors[i];
            if (beanNames.contains(pd.getName())) {
               pd.setValue("transient", Boolean.TRUE);
            }
         }
      } catch (IntrospectionException var4) {
         ErrorLogHelper.exception("", "setupBeanInfo", var4);
      }

   }

   public VNACalibratedSample() {
      beanNames.add("RHO");
      beanNames.add("ZComplex50Ohms");
      beanNames.add("DiagramX");
      setupBeanInfo();
   }

   public void copy(VNACalibratedSample pSource) {
      this.setGroupDelay(pSource.getGroupDelay());
      this.setMag(pSource.getMag());
      this.setR(pSource.getR());
      this.setReflectionLoss(pSource.getReflectionLoss());
      this.setReflectionPhase(pSource.getReflectionPhase());
      this.setRelativeSignalStrength1(pSource.getRelativeSignalStrength1());
      this.setRelativeSignalStrength2(pSource.getRelativeSignalStrength2());
      this.setRelativeSignalStrength3(pSource.getRelativeSignalStrength3());
      this.setRHO(pSource.getRHO());
      this.setSWR(pSource.getSWR());
      this.setTheta(pSource.getTheta());
      this.setTransmissionLoss(pSource.getTransmissionLoss());
      this.setTransmissionPhase(pSource.getTransmissionPhase());
      this.setX(pSource.getX());
      this.setZ(pSource.getZ());
      this.setZComplex50Ohms(pSource.getZComplex50Ohms());
   }

   public Element asElement(int index) {
      Element rc = new Element("sample");
      rc.addContent((new Element("index")).setText(Long.toString((long)index)));
      rc.addContent((new Element("frequency")).setText(Long.toString(this.getFrequency())));
      rc.addContent((new Element("magnitude")).setText(Double.toString(this.getMag())));
      rc.addContent((new Element("r")).setText(Double.toString(this.getR())));
      rc.addContent((new Element("rss1")).setText(Double.toString(this.getRelativeSignalStrength1())));
      rc.addContent((new Element("rss2")).setText(Double.toString(this.getRelativeSignalStrength2())));
      rc.addContent((new Element("rss3")).setText(Double.toString(this.getRelativeSignalStrength3())));
      rc.addContent((new Element("reflectionloss")).setText(Double.toString(this.getReflectionLoss())));
      rc.addContent((new Element("reflectionphase")).setText(Double.toString(this.getReflectionPhase())));
      rc.addContent((new Element("transmissionloss")).setText(Double.toString(this.getTransmissionLoss())));
      rc.addContent((new Element("transmissionphase")).setText(Double.toString(this.getTransmissionPhase())));
      rc.addContent((new Element("theta")).setText(Double.toString(this.getTheta())));
      rc.addContent((new Element("swr")).setText(Double.toString(this.getSWR())));
      rc.addContent((new Element("x")).setText(Double.toString(this.getX())));
      rc.addContent((new Element("z")).setText(Double.toString(this.getZ())));
      rc.addContent((new Element("Tgr")).setText(Double.toString(this.getGroupDelay())));
      return rc;
   }

   public double getDataByScaleType(VNAScaleSymbols.SCALE_TYPE type) {
      if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNPHASE) {
         return this.reflectionPhase;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONPHASE) {
         return this.transmissionPhase;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_TRANSMISSIONLOSS) {
         return this.transmissionLoss;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_RETURNLOSS) {
         return this.reflectionLoss;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_RSS) {
         return this.RelativeSignalStrength1;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_RS) {
         return this.R;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_SWR) {
         return this.swr;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_THETA) {
         return this.theta;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_XS) {
         return this.X;
      } else if (type == VNAScaleSymbols.SCALE_TYPE.SCALE_Z_ABS) {
         return this.z;
      } else {
         return type == VNAScaleSymbols.SCALE_TYPE.SCALE_GRPDLY ? this.groupDelay : 0.0D;
      }
   }

   public int getDiagramX() {
      return this.diagramX;
   }

   public long getFrequency() {
      return this.frequency;
   }

   public double getGroupDelay() {
      return this.groupDelay;
   }

   public double getMag() {
      return this.mag;
   }

   public double getR() {
      return this.R;
   }

   public double getReflectionLoss() {
      return this.reflectionLoss;
   }

   public double getReflectionPhase() {
      return this.reflectionPhase;
   }

   public double getRelativeSignalStrength1() {
      return this.RelativeSignalStrength1;
   }

   public double getRelativeSignalStrength2() {
      return this.RelativeSignalStrength2;
   }

   public double getRelativeSignalStrength3() {
      return this.RelativeSignalStrength3;
   }

   public Complex getRHO() {
      return this.RHO;
   }

   public double getSWR() {
      return this.swr;
   }

   public double getTheta() {
      return this.theta;
   }

   public double getTransmissionLoss() {
      return this.transmissionLoss;
   }

   public double getTransmissionPhase() {
      return this.transmissionPhase;
   }

   public double getX() {
      return this.X;
   }

   public double getZ() {
      return this.z;
   }

   public Complex getZComplex50Ohms() {
      return this.zComplex50Ohms;
   }

   public void setDiagramX(int diagramX) {
      this.diagramX = diagramX;
   }

   public void setFrequency(long frequency) {
      this.frequency = frequency;
   }

   public void setGroupDelay(double groupDelay) {
      this.groupDelay = groupDelay;
   }

   public void setMag(double mAG) {
      this.mag = mAG;
   }

   public void setR(double r) {
      this.R = r;
   }

   public void setReflectionLoss(double reflectionLoss) {
      this.reflectionLoss = reflectionLoss;
   }

   public void setReflectionPhase(double phase) {
      this.reflectionPhase = phase;
   }

   public void setRelativeSignalStrength1(double relativeSignalStrength1) {
      this.RelativeSignalStrength1 = relativeSignalStrength1;
   }

   public void setRelativeSignalStrength2(double relativeSignalStrength2) {
      this.RelativeSignalStrength2 = relativeSignalStrength2;
   }

   public void setRelativeSignalStrength3(double relativeSignalStrength3) {
      this.RelativeSignalStrength3 = relativeSignalStrength3;
   }

   public void setRHO(Complex rHO) {
      this.RHO = rHO;
   }

   public void setSWR(double pSWR) {
      this.swr = pSWR;
   }

   public void setTheta(double pTheta) {
      this.theta = pTheta;
   }

   public void setTransmissionLoss(double transmissionLoss) {
      this.transmissionLoss = transmissionLoss;
   }

   public void setTransmissionPhase(double transmissionPhase) {
      this.transmissionPhase = transmissionPhase;
   }

   public void setX(double x) {
      this.X = x;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public void setZComplex50Ohms(Complex zComplex50Ohms) {
      this.zComplex50Ohms = zComplex50Ohms;
   }
}
