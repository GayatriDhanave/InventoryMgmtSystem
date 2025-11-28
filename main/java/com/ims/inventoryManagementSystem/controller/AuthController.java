package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.entity.ActiveSession;
import com.ims.inventoryManagementSystem.entity.UserData;
import com.ims.inventoryManagementSystem.response.ResponseCode;
import com.ims.inventoryManagementSystem.exception.IMSException;
import com.ims.inventoryManagementSystem.repository.ActiveSessionRepository;
import com.ims.inventoryManagementSystem.repository.UserDataRepo;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import com.ims.inventoryManagementSystem.security.JwtUtil;
import com.ims.inventoryManagementSystem.security.SessionSocketHandler;
import com.ims.inventoryManagementSystem.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDataRepo repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    ActiveSessionRepository activeSessionRepository;

//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SessionSocketHandler socketHandler;

    /**
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserData user) {
        log.info("START :: CLASS :: AuthController :: METHOD :: register");
        if (repo.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists!");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        log.info("END :: CLASS :: AuthController :: METHOD :: register");
        return ResponseEntity.ok("Registration successful!");
    }

    /**
     *
     * @param loginData
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        log.info("START :: CLASS :: AuthController :: METHOD :: login");

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.get("email"), loginData.get("password"))
            );
            String header = request.getHeader("Authorization");
            String token = null;
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
                ActiveSession session = activeSessionRepository.getActiveSessionByEmailAndSessionId(loginData.get("email"), token);
                if (session == null) {
                    ActiveSession newSession = new ActiveSession();
                    newSession.setSessionId(token);
                    newSession.setEmail(loginData.get("email"));
                    newSession.setCreatedAt(LocalDateTime.now());
                    activeSessionRepository.save(newSession);
                } else {
//                    messagingTemplate.convertAndSend(
//                            "/topic/index.html/" + request.getHeader("email"),
//                            "FORCE_LOGOUT"
//                    );
                    activeSessionRepository.delete(session);
                }
            } else {
                 token = jwtUtil.generateToken(loginData.get("email"));
            ActiveSession session=activeSessionRepository.getActiveSessionByEmail(loginData.get("email"));
            if(session==null){
                ActiveSession newSession=new ActiveSession();
                newSession.setSessionId(token);
                newSession.setEmail(loginData.get("email"));
                newSession.setCreatedAt(LocalDateTime.now());
                newSession.setExpiresAt(LocalDateTime.now().plusMinutes(10));
                activeSessionRepository.save(newSession);
            } else {
                tokenBlacklistService.blacklistToken(session.getSessionId());
                session.setSessionId(token);
               activeSessionRepository.save(session);
            }
            }
            Map<String, String> response = new HashMap<>();
            socketHandler.forceLogout(loginData.get("email"));
            response.put("token", token);
            response.put("status", "success");
            response.put("email", loginData.get("email"));
            log.info("END :: CLASS :: AuthController :: METHOD :: login");
            return ResponseEntity.ok(Map.of("token", token, "status", "success", "email", loginData.get("email")));
        } catch (Exception e) {
            throw new IMSException(ResponseCode.BAD_CREDENTIALS, ResponseMessage.INVALID_DATA);
        }
    }

    /**
     *
     * @param header
     * @param email
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header, @RequestHeader("email") String email) {
        log.info("START :: CLASS :: AuthController :: METHOD :: logout");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            tokenBlacklistService.blacklistToken(token);
            ActiveSession session=activeSessionRepository.getActiveSessionByEmailAndSessionId(email, token);
            activeSessionRepository.delete(session);
        }
        log.info("END :: CLASS :: AuthController :: METHOD :: logout");
        return ResponseEntity.ok("Logged out successfully!");
    }
}

