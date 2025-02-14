package com.example.crochetPatterns.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvise {

    @ExceptionHandler(ElementNotFoundException.class)
    public String handleElementNotFoundException(ElementNotFoundException ex,
                                                 HttpServletRequest request,
                                                 Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        return "error404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 HttpServletRequest request,
                                                 Model model) {
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        return "error400";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex,
                                              HttpServletRequest request,
                                              Model model) {
        model.addAttribute("message", "Brak uprawnień do dostępu");
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        return "error403";
    }

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