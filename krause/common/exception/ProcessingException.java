package krause.common.exception;

import java.sql.SQLException;

public class ProcessingException extends Exception {
   public ProcessingException() {
   }

   public ProcessingException(String message) {
      super(message);
   }

   public ProcessingException(String message, Throwable cause) {
      super(message, cause);
   }

   public ProcessingException(Throwable cause) {
      super(cause);
   }

   public static void wrapSQLException(SQLException sqlE) throws ProcessingException {
      if ("23505".equals(sqlE.getSQLState())) {
         throw new DuplicateKeyException(sqlE);
      } else {
         throw new ProcessingException(sqlE);
      }
   }
}
