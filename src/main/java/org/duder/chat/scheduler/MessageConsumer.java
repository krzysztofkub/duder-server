package org.duder.chat.scheduler;

import org.duder.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
public class MessageConsumer {

    private final MessageRepository messageRepository;
    private final MessageCache messageCache;

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    public MessageConsumer(MessageRepository messageRepository, MessageCache messageCache) {
        this.messageRepository = messageRepository;
        this.messageCache = messageCache;

        ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        executorService.execute(this::persistingTask);
    }

    private void persistingTask() {
        ChatMessage message = messageCache.take();
        log.info("Received message " + message);
        log.info("There is" + messageCache.count() + "messages in queue");

        MessageEntity messageEntity = MessageEntity
                .builder()
                .messageType(message.getType())
                .content(message.getContent())
                .author(message.getSender())
                .build();
        messageRepository.save(messageEntity);
    }

}
