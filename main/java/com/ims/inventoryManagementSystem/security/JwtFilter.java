//package com.ims.inventoryManagementSystem.security;
//
//import com.ims.inventoryManagementSystem.repository.ActiveSessionRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
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
//    @Autowired
//    ActiveSessionRepository activeSessionRepository;
//
//@Override
//protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//        throws ServletException, IOException {
//
//    String authHeader = request.getHeader("Authorization");
//
//    String token = null;
//    String email = null;
//
//    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//        token = authHeader.substring(7);
//        try {
//            email = jwtUtil.extractEmail(token);
//        } catch (Exception e) {
//            logger.error("Invalid JWT token", e);
//        }
//    }
//
//    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        if (jwtUtil.validateToken(token) && !tokenBlacklistService.isTokenBlacklisted(token)) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//            UsernamePasswordAuthenticationToken authToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authToken);
////            logger.info("Auth header: {}", authHeader);
////            logger.info("Token: {}", token);
////            logger.info("Email: {}", email);
////            logger.info("Token valid: {}", jwtUtil.validateToken(token));
////            logger.info("Blacklisted: {}", tokenBlacklistService.isTokenBlacklisted(token));
//        } else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("{\"error\":\"Session expired\"}");
////            response.getWriter().write("error");
//        }
//    }
//    chain.doFilter(request, response);
//}
//}