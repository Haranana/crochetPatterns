package com.example.crochetPatterns.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class AvatarFileValidator implements ConstraintValidator<avatarFileConstraint, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // Jeśli plik nie został przesłany – uznajemy to za poprawne (nie zmieniamy avatara)
        if (file == null || file.isEmpty()) {
            return true;
        }
        String contentType = file.getContentType();
        // Akceptujemy PNG lub JPEG, limit rozmiaru 5MB
        return ("image/png".equals(contentType) || "image/jpeg".equals(contentType)) && file.getSize() < 5000000;
    }
}