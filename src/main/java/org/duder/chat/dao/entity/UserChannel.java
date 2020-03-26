package org.duder.chat.dao.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.duder.chat.dao.entity.id.UserChannelId;

import javax.persistence.*;

@Entity
@Getter
@Setter
@IdClass(UserChannelId.class) // Id class required if class consists of 2 foreign keys making primary key
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
