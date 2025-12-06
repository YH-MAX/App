package com.smartwater.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = baseBody(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request
        );

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        body.put("errors", fieldErrors);

        log.warn("Validation failed on {} {}: {}", request.getMethod(),
                request.getRequestURI(), fieldErrors);

        return ResponseEntity.badRequest().body(body); // 400
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = baseBody(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );

        log.warn("Not found on {} {}: {}", request.getMethod(),
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body); // 404
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = baseBody(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );

        log.warn("Bad request on {} {}: {}", request.getMethod(),
                request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(body); // 400
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = baseBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request
        );

        log.error("Unexpected error on {} {}: {}", request.getMethod(),
                request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body); // 500
    }

    private Map<String, Object> baseBody(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return body;
    }
}
