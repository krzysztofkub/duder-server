package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.User;
import org.duder.chat.utils.MySQLContainerProvider;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Before
    public void beforeEach() {

    }

    @Test
    public void whenDifferentLogin_thenUsersNotTheSame() {
        // given
        final String login = "dude";

        // when
        final User u1 = User.builder().login(login).build();
        final User u2 = User.builder().login(login).build();
        final User u3 = User.builder().login(login + "asdasd").build();

        // then
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
    }

    @Test
    public void givenLogin_whenSaved_thenFindByLoginOrId() {
        // given
        final String login = "dude";
        final User toSave = User.builder()
                .login(login)
                .nickname("The Dude")
                .password("asd")
                .build();

        // when
        final User saved = userRepository.save(toSave);
        final Long id = saved.getId();

        // then
        final Optional<User> foundByLogin = userRepository.findByLogin(login);
        final User byLogin = foundByLogin.get();

        final Optional<User> foundById = userRepository.findById(id);
        final User byId = foundById.get();

        assertNotNull(toSave);
        assertNotNull(saved);
        assertNotNull(byLogin);
        assertNotNull(byId);

        assertEquals(toSave.getLogin(), saved.getLogin());
        assertEquals(toSave.getLogin(), byLogin.getLogin());

        assertEquals(toSave.getId(), saved.getId());
        assertEquals(toSave.getId(), byLogin.getId());

        assertEquals(byLogin, byId);
    }


}