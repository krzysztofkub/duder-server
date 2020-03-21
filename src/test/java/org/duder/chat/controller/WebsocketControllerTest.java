package org.duder.chat.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.duder.chat.utils.MySQLContainerProvider;
import org.duder.chat.model.ChatMessage;
import org.duder.chat.utils.WebsocketClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WebsocketControllerTest {
    @Value("${local.server.port}")
    private int port;
    private String URL;
    private static final String SEND_MESSAGE_ENDPOINT = "/app/sendMessage";
    private static final String SUBSCRIBE_CHAT_ENDPOINT = "/topic/public";
    private CompletableFuture<ChatMessage> completableFuture;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/ws";
    }



    @Test
    public void testCreateGameEndpoint() throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        WebsocketClient websocketClient = new WebsocketClient(URL, SUBSCRIBE_CHAT_ENDPOINT, SEND_MESSAGE_ENDPOINT);
        websocketClient.subscribeForOneMessage(completableFuture, ChatMessage.class);
        ChatMessage chatMessage = ChatMessage.builder()
                .sender("ASD")
                .build();
        websocketClient.sendMessage(chatMessage);

        ChatMessage websocketMessage = completableFuture.get(10, TimeUnit.SECONDS);
        assertNotNull(websocketMessage);
        //Wait till scheduler persists new message to db
        Thread.sleep(2000);
    }
}