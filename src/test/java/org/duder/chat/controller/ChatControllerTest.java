package org.duder.chat.controller;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations="classpath:application-test.properties")
public class ChatControllerTest {

    @Autowired
    private MessageRepository messageRepository;

    @ClassRule
    public static GenericContainer mysql = new GenericContainer(new ImageFromDockerfile("mysql-test")
            .withDockerfileFromBuilder(dockerfileBuilder -> {
                dockerfileBuilder.from("mysql:5.7.22")
                        .env("MYSQL_ROOT_PASSWORD", "test")
                        .env("MYSQL_DATABASE", "test")
                        .env("MYSQL_USER", "test")
                        .env("MYSQL_PASSWORD", "test")
                        .add("a_schema.sql", "/docker-entrypoint-initdb.d")
                        .add("b_data.sql", "/docker-entrypoint-initdb.d");
            })
            .withFileFromClasspath("a_schema.sql", "db/mysql/schema.sql")
            .withFileFromClasspath("b_data.sql", "db/mysql/data.sql"))
            .withExposedPorts(3306)
            .withCreateContainerCmdModifier(
                    new Consumer<CreateContainerCmd>() {
                        @Override
                        public void accept(CreateContainerCmd createContainerCmd) {
                            createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306)));
                        }
                    }
            )
            .waitingFor(Wait.forListeningPort());

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
        messageRepository.save(messageEntity);

        List<MessageEntity> all = messageRepository.findAll();
        assertEquals(1, all.size());
    }
}