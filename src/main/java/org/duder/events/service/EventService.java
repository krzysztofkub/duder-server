package org.duder.events.service;

import org.duder.events.dto.EventDto;

import java.util.List;

public interface EventService {
    List<EventDto> findAll(int page, int size);
}
