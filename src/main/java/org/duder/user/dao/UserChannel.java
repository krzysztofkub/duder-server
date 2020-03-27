package org.duder.user.dao;

import lombok.Getter;
import lombok.Setter;
import org.duder.chat.dao.Channel;
import org.duder.user.dao.id.UserChannelId;

import javax.persistence.*;

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
