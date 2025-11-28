package com.ims.inventoryManagementSystem.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SessionSocketHandler extends TextWebSocketHandler {

    public static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        if (session.getPrincipal() != null) {
//            String email = session.getPrincipal().getName();
//            sessions.put(email, session);
//        }
//    }

//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        String email = null;
//        if (session.getPrincipal() != null) {
//            email = session.getPrincipal().getName();
//        } else {
//            Object attrEmail = session.getAttributes().get("email");
//            if (attrEmail != null) {
//                email = attrEmail.toString();
//            }
//        }
//
//        if (email != null) {
//            sessions.put(email, session);
//        } else {
//            // Optionally close connection if unauthenticated
//            // session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthenticated"));
//        }
//    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WS CONNECTED: principal = " +
                (session.getPrincipal() == null ? "NULL" : session.getPrincipal().getName()));

        if (session.getPrincipal() != null) {
            String email = session.getPrincipal().getName();
            sessions.put(email, session);
           log.info("WS STORED SESSION FOR: " + email);
        } else {
            log.info("WEBSOCKET PRINCIPAL IS NULL — SESSION NOT STORED");
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().remove(session);
    }

    public void forceLogout(String email) throws IOException {
        WebSocketSession existingSession = sessions.get(email);
        if (existingSession != null) {
            existingSession.sendMessage(new TextMessage("LOGOUT"));
            existingSession.close();
        }
    }
}
