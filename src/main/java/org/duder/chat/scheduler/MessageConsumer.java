package org.duder.chat.scheduler;

import org.duder.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public class MessageConsumer {

    private final MessageRepository messageRepository;
    private final MessageCache messageCache;

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    public MessageConsumer(MessageRepository messageRepository, MessageCache messageCache) {
        this.messageRepository = messageRepository;
        this.messageCache = messageCache;

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, r -> {
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

            MessageEntity messageEntity = MessageEntity
                    .builder()
                    .messageType(message.getType())
                    .content(message.getContent())
                    .author(message.getSender())
                    .build();
            messageRepository.save(messageEntity);
        }
    }

}
