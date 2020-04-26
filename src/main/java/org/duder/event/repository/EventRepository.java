package org.duder.event.repository;

import org.duder.event.dao.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Page<Event> findAllByTimestampAfterOrderByTimestamp(Timestamp timestamp, Pageable pageable);
}
