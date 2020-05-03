package org.duder.event.service;


import org.duder.dto.event.EventPreview;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Long create(EventPreview eventDto, String sessionToken);

    Optional<EventPreview> findEvent(Long id);

    List<EventPreview> findAllUnFinished(int page, int size);

    List<EventPreview> findAllUnfinishedPrivate(int page, int size, String sessionToken);
}
