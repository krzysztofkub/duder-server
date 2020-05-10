package org.duder.event.repository;

import org.duder.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Page<Event> findAllByIsPrivateAndTimestampAfter(Boolean isPrivate, Timestamp timestamp, Pageable pageable);

    @Query(
            value = "SELECT ue.event FROM UserEvent ue " +
                    "LEFT OUTER JOIN ue.event e " +
                    "LEFT OUTER JOIN ue.user u " +
                    "WHERE ue.participantType = 'HOST' " +
                    "AND u.id = :userId " +
                    "AND e.timestamp > :timestamp " +
                    "ORDER BY e.timestamp"
    )
    Page<Event> findAllByUserAndTimestampAfter(Long userId, Timestamp timestamp, Pageable pageable);

    @Query(
            value = "SELECT ue.event FROM UserEvent ue " +
                    "LEFT OUTER JOIN ue.event e " +
                    "LEFT OUTER JOIN ue.user u " +
                    "WHERE e.isPrivate = true " +
                    "AND ue.participantType = 'HOST' " +
                    "AND u.id IN :users " +
                    "AND e.timestamp > :timestamp " +
                    "ORDER BY e.timestamp"
    )
    Page<Event> findAllUnfinishedPrivateEventsForUsers(@Param("users") List<Long> users, @Param("timestamp") Timestamp timestamp, Pageable pageable);
}
