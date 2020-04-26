package org.duder.event.service;

import com.google.common.collect.Lists;
import ord.duder.dto.event.EventPreview;
import ord.duder.dto.user.Dude;
import org.duder.event.dao.Event;
import org.duder.event.repository.EventRepository;
import org.duder.event.dao.Hobby;
import org.duder.event.repository.HobbyRepository;
import org.duder.user.dao.User;
import org.duder.user.dao.UserEvent;
import org.duder.user.dao.id.UserEventId;
import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
class DefaultEventService implements EventService {

    private final EventRepository eventRepository;
    private final HobbyRepository hobbyRepository;
    private final UserService userService;

    DefaultEventService(EventRepository eventRepository, HobbyRepository hobbyRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.hobbyRepository = hobbyRepository;
        this.userService = userService;
    }

    @Override
    public List<EventPreview> findAllUnFinished(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp"));
        return eventRepository
                .findAllByTimestampAfterOrderByTimestamp(new Timestamp(System.currentTimeMillis()), pageRequest)
                .getContent()
                .stream()
                .map(this::mapEventToPreview)
                .collect(Collectors.toList());
    }

    private EventPreview mapEventToPreview(Event e) {
        return EventPreview.builder()
                .name(e.getName())
                .description(e.getDescription())
                .hobbies(e.getHobbies().stream().map(Hobby::getName).collect(Collectors.toSet()))
                .numberOfParticipants(e.getEventUsers().size() - 1)
                .timestamp(e.getTimestamp().getTime())
                .host(findHost(e.getEventUsers()))
                .build();
    }

    private Dude findHost(List<UserEvent> eventUsers) {
        return eventUsers.stream()
                .filter(UserEvent::isUserHost)
                .map(UserEvent::getUser)
                .map(user -> Dude.builder()
                        .nickname(user.getNickname())
                        .build())
                .findAny().orElse(null);
    }

    @Override
    @Transactional
    public Long create(EventPreview eventPreview, String sessionToken) {
        User user = userService.getUserByToken(sessionToken).orElseThrow(InvalidSessionTokenException::new);

        Event event = Event.builder()
                .name(eventPreview.getName())
                .description(eventPreview.getDescription())
                .hobbies(hobbyRepository.findAllByNameIn(eventPreview.getHobbies()))
                .timestamp(new Timestamp(eventPreview.getTimestamp()))
                .build();

        UserEventId userEventId = new UserEventId();
        userEventId.setUser(user);
        userEventId.setEvent(event);

        UserEvent userEvent = new UserEvent();
        userEvent.setPrimaryKey(userEventId);
        userEvent.setUserHost(true);

        event.setEventUsers(Lists.newArrayList(userEvent));
        return eventRepository.save(event).getId();
    }

    @Override
    public Optional<EventPreview> findEvent(Long id) {
        return eventRepository.findById(id)
                .map(this::mapEventToPreview);
    }
}
