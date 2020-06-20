package org.duder.security.http;

import org.duder.security.SessionHolder;
import org.duder.security.exception.UserNotFoundException;
import org.duder.user.model.User;
import org.duder.user.service.SessionService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthencticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final SessionService sessionService;
    private final SessionHolder sessionHolder;

    public AuthencticationProvider(SessionService sessionService, SessionHolder sessionHolder) {
        this.sessionService = sessionService;
        this.sessionHolder = sessionHolder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        //
    }

    @Override
    protected UserDetails retrieveUser(String s, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        Object token = usernamePasswordAuthenticationToken.getCredentials();
        return Optional
                .ofNullable(token)
                .map(String::valueOf)
                .flatMap(this::processUser)
                .map(user -> new org.springframework.security.core.userdetails.User(user.getNickname(), "password",
                        true,
                        true,
                        true,
                        true,
                        AuthorityUtils.createAuthorityList("USER")
                ))
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with provided token " + token));
    }

    private Optional<User> processUser(String token) {
        Optional<User> userByToken = sessionService.getUserByToken(token);
        userByToken.ifPresent(u -> sessionHolder.user = userByToken.get());
        return userByToken;
    }
}
