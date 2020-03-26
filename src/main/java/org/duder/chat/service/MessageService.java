package org.duder.chat.service;

import org.duder.chat.dto.ChatMessage;

import java.util.List;
import java.util.Map;

public interface MessageService {

    String TOPIC = "/topic/";
    String PUBLIC_ENDPOINT = "public";

    void sendMessage(ChatMessage chatMessage);

    void sendChannelMessage(ChatMessage chatMessage, int channelId);

    List<ChatMessage> getPublicChannelState();
}
