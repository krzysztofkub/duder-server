package org.duder.utils;

import org.duder.chat.dto.ChatMessageDto;
import org.duder.chat.dto.MessageTypeDto;
import org.duder.user.dao.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Web socket client for exchanging ChatMessageDto-es
 */
// TODO In future this might need to be generified to handle other types, not only ChatMessageDto
public class MyWebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(MyWebSocketClient.class);

    private final static ChatMessageDto DEFAULT_CHAT_MESSAGE_DTO = ChatMessageDto.builder()
            .sender("dude")
            .content("what's up dog?")
            .type(MessageTypeDto.CHAT)
            .build();

    // If not provided this is the handler for any messages received by the client
    // Just logs into console
    private static final StompFrameHandler defaultFrameHandler = new StompFrameHandler() {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return ChatMessageDto.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            log.info("Frame received: {}", o);
        }
    };

    private final String defaultTopic;
    private final String defaultSendEndpoint;

    private StompSession session;

    public MyWebSocketClient(String url, String defaultTopic, User user) throws InterruptedException, ExecutionException, TimeoutException {
        this(url, defaultTopic, user, null);
    }

    public MyWebSocketClient(String url, User user, String defaultSendEndpoint) throws InterruptedException, ExecutionException, TimeoutException {
        this(url, null, user, defaultSendEndpoint);

    }

    public MyWebSocketClient(String url, String defaultTopic, User user, String defaultSendEndpoint) throws InterruptedException, ExecutionException, TimeoutException {
        this.defaultTopic = defaultTopic;
        this.defaultSendEndpoint = defaultSendEndpoint;
        initializeSession(url, user);

    }

    private void initializeSession(String url, User user) throws InterruptedException, ExecutionException, TimeoutException {
        // Stock client
        final StandardWebSocketClient standardClient = new StandardWebSocketClient();

        // Wrapping in SockJS
        final Transport transport = new WebSocketTransport(standardClient);
        final List<Transport> transports = Collections.singletonList(transport);
        final SockJsClient sockJsClient = new SockJsClient(transports);

        // [SockJS] JSON based communication require JSON de/en-coder
        final Jackson2SockJsMessageCodec codec = new Jackson2SockJsMessageCodec();
        sockJsClient.setMessageCodec(codec);

        // Wrapping in STOMP
        WebSocketStompClient client = new WebSocketStompClient(sockJsClient);

        // [STOMP] Add this to make ChatMessageDto class automatically parsable from/to JSON
        // Otherwise send with ChatMessageDto as parameter will throw UnsupportedCastException
        client.setMessageConverter(new MappingJackson2MessageConverter());

        // TODO I assume this is needed for handshake and upgrade to stomp protocol?
        final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        final StompHeaders stompHeaders = new StompHeaders();
        if (user != null) {
            stompHeaders.add("login", user.getLogin());
            stompHeaders.add("usercode", user.getPassword());
        }

        MyStompSessionHandler sessionHandler = new MyStompSessionHandler();
        final ListenableFuture<StompSession> futureSession = client.connect(url, headers, stompHeaders, sessionHandler);

        // Finally acquire stomp session
        session = futureSession.get(10, TimeUnit.SECONDS);
    }

    // subscribe(...) methods return Subscription instance to enable un-subscribing

    public StompSession.Subscription subscribe() {
        return subscribe(defaultTopic);
    }

    public StompSession.Subscription subscribe(String topic) {
        return subscribe(topic, defaultFrameHandler);
    }

    public StompSession.Subscription subscribe(String topic, StompFrameHandler messageHandler) {
        return session.subscribe(topic, messageHandler);
    }

    public CompletableFuture<ChatMessageDto> subscribeForOneMessage() {
        return subscribeForOneMessage(defaultTopic);
    }

    public CompletableFuture<ChatMessageDto> subscribeForOneMessage(String topic) {
        final CompletableFuture<ChatMessageDto> futureMessage = new CompletableFuture<>();
        final StompFrameHandler frameHandler = new StompFrameHandler() {
            @NotNull
            @Override
            public Type getPayloadType(@NotNull StompHeaders headers) {
                return ChatMessageDto.class;
            }

            @Override
            public void handleFrame(@NotNull StompHeaders headers, Object o) {
                // TODO I am not sure but in future if we send eg. ACK messages I think o here can be null, TBA
                log.info("Frame received: {}", o);
                if (o != null) {
                    final ChatMessageDto message = (ChatMessageDto) o;
                    futureMessage.complete(message);
                }
            }
        };
        final StompSession.Subscription subscription = session.subscribe(topic, frameHandler);
        futureMessage.thenRun(subscription::unsubscribe);
        return futureMessage;
    }

    public void sendMessage() {
        sendMessage(DEFAULT_CHAT_MESSAGE_DTO, defaultSendEndpoint);
    }

    public void sendMessage(ChatMessageDto message) {
        sendMessage(message, defaultSendEndpoint);
    }

    public void sendMessage(ChatMessageDto message, String endpoint) {
        session.send(endpoint, message);
    }
}


