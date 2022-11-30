package org.acme.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionManager {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<String> toResponse(HttpException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
    }
}

