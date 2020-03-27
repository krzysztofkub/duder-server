package org.duder.chat.service;

import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.dto.ChatMessageDto;
import org.duder.chat.service.scheduler.MessageCache;
import org.duder.user.dao.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public void sendMessage(ChatMessageDto chatMessageDto) {
        simpMessagingTemplate.convertAndSend(TOPIC+ PUBLIC_ENDPOINT, chatMessageDto);
        messageCache.add(chatMessageDto);
    }

    @Override
    public void sendChannelMessage(ChatMessageDto chatMessageDto, int channelId) {
        simpMessagingTemplate.convertAndSend(TOPIC + channelId, chatMessageDto);
        //TODO add message to cache and save to db
    }

    @Override
    public List<ChatMessageDto> getPublicChannelState() {
        return messageRepository
                .findAll()
                .stream()
                .map(m -> ChatMessageDto
                        .builder()
                        .content(m.getContent())
                        .sender(m.getAuthor().getNickname())
                        .type(m.getMessageTypeDto())
                        .build())
                .collect(Collectors.toList());
    }

    //TODO add getChannelState(int channelId)
}
