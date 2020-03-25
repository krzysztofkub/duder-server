package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
