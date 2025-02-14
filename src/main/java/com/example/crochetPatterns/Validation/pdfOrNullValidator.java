package com.example.crochetPatterns.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class pdfOrNullValidator implements ConstraintValidator<pdfOrNullFileConstraint , MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null || value.isEmpty()) {
            return true;
        }

        return "application/pdf".equals(value.getContentType()) && value.getSize()<25000000;
    }
}
