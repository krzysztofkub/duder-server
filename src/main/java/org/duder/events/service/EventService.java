package org.duder.events.service;

import org.duder.events.dto.EventDto;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventDto> findAllUnFinished(int page, int size);

    Long create(EventDto eventDto, String sessionToken);

    Optional<EventDto> findEvent(Long id);
}
