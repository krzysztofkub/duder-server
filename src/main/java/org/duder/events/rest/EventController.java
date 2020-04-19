package org.duder.events.rest;

import org.duder.events.dto.EventDto;
import org.duder.events.service.EventService;
import org.springframework.web.bind.annotation.*;

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
        return eventService.findAllUnFinished(page, size);
    }

    @PostMapping()
    public void create(@RequestBody EventDto eventDto, @RequestHeader("Authorization") String sessionToken) {
        eventService.create(eventDto, sessionToken);
    }
}
