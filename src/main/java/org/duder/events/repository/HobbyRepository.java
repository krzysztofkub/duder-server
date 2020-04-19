package org.duder.events.repository;

import org.duder.events.dao.Hobby;
import org.duder.events.model.HobbyName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Set<Hobby> findAllByNameIn(Set<HobbyName> names);
}
