package org.duder.event.repository;

import org.duder.dto.event.HobbyName;
import org.duder.event.model.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Set<Hobby> findAllByNameIn(Set<HobbyName> names);
}
