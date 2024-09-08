package krause.vna.data.fft.loni;

public class NotPowerOf2Exception extends Exception {
   private final int _numOfPoint;

   NotPowerOf2Exception(int index) {
      this._numOfPoint = index;
   }

   public String toString() {
      return "NotPowerOf2Exception[ " + this._numOfPoint + " is not an integer power of 2 ]";
   }
}
