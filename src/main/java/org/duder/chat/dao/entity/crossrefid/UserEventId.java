package org.duder.chat.dao.entity.crossrefid;

import org.duder.chat.dao.entity.Event;
import org.duder.chat.dao.entity.User;

import java.io.Serializable;
import java.util.Objects;

/*
Cross reference id for user-event relation
- must implement Serializable
- must override equals and hashCode
 */
public class UserEventId implements Serializable {

    private static final long serialVersionUID = 7336041242493052291L;

    private User user;
    private Event event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEventId that = (UserEventId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, event);
    }

}
