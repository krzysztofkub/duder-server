package org.duder.user.dao;

import lombok.Getter;
import lombok.Setter;
import org.duder.event.dao.Event;
import org.duder.user.dao.id.UserEventId;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AssociationOverrides({
        @AssociationOverride(
                name = "primaryKey.user",
                joinColumns = @JoinColumn(name = "id_user")),
        @AssociationOverride(
                name = "primaryKey.event",
                joinColumns = @JoinColumn(name = "id_event"))
})
// TODO this is one implementation of many-to-many relation with extra columns, the other is UserChannel, review which fits our needs better and refactor later
public class UserEvent {

    // Composite id key
    @EmbeddedId
    private UserEventId primaryKey;

    @Transient
    public User getUser() {
        return primaryKey.getUser();
    }

    @Transient
    public Event getEvent() {
        return primaryKey.getEvent();
    }

    @Enumerated(EnumType.STRING)
    private ParticipantType participantType;
}
