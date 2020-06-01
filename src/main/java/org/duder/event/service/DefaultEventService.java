package org.duder.event.service;

import com.google.common.collect.Lists;
import org.duder.common.DuderBean;
import org.duder.common.ImageService;
import org.duder.dto.event.CreateEvent;
import org.duder.dto.event.EventLoadingMode;
import org.duder.dto.event.EventPreview;
import org.duder.dto.user.Dude;
import org.duder.event.model.Event;
import org.duder.event.model.Hobby;
import org.duder.event.repository.EventRepository;
import org.duder.event.repository.HobbyRepository;
import org.duder.user.exception.InvalidSessionTokenException;
import org.duder.user.model.ParticipantType;
import org.duder.user.model.User;
import org.duder.user.model.UserEvent;
import org.duder.user.service.LoggedDuderBean;
import org.duder.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
class DefaultEventService extends LoggedDuderBean implements EventService {

    private final EventRepository eventRepository;
    private final HobbyRepository hobbyRepository;
    private final UserService userService;
    private final ImageService imageService;

    DefaultEventService(EventRepository eventRepository,
                        HobbyRepository hobbyRepository,
                        UserService userService,
                        ImageService imageService) {
        this.eventRepository = eventRepository;
        this.hobbyRepository = hobbyRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public Long create(CreateEvent createEvent, MultipartFile image) {
        User user = userService.getUserByToken(getSessionToken()).orElseThrow(InvalidSessionTokenException::new);

        Event event = Event.builder()
                .name(createEvent.getName())
                .description(createEvent.getDescription())
                .hobbies(hobbyRepository.findAllByNameIn(createEvent.getHobbies()))
                .timestamp(new Timestamp(createEvent.getTimestamp()))
                .isPrivate(createEvent.getIsPrivate())
                .created(new Timestamp(System.currentTimeMillis()))
                .build();
        debug("Created event " + event);

        UserEvent userEvent = new UserEvent();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEvent.setParticipantType(ParticipantType.HOST);

        event.setEventUsers(Lists.newArrayList(userEvent));
        Long id = eventRepository.save(event).getId();

        Optional<String> imageUrl = imageService.saveImage(image, event, id);
        imageUrl.ifPresent(url -> {
            event.setImageUrl(url);
            debug("Saved image for event");
        });
        return id;
    }

    @Override
    public Optional<EventPreview> findEvent(Long id) {
        return eventRepository.findById(id)
                .map(this::mapEventToPreview);
    }

    @Override
    public List<EventPreview> findAllUnfinished(int page, int size, EventLoadingMode eventLoadingMode) {
        List<EventPreview> events;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        switch (eventLoadingMode) {
            case PRIVATE:
                events = loadPrivateEvents(page, size, timestamp, getSessionToken());
                break;
            case OWN:
                events = loadOwnEvents(page, size, timestamp, getSessionToken());
                break;
            case PUBLIC:
            default:
                events = loadPublicEvents(page, size, timestamp);
                break;
        }

        return events;
    }

    private List<EventPreview> loadPrivateEvents(int page, int size, Timestamp timestamp, String sessionToken) {
        Pageable pageRequest = PageRequest.of(page, size);
        List<Long> friendIds = userService.getUserFriendsByToken(sessionToken).stream().map(User::getId).collect(Collectors.toList());
        userService.getUserByToken(sessionToken).ifPresent(u -> friendIds.add(u.getId()));
        if (friendIds.size() == 0) {
            return new ArrayList<>();
        } else {
            return eventRepository.findAllUnfinishedPrivateEventsForUsers(friendIds, timestamp, pageRequest)
                    .getContent()
                    .stream()
                    .map(this::mapEventToPreview)
                    .collect(Collectors.toList());
        }
    }

    private List<EventPreview> loadOwnEvents(int page, int size, Timestamp timestamp, String sessionToken) {
        Long userId = userService.getUserByToken(sessionToken).orElseThrow(InvalidSessionTokenException::new).getId();
        Pageable pageRequest = PageRequest.of(page, size);
        return eventRepository.findAllByUserAndTimestampAfter(userId, timestamp, pageRequest)
                .getContent()
                .stream()
                .map(this::mapEventToPreview)
                .collect(Collectors.toList());
    }

    private List<EventPreview> loadPublicEvents(int page, int size, Timestamp timestamp) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("timestamp"));
        return eventRepository
                .findAllByIsPrivateAndTimestampAfter(false, timestamp, pageRequest)
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
                .isPrivate(e.isPrivate())
                .imageUrl(e.getImageUrl())
                .build();
    }

    private Dude findHost(List<UserEvent> eventUsers) {
        return eventUsers.stream()
                .filter(this::isUserHost)
                .map(UserEvent::getUser)
                .map(user -> Dude.builder()
                        .nickname(user.getNickname())
                        .imageUrl(user.getImageUrl())
                        .build())
                .findAny().orElse(null);
    }

    private boolean isUserHost(UserEvent userEvent) {
        return userEvent.getParticipantType() == ParticipantType.HOST;
    }
}
