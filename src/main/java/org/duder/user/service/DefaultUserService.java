package org.duder.user.service;

import org.duder.user.dao.User;
import org.duder.user.repository.UserRepository;
import org.duder.user.dto.UserDto;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final Map<String, UserDetails> tokenCache = new HashMap<>();

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User register(UserDto userDto) {
        Optional<User> byLogin = userRepository.findByLogin(userDto.getLogin());
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
        return userOpt.map(this::preProcessLoggedUser);
    }

    @Override
    public Optional<UserDetails> getUserByToken(String token) {
        return Optional.ofNullable(tokenCache.get(token));
    }

    @Override
    public boolean authenticateUser(String login, String password) {
        return userRepository.findByLoginIgnoreCaseAndPasswordIgnoreCase(login, password).isPresent();
    }

    private UserDto preProcessLoggedUser(User user) {
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
                new org.springframework.security.core.userdetails.User(user.getNickname(), "password",
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.createAuthorityList("USER")
                );
        tokenCache.put(token, userDetails);
    }
}
