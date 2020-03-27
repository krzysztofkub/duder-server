package org.duder.integration;

import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.dto.ChatMessage;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import static org.junit.Assert.assertEquals;


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
    private MessageRepository messageRepository;

    private String url;
    private String GET_CHAT_STATE_ENDPOINT = "/getChatState";

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:" + port;
    }

    @Test
    public void getChatState() {
        //given
        //Data persisted by .sql file -> look on class annotation

        //when
        ChatMessage[] messages = testRestTemplate.getForObject(url + GET_CHAT_STATE_ENDPOINT, ChatMessage[].class);
        ChatMessage message = messages[0];

        assertEquals(1, messages.length);
    }
}