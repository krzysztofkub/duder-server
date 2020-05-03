package org.duder.user.repository;

import org.duder.user.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginIgnoreCase(String login);

    Optional<User> findByLoginIgnoreCaseAndPasswordIgnoreCase(String login, String password);
}
