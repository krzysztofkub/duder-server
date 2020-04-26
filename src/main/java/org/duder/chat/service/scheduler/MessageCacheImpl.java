package org.duder.chat.service.scheduler;

import ord.duder.dto.chat.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
class MessageCacheImpl implements MessageCache {
    private BlockingQueue<ChatMessage> messages = new LinkedBlockingQueue<>();

    public void add(ChatMessage chatMessageDto) {
        messages.add(chatMessageDto);
    }

    public ChatMessage take() {
        return messages.poll();
    }

    public int count() {
        return messages.size();
    }
}
