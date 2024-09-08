package krause.vna.importers;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.complex.Complex;

public class SnPInfoBlock {
   private String comment;
   private String filename;
   private Complex reference;
   private long frequencyMultiplier;
   private SnPInfoBlock.FORMAT format;
   private SnPInfoBlock.PARAMETER parameter;
   private List<SnPRecord> records = new ArrayList();

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public String getFilename() {
      return this.filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public Complex getReference() {
      return this.reference;
   }

   public void setReference(Complex reference) {
      this.reference = reference;
   }

   public long getFrequencyMultiplier() {
      return this.frequencyMultiplier;
   }

   public void setFrequencyMultiplier(long frequencyMultiplier) {
      this.frequencyMultiplier = frequencyMultiplier;
   }

   public SnPInfoBlock.FORMAT getFormat() {
      return this.format;
   }

   public void setFormat(SnPInfoBlock.FORMAT format) {
      this.format = format;
   }

   public SnPInfoBlock.PARAMETER getParameter() {
      return this.parameter;
   }

   public void setParameter(SnPInfoBlock.PARAMETER parameter) {
      this.parameter = parameter;
   }

   public List<SnPRecord> getRecords() {
      return this.records;
   }

   public void setRecords(List<SnPRecord> records) {
      this.records = records;
   }

   public String toString() {
      return "SnPInfoBlock [comment=" + this.comment + ", filename=" + this.filename + ", format=" + this.format + ", frequencyMultiplier=" + this.frequencyMultiplier + ", parameter=" + this.parameter + ", records=" + this.records + ", reference=" + this.reference + "]";
   }

   public static enum FORMAT {
      DB,
      MA,
      RI;
   }

   public static enum PARAMETER {
      S,
      Y,
      Z,
      H,
      G;
   }
}
