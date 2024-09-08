package krause.common.exception;

public class IllegalResponseException extends ProcessingException {
   public IllegalResponseException() {
   }

   public IllegalResponseException(String message) {
      super(message);
   }

   public IllegalResponseException(String message, Throwable cause) {
      super(message, cause);
   }

   public IllegalResponseException(Throwable cause) {
      super(cause);
   }
}
