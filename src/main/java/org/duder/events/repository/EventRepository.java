package org.duder.events.repository;

import org.duder.events.dao.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Page<Event> findAllByTimestampAfterOrderByTimestamp(Timestamp timestamp, Pageable pageable);
}
