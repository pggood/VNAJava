package krause.common.exception;

public class UserTokenMismatchException extends ProcessingException {
   public UserTokenMismatchException() {
   }

   public UserTokenMismatchException(String message, Throwable cause) {
      super(message, cause);
   }

   public UserTokenMismatchException(Throwable cause) {
      super(cause);
   }

   public UserTokenMismatchException(String text) {
      super(text);
   }
}
