package org.duder.chat.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WebsocketClient {
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private final ObjectMapper mapper = new ObjectMapper();
    private StompSession stompSession;

    private final String url;
    private final String topic;
    private final String sendEndpoint;

    public WebsocketClient(String url, String topic, String sendEndpoint) {
        this.url = url;
        this.topic = topic;
        this.sendEndpoint = sendEndpoint;
        connect();
    }

    private void connect() {
        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        try {
            stompSession = stompClient.connect(url, headers, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public <T> void subscribeForOneMessage(CompletableFuture completableFuture, Class<T> tClass) {
        stompSession.subscribe(topic, new StompFrameHandler() {
            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                try {
                    completableFuture.complete(mapper.readValue((byte[]) o, tClass));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(Object message) throws JsonProcessingException {
        stompSession.send(sendEndpoint, mapper.writeValueAsBytes(message));
    }
}
