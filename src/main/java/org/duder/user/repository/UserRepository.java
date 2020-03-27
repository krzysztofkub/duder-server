package org.duder.user.repository;

import org.duder.user.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    Optional<User> findByLoginIgnoreCaseAndPasswordIgnoreCase(String login, String password);
}
