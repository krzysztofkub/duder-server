package org.duder.chat.controller;

import org.duder.chat.model.ChatMessage;
import org.duder.chat.model.MessageType;
import org.duder.chat.scheduler.MessageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;
    private final MessageCache messageCache;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, MessageCache messageCache) {
        this.messagingTemplate = messagingTemplate;
        this.messageCache = messageCache;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = getUsername(event);
        if(username != null) {
            logger.info("User Disconnected : " + username);

            ChatMessage chatMessage = ChatMessage
                    .builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

    private String getUsername(AbstractSubProtocolEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        return (String) headerAccessor.getSessionAttributes().get("username");
    }
}
