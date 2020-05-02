package org.duder.user.rest;

import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.exception.UserAlreadyExistsException;
import org.duder.user.exception.WrongUserCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
class UserExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Void> handleUserAlreadyExists(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(WrongUserCredentialsException.class)
    protected ResponseEntity<Void> handleWrongUserCredentialsException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @ExceptionHandler(InvalidSessionTokenException.class)
    protected ResponseEntity<Void> handleInvalidSessionTokenException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
