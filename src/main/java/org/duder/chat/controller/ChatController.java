package org.duder.chat.controller;

import org.duder.chat.model.ChatMessage;
import org.duder.chat.scheduler.MessageCache;
import org.duder.chat.scheduler.MessageConsumer;
import org.duder.chat.scheduler.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final MessageCache messageCache;

    private final MessageRepository messageRepository;

    public ChatController(MessageCache messageCache, MessageRepository messageRepository, MessageConsumer messageConsumer) {
        this.messageCache = messageCache;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        messageCache.add(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
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
