package cm.twentysix.user.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private Enum<?>[] enums;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enums = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && Arrays.stream(enums)
                .anyMatch(enumValue -> enumValue.toString().equals(value));
    }
}
