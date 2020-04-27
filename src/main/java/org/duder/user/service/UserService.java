package org.duder.user.service;

import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.dao.User;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {
    User register(RegisterAccount account) throws UserAlreadyExistsException;

    Optional<LoginResponse> login(String username, String password);

    Optional<UserDetails> getUserDetailsByToken(String token);

    Optional<User> getUserByToken(String token);

    boolean authenticateUser(String login, String password);
}
