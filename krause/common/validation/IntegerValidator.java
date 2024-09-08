package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import krause.common.resources.CommonMessages;

public class IntegerValidator {
   public static int parse(String value, int min, int max, String context, ValidationResults results) {
      NumberFormat fmt = NumberFormat.getNumberInstance();
      int rc = 0;

      ValidationResult res;
      try {
         rc = fmt.parse(value).intValue();
         String msg;
         if (rc < min) {
            msg = CommonMessages.getString("IntegerValidator.tooSmall");
            res = new ValidationResult(MessageFormat.format(msg, min));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }

         if (rc > max) {
            msg = CommonMessages.getString("IntegerValidator.tooLarge");
            res = new ValidationResult(MessageFormat.format(msg, max));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }
      } catch (ParseException var9) {
         res = new ValidationResult(var9, var9.getMessage());
         res.setType(ValidationResult.ValidationType.ERROR);
         res.setErrorObject(context);
         results.add(res);
      }

      return rc;
   }
}
