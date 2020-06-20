package org.duder.user.service;

import org.apache.commons.lang3.StringUtils;
import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class DefaultSessionService extends LoggedDuderAwareBean implements SessionService {
    private final UserRepository userRepository;

    public DefaultSessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidSessionTokenException("token is empty");
        }
        return userRepository.findBySessionToken(token);
    }
}
