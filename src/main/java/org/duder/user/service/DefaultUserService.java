package org.duder.user.service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.duder.dto.user.LoggedAccount;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.dao.User;
import org.duder.user.dto.FacebookUserData;
import org.duder.user.repository.UserRepository;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final FacebookService facebookService;
    private final BiMap<String, UserDetails> tokenCache = HashBiMap.create();

    public DefaultUserService(UserRepository userRepository, FacebookService facebookService) {
        this.userRepository = userRepository;
        this.facebookService = facebookService;
    }


    @Override
    public User register(RegisterAccount userDto) {
        Optional<User> byLogin = userRepository.findByLoginIgnoreCase(userDto.getLogin());
        byLogin.ifPresent(user -> {
            throw new UserAlreadyExistsException(user);
        });

        User user = User.builder()
                .login(userDto.getLogin())
                .nickname(userDto.getNickname())
                .password(userDto.getPassword())
                .build();
        return userRepository.save(user);
    }

    private User register(FacebookUserData fbUserData) {
        String password = RandomStringUtils.random(10);
        RegisterAccount registerAccount = RegisterAccount.builder()
                .login(fbUserData.getEmail())
                .nickname(fbUserData.getName())
                .password(password)
                .build();
        return register(registerAccount);
    }

    @Override
    public Optional<LoginResponse> login(String login, String password) {
        return userRepository.findByLoginIgnoreCaseAndPasswordIgnoreCase(login, password)
                .map(this::processLoggedUser)
                .map(this::mapToLoginResponse);
    }

    @Override
    public Optional<LoginResponse> fbLogin(String accessToken) {
        FacebookUserData fbUserData = facebookService.getEmailAddress(accessToken);

        Optional<User> userOpt = userRepository.findByLoginIgnoreCase(fbUserData.getEmail());
        if (userOpt.isPresent()) {
            return login(userOpt.get().getLogin(), userOpt.get().getPassword());
        }
        User user = register(fbUserData);
        return login(user.getLogin(), user.getPassword());
    }

    private LoginResponse mapToLoginResponse(LoggedAccount loggedAccount) {
        return LoginResponse.builder()
                .nickname(loggedAccount.getNickname())
                .sessionToken(loggedAccount.getSessionToken())
                .build();
    }

    @Override
    public Optional<UserDetails> getUserDetailsByToken(String token) {
        return Optional.ofNullable(tokenCache.get(token));
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return getUserDetailsByToken(token)
                .flatMap(userDetails -> userRepository.findByLoginIgnoreCase(userDetails.getUsername()));
    }

    @Override
    public Set<User> getUserFriendsByToken(String token) {
        UserDetails userDetails = tokenCache.get(token);
        return userRepository
                .findByLoginIgnoreCase(userDetails.getUsername())
                .map(User::getFriends)
                .orElse(new HashSet<>());
    }

    private LoggedAccount processLoggedUser(User user) {
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString();

        LoggedAccount loggedAccount = LoggedAccount.builder()
                .login(user.getLogin())
                .nickname(user.getNickname())
                .sessionToken(token).build();

        putUserToSessionCache(token, loggedAccount);
        return loggedAccount;
    }

    private void putUserToSessionCache(String token, LoggedAccount loggedAccount) {
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(loggedAccount.getLogin(), "password",
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.createAuthorityList("USER")
                );
        tokenCache.forcePut(token, userDetails);
    }
}
