package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import krause.common.resources.CommonMessages;

public class DoubleValidator {
   public static double parse(String numAsString, Double min, Double max, String context, ValidationResults results) {
      NumberFormat fmt = NumberFormat.getNumberInstance();
      double rc = 0.0D;

      ValidationResult res;
      try {
         rc = fmt.parse(numAsString).doubleValue();
         String msg;
         if (rc < min) {
            msg = CommonMessages.getString("DoubleValidator.tooSmall");
            res = new ValidationResult(MessageFormat.format(msg, min));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }

         if (rc > max) {
            msg = CommonMessages.getString("DoubleValidator.tooLarge");
            res = new ValidationResult(MessageFormat.format(msg, max));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }
      } catch (ParseException var10) {
         res = new ValidationResult(var10, var10.getMessage());
         res.setType(ValidationResult.ValidationType.ERROR);
         res.setErrorObject(context);
         results.add(res);
      }

      return rc;
   }
}
