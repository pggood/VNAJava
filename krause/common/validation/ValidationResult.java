package krause.common.validation;

public class ValidationResult {
   private Object errorObject;
   private Exception exception;
   private ValidationResult.ValidationType type;
   private String message;

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   public ValidationResult(String pMessage) {
      this.type = ValidationResult.ValidationType.ERROR;
      this.setMessage(pMessage);
   }

   public ValidationResult(Exception pEx, String pMessage) {
      this.type = ValidationResult.ValidationType.ERROR;
      this.setMessage(pMessage);
      this.setException(pEx);
   }

   public void setException(Exception exception) {
      this.exception = exception;
   }

   public Exception getException() {
      return this.exception;
   }

   public void setErrorObject(Object errorObject) {
      this.errorObject = errorObject;
   }

   public Object getErrorObject() {
      return this.errorObject;
   }

   public void setType(ValidationResult.ValidationType type) {
      this.type = type;
   }

   public ValidationResult.ValidationType getType() {
      return this.type;
   }

   public String toString() {
      StringBuffer rc = new StringBuffer();
      if (this.getErrorObject() != null) {
         rc.append(this.errorObject + ": ");
      }

      rc.append(this.message);
      if (this.exception != null) {
         rc.append(" [" + this.exception.getLocalizedMessage() + "]");
      }

      return rc.toString();
   }

   public static enum ValidationType {
      INFO,
      WARNING,
      ERROR;
   }
}
