package org.duder.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp timestamp;
    private UserDto host;
}
