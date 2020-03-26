package org.duder.chat.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "event_hobby"
            , joinColumns = {@JoinColumn(name = "id_event")}
            , inverseJoinColumns = {@JoinColumn(name = "id_hobby")})
    private Set<Hobby> hobbies = new HashSet<>();

    // Users participating/interested in the event
    @OneToMany(mappedBy = "primaryKey.event", cascade = CascadeType.ALL)
    private List<UserEvent> eventUsers = new ArrayList<>();

}
