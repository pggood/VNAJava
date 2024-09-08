package krause.common.exception;

public class UserIdNotValidException extends ProcessingException {
   public UserIdNotValidException() {
   }

   public UserIdNotValidException(String message) {
      super(message);
   }

   public UserIdNotValidException(String message, Throwable cause) {
      super(message, cause);
   }

   public UserIdNotValidException(Throwable cause) {
      super(cause);
   }
}
