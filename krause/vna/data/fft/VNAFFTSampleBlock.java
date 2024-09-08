package krause.vna.data.fft;

import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import org.apache.commons.math3.complex.Complex;

public class VNAFFTSampleBlock {
   private VNACalibratedSampleBlock scanData;
   private Complex[] fftInput = null;
   private Complex[] fftOutput = null;

   public VNACalibratedSampleBlock getScanData() {
      return this.scanData;
   }

   public void setScanData(VNACalibratedSampleBlock scanData) {
      this.scanData = scanData;
   }

   public Complex[] getFftInput() {
      return this.fftInput;
   }

   public void setFftInput(Complex[] fftInput) {
      this.fftInput = fftInput;
   }

   public Complex[] getFftOutput() {
      return this.fftOutput;
   }

   public void setFftOutput(Complex[] fftOutput) {
      this.fftOutput = fftOutput;
   }
}
