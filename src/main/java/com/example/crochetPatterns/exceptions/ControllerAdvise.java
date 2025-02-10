package com.example.crochetPatterns.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Klasa ControllerAdvise służy do globalnej obsługi wyjątków w aplikacji.
 * W przypadku wystąpienia wyjątku, metoda odpowiedzialna za jego obsługę
 * zwraca odpowiedni szablon Thymeleaf z informacjami o błędzie.
 */
@ControllerAdvice
public class ControllerAdvise {

    /**
     * Obsługuje wyjątek informujący o braku elementu w bazie danych.
     * Szablon: error404.html
     */
    @ExceptionHandler(ElementNotFoundException.class)
    public String handleElementNotFoundException(ElementNotFoundException ex,
                                                 HttpServletRequest request,
                                                 Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        return "error404";
    }

    /**
     * Obsługuje wyjątki typu IllegalArgumentException.
     * Szablon: error400.html
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 HttpServletRequest request,
                                                 Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        return "error400";
    }

    /**
     * Obsługuje wyjątki związane z brakiem uprawnień.
     * Szablon: error403.html
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex,
                                              HttpServletRequest request,
                                              Model model) {
        model.addAttribute("message", "Brak uprawnień do dostępu");
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        return "error403";
    }

    /**
     * Globalna obsługa pozostałych wyjątków.
     * Szablon: defaultError.html
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex,
                                         HttpServletRequest request,
                                         Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "defaultError";
    }
}