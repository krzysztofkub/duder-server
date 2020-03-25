package org.duder.chat.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "event_hobby"
            , joinColumns = {@JoinColumn(name = "id_event")}
            , inverseJoinColumns = {@JoinColumn(name = "id_hobby")})
    private Set<Hobby> hobbies = new HashSet<>();

    // Users participating/interested in the event
    @OneToMany(mappedBy = "event")
    private Set<UserEvent> eventUsers = new HashSet<>();
}
