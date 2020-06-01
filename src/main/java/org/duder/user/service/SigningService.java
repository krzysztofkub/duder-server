package org.duder.user.service;

import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.exception.UserAlreadyExistsException;
import org.duder.user.model.User;

import java.util.Optional;

public interface SigningService {
    User register(RegisterAccount account) throws UserAlreadyExistsException;

    Optional<LoginResponse> login(String username, String password);

    Optional<LoginResponse> fbLogin(String accessToken);
}
