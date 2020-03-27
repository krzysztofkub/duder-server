package org.duder.chat.service.scheduler;

import org.duder.chat.dto.ChatMessage;
import org.duder.chat.service.MessageService;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
class MessageCacheImpl implements MessageCache {
    private BlockingQueue<ChatMessage> messages = new LinkedBlockingQueue<>();

    public void add(ChatMessage chatMessage) {
        messages.add(chatMessage);
    }

    public ChatMessage take() {
        return messages.poll();
    }

    public int count() {
        return messages.size();
    }
}
