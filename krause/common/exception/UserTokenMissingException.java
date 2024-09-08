package krause.common.exception;

public class UserTokenMissingException extends ProcessingException {
   public UserTokenMissingException() {
   }

   public UserTokenMissingException(String message, Throwable cause) {
      super(message, cause);
   }

   public UserTokenMissingException(Throwable cause) {
      super(cause);
   }

   public UserTokenMissingException(String text) {
      super(text);
   }
}
