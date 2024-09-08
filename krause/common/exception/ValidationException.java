package krause.common.exception;

import krause.common.validation.ValidationResults;

public class ValidationException extends ProcessingException {
   private final ValidationResults results;

   public ValidationException() {
      this.results = null;
   }

   public ValidationException(String message) {
      super(message);
      this.results = null;
   }

   public ValidationException(String message, Throwable cause) {
      super(message, cause);
      this.results = null;
   }

   public ValidationException(Throwable cause) {
      super(cause);
      this.results = null;
   }

   public ValidationException(Throwable cause, ValidationResults pResults) {
      super(cause);
      this.results = pResults;
   }

   public ValidationException(ValidationResults pResults) {
      this.results = pResults;
   }

   public ValidationResults getResults() {
      return this.results;
   }
}
