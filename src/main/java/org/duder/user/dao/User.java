package org.duder.user.dao;

import lombok.*;
import org.duder.chat.dao.Message;
import org.duder.event.dao.Hobby;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We will be identifying user by login first without knowing id.
    // Login is (at least should be) immutable! Hence natural id.
    @Column(unique = true, nullable = false)
    @NaturalId
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @OneToMany(mappedBy = "author")
    private List<Message> messages = new ArrayList<>();

    // Areas of user interests.
    @ManyToMany
    @JoinTable(name = "user_hobby"
            , joinColumns = {@JoinColumn(name = "id_user")}
            , inverseJoinColumns = {@JoinColumn(name = "id_hobby")})
    private Set<Hobby> hobbies = new HashSet<>();

    // Channels user has access/belongs to.
    @OneToMany(mappedBy = "user")
    private List<UserChannel> userChannels = new ArrayList<>();

    // Events user is interested or has participated in.
    @OneToMany(mappedBy = "primaryKey.user", cascade = CascadeType.ALL)
    private List<UserEvent> userEvents = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_friend"
            , joinColumns = {@JoinColumn(name = "id_user")}
            , inverseJoinColumns = {@JoinColumn(name = "id_friend")})
    private Set<User> friends = new HashSet<>();

    @Transient
    private String sessionToken;

    // Equals & hash code by natural id - should be unique and not nullable
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
