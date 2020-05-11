package org.duder.user.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.duder.dto.user.Dude;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.dto.FacebookUserData;
import org.duder.user.exception.UserAlreadyExistsException;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final FacebookService facebookService;

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
        String password = RandomStringUtils.randomAscii(10);
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
                .map(this::processLoggedUser);
    }

    private LoginResponse processLoggedUser(User user) {
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        user.setSessionToken(token);
        userRepository.save(user);
        return LoginResponse.builder()
                .nickname(user.getNickname())
                .profileImageUrl(user.getImageUrl())
                .sessionToken(token)
                .build();
    }

    @Override
    public Optional<LoginResponse> fbLogin(String accessToken) {
        FacebookUserData fbUserData = facebookService.getFacebookUserData(accessToken);
        User user = userRepository
                .findByLoginIgnoreCase(fbUserData.getEmail())
                .orElseGet(() -> register(fbUserData));
        updateUserProfilePicture(fbUserData.getImageUrl(), user);
        return login(user.getLogin(), user.getPassword());
    }

    private void updateUserProfilePicture(String imageUrl, User user) {
        if (!imageUrl.equals(user.getImageUrl())) {
            user.setImageUrl(imageUrl);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void updateProfileImage(String url, String sessionToken) {
        userRepository.findBySessionToken(sessionToken)
                .ifPresent(user -> updateUserProfilePicture(url, user));
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return userRepository.findBySessionToken(token);
    }

    @Override
    public Set<User> getUserFriendsByToken(String token) {
        return userRepository.findBySessionToken(token)
                .map(User::getFriends)
                .orElse(new HashSet<>());
    }

    @Override
    public List<Dude> getDudes(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nickname"));
        return userRepository.findAll(pageRequest)
                .stream()
                .map(this::mapToDude)
                .collect(Collectors.toList());
    }

    private Dude mapToDude(User user) {
        return Dude.builder()
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
