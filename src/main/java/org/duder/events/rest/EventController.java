package org.duder.events.rest;

import org.duder.events.dto.EventDto;
import org.duder.events.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/event")
class EventController {

    private final EventService eventService;

    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping()
    public List<EventDto> findAll(@RequestParam int page, @RequestParam int size) {
        return eventService.findAll(page, size);
    }
}
