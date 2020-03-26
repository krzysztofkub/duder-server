package org.duder.integration;


import org.duder.chat.dao.model.Message;
import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.dto.ChatMessage;
import org.duder.chat.dto.MessageType;
import org.duder.utils.DataSQLValues;
import org.duder.utils.MySQLContainerProvider;
import org.duder.utils.MyWebSocketClient;
import org.duder.user.dao.repository.UserRepository;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
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
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;
    private String url;

    private static final String SEND_MESSAGE_ENDPOINT = "/app/sendMessage";
    private static final String SUBSCRIBE_CHAT_ENDPOINT = "/topic/public";

    private final String CONTENT = "Content";
    private final MessageType MESSAGE_TYPE = MessageType.CHAT;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Before
    public void setup() {
        url = "ws://localhost:" + port + "/ws";
    }

    @Test
    @Rollback(false)
    @Transactional
    public void sendMessage_sendsMessagesToSubscribersAndPersistsMessageToDb_always() throws InterruptedException, ExecutionException, TimeoutException {
        //given
        final MyWebSocketClient client = new MyWebSocketClient(url, SUBSCRIBE_CHAT_ENDPOINT, SEND_MESSAGE_ENDPOINT);
        final CompletableFuture<ChatMessage> completableFuture = client.subscribeForOneMessage();
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(DataSQLValues.getUser().getLogin())
                .content(CONTENT)
                .type(MESSAGE_TYPE)
                .build();

        //when
        client.sendMessage(chatMessage);
        ChatMessage message = completableFuture.get(10, TimeUnit.SECONDS);
        //wait for scheduler (saving to db)
        Thread.sleep(2000);
        List<Message> messagesFromDb = messageRepository.findByAuthorIdOrderByTimestampDesc(DataSQLValues.getUser().getId());


        //then
        assertTrue(messagesFromDb.size() > 1);
        //first message is persisted by data.sql
        Message messageEntity = messagesFromDb.get(0);
        assertNotNull(message);
        assertNotNull(messageEntity);
        assertEquals(DataSQLValues.getUser().getLogin(), messageEntity.getAuthor().getLogin());
        assertEquals(CONTENT, messageEntity.getContent());
        assertEquals(MESSAGE_TYPE, messageEntity.getMessageType());
    }

    @Test
    public void sendMessage_channel() throws InterruptedException, ExecutionException, TimeoutException {
        //given
        int channelId = 1;
        int dummyChannelId = 2;

        MyWebSocketClient messageProducer = new MyWebSocketClient(url, "/topic/" + channelId, SEND_MESSAGE_ENDPOINT + "/" + channelId);
        MyWebSocketClient messageReceiver = new MyWebSocketClient(url, "/topic/" + channelId, null);
        MyWebSocketClient dummyClient = new MyWebSocketClient(url, "/topic/" + dummyChannelId, null);

        final CompletableFuture<ChatMessage> completableFuture = messageReceiver.subscribeForOneMessage();
        final CompletableFuture<ChatMessage> dummyCompletableFuture = dummyClient.subscribeForOneMessage();

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(DataSQLValues.getUser().getLogin())
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