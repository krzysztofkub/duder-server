package org.duder.chat.service;

import org.duder.dto.chat.ChatMessage;

import java.util.List;

public interface MessageService {

    String TOPIC = "/topic/";
    String PUBLIC_ENDPOINT = "public";

    void sendMessage(ChatMessage chatMessageDto);

    void sendChannelMessage(ChatMessage chatMessageDto, int channelId);

    List<ChatMessage> getPublicChannelState();
}
