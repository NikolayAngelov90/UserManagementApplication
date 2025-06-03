package com.usermanagement.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        StringBuilder sb = new StringBuilder();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName;
            try {
                fieldName = error.getField();

            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }

            String message = error.getDefaultMessage();
            sb.append(String.format("%s: %s\n", fieldName, message));
        });

        return ResponseEntity
                .badRequest()
                .body(sb.toString());
    }
}
