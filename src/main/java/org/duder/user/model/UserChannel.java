package org.duder.user.model;

import lombok.Getter;
import lombok.Setter;
import org.duder.chat.model.Channel;
import org.duder.user.model.id.UserChannelId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@IdClass(UserChannelId.class) // Id class required if class consists of 2 foreign keys making primary key
// TODO this is one implementation of many-to-many relation with extra columns, the other is UserEvent, review which fits our needs better and refactor later
public class UserChannel {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_channel")
    private Channel channel;

    // TODO maybe even new table with these data would be better?
    private boolean isUserAdmin;

}
