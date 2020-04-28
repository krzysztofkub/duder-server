package org.duder.security;

import org.duder.user.dao.User;
import org.duder.user.service.UserService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class WebSocketAuthenticatorService {
    private final UserService userService;

    public WebSocketAuthenticatorService(UserService userService) {
        this.userService = userService;
    }

    // This method MUST return a UsernamePasswordAuthenticationToken instance, the spring security chain is testing it with 'instanceof' later on.
    // So don't use a subclass of it or any other class
    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(final String  token) throws AuthenticationException {
        if (token == null || token.trim().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Session token was null or empty.");
        }
        Optional<User> userByToken = userService.getUserByToken(token);
        if (! userByToken.isPresent()) {
            throw new BadCredentialsException("Not existing session token " + token);
        }

        // null credentials, we do not pass the password along
        return new UsernamePasswordAuthenticationToken(
                userByToken.get().getLogin(),
                null,
                Collections.singleton((GrantedAuthority) () -> "USER") // MUST provide at least one role
        );
    }
}
