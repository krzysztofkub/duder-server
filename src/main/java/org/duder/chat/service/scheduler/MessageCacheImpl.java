package org.duder.chat.service.scheduler;

import org.duder.chat.dto.ChatMessageDto;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
class MessageCacheImpl implements MessageCache {
    private BlockingQueue<ChatMessageDto> messages = new LinkedBlockingQueue<>();

    public void add(ChatMessageDto chatMessageDto) {
        messages.add(chatMessageDto);
    }

    public ChatMessageDto take() {
        return messages.poll();
    }

    public int count() {
        return messages.size();
    }
}
