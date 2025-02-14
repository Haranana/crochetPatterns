package com.example.crochetPatterns.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Null;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;

public class pdfFileValidator implements ConstraintValidator<pdfFileConstraint , MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext constraintValidatorContext) {
        return "application/pdf".equals(value.getContentType()) && value.getSize()<25000000;
    }
}
