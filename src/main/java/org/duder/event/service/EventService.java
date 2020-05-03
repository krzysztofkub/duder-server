package org.duder.event.service;


import org.duder.dto.event.EventPreview;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Long create(EventPreview eventDto, String sessionToken);

    Optional<EventPreview> findEvent(Long id);

    List<EventPreview> findAllUnfinished(int page, int size, boolean isPrivate, String sessionToken);
}
