package org.duder.chat.controller;

import org.duder.chat.model.ChatMessage;
import org.duder.chat.model.MessageType;
import org.duder.chat.scheduler.MessageCache;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
    private final MessageCache messageCache;

    public WebsocketController(MessageCache messageCache) {
        this.messageCache = messageCache;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
}
