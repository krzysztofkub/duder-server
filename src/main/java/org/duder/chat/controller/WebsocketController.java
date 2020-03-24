package org.duder.chat.controller;

import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.model.ChatMessage;
import org.duder.chat.scheduler.MessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebsocketController {
    private final MessageCache messageCache;
    private final MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WebsocketController(MessageCache messageCache, MessageRepository messageRepository) {
        this.messageCache = messageCache;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/sendMessage")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSend("/topic/public", chatMessage);
        messageCache.add(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/sendMessage/{channelId}")
    public ChatMessage sendMessage(@DestinationVariable int channelId, @Payload ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSend("/topic/" + channelId, chatMessage);
        return chatMessage;
    }

    @GetMapping("/getChatState")
    @ResponseBody
    public List<ChatMessage> getChatState() {
        return messageRepository
                .findAll()
                .stream()
                .map(m -> ChatMessage
                        .builder()
                        .content(m.getContent())
                        .sender(m.getAuthor().getName())
                        .type(m.getMessageType())
                        .build())
                .collect(Collectors.toList());
    }
}

