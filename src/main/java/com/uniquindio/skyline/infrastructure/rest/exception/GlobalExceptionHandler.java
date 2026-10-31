package com.uniquindio.skyline.infrastructure.rest.exception;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainRuleException.class)
    public ResponseEntity<Map<String, Object>> handleDomainRule(DomainRuleException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage()); // 422
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "The requested resource does not exist"); // 404
    }

    // A DTO field failed its validation (@Valid, @NotBlank, @Positive, ...). Lists each field with its message.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFields(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> body = new LinkedHashMap<>(baseBody(HttpStatus.BAD_REQUEST, "The request has invalid fields"));
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body); // 400
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        String message = switch (ex) {
            case MethodArgumentTypeMismatchException e -> "The parameter '" + e.getName() + "' has the wrong format";
            case MissingServletRequestParameterException e -> "The parameter '" + e.getParameterName() + "' is required";
            case IllegalArgumentException e -> e.getMessage();
            default -> "The request body is not valid JSON or a value has the wrong format";
        };
        return buildResponse(HttpStatus.BAD_REQUEST, message); // 400
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(baseBody(status, message));
    }

    private Map<String, Object> baseBody(HttpStatus status, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "message", message
        );
    }
}