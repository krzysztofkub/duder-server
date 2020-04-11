package org.duder.user.rest;

import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Void> handleUserAlreadyExists(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
