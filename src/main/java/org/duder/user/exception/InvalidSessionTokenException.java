package org.duder.user.exception;

public class InvalidSessionTokenException extends RuntimeException {

    public InvalidSessionTokenException() {
    }

    public InvalidSessionTokenException(String message) {
        super(message);
    }
}
