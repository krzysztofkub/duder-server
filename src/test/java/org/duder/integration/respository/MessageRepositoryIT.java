package org.duder.integration.respository;

import org.duder.chat.model.Message;
import org.duder.chat.repository.MessageRepository;
import org.duder.common.MySQLContainerProvider;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MessageRepositoryIT {

    @Autowired
    private MessageRepository messageRepository;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Test(expected = DataIntegrityViolationException.class)
    @Rollback(false)
    public void save_throwsException_whenAuthorNotProvided() {
        Message message = Message.builder().build();
        messageRepository.save(message);
    }
}
