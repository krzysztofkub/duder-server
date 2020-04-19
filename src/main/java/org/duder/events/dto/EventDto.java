package org.duder.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.duder.events.model.HobbyName;
import org.duder.user.dto.UserDto;

import java.sql.Timestamp;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class EventDto {
    private String name;
    private Set<HobbyName> hobbies;
    private int numberOfParticipants;
    private Timestamp timestamp;
    private UserDto host;
}
