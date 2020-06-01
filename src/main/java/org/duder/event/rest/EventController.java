package org.duder.event.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.duder.chat.exception.DataNotFoundException;
import org.duder.chat.websocket.WebSocketEventListener;
import org.duder.common.DuderBean;
import org.duder.dto.event.CreateEvent;
import org.duder.dto.event.EventLoadingMode;
import org.duder.dto.event.EventPreview;
import org.duder.dto.user.Dude;
import org.duder.event.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/event")
class EventController extends DuderBean {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final EventService eventService;

    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping()
    public List<EventPreview> findAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) EventLoadingMode mode) {
        if (mode == null) {
            mode = EventLoadingMode.PUBLIC;
        }
        return eventService.findAllUnfinished(page, size, mode);
    }

    @PostMapping()
    public ResponseEntity<Void> create(
            @RequestPart CreateEvent createEvent,
            @RequestPart(value = "image", required = false) final MultipartFile image,
            @RequestHeader("Authorization") String sessionToken) {
        info("Received create event request " + createEvent + " with sessionToken = " + sessionToken);
        if (image != null) {
            info("Received image with event request");
        }

        Long eventId = eventService.create(createEvent, image);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(eventId).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public EventPreview getEvent(@PathVariable Long id) {
        return eventService.findEvent(id)
                .orElseThrow(() -> new DataNotFoundException("No event found with id " + id));
    }
}
