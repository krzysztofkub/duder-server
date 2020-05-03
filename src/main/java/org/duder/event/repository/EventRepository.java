package org.duder.event.repository;

import org.duder.event.dao.Event;
import org.duder.user.dao.User;
import org.duder.user.dao.UserEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Page<Event> findAllByTimestampAfterOrderByTimestamp(Timestamp timestamp, Pageable pageable);

    @Query(
            value = "SELECT ue.event FROM UserEvent ue " +
                    "LEFT OUTER JOIN ue.event e " +
                    "LEFT OUTER JOIN ue.user u where e.isPrivate = true and ue.participantType = 'HOST' and u.id IN :users ORDER BY e.timestamp"
    )
    List<Event> findAllPrivateEventsForUser(@Param("users") List<Long> users, Pageable pageable);
}
