package org.duder.events.service;

import org.duder.events.dao.Event;
import org.duder.events.dto.EventDto;
import org.duder.events.repository.EventRepository;
import org.duder.user.dao.Hobby;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
class DefaultEventService implements EventService {

    private final EventRepository eventRepository;

    DefaultEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventDto> findAll(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return eventRepository
                .findAll(pageRequest)
                .getContent()
                .stream()
                .map(e -> EventDto.builder()
                        .name(e.getName())
                        .hobbies(e.getHobbies().stream().map(Hobby::getName).collect(Collectors.toSet()))
                        .numberOfParticipants(e.getEventUsers().size() - 1)
                        .timestamp(e.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
