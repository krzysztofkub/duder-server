package org.duder.chat.scheduler;

import com.google.common.collect.EvictingQueue;
import org.duder.chat.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MessageCache {
    private BlockingQueue<ChatMessage> messages = new LinkedBlockingQueue<>();

    public void add(ChatMessage chatMessage) {
        messages.add(chatMessage);
    }

    public ChatMessage take() {
        return messages.poll();
    }

    public int count() {
        return messages.size();
    }
}
