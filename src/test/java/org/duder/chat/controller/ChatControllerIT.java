package org.duder.chat.controller;

import org.duder.chat.utils.MySQLContainerProvider;
import org.duder.chat.model.MessageType;
import org.duder.chat.scheduler.MessageEntity;
import org.duder.chat.scheduler.MessageRepository;
import org.junit.ClassRule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChatControllerIT {

    @Autowired
    private MessageRepository messageRepository;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Test
    @Transactional
    public void getChatState() {
        MessageEntity messageEntity = MessageEntity
                .builder()
                .messageType(MessageType.CHAT)
                .author("COS")
                .content("ASASDASD")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
        messageRepository.saveAndFlush(messageEntity);

        List<MessageEntity> all = messageRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(messageEntity, all.get(0));
    }
}