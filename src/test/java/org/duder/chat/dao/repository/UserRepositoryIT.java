package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.User;
import org.duder.chat.utils.MySQLContainerProvider;
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

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static GenericContainer mysqlContainer = MySQLContainerProvider.getInstance();

    @Test
    public void findByLogin() {
        Optional<User> login = userRepository.findByLogin("login");
        assertTrue(login.isPresent());
    }

    @Test
    @Rollback(false)
    public void saveUserWithMandatoryFields_selectItByLogin() {
        //given
        User user = User.builder()
                .login("login2")
                .nickname("nickname2")
                .password("password2")
                .build();

        //when
        User save = userRepository.save(user);
        Optional<User> login2 = userRepository.findByLogin("login2");

        //then
        assertNotNull(save.getId());
        assertTrue(login2.isPresent());
        userRepository.delete(user);
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

    @Test(expected = DataIntegrityViolationException.class)
    @Rollback(false)
    public void save_throwsException_whenSavingUserWithoutMandatoryFields() {
        User user = User.builder()
                .login("login2")
                .nickname("nickname2")
                .build();

        User save = userRepository.save(user);

        assertNull(save.getId());
    }
}