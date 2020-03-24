package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long aLong);

    Optional<User> findByName(String name);
}
