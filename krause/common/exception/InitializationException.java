package krause.common.exception;

public class InitializationException extends ProcessingException {
   public InitializationException() {
   }

   public InitializationException(String message, Throwable cause) {
      super(message, cause);
   }

   public InitializationException(String message) {
      super(message);
   }

   public InitializationException(Throwable cause) {
      super(cause);
   }
}
