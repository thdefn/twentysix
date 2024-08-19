package cm.twentysix.product.util.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrAfterDateTimeValidator.class)
public @interface NullOrAfterDateTime {
    String message() default "invalid datetime format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
