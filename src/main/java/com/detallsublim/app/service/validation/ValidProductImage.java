package com.detallsublim.app.service.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidProductImageValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidProductImage {
    String message() default "La imagen debe ser JPEG, PNG o WebP y no superar 1 MB.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
