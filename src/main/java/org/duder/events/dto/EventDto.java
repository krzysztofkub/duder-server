package org.duder.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.duder.user.dao.Hobby;
import org.duder.user.dao.UserEvent;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class EventDto {
    private String name;
    private Set<String> hobbies;
    private int numberOfParticipants;
    private Timestamp timestamp;
}
