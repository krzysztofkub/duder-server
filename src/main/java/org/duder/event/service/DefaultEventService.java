package org.duder.event.service;

import com.google.common.collect.Lists;
import org.duder.dto.event.CreateEvent;
import org.duder.dto.event.EventPreview;
import org.duder.dto.user.Dude;
import org.duder.event.dao.Event;
import org.duder.event.repository.EventRepository;
import org.duder.event.dao.Hobby;
import org.duder.event.repository.HobbyRepository;
import org.duder.user.dao.ParticipantType;
import org.duder.user.dao.User;
import org.duder.user.dao.UserEvent;
import org.duder.user.dao.id.UserEventId;
import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    @Transactional
    public Long create(CreateEvent createEvent, String sessionToken) {
        User user = userService.getUserByToken(sessionToken).orElseThrow(InvalidSessionTokenException::new);

        Event event = Event.builder()
                .name(createEvent.getName())
                .description(createEvent.getDescription())
                .hobbies(hobbyRepository.findAllByNameIn(createEvent.getHobbies()))
                .timestamp(new Timestamp(createEvent.getTimestamp()))
                .isPrivate(createEvent.isPrivate())
                .build();

        UserEvent userEvent = new UserEvent();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEvent.setParticipantType(ParticipantType.HOST);

        event.setEventUsers(Lists.newArrayList(userEvent));
        return eventRepository.save(event).getId();
    }

    @Override
    public Optional<EventPreview> findEvent(Long id) {
        return eventRepository.findById(id)
                .map(this::mapEventToPreview);
    }
    @Override
    public List<EventPreview> findAllUnfinished(int page, int size, boolean isPrivate, String sessionToken) {

        List<EventPreview> events;
        if (isPrivate) {
            Pageable pageRequest = PageRequest.of(page, size);
            List<Long> friendIds = userService.getUserFriendsByToken(sessionToken).stream().map(User::getId).collect(Collectors.toList());
            events = eventRepository.findAllPrivateEventsForUser(friendIds, pageRequest)
                    .stream()
                    .map(this::mapEventToPreview)
                    .collect(Collectors.toList());
        } else {
            Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp"));
            events = eventRepository
                    .findAllByTimestampAfterOrderByTimestamp(new Timestamp(System.currentTimeMillis()), pageRequest)
                    .getContent()
                    .stream()
                    .map(this::mapEventToPreview)
                    .collect(Collectors.toList());
        }

        return events;
    }

    private EventPreview mapEventToPreview(Event e) {
        return EventPreview.builder()
                .name(e.getName())
                .description(e.getDescription())
                .hobbies(e.getHobbies().stream().map(Hobby::getName).collect(Collectors.toSet()))
                .numberOfParticipants(e.getEventUsers().size() - 1)
                .timestamp(e.getTimestamp().getTime())
                .host(findHost(e.getEventUsers()))
                .isPrivate(e.isPrivate())
                .build();
    }

    private Dude findHost(List<UserEvent> eventUsers) {
        return eventUsers.stream()
                .filter(this::isUserHost)
                .map(UserEvent::getUser)
                .map(user -> Dude.builder()
                        .nickname(user.getNickname())
                        .build())
                .findAny().orElse(null);
    }

    private boolean isUserHost(UserEvent userEvent) {
        return userEvent.getParticipantType() == ParticipantType.HOST;
    }
}
