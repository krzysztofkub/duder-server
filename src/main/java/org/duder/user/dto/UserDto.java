package org.duder.user.dto;

import lombok.Builder;
import lombok.Data;
import org.duder.user.dao.User;

@Data
@Builder
public class UserDto {
    private String login;
    private String nickname;
    private String password;
    private String sessionToken;
}
