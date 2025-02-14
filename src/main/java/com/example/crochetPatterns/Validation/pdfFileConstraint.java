package com.example.crochetPatterns.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = pdfFileValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface pdfFileConstraint {
    String message() default "file is not in proper PDF format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
