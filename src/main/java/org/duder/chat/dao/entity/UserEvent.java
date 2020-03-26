package org.duder.chat.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.duder.chat.dao.entity.id.UserEventId;

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

    // TODO maybe even new table with these data would be better?
    private boolean isUserHost;
    private boolean isUserParticipant;
    private boolean isUserInterested;
}
