package org.duder.event.service;


import org.duder.dto.event.CreateEvent;
import org.duder.dto.event.EventLoadingMode;
import org.duder.dto.event.EventPreview;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Long create(CreateEvent event, String sessionToken);

    Optional<EventPreview> findEvent(Long id);

    List<EventPreview> findAllUnfinished(int page, int size, EventLoadingMode eventLoadingMode, String sessionToken);
}
