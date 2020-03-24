package org.duder.chat.utils;


import org.duder.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

/*
Provides handling and logging for STOMP session where messages are ChatMessage instances
 */
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(MyStompSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders headers) {
        log.info("New session established #" + session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] bytes, Throwable throwable) {
        log.error("Exception in session #{}, on command: {}, headers: {}, details: {}", session.getSessionId(), command, headers, throwable);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable throwable) {
        log.error("Transport error in session #{}, details: {}", session.getSessionId(), throwable);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        // TODO In future I think this might be generified somehow, or maybe class could/should be deduced from the header, so far - not neede
        return ChatMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object o) {
        try {
            final ChatMessage message = (ChatMessage) o;
            log.info("Received message: {}", message.toString());
        } catch (ClassCastException e) {
            log.error("Received massage: {} cannot be cast to: {}", o, ChatMessage.class);
        }
    }
}
