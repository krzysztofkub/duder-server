package org.duder.user.service;

import org.duder.user.model.User;

public interface ProfileService {
    void updateProfilePicture(String url);

    void updateUserProfilePicture(String imageUrl, User user);
}
