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