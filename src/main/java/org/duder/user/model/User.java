package org.duder.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.duder.chat.model.Message;
import org.duder.event.model.Hobby;
import org.hibernate.annotations.NaturalId;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(name = "user_hobby"
            , joinColumns = {@JoinColumn(name = "id_user")}
            , inverseJoinColumns = {@JoinColumn(name = "id_hobby")})
    private Set<Hobby> hobbies = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<UserChannel> userChannels = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserEvent> userEvents = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_friend"
            , joinColumns = {@JoinColumn(name = "id_user")}
            , inverseJoinColumns = {@JoinColumn(name = "id_friend")})
    private Set<User> friends = new HashSet<>();

    @OneToMany(
            mappedBy = "sender",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<UserFriendInvitation> sentInvitations = new HashSet<>();

    @OneToMany(
            mappedBy = "receiver",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<UserFriendInvitation> receivedInvitations = new HashSet<>();

    private String sessionToken;

    private String imageUrl;

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
