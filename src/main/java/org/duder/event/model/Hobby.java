package org.duder.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.duder.dto.event.HobbyName;
import org.duder.user.model.User;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
