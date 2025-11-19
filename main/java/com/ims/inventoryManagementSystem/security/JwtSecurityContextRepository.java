//package com.ims.inventoryManagementSystem.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.context.HttpRequestResponseHolder;
//import org.springframework.security.web.context.SecurityContextRepository;
//
//import java.util.List;
//
//public class JwtSecurityContextRepository implements SecurityContextRepository {
//
//    private final JwtUtil jwtUtil;
//
//    public JwtSecurityContextRepository(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public SecurityContext loadContext(HttpRequestResponseHolder holder) {
//        HttpServletRequest request = holder.getRequest();
//        String authHeader = request.getHeader("Authorization");
//
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            String email = jwtUtil.extractEmail(token);
//
//            if (email != null && jwtUtil.validateToken(token)) {
//                Authentication auth =
//                        new UsernamePasswordAuthenticationToken(email, null, List.of());
//                context.setAuthentication(auth);
//            }
//        }
//        return context;
//    }
//
//    @Override
//    public void saveContext(SecurityContext context, HttpServletRequest request,
//                            HttpServletResponse response) {
//        // Not needed for JWT
//    }
//
//    @Override
//    public boolean containsContext(HttpServletRequest request) {
//        return false;
//    }
//}
//
