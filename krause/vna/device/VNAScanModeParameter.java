package krause.vna.device;

import krause.vna.data.VNAScanMode;
import krause.vna.gui.scale.VNAScaleSymbols;

public class VNAScanModeParameter {
   private boolean requiresLoad = false;
   private boolean requiresLoop = false;
   private boolean requiresOpen = false;
   private boolean requiresShort = false;
   private boolean requiresRSS1 = false;
   private boolean requiresRSS2 = false;
   private boolean requiresRSS3 = false;
   private VNAScaleSymbols.SCALE_TYPE scaleLeft;
   private VNAScaleSymbols.SCALE_TYPE scaleRight;
   private VNAScanMode mode;

   public VNAScanModeParameter(VNAScanMode pMode, boolean pOpen, boolean pShort, boolean pLoad, boolean pLoop, VNAScaleSymbols.SCALE_TYPE pScaleLeft, VNAScaleSymbols.SCALE_TYPE pScaleRight) {
      this.requiresLoad = pLoad;
      this.requiresOpen = pOpen;
      this.requiresShort = pShort;
      this.requiresLoop = pLoop;
      this.scaleLeft = pScaleLeft;
      this.scaleRight = pScaleRight;
      this.mode = pMode;
   }

   public VNAScaleSymbols.SCALE_TYPE getScaleLeft() {
      return this.scaleLeft;
   }

   public VNAScaleSymbols.SCALE_TYPE getScaleRight() {
      return this.scaleRight;
   }

   public boolean isRequiresLoad() {
      return this.requiresLoad;
   }

   public boolean isRequiresLoop() {
      return this.requiresLoop;
   }

   public boolean isRequiresOpen() {
      return this.requiresOpen;
   }

   public boolean isRequiresRSS1() {
      return this.requiresRSS1;
   }

   public boolean isRequiresRSS2() {
      return this.requiresRSS2;
   }

   public boolean isRequiresRSS3() {
      return this.requiresRSS3;
   }

   public boolean isRequiresShort() {
      return this.requiresShort;
   }

   public void setRequiresLoad(boolean requiresLoad) {
      this.requiresLoad = requiresLoad;
   }

   public void setRequiresLoop(boolean requiresLoop) {
      this.requiresLoop = requiresLoop;
   }

   public void setRequiresOpen(boolean requiresOpen) {
      this.requiresOpen = requiresOpen;
   }

   public void setRequiresRSS1(boolean requiresRSS1) {
      this.requiresRSS1 = requiresRSS1;
   }

   public void setRequiresRSS2(boolean requiresRSS2) {
      this.requiresRSS2 = requiresRSS2;
   }

   public void setRequiresRSS3(boolean requiresRSS3) {
      this.requiresRSS3 = requiresRSS3;
   }

   public void setRequiresShort(boolean requiresShort) {
      this.requiresShort = requiresShort;
   }

   public void setScaleLeft(VNAScaleSymbols.SCALE_TYPE scaleLeft) {
      this.scaleLeft = scaleLeft;
   }

   public void setScaleRight(VNAScaleSymbols.SCALE_TYPE scaleRight) {
      this.scaleRight = scaleRight;
   }

   public void setMode(VNAScanMode mode) {
      this.mode = mode;
   }

   public VNAScanMode getMode() {
      return this.mode;
   }

   public String toString() {
      return this.mode.toString();
   }
}
