package krause.common.validation;

import java.text.MessageFormat;
import krause.common.resources.CommonMessages;

public class StringValidator {
   public static String parse(String value, long minLen, long maxLen, String context, ValidationResults results) {
      String msg;
      ValidationResult res;
      if ((long)value.length() < minLen) {
         msg = CommonMessages.getString("StringValidator.tooShort");
         res = new ValidationResult(MessageFormat.format(msg, minLen));
         res.setType(ValidationResult.ValidationType.ERROR);
         res.setErrorObject(context);
         results.add(res);
      } else if ((long)value.length() > maxLen) {
         msg = CommonMessages.getString("StringValidator.tooLong");
         res = new ValidationResult(MessageFormat.format(msg, maxLen));
         res.setType(ValidationResult.ValidationType.ERROR);
         res.setErrorObject(context);
         results.add(res);
      }

      return value;
   }
}
