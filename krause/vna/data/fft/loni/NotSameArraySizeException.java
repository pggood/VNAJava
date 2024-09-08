package krause.vna.data.fft.loni;

public class NotSameArraySizeException extends Exception {
   public String toString() {
      return "NotSameArraySizeException[ The dimensions of input arrays are not the same! ]";
   }
}
