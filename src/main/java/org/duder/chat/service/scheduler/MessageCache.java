package org.duder.chat.service.scheduler;

import org.duder.dto.chat.ChatMessage;

public interface MessageCache {
    void add(ChatMessage chatMessageDto);

    ChatMessage take();

    int count();
}
