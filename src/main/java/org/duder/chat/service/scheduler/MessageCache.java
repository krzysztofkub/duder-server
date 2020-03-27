package org.duder.chat.service.scheduler;

import org.duder.chat.dto.ChatMessageDto;

public interface MessageCache {
    void add(ChatMessageDto chatMessageDto);

    ChatMessageDto take();

    int count();
}
