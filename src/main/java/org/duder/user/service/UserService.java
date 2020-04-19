package org.duder.user.service;

import org.duder.user.dao.User;
import org.duder.user.dto.UserDto;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {
    User register(UserDto userDto) throws UserAlreadyExistsException;

    Optional<UserDto> login(String username, String password);

    Optional<UserDetails> getUserDetailsByToken(String token);

    Optional<User> getUserByToken(String token);

    boolean authenticateUser(String login, String password);
}
