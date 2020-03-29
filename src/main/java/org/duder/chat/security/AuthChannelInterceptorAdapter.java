package org.duder.chat.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {
    private static final String USERNAME_HEADER = "login";
    private static final String PASSWORD_HEADER = "password";
    private final WebSocketAuthenticatorService webSocketAuthenticatorService;

    public AuthChannelInterceptorAdapter(WebSocketAuthenticatorService webSocketAuthenticatorService) {
        this.webSocketAuthenticatorService = webSocketAuthenticatorService;
    }

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) throws AuthenticationException {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String username = accessor.getFirstNativeHeader(USERNAME_HEADER);
            final String password = accessor.getFirstNativeHeader(PASSWORD_HEADER);

            try {
                final UsernamePasswordAuthenticationToken user = webSocketAuthenticatorService.getAuthenticatedOrFail(username, password);
                accessor.setUser(user);

            } catch (BadCredentialsException e) {
                accessor.addNativeHeader("code", "BAD_CREDENTIALS");

                Map<String, Object> headers = new HashMap<>();
                headers.put("stompCommand", StompCommand.ERROR);
                headers.put("code", "BAD_CREDENTIALS");
                return new GenericMessage<>(message.getPayload(), headers);
            }
        }
        return message;
    }
}
