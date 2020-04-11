package org.duder.user.exception;

public class WrongUserCredentialsException extends RuntimeException {
    public WrongUserCredentialsException(String message) {
        super(message);
    }
}
