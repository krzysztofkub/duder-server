package org.duder.chat.service.scheduler;

import org.duder.chat.dto.ChatMessage;

public interface MessageCache {
    void add(ChatMessage chatMessage);

    ChatMessage take();

    int count();
}
