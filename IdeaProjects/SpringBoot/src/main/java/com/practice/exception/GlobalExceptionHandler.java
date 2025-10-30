package com.practice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "[AOP] Internal Error: " + ex.getMessage());
        body.put("user", ex.getUser());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserExists(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("[AOP] Internal Error: " + ex.getMessage());
    }
}
