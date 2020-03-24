package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
