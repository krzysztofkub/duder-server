package org.duder.integration;

import org.duder.dto.user.LoginResponse;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class ProfileIT extends AbstractIT {
    private static final String UPDATE_IMAGE_ENDPOINT = "/api/profile/image";

    @Test
    @Rollback(false)
    public void update_user_image() {
        //given
        String imageUrl = "this is new image url";
        HttpHeaders headers = getActiveSessionToken();
        URI uri = UriComponentsBuilder.
                fromHttpUrl(url).
                path(UPDATE_IMAGE_ENDPOINT).
                build().toUri();
        RequestEntity<String> requestEntity = RequestEntity.post(uri).headers(headers).body(imageUrl);

        //when
        testRestTemplate.exchange(requestEntity, Void.class);

        //then
        LoginResponse userDto = testRestTemplate.getForObject(url + LOGIN_ENDPOINT, LoginResponse.class);
        assertEquals(imageUrl, userDto.getProfileImageUrl());
    }
}
