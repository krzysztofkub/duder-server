package org.duder.chat.service;

import org.duder.chat.dto.ChatMessageDto;

import java.util.List;

public interface MessageService {

    String TOPIC = "/topic/";
    String PUBLIC_ENDPOINT = "public";

    void sendMessage(ChatMessageDto chatMessageDto);

    void sendChannelMessage(ChatMessageDto chatMessageDto, int channelId);

    List<ChatMessageDto> getPublicChannelState();
}
