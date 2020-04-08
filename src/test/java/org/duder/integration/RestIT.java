package org.duder.integration;

import org.duder.chat.dto.ChatMessageDto;
import org.duder.user.dao.User;
import org.duder.user.repository.UserRepository;
import org.duder.user.dto.Code;
import org.duder.user.dto.UserDto;
import org.duder.user.dto.Response;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;


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
        String token = testRestTemplate.getForObject(url + LOGIN, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        //when
        ResponseEntity<ChatMessageDto[]> exchange = testRestTemplate.exchange(url + GET_CHAT_STATE_ENDPOINT, HttpMethod.GET, new HttpEntity<>(headers), ChatMessageDto[].class);
        ChatMessageDto[] messages = exchange.getBody();
        ChatMessageDto message = messages[0];

        assertEquals(1, messages.length);
    }

    @Test
    public void register_createsUser_whenPassedValidation() {
        //given
        String login = "skfjasodfja";
        String nickname = "asdokjas";
        UserDto userDto = UserDto.builder()
                .login(login)
                .nickname(nickname)
                .password("password2")
                .build();

        //when
        ResponseEntity<Response> response = testRestTemplate.postForEntity(url + REGISTER_USER_ENDPOINT, userDto, Response.class);

        //then
        Optional<User> user = userRepository.findByLogin("login2");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(user.isPresent());
        assertEquals(Code.OK, response.getBody().getCode());
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
        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(url + REGISTER_USER_ENDPOINT, userDto, Response.class);
        Response response = responseEntity.getBody();

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals(Code.USER_EXISTS, response.getCode());
        System.out.println(response.getMessage());
    }

}