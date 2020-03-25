package org.duder.chat.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // We will be identifying user by login first without knowing id.
    // Login is (at least should be) immutable! Hence natural id.
    @Column(unique = true)
    @NaturalId
    private String login;

    private String password;
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
}
