package krause.common.exception;

public class PropertyNotFoundException extends Exception {
   public PropertyNotFoundException() {
   }

   public PropertyNotFoundException(Throwable cause) {
      super(cause);
   }

   public PropertyNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }

   public PropertyNotFoundException(String message) {
      super(message);
   }
}
