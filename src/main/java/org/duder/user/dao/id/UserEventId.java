package org.duder.user.dao.id;

import lombok.Data;
import org.duder.user.dao.Event;
import org.duder.user.dao.User;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

/*
Cross reference id for user-event relation
- must implement Serializable
- must override equals and hashCode
 */
@Embeddable
@Data
public class UserEventId implements Serializable {

    private static final long serialVersionUID = 7336041242493052291L;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
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
