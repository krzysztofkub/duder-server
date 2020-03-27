package org.duder.user.exception;

import org.duder.user.dao.model.User;

public class UserAlreadyExistsException extends RuntimeException{
    private User user;
    public UserAlreadyExistsException(User user) {
        super("User with login " + user.getLogin() + "already exists");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
