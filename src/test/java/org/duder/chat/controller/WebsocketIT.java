package org.duder.chat.controller;


import org.duder.chat.model.ChatMessage;
import org.duder.chat.model.MessageType;
import org.duder.chat.scheduler.MessageCache;
import org.duder.chat.scheduler.MessageEntity;
import org.duder.chat.scheduler.MessageRepository;
import org.duder.chat.utils.MySQLContainerProvider;
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

import static org.junit.Assert.*;

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

    private static final String SEND_MESSAGE_ENDPOINT = "/app/sendMessage";
    private static final String SUBSCRIBE_CHAT_ENDPOINT = "/topic/public";

    private final String SENDER = "Sender";
    private final String CONTENT = "Content";
    private final MessageType MESSAGE_TYPE = MessageType.CHAT;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Before
    public void setup() {
        url = "ws://localhost:" + port + "/ws";
    }

    @Test
    public void sendMessage_sendsMessagesToSubscibersAndPersistsMessageToDb() throws InterruptedException, ExecutionException, TimeoutException {
        //given
        CompletableFuture<ChatMessage> completableFuture = new CompletableFuture<>();
        WebsocketClient websocketClient = new WebsocketClient(url, SUBSCRIBE_CHAT_ENDPOINT, SEND_MESSAGE_ENDPOINT);
        websocketClient.subscribeForOneMessage(completableFuture, ChatMessage.class);
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(SENDER)
                .content(CONTENT)
                .type(MESSAGE_TYPE)
                .build();

        //when
        websocketClient.sendMessage(chatMessage);
        ChatMessage websocketMessage = completableFuture.get(10, TimeUnit.SECONDS);
        //wait for scheduler
        Thread.sleep(2000);
        List<MessageEntity> messagesFromDb = messageRepository.findAll();
        MessageEntity messageEntity = messagesFromDb.get(0);

        //then
        assertNotNull(websocketMessage);
        assertNotNull(messageEntity);
        assertEquals(SENDER, messageEntity.getAuthor());
        assertEquals(CONTENT, messageEntity.getContent());
        assertEquals(MESSAGE_TYPE, messageEntity.getMessageType());
    }

    @Test
    public void sendMessage_channel() throws InterruptedException, ExecutionException, TimeoutException {
        //given
        int channelId = 1;
        int dummyChannelId = 2;

        CompletableFuture<ChatMessage> completableFuture = new CompletableFuture<>();
        CompletableFuture<ChatMessage> dummyCompletableFuture = new CompletableFuture<>();

        WebsocketClient messageProducer = new WebsocketClient(url, "/topic/" + channelId, SEND_MESSAGE_ENDPOINT + "/" + channelId);
        WebsocketClient messageReceiver = new WebsocketClient(url, "/topic/" + channelId, null);
        WebsocketClient dummyClient = new WebsocketClient(url, "/topic/" + dummyChannelId, null);

        messageReceiver.subscribeForOneMessage(completableFuture, ChatMessage.class);
        dummyClient.subscribeForOneMessage(dummyCompletableFuture, ChatMessage.class);

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(SENDER)
                .content(CONTENT)
                .type(MESSAGE_TYPE)
                .build();

        //when
        messageProducer.sendMessage(chatMessage);

        //then
        ChatMessage response = completableFuture.get(10, TimeUnit.SECONDS);
        ChatMessage dummyResponse = dummyCompletableFuture.getNow(null);

        assertNotNull(response);
        assertNull(dummyResponse);
    }
}