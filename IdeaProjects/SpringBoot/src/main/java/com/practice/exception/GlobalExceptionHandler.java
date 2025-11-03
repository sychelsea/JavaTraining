package com.practice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "status", 409,
                    "message", "[AOP] Internal Error: " + ex.getMessage(),
                    "user", ex.getUser()
                ));

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserExists(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("[AOP] Internal Error: " + ex.getMessage());
    }

    @ExceptionHandler(UserOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLockFailure(UserOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", 409,
                        "message", "[AOP] Internal Error: " + ex.getMessage(),
                        "user", ex.getUser()
                ));
    }
}
