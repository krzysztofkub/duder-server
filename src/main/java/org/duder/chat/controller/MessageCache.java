package org.duder.chat.controller;

import com.google.common.collect.EvictingQueue;
import org.duder.chat.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Component
public class MessageCache {
    private Queue<ChatMessage> messages = EvictingQueue.create(30);

    public void add(ChatMessage chatMessage) {
        messages.add(chatMessage);
    }

    public Queue<ChatMessage> getMessages() {
        return messages;
    }
}
