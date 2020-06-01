package org.duder.security.websocket;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        // You can customize your authorization mapping here.
        messages.anyMessage().authenticated();
    }

    // TODO: For test purpose (and simplicity) i disabled CSRF, we should re-enable this and provide a CRSF endpoint
    //  also most likely we have to change webSecurityConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
