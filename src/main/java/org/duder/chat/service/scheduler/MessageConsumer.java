package org.duder.chat.service.scheduler;

import org.duder.chat.exception.DataNotFoundException;
import org.duder.chat.model.Message;
import org.duder.chat.repository.MessageRepository;
import org.duder.dto.chat.ChatMessage;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageCacheImpl messageCache;

    public MessageConsumer(MessageRepository messageRepository, UserRepository userRepository, MessageCacheImpl messageCache) {
        this.messageRepository = messageRepository;
        this.messageCache = messageCache;
        this.userRepository = userRepository;

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        executorService.scheduleAtFixedRate(this::persistingTask, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void persistingTask() {
        ChatMessage message;
        while ((message = messageCache.take()) != null) {
            log.info("Received message " + message);
            log.info("There are " + messageCache.count() + " messages in queue");

            String login = message.getSender();
            final User user = userRepository.findByLoginIgnoreCase(login).orElseThrow(() -> new DataNotFoundException("Can't find user " + login));
            Message messageEntity = Message
                    .builder()
                    .messageType(message.getType())
                    .content(message.getContent())
                    .author(user)
                    .build();
            messageRepository.save(messageEntity);
        }
    }

}
