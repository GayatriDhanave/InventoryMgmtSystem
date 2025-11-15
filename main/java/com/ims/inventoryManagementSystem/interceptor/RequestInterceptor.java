package com.ims.inventoryManagementSystem.interceptor;


import com.ims.inventoryManagementSystem.entity.ActiveSession;
import com.ims.inventoryManagementSystem.repository.ActiveSessionRepository;
import com.ims.inventoryManagementSystem.security.JwtUtil;
import com.ims.inventoryManagementSystem.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

//@Slf4j
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
////
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private TokenBlacklistService tokenBlacklistService;
////
////    @Autowired
////    ActiveSessionRepository activeSessionRepository;
////
////
////    @Override
////    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
////            throws Exception {
////        String email = request.getHeader("email");
////        String uniqueId = request.getHeader("sessionId");
////        if (email == null || uniqueId == null) {
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            response.getWriter().write("Missing session data");
////            return false;
////        }
////
////        ActiveSession session = activeSessionRepository.findByEmail(email);
////        if(session==null){
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            response.getWriter().write("Session expired ");
//////            activeSessionRepository.delete(session);
////            return false;
////        }
////        if (session != null && !session.getSessionId().equals(uniqueId)) {
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            response.getWriter().write("Session expired or invalid");
////            activeSessionRepository.delete(session);
////            return false;
////        }
////        if (Duration.between(session.getCreatedAt().toInstant(), Instant.now()).toMinutes() > 10) {
////            activeSessionRepository.deleteByEmail(email);
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////            response.getWriter().write("Session expired, please relogin");
////            return false;
////        }
////
////        return true;
////
////    }
////
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        String authHeader = request.getHeader("Authorization");
//
//        String token = null;
//        String email = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            try {
//                email = jwtUtil.extractEmail(token);
//            } catch (Exception e) {
//                log.error("Invalid JWT token", e);
//            }
//        }
//
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            if (jwtUtil.validateToken(token) && !tokenBlacklistService.isTokenBlacklisted(token)) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//                return true;
//            }
//        }
//        return false;
//    }
//
//}
