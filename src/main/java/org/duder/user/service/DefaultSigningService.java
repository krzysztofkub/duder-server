package org.duder.user.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.dto.FacebookUserData;
import org.duder.user.exception.UserAlreadyExistsException;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class DefaultSigningService extends LoggedDuderBean implements SigningService {
    private final FacebookService facebookService;
    private final UserRepository userRepository;
    private final ProfileService profileService;

    public DefaultSigningService(FacebookService facebookService, UserRepository userRepository, ProfileService profileService) {
        this.facebookService = facebookService;
        this.userRepository = userRepository;
        this.profileService = profileService;
    }

    @Override
    public User register(RegisterAccount userDto) {
        Optional<User> byLogin = userRepository.findByLoginIgnoreCase(userDto.getLogin());
        byLogin.ifPresent(user -> {
            warn("User with that login already exists : " + userDto.getLogin());
            throw new UserAlreadyExistsException(user);
        });

        User user = User.builder()
                .login(userDto.getLogin())
                .nickname(userDto.getNickname())
                .password(userDto.getPassword())
                .build();
        User savedUser = userRepository.save(user);
        info("Created new user with login : " + savedUser.getLogin());
        return savedUser;
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
        profileService.updateUserProfilePicture(fbUserData.getImageUrl(), user);
        return login(user.getLogin(), user.getPassword());
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
}
