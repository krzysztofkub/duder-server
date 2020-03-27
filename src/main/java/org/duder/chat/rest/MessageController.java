package org.duder.chat.rest;

import org.duder.chat.dto.ChatMessage;
import org.duder.chat.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/getChatState")
    public List<ChatMessage> getChatState() {
        return messageService.getPublicChannelState();
    }
}
