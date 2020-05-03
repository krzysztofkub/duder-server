package org.duder.user.dao;

import lombok.Getter;
import lombok.Setter;
import org.duder.event.dao.Event;
import org.duder.user.dao.id.UserEventId;

import javax.persistence.*;

@Entity
@Getter
@Setter
// TODO this is one implementation of many-to-many relation with extra columns, the other is UserChannel, review which fits our needs better and refactor later
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    @Enumerated(EnumType.STRING)
    private ParticipantType participantType;
}
