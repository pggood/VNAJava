package krause.common.exception;

public class UserNotAuthenticatedException extends ProcessingException {
   public UserNotAuthenticatedException() {
   }

   public UserNotAuthenticatedException(String message, Throwable cause) {
      super(message, cause);
   }

   public UserNotAuthenticatedException(Throwable cause) {
      super(cause);
   }

   public UserNotAuthenticatedException(String text) {
      super(text);
   }
}
