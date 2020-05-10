package org.duder.integration;

import org.duder.dto.chat.ChatMessage;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.duder.utils.MySQLContainerProvider;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.GenericContainer;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class RestIT {

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String GET_CHAT_STATE_ENDPOINT = "/api/chat/getChatState";
    private static final String REGISTER_USER_ENDPOINT = "/user/register";
    private static final String LOGIN_ENDPOINT = "/user/login?login=" + LOGIN + "&password=" + PASSWORD;
    private static final String UPDATE_IMAGE_ENDPOINT = "/user/image";

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;
    private String url;

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:" + port;
    }

    @Test
    public void getChatState() {
        //given
        HttpHeaders headers = getActiveSessionToken();

        //when
        ResponseEntity<ChatMessage[]> exchange = testRestTemplate.exchange(url + GET_CHAT_STATE_ENDPOINT, HttpMethod.GET, new HttpEntity<>(headers), ChatMessage[].class);
        ChatMessage[] messages = exchange.getBody();

        assertEquals(1, messages.length);
    }

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

    private HttpHeaders getActiveSessionToken() {
        LoginResponse userDto = testRestTemplate.getForObject(url + LOGIN_ENDPOINT, LoginResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", userDto.getSessionToken());
        return headers;
    }
}