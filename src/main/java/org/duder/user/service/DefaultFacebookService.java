package org.duder.user.service;

import org.duder.user.dto.FacebookDataWrapper;
import org.duder.user.dto.FacebookUserData;
import org.duder.user.dto.FacebookUserValidationResponse;
import org.duder.user.exception.InvalidSessionTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
class DefaultFacebookService implements FacebookService {
    private final String appId;
    private final String appSecret;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    private final String FACEBOOK_URL = "https://graph.facebook.com";
    private final String USER_VALIDATION_ENDPOINT = "/debug_token";

    public DefaultFacebookService(@Value("${duder.facebook.app.id}") String appId,
                                  @Value("${duder.facebook.app.secret}") String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    @Override
    public FacebookUserData getEmailAddress(String accessToken) {
        FacebookUserValidationResponse fbUser = validateAccessToken(accessToken);
        String uri = UriComponentsBuilder
                .fromHttpUrl(FACEBOOK_URL)
                .path(fbUser.getUser_id())
                .queryParam("access_token", accessToken)
                .queryParam("fields", "email,name")
                .toUriString();

        return restTemplate.getForObject(uri, FacebookUserData.class);
    }

    private FacebookUserValidationResponse validateAccessToken(String accessToken) {
        String uri = UriComponentsBuilder.fromHttpUrl(FACEBOOK_URL)
                .path(USER_VALIDATION_ENDPOINT)
                .queryParam("access_token", appId + "|" + appSecret)
                .queryParam("input_token", accessToken)
                .toUriString();
        FacebookUserValidationResponse response = restTemplate.getForObject(uri, FacebookDataWrapper.class).getData();
        if (response.getIs_valid() == null || !response.getIs_valid()) {
            throw new InvalidSessionTokenException("Bad facebook access token provided");
        }
        return response;
    }
}
