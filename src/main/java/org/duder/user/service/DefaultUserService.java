package org.duder.user.service;

import org.duder.user.dao.User;
import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.repository.UserRepository;
import org.duder.user.dto.UserDto;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final Map<String, UserDetails> tokenCache = new ConcurrentHashMap<>();

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User register(UserDto userDto) {
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

    @Override
    public Optional<UserDto> login(String login, String password) {
        Optional<User> userOpt = userRepository.findByLoginIgnoreCaseAndPasswordIgnoreCase(login, password);
        return userOpt.map(this::processLoggedUser);
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
    public boolean authenticateUser(String login, String password) {
        return userRepository.findByLoginIgnoreCaseAndPasswordIgnoreCase(login, password).isPresent();
    }

    private UserDto processLoggedUser(User user) {
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString();

        UserDto userDto = UserDto.builder()
                .login(user.getLogin())
                .nickname(user.getNickname())
                .sessionToken(token).build();

        putUserToSessionCache(token, userDto);
        return userDto;
    }

    private void putUserToSessionCache(String token, UserDto user) {
        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(user.getLogin(), "password",
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.createAuthorityList("USER")
                );
        tokenCache.put(token, userDetails);
    }
}
