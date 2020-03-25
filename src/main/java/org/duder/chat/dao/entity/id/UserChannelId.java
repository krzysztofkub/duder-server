package org.duder.chat.dao.entity.id;

import org.duder.chat.dao.entity.Channel;
import org.duder.chat.dao.entity.User;

import java.io.Serializable;
import java.util.Objects;

/*
Cross reference id for user-event relation
- must implement Serializable
- must override equals and hashCode
 */
public class UserChannelId implements Serializable {

    private static final long serialVersionUID = 9105621402213818500L;

    private User user;
    private Channel channel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserChannelId that = (UserChannelId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, channel);
    }
}
