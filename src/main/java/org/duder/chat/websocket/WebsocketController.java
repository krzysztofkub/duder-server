package org.duder.chat.websocket;

import org.duder.chat.dto.ChatMessage;
import org.duder.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
    private final MessageService messageService;

    public WebsocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        messageService.sendMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/sendMessage/{channelId}")
    public ChatMessage sendMessage(@DestinationVariable int channelId, @Payload ChatMessage chatMessage) {
        messageService.sendChannelMessage(chatMessage, channelId);
        return chatMessage;
    }
}

