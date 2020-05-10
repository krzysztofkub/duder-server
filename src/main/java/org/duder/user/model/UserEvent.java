package org.duder.user.model;

import lombok.Getter;
import lombok.Setter;
import org.duder.event.model.Event;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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
