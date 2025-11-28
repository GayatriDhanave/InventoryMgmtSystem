package com.ims.inventoryManagementSystem.config;

import com.ims.inventoryManagementSystem.security.SessionSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*")
//                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic");
//        registry.setApplicationDestinationPrefixes("/app");
//    }
//}
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(new SessionSocketHandler(), "/session")
//                .setAllowedOrigins("*");
//    }
//}

//package com.ims.inventoryManagementSystem.config;

import com.ims.inventoryManagementSystem.security.CustomHandshakeHandler;
import com.ims.inventoryManagementSystem.security.SessionSocketHandler;
import com.ims.inventoryManagementSystem.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SessionSocketHandler sessionSocketHandler;
    private final JwtUtil jwtUtil;

    @Autowired
    public WebSocketConfig(SessionSocketHandler sessionSocketHandler, JwtUtil jwtUtil) {
        this.sessionSocketHandler = sessionSocketHandler;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Use a custom handshake handler that creates a Principal from the token
        CustomHandshakeHandler handshakeHandler = new CustomHandshakeHandler(jwtUtil);

        registry.addHandler(sessionSocketHandler, "/session")
                .setAllowedOrigins("*")
                .setHandshakeHandler(handshakeHandler);
    }
}


