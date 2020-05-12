package org.duder.user.service;

import org.duder.dto.user.Dude;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.exception.UserAlreadyExistsException;
import org.duder.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    User register(RegisterAccount account) throws UserAlreadyExistsException;

    Optional<LoginResponse> login(String username, String password);

    Optional<LoginResponse> fbLogin(String accessToken);

    void updateProfileImage(String url, String sessionToken);

    Optional<User> getUserByToken(String token);

    Set<User> getUserFriendsByToken(String token);

    List<Dude> getDudes(int page, int size, String sessionToken);
}
