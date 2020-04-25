package org.duder.chat.websocket;

import org.duder.chat.dto.ChatMessageDto;
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
    public ChatMessageDto sendMessage(@Payload ChatMessageDto chatMessageDto) {
        messageService.sendMessage(chatMessageDto);
        return chatMessageDto;
    }

    @MessageMapping("/message/channel")
    public ChatMessageDto sendMessageToChannel(@Payload ChatMessageDto chatMessageDto) {
        messageService.sendChannelMessage(chatMessageDto, Integer.parseInt(chatMessageDto.getTo()));
        return chatMessageDto;
    }

    @MessageMapping("/message/user")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal user, @Header("simpSessionId") String sessionId) {
        simpMessagingTemplate.convertAndSendToUser(chatMessageDto.getTo(), "/queue/reply", chatMessageDto);
    }
}

