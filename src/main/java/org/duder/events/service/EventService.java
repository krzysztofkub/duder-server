package org.duder.events.service;

import org.duder.events.dto.EventDto;

import java.util.List;

public interface EventService {
    List<EventDto> findAllUnFinished(int page, int size);

    void create(EventDto eventDto, String sessionToken);

}
