package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import krause.common.resources.CommonMessages;
import org.apache.commons.math3.complex.Complex;

public class ComplexValidator {
   public static double parseReal(String sReal, Complex min, Complex max, String context, ValidationResults results) {
      NumberFormat fmt = NumberFormat.getNumberInstance();
      double real = 0.0D;

      ValidationResult res;
      try {
         real = fmt.parse(sReal).doubleValue();
         String msg;
         if (real < min.getReal()) {
            msg = CommonMessages.getString("ComplexValidator.realTooSmall");
            res = new ValidationResult(MessageFormat.format(msg, min.getReal()));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }

         if (real > max.getReal()) {
            msg = CommonMessages.getString("ComplexValidator.realTooLarge");
            res = new ValidationResult(MessageFormat.format(msg, max.getReal()));
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

      return real;
   }

   public static double parseImaginary(String sImaginary, Complex min, Complex max, String context, ValidationResults results) {
      NumberFormat fmt = NumberFormat.getNumberInstance();
      double imaginary = 0.0D;

      ValidationResult res;
      try {
         imaginary = fmt.parse(sImaginary).doubleValue();
         String msg;
         if (imaginary < min.getImaginary()) {
            msg = CommonMessages.getString("ComplexValidator.imaginaryTooSmall");
            res = new ValidationResult(MessageFormat.format(msg, min.getImaginary()));
            res.setType(ValidationResult.ValidationType.ERROR);
            res.setErrorObject(context);
            results.add(res);
         }

         if (imaginary > max.getImaginary()) {
            msg = CommonMessages.getString("ComplexValidator.imaginaryTooLarge");
            res = new ValidationResult(MessageFormat.format(msg, max.getImaginary()));
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

      return imaginary;
   }
}
