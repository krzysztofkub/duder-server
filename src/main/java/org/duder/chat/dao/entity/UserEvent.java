package org.duder.chat.dao.entity;

import lombok.Data;
import org.duder.chat.dao.entity.id.UserEventId;

import javax.persistence.*;

@Entity
@Data
@AssociationOverrides({
        @AssociationOverride(
                name = "primaryKey.user",
                joinColumns = @JoinColumn(name = "id_user")),
        @AssociationOverride(
                name = "primaryKey.event",
                joinColumns = @JoinColumn(name = "id_event"))
})
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
