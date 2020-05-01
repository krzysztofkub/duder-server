package org.duder.user.service;

import org.duder.user.dto.FacebookUserData;

public interface FacebookService {
    FacebookUserData getEmailAddress(String accessToken);
}
