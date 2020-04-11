package org.duder.integration;

import org.duder.chat.dto.ChatMessageDto;
import org.duder.user.dao.User;
import org.duder.user.dto.UserDto;
import org.duder.user.repository.UserRepository;
import org.duder.utils.MySQLContainerProvider;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestIT {

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;

    private String url;
    private String GET_CHAT_STATE_ENDPOINT = "/api/getChatState";
    private String REGISTER_USER_ENDPOINT = "/user/register";
    private String LOGIN = "/user/login?login=login&password=password";

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:" + port;
    }

    @Test
    public void getChatState() {
        //given
        UserDto userDto = testRestTemplate.getForObject(url + LOGIN, UserDto.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", userDto.getSessionToken());

        //when
        ResponseEntity<ChatMessageDto[]> exchange = testRestTemplate.exchange(url + GET_CHAT_STATE_ENDPOINT, HttpMethod.GET, new HttpEntity<>(headers), ChatMessageDto[].class);
        ChatMessageDto[] messages = exchange.getBody();

        assertEquals(1, messages.length);
    }

    @Test
    public void register_createsUser_whenPassedValidation() {
        //given
        String login = "skfjasodfja";
        String nickname = "asdokjas";
        String password = "password2";
        UserDto userDto = UserDto.builder()
                .login(login)
                .nickname(nickname)
                .password(password)
                .build();

        //when
        ResponseEntity<Void> response = testRestTemplate.postForEntity(url + REGISTER_USER_ENDPOINT, userDto, Void.class);

        //then
        Optional<User> user = userRepository.findByLogin(login);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(user.isPresent());
        userRepository.delete(user.get());
    }


    @Test
    public void register_returnError_whenLoginAlreadyExists() {
        //given
        UserDto userDto = UserDto.builder()
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