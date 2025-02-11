package com.example.crochetPatterns.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class AvatarOrNullFileValidator implements ConstraintValidator<avatarOrNullFileConstraint, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty()) {
            return true;
        }
        String contentType = file.getContentType();
        return ("image/png".equals(contentType) || "image/jpeg".equals(contentType)) && file.getSize() < 5000000;
    }
}