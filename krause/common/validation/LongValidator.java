package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import krause.common.resources.CommonMessages;

public class LongValidator {
   public static long parse(String value, long min, long max, String context, ValidationResults results) {
      NumberFormat fmt = NumberFormat.getNumberInstance();
      long rc = 0L;

      ValidationResult res;
      try {
         rc = fmt.parse(value).longValue();
         String msg;
         if (rc < min) {
            msg = CommonMessages.getString("LongValidator.tooSmall");
            res = new ValidationResult(MessageFormat.format(msg, min));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }

         if (rc > max) {
            msg = CommonMessages.getString("LongValidator.tooLarge");
            res = new ValidationResult(MessageFormat.format(msg, max));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }
      } catch (ParseException var12) {
         res = new ValidationResult(var12, var12.getMessage());
         res.setType(ValidationResult.ValidationType.ERROR);
         res.setErrorObject(context);
         results.add(res);
      }

      return rc;
   }
}
