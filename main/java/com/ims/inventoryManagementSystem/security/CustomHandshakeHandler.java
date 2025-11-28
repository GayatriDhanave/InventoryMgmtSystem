package com.ims.inventoryManagementSystem.security;

//package com.ims.inventoryManagementSystem.security;

import com.sun.security.auth.UserPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * Determine user for WebSocket handshake by reading token from query parameter "token".
 */
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    public CustomHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

//        // Try to get token from query param "token"
//        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletReq = ((ServletServerHttpRequest) request).getServletRequest();
//
//            // token can be sent as query param or as Authorization header
            String token = servletReq.getParameter("token");
//            if (token == null || token.isBlank()) {
//                String authHeader = servletReq.getHeader("Authorization");
//                if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                    token = authHeader.substring(7);
//                }
//            }
//
//            if (token != null && !token.isBlank()) {
//                try {
//                    if (jwtUtil.validateToken(token)) {
//                        String email = jwtUtil.extractEmail(token);
//                        // store email in attributes if you want
//                        attributes.put("email", email);
//                        return new UserPrincipal(email);
//                    }
//                } catch (Exception e) {
//                    // validateToken/extractEmail can throw; fallthrough and return null (unauthenticated)
//                    logger.warn("WebSocket handshake token invalid: {}");
//                }
//            }
//        }
//
//        // No valid token - return null -> Principal is null
//        return null;
        // inside determineUser(...) before returning Principal
        if (token != null && !token.isBlank()) {
            try {
                boolean valid=false;
                if("VALID".equals(jwtUtil.validateToken(token))) {
                    valid = true;
                }
                if (valid) {
                    String email = jwtUtil.extractEmail(token);
                    attributes.put("email", email);
                    logger.info("WebSocket handshake: valid token for email={}");
                    return new UserPrincipal(email);
                } else {
                    logger.warn("WebSocket handshake: token invalid or expired");
                }
            } catch (Exception e) {
                logger.warn("WebSocket handshake: error validating token - {}");
            }
        } else {
            logger.info("WebSocket handshake: no token provided in query or Authorization header");
        }
return null;
    }
}

