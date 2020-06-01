package org.duder.user.service;

import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class DefaultProfileService extends LoggedDuderBean implements ProfileService {

    private final UserRepository userRepository;

    public DefaultProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void updateUserProfilePicture(String imageUrl, User user) {
        if (!imageUrl.equals(user.getImageUrl())) {
            user.setImageUrl(imageUrl);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void updateProfilePicture(String url) {
        userRepository.findBySessionToken(getSessionToken())
                .ifPresent(user -> updateUserProfilePicture(url, user));
    }
}
