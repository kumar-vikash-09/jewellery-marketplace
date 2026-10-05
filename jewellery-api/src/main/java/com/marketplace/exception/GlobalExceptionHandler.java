package com.marketplace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(
            ResourceNotFoundException ex) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDuplicate(
            DuplicateResourceException ex) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 409,
                "message", ex.getMessage()
        );
    }
}
