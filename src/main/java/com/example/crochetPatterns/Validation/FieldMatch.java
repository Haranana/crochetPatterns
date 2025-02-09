package com.example.crochetPatterns.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = FieldMatchValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface FieldMatch {
    String message() default "Pola nie są zgodne!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Nazwy pól do porównania
    String first();
    String second();

    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FieldMatch[] value();
    }
}