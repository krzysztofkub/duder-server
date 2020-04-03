package org.duder.user.rest;

import org.duder.user.dto.Code;
import org.duder.user.dto.Response;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Response> handleUserAlreadyExists(Exception e) {
        Response response = Response.builder()
                .code(Code.USER_EXISTS)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
