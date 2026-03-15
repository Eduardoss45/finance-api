package com.finances.finances_api.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldViolation> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(GlobalExceptionHandler::toViolation)
                .toList();

        ValidationErrorResponse body = new ValidationErrorResponse(
                "Validation failed",
                Instant.now(),
                errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private static FieldViolation toViolation(FieldError error) {
        String message = error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage();
        return new FieldViolation(error.getField(), message);
    }

    public record ValidationErrorResponse(String message, Instant timestamp, List<FieldViolation> errors) {
    }

    public record FieldViolation(String field, String message) {
    }
}
