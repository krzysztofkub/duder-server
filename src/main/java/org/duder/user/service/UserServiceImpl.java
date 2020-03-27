package org.duder.user.service;

import org.duder.user.dao.model.User;
import org.duder.user.dao.repository.UserRepository;
import org.duder.user.dto.UserDto;
import org.duder.user.exception.UserAlreadyExistsException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
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
}
