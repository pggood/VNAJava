package krause.common.exception;

public class FunctionNotSupportedException extends ProcessingException {
   public FunctionNotSupportedException() {
   }

   public FunctionNotSupportedException(String message) {
      super(message);
   }

   public FunctionNotSupportedException(String message, Throwable cause) {
      super(message, cause);
   }

   public FunctionNotSupportedException(Throwable cause) {
      super(cause);
   }
}
