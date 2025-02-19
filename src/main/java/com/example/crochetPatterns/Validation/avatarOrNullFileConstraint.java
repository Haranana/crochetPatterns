package com.example.crochetPatterns.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AvatarOrNullFileValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface avatarOrNullFileConstraint {
    String message() default "Incorrect file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}