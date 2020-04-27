package org.duder.event.dao;

import lombok.*;
import org.duder.dto.event.HobbyName;
import org.duder.user.dao.User;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Hobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // It seems reasonable to have name unique here 
    @Column(unique = true, nullable = false)
    @NaturalId
    @Enumerated(EnumType.STRING)
    private HobbyName name;

    @ManyToMany(mappedBy = "hobbies")
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "hobbies")
    private Set<Event> events = new HashSet<>();
}
