package org.duder.chat.service;

import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.dto.ChatMessage;
import org.duder.chat.dto.MessageType;
import org.duder.chat.service.scheduler.MessageCache;
import org.duder.user.dao.model.User;
import org.duder.user.dao.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
class MessageServiceImpl implements MessageService{

    private final MessageCache messageCache;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageServiceImpl(MessageCache messageCache, SimpMessagingTemplate simpMessagingTemplate, MessageRepository messageRepository, UserRepository userRepository) {
        this.messageCache = messageCache;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendMessage(ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSend(TOPIC+ PUBLIC_ENDPOINT, chatMessage);
        messageCache.add(chatMessage);
    }

    @Override
    public void sendChannelMessage(ChatMessage chatMessage, int channelId) {
        simpMessagingTemplate.convertAndSend(TOPIC + channelId, chatMessage);
        //TODO add message to cache and save to db
    }

    @Override
    public List<ChatMessage> getPublicChannelState() {
        return messageRepository
                .findAll()
                .stream()
                .map(m -> ChatMessage
                        .builder()
                        .content(m.getContent())
                        .sender(m.getAuthor().getNickname())
                        .type(m.getMessageType())
                        .build())
                .collect(Collectors.toList());
    }

    //TODO add getChannelState(int channelId)
}
