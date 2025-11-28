package com.ims.inventoryManagementSystem.interceptor;
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




import com.ims.inventoryManagementSystem.entity.ActiveSession;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.repository.ActiveSessionRepository;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import com.ims.inventoryManagementSystem.security.JwtUtil;
import com.ims.inventoryManagementSystem.service.IService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;


@Slf4j
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
//    @Autowired
//    private IService userSessionService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    /**
//     * Intercepts HTTP requests to perform pre-processing and authorization checks for requests
//     * targeting resources with a URI that starts with "/crm/".
//     * <ul>
//     * - Validates the presence of a bearer token in the "Authorization" header.
//     * - Checks if the token has expired and deletes the session in case of expiration.
//     * - Verifies the presence of an active session associated with the token and user email.
//     * - Extracts user role, ID, and email from the token and sets them as attributes in the request.
//     * </ul>
//     *
//     * Failure to meet these conditions will result in an HTTP status code of 401 Unauthorized or other
//     * relevant error codes.
//     *
//     * @param request  the HttpServletRequest object of the ongoing request
//     * @param response the HttpServletResponse object for modifying the response
//     * @param handler  the handler object to execute (can be used to retrieve additional metadata if needed)
//     * @return true if the request passes all validations and the handler can proceed, otherwise false
//     * @throws Exception if an underlying error or issue occurs during processing
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("Enter: RequestInterceptor.preHandle");
//        String uri = request.getRequestURI();
//
//        if (uri.startsWith("/crm/")) {
//            log.info("Request URI: {}", uri);
//            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//                return true;
//            }
//
//            String token = request.getHeader("Authorization");
//            log.info("Token: {}", token);
//
//            if (token == null || !token.startsWith("Bearer ")) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return false;
//            }
//
//            token = token.substring(7).trim();
//
//            String email;
//
//            // ---- STEP 1: Extract email safely ----
//            try {
//                email = jwtUtil.getEmail(token);
//            } catch (ExpiredJwtException ex) {
//                email = ex.getClaims().getSubject();   // email from expired token
//
//                // token expired → delete session → return error
//                userSessionService.deleteSessionByEmail(email);
//
//                response.setStatus(Integer.parseInt(ResponseCode.SESSION_EXPIRED));
//                log.error("Exit: RequestInterceptor.preHandle with error: Session expired for user: {}", email);
//                throw new IMSException(ResponseCode.SESSION_EXPIRED, ResponseMessage.SESSION_EXPIRED);
//            }
//            //validate token
//            if(jwtUtil.validateToken(token).isEmpty() || jwtUtil.validateToken(token)== null){
//                throw new Exception(ResponseCode.INVALID_TOKEN);
//            }
//            // ---- STEP 2: Check expiry normally ----
//            if (jwtUtil.isTokenExpired(token)) {
//                userSessionService.deleteSessionByEmail(email);
//
//                response.setStatus(Integer.parseInt(ResponseCode.SESSION_EXPIRED));
//                log.error("Exit: RequestInterceptor.preHandle with error: Session expired for user: {}", email);
//                throw new IMSException(ResponseCode.SESSION_EXPIRED, ResponseMessage.SESSION_EXPIRED);
//            }
//
//            // ---- STEP 3: Validate active session ----
//            if (!userSessionService.findSessionByToken(token, email)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                log.error("Exit: RequestInterceptor.preHandle with error: Another session active for user: {}", email);
//                throw new IMSException(ResponseCode.SESSION_NOT_FOUND, ResponseMessage.SESSION_NOT_FOUND);
//            }
//
//            // ---- STEP 4: Extract and set user details ----
////            String role = jwtUtil.getRole(token);
////            if (role == null) {
////                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////                log.error("Exit: RequestInterceptor.preHandle with error: User not found for token: {}", token);
////                throw new Exception(ResponseCode.USER_NOT_FOUND);
////            }
//
//            Long id = jwtUtil.getId(token);
////            request.setAttribute("role", role);
//            request.setAttribute("userId", id);
//            request.setAttribute("email", email);
//        }
//
//        return true;
//    }

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Autowired
    private ActiveSessionRepository sessionRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = header.substring(7);

        ActiveSession session = (ActiveSession) sessionRepo.findBySessionId(token);

        if (session == null || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true; // allow request
    }
}



//}