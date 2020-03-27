package org.duder.chat.repository;

import org.duder.chat.dao.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByAuthorIdOrderByTimestampDesc(Long id);
}
