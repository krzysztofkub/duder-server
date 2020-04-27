package org.duder.event.service;


import org.duder.dto.event.EventPreview;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<EventPreview> findAllUnFinished(int page, int size);

    Long create(EventPreview eventDto, String sessionToken);

    Optional<EventPreview> findEvent(Long id);
}
