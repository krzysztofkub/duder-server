package org.duder.chat.controller;

import org.duder.chat.model.ChatMessage;
import org.duder.chat.scheduler.MessageCache;
import org.duder.chat.scheduler.MessageRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebsocketController {
    private final MessageCache messageCache;
    private final MessageRepository messageRepository;

    public WebsocketController(MessageCache messageCache, MessageRepository messageRepository) {
        this.messageCache = messageCache;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        messageCache.add(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/sendMessage/{channelId}")
    @SendTo("/topic/{channelId")
    public ChatMessage sendMessage(@DestinationVariable int channelId, @Payload ChatMessage chatMessage) {
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
                        .type(m.getMessageType())
                        .sender(m.getAuthor())
                        .build())
                .collect(Collectors.toList());
    }
}

