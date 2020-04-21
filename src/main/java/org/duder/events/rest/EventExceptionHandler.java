package org.duder.events.rest;

import org.duder.chat.exception.DataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EventExceptionHandler {
    @ExceptionHandler(DataNotFoundException.class)
    protected ResponseEntity<Void> handleDataNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
