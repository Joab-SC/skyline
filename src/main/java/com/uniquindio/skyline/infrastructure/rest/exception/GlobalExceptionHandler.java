package com.uniquindio.skyline.infrastructure.rest.exception;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainRuleException.class)
    public ResponseEntity<Map<String, Object>> handleDomainRule(DomainRuleException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage()); // 400
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "The requested resource does not exist"); // 404
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "message", message
        );
        return ResponseEntity.status(status).body(body);
    }
}