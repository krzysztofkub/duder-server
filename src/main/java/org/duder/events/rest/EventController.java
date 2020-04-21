package org.duder.events.rest;

import org.duder.chat.exception.DataNotFoundException;
import org.duder.events.dto.EventDto;
import org.duder.events.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Void> create(@RequestBody EventDto eventDto, @RequestHeader("Authorization") String sessionToken) {
        Long eventId = eventService.create(eventDto, sessionToken);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(eventId).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.findEvent(id)
                .orElseThrow(() -> new DataNotFoundException("No event found with id " + id));
    }
}
