package org.duder.chat.controller;

import org.duder.chat.dao.entity.Message;
import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.model.ChatMessage;
import org.duder.chat.model.MessageType;
import org.duder.chat.utils.MySQLContainerProvider;
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
        Message messageEntity = Message
                .builder()
                .messageType(MessageType.CHAT)
                .content("ASASDASD")
                .build();
        messageRepository.saveAndFlush(messageEntity);

        //when
        ChatMessage[] messages = testRestTemplate.getForObject(url + GET_CHAT_STATE_ENDPOINT, ChatMessage[].class);
        ChatMessage message = messages[0];

        assertEquals(1, messages.length);
        assertEquals(messageEntity.getMessageType(), message.getType());
        assertEquals(messageEntity.getAuthor(), message.getSender());
        assertEquals(messageEntity.getContent(), message.getContent());
    }
}