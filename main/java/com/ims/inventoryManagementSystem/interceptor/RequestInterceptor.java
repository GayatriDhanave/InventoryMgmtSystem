//package com.ims.inventoryManagementSystem.interceptor;
//
//import com.ims.inventoryManagementSystem.security.JwtUtil;
//import com.ims.inventoryManagementSystem.security.TokenBlacklistService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Slf4j
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private TokenBlacklistService tokenBlacklistService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//
////        String path = request.getRequestURI();
//
//        // ALLOW PUBLIC ROUTES (VERY IMPORTANT)
////        if (
////                path.startsWith("/inventoryManagementSystem_war/auth") ||
////                        path.startsWith("/auth") ||
////                        path.endsWith("index.html") ||
////                        path.endsWith("index1.html") ||
////                        path.startsWith("/css") ||
////                        path.startsWith("/js") ||
////                        path.startsWith("/images") ||
////                        path.equals("/") ||
////                        path.equals("/inventoryManagementSystem_war/")
//        String path = request.getRequestURI();
//
//        if (
//                path.endsWith(".html") ||
//                        path.startsWith("/" + request.getContextPath() + "/") ||
//                        path.contains("index1.html") ||
//                        path.contains("dashboard.html") ||
//                        path.startsWith("/auth") ||
//                        path.startsWith("/css") ||
//                        path.startsWith("/js") ||
//                        path.startsWith("/images") ||
//                        path.startsWith("/login") ||
//                        path.startsWith("/register")
//        ) {
//            return true;
//        }
//
//
//        // CHECK TOKEN
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Missing Authorization header");
//            return false;
//        }
//
//        String token = authHeader.substring(7);
//        String email;
//
//        try {
//            email = jwtUtil.extractEmail(token);
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid token");
//            return false;
//        }
//
//        if (!jwtUtil.validateToken(token)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Expired Token");
//            return false;
//        }
//
//        if (tokenBlacklistService.isTokenBlacklisted(token)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Blacklisted Token");
//            return false;
//        }
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//        UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//
//        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        return true;
//    }
//}
