package org.duder.chat.scheduler;

import org.duder.chat.dao.entity.Message;
import org.duder.chat.dao.entity.User;
import org.duder.chat.dao.repository.MessageRepository;
import org.duder.chat.dao.repository.UserRepository;
import org.duder.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageCache messageCache;

    public MessageConsumer(MessageRepository messageRepository, UserRepository userRepository, MessageCache messageCache) {
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

            final Optional<User> sender = userRepository.findByNickname(message.getSender());
            Message messageEntity = Message
                    .builder()
                    .messageType(message.getType())
                    .content(message.getContent())
                    .author(sender.get())
                    .build();
            messageRepository.save(messageEntity);
        }
    }

}
