package krause.vna.data.calibrated;

import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;

public class VNACalibrationContext {
   private VNACalibrationBlock calibrationBlock;
   private Double calibrationTemperature = null;
   private Double conversionTemperature = null;
   private VNADeviceInfoBlock dib = null;
   private VNAScanMode scanMode = null;
   private double returnLossPerBit = 0.0D;
   private double transmissionLossPerBit = 0.0D;
   private int adcBits = 10;
   private double portExtensionPhaseConstant = 0.0D;

   public VNACalibrationBlock getCalibrationBlock() {
      return this.calibrationBlock;
   }

   public Double getCalibrationTemperature() {
      return this.calibrationTemperature;
   }

   public Double getConversionTemperature() {
      return this.conversionTemperature;
   }

   public VNADeviceInfoBlock getDib() {
      return this.dib;
   }

   public VNAScanMode getScanMode() {
      return this.scanMode;
   }

   public void setCalibrationBlock(VNACalibrationBlock calibrationBlock) {
      this.calibrationBlock = calibrationBlock;
   }

   public void setCalibrationTemperature(Double calibrationTemperature) {
      this.calibrationTemperature = calibrationTemperature;
   }

   public void setConversionTemperature(Double conversionTemperature) {
      this.conversionTemperature = conversionTemperature;
   }

   public void setDib(VNADeviceInfoBlock dib) {
      this.dib = dib;
   }

   public void setScanMode(VNAScanMode scanMode) {
      this.scanMode = scanMode;
   }

   public double getReturnLossPerBit() {
      return this.returnLossPerBit;
   }

   public void setReturnLossPerBit(double returnLossPerBit) {
      this.returnLossPerBit = returnLossPerBit;
   }

   public double getTransmissionLossPerBit() {
      return this.transmissionLossPerBit;
   }

   public void setTransmissionLossPerBit(double transmissionLossPerBit) {
      this.transmissionLossPerBit = transmissionLossPerBit;
   }

   public int getAdcBits() {
      return this.adcBits;
   }

   public void setAdcBits(int adcBits) {
      this.adcBits = adcBits;
   }

   public double getPortExtensionPhaseConstant() {
      return this.portExtensionPhaseConstant;
   }

   public void setPortExtensionPhaseConstant(double portExtensionPhaseConstant) {
      this.portExtensionPhaseConstant = portExtensionPhaseConstant;
   }
}
