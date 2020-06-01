package org.duder.security;

import org.duder.user.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class SessionHolder {
    //TODO USE SPRING SECURITY SESSION HOLDER
    // https://www.baeldung.com/get-user-in-spring-security
    public String sessionToken;
    public User user;
}
