package org.duder.chat.dao.entity;

import lombok.Data;
import org.duder.chat.dao.entity.crossrefid.UserEventId;

import javax.persistence.*;

@Entity
@Data
@IdClass(UserEventId.class) // Id class required if class consists of 2 foreign keys making primary key
public class UserEvent {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_event")
    private Event event;

    // TODO maybe even new table with these data would be better?
    private boolean isUserHost;
    private boolean isUserParticipant;
    private boolean isUserInterested;
}
