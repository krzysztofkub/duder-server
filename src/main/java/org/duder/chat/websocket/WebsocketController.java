package org.duder.chat.websocket;

import org.duder.dto.chat.ChatMessage;
import org.duder.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebsocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebsocketController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/message")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        messageService.sendMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/message/channel")
    public ChatMessage sendMessageToChannel(@Payload ChatMessage chatMessage) {
        messageService.sendChannelMessage(chatMessage, Integer.parseInt(chatMessage.getTo()));
        return chatMessage;
    }

    @MessageMapping("/message/user")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal user, @Header("simpSessionId") String sessionId) {
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(), "/queue/reply", chatMessage);
    }
}

