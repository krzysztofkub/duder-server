package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
}
