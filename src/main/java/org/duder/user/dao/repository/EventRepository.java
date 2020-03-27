package org.duder.user.dao.repository;

import org.duder.user.dao.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
