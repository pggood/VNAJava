package krause.common.exception;

public class UserNotActivatedException extends ProcessingException {
   public UserNotActivatedException() {
   }

   public UserNotActivatedException(String message, Throwable cause) {
      super(message, cause);
   }

   public UserNotActivatedException(Throwable cause) {
      super(cause);
   }

   public UserNotActivatedException(String text) {
      super(text);
   }
}
