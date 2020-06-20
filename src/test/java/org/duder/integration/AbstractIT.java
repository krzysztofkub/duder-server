package org.duder.integration;

import org.duder.common.MySQLContainerProvider;
import org.duder.dto.user.LoginResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
abstract public class AbstractIT {

    protected static final String LOGIN = "login";
    protected static final String PASSWORD = "password";
    protected static final String LOGIN_ENDPOINT = "/user/login?login=" + LOGIN + "&password=" + PASSWORD;
    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();
    protected String url;
    @Autowired
    protected TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port;

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:" + port;
    }

    protected HttpHeaders getActiveSessionToken() {
        LoginResponse userDto = testRestTemplate.getForObject(url + LOGIN_ENDPOINT, LoginResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", userDto.getSessionToken());
        return headers;
    }
}
