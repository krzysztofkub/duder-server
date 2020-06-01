package org.duder.security.websocket;

import org.duder.security.SessionHolder;
import org.duder.user.model.User;
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
    private final SessionHolder tokenHolder;

    public WebSocketAuthenticatorService(UserService userService, SessionHolder tokenHolder) {
        this.userService = userService;
        this.tokenHolder = tokenHolder;
    }

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(final String token) throws AuthenticationException {
        if (token == null || token.trim().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Session token was null or empty.");
        }
        Optional<User> userByToken = getValidatedUser(token);

        tokenHolder.sessionToken = token;

        return new UsernamePasswordAuthenticationToken(
                userByToken.get().getLogin(),
                null,
                Collections.singleton((GrantedAuthority) () -> "USER") // MUST provide at least one role
        );
    }

    private Optional<User> getValidatedUser(String token) {
        Optional<User> userByToken = userService.getUserByToken(token);
        if (!userByToken.isPresent()) {
            throw new BadCredentialsException("Not existing session token " + token);
        }
        return userByToken;
    }
}
