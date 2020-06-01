package org.duder.event.service;


import org.duder.dto.event.CreateEvent;
import org.duder.dto.event.EventLoadingMode;
import org.duder.dto.event.EventPreview;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Long create(CreateEvent event, MultipartFile image);

    Optional<EventPreview> findEvent(Long id);

    List<EventPreview> findAllUnfinished(int page, int size, EventLoadingMode eventLoadingMode);
}
