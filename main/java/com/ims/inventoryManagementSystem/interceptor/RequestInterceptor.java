//package com.ims.inventoryManagementSystem.interceptor;
//
//
//import com.ims.inventoryManagementSystem.entity.ActiveSession;
//import com.ims.inventoryManagementSystem.repository.ActiveSessionRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.time.Duration;
//import java.time.Instant;
//
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    ActiveSessionRepository activeSessionRepository;
//
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        String email = request.getHeader("email");
//        String uniqueId = request.getHeader("sessionId");
//        if (email == null || uniqueId == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Missing session data");
//            return false;
//        }
//
//        ActiveSession session = activeSessionRepository.findByEmail(email);
//        if(session==null){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Session expired ");
////            activeSessionRepository.delete(session);
//            return false;
//        }
//        if (session != null && !session.getSessionId().equals(uniqueId)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Session expired or invalid");
//            activeSessionRepository.delete(session);
//            return false;
//        }
//        if (Duration.between(session.getCreatedAt().toInstant(), Instant.now()).toMinutes() > 10) {
//            activeSessionRepository.deleteByEmail(email);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Session expired, please relogin");
//            return false;
//        }
//
//        return true;
//
//    }
//
//}
