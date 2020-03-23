package org.duder.chat.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.duder.chat.model.MessageType;
import org.duder.chat.scheduler.MessageCache;
import org.duder.chat.scheduler.MessageEntity;
import org.duder.chat.scheduler.MessageRepository;
import org.duder.chat.utils.MySQLContainerProvider;
import org.duder.chat.model.ChatMessage;
import org.duder.chat.utils.WebsocketClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WebsocketIT {

    @Autowired
    private MessageCache messageCache;
    @Autowired
    private MessageRepository messageRepository;

    @LocalServerPort
    private int port;
    private String url;
    private CompletableFuture<ChatMessage> completableFuture;

    private static final String SEND_MESSAGE_ENDPOINT = "/app/sendMessage";
    private static final String SUBSCRIBE_CHAT_ENDPOINT = "/topic/public";

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        url = "ws://localhost:" + port + "/ws";
    }

    @Test
    public void sendMessage_sendsMessagesToSubscibersAndPersistsMessageToDb() throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        //given
        final String sender = "Sender";
        final String content = "Content";
        final MessageType messageType = MessageType.CHAT;

        WebsocketClient websocketClient = new WebsocketClient(url, SUBSCRIBE_CHAT_ENDPOINT, SEND_MESSAGE_ENDPOINT);
        websocketClient.subscribeForOneMessage(completableFuture, ChatMessage.class);
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .content(content)
                .type(messageType)
                .build();

        //when
        websocketClient.sendMessage(chatMessage);

        //then
        ChatMessage websocketMessage = completableFuture.get(10, TimeUnit.SECONDS);

        //wait for scheduler
        Thread.sleep(2000);

        List<MessageEntity> messagesFromDb = messageRepository.findAll();
        MessageEntity messageEntity = messagesFromDb.get(0);

        assertNotNull(websocketMessage);
        assertNotNull(messageEntity);
        assertEquals(sender, messageEntity.getAuthor());
        assertEquals(content, messageEntity.getContent());
        assertEquals(messageType, messageEntity.getMessageType());
    }
}