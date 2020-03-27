package org.duder.chat.websocket;

import org.duder.chat.dto.ChatMessageDto;
import org.duder.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebsocketController {
    private final MessageService messageService;


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WebsocketController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/sendMessage")
    public ChatMessageDto sendMessage(@Payload ChatMessageDto chatMessageDto) {
        messageService.sendMessage(chatMessageDto);
        return chatMessageDto;
    }

    @MessageMapping("/sendMessage/{channelId}")
    public ChatMessageDto sendMessage(@DestinationVariable int channelId, @Payload ChatMessageDto chatMessageDto) {
        messageService.sendChannelMessage(chatMessageDto, channelId);
        return chatMessageDto;
    }

    @MessageMapping("/sendMessageToUser")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto, Principal user, @Header("simpSessionId") String sessionId) {
        simpMessagingTemplate.convertAndSendToUser(chatMessageDto.getTo(), "/queue/reply", chatMessageDto);
    }
}

