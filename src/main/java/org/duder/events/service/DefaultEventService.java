package org.duder.events.service;

import com.google.common.collect.Lists;
import org.duder.events.dao.Event;
import org.duder.events.dto.EventDto;
import org.duder.events.repository.EventRepository;
import org.duder.events.dao.Hobby;
import org.duder.events.repository.HobbyRepository;
import org.duder.user.dao.User;
import org.duder.user.dao.UserEvent;
import org.duder.user.dao.id.UserEventId;
import org.duder.user.dto.UserDto;
import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.sql.Timestamp;
import java.util.List;
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
    public List<EventDto> findAllUnFinished(int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp"));
        return eventRepository
                .findAllByTimestampAfterOrderByTimestamp(new Timestamp(System.currentTimeMillis()), pageRequest)
                .getContent()
                .stream()
                .map(e -> EventDto.builder()
                        .name(e.getName())
                        .hobbies(e.getHobbies().stream().map(Hobby::getName).collect(Collectors.toSet()))
                        .numberOfParticipants(e.getEventUsers().size() - 1)
                        .timestamp(e.getTimestamp().getTime())
                        .host(findHost(e.getEventUsers()))
                        .build())
                .collect(Collectors.toList());
    }

    private UserDto findHost(List<UserEvent> eventUsers) {
        return eventUsers.stream()
                .filter(UserEvent::isUserHost)
                .map(UserEvent::getUser)
                .map(user -> UserDto.builder()
                        .nickname(user.getNickname())
                        .build())
                .findAny().orElse(null);
    }

    @Override
    @Transactional
    public void create(EventDto eventDto, String sessionToken) {
        User user = userService.getUserByToken(sessionToken).orElseThrow(InvalidSessionTokenException::new);

        Event event = Event.builder()
                .name(eventDto.getName())
                .hobbies(hobbyRepository.findAllByNameIn(eventDto.getHobbies()))
                .timestamp(new Timestamp(eventDto.getTimestamp()))
                .build();

        UserEventId userEventId = new UserEventId();
        userEventId.setUser(user);
        userEventId.setEvent(event);

        UserEvent userEvent = new UserEvent();
        userEvent.setPrimaryKey(userEventId);
        userEvent.setUserHost(true);

        event.setEventUsers(Lists.newArrayList(userEvent));
        eventRepository.save(event);
    }
}
