package org.duder.integration;

import org.duder.dto.chat.ChatMessage;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SigningIT extends AbstractIT {

    private static final String REGISTER_USER_ENDPOINT = "/user/register";

    @Autowired
    private UserRepository userRepository;

    @Test
    public void register_createsUser_whenPassedValidation() {
        //given
        String login = "skfjasodfja";
        String nickname = "asdokjas";
        String password = "password2";
        RegisterAccount userDto = RegisterAccount.builder()
                .login(login)
                .nickname(nickname)
                .password(password)
                .build();

        //when
        ResponseEntity<Void> response = testRestTemplate.postForEntity(url + REGISTER_USER_ENDPOINT, userDto, Void.class);

        //then
        Optional<User> user = userRepository.findByLoginIgnoreCase(login);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(user.isPresent());
        userRepository.delete(user.get());
    }


    @Test
    public void register_returnError_whenLoginAlreadyExists() {
        //given
        RegisterAccount userDto = RegisterAccount.builder()
                .login("login")
                .nickname("nickname")
                .password("password")
                .build();

        //when
        ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity(url + REGISTER_USER_ENDPOINT, userDto, Void.class);

        //then
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }
}