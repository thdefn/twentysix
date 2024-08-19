package cm.twentysix.product.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NullOrAfterDateTimeValidator implements ConstraintValidator<NullOrAfterDateTime, String> {
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(NullOrAfterDateTime constraintAnnotation) {
        dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if (string == null) {
            return true;
        }
        try {
            return LocalDateTime.parse(string).isAfter(LocalDateTime.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
