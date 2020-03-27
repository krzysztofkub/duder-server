package org.duder.integration;


import org.duder.chat.dao.Message;
import org.duder.chat.repository.MessageRepository;
import org.duder.chat.dto.ChatMessageDto;
import org.duder.chat.dto.MessageTypeDto;
import org.duder.user.dao.User;
import org.duder.utils.DataSQLValues;
import org.duder.utils.MySQLContainerProvider;
import org.duder.utils.MyWebSocketClient;
import org.duder.user.repository.UserRepository;
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
import java.util.Optional;
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
    private static final String SEND_MESSAGE_TO_USER_ENDPOINT = "/app/sendMessageToUser";
    private static final String SUBSCRIBE_CHAT_ENDPOINT = "/topic/public";

    private final String CONTENT = "Content";
    private final MessageTypeDto MESSAGE_TYPE = MessageTypeDto.CHAT;

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
        Optional<User> user = userRepository.findById(DataSQLValues.getUser().getId());
        assertTrue(user.isPresent());

        final MyWebSocketClient client = new MyWebSocketClient(url, SUBSCRIBE_CHAT_ENDPOINT, SEND_MESSAGE_ENDPOINT, user.get());
        final CompletableFuture<ChatMessageDto> completableFuture = client.subscribeForOneMessage();
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .sender(DataSQLValues.getUser().getLogin())
                .content(CONTENT)
                .type(MESSAGE_TYPE)
                .build();

        //when
        client.sendMessage(chatMessageDto);
        ChatMessageDto message = completableFuture.get(10, TimeUnit.SECONDS);
        //wait for scheduler (saving to db)
        Thread.sleep(2000);
        List<Message> messagesFromDb = messageRepository.findByAuthorIdOrderByTimestampDesc(DataSQLValues.getUser().getId());


        //then
        assertNotNull(message);
        assertTrue(messagesFromDb.size() > 1);
        //first message is persisted by data.sql
        Message messageEntity = messagesFromDb.get(0);
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

        Optional<User> user = userRepository.findByLogin("login");
        assertTrue(user.isPresent());

        Optional<User> user2 = userRepository.findByLogin("login2");
        assertTrue(user2.isPresent());

        Optional<User> user3 = userRepository.findByLogin("login3");
        assertTrue(user3.isPresent());

        Optional<User> user4 = userRepository.findByLogin("login3");
        assertTrue(user4.isPresent());

        MyWebSocketClient messageProducer = new MyWebSocketClient(url, "/topic/" + channelId, SEND_MESSAGE_ENDPOINT + "/" + channelId, user.get());
        MyWebSocketClient messageReceiver = new MyWebSocketClient(url, "/topic/" + channelId, null, user2.get());
        MyWebSocketClient messageReceiver2 = new MyWebSocketClient(url, "/topic/" + channelId, null, user3.get());
        MyWebSocketClient dummyClient = new MyWebSocketClient(url, "/topic/" + dummyChannelId, null, user4.get());

        final CompletableFuture<ChatMessageDto> completableFuture = messageReceiver.subscribeForOneMessage();
        final CompletableFuture<ChatMessageDto> completableFuture2 = messageReceiver2.subscribeForOneMessage();
        final CompletableFuture<ChatMessageDto> dummyCompletableFuture = dummyClient.subscribeForOneMessage();

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .sender(DataSQLValues.getUser().getLogin())
                .content(CONTENT)
                .type(MESSAGE_TYPE)
                .build();

        //when
        messageProducer.sendMessage(chatMessageDto);

        //then
        ChatMessageDto response = completableFuture.get(10, TimeUnit.SECONDS);
        ChatMessageDto response2 = completableFuture2.get(10, TimeUnit.SECONDS);
        ChatMessageDto dummyResponse = dummyCompletableFuture.getNow(null);

        assertNotNull(response);
        assertNotNull(response2);
        assertNull(dummyResponse);
    }

    @Test
    @Rollback(false)
    public void sendMessage_toUser() throws InterruptedException, ExecutionException, TimeoutException {
        //given
        Optional<User> producerUser = userRepository.findByLogin("login2");
        assertTrue(producerUser.isPresent());
        Optional<User> receiverUser = userRepository.findByLogin("login");
        assertTrue(receiverUser.isPresent());

        MyWebSocketClient messageProducer = new MyWebSocketClient(url, "/user/queue/reply", SEND_MESSAGE_TO_USER_ENDPOINT, producerUser.get());
        MyWebSocketClient messageReceiver = new MyWebSocketClient(url, "/user/queue/reply", null, receiverUser.get());

        final CompletableFuture<ChatMessageDto> completableFuture = messageReceiver.subscribeForOneMessage();

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .sender(producerUser.get().getLogin())
                .content(CONTENT)
                .type(MESSAGE_TYPE)
                .to(receiverUser.get().getLogin())
                .build();

        //when
        messageProducer.sendMessage(chatMessageDto);

        //then
        ChatMessageDto response = completableFuture.get(10, TimeUnit.SECONDS);

        assertNotNull(response);
    }
}