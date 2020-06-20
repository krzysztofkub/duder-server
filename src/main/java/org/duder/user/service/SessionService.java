package org.duder.user.service;

import org.duder.user.model.User;

import java.util.Optional;

public interface SessionService {

    Optional<User> getUserByToken(String token);
}
