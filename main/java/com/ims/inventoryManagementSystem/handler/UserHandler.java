package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.entity.ActiveSession;
import com.ims.inventoryManagementSystem.entity.UserData;
import com.ims.inventoryManagementSystem.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class UserHandler implements  IUserHandler {

    @Autowired
    private IService userService;
//    @Autowired
//    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    @Override
    public ResponseEntity<Map<String, Object>> login (String email, String password) {
        log.info("START :: CLASS :: UserHandler :: METHOD :: EMAIL :: {}", email);
        Map<String, Object> result=new HashMap<>();
        UserData userData= userService.getUserByEmail(email);
        String sessionId= UUID.randomUUID().toString();
        ActiveSession activeSession=new ActiveSession();
        activeSession.setSessionId(sessionId);
        activeSession.setCreatedAt(new Date());
        activeSession.setEmail(email);
        userService.addSession(activeSession);

        if(userData.getPassword().equals(password)){
            result.put("email", userData.getEmail());
            result.put("success", true);
            result.put("sessionId", sessionId);
        }
        log.info("END :: CLASS :: UserHandler :: METHOD :: login :: EMAIL :: {}", email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> register (UserData userData) {
        log.info("START :: CLASS :: UserHandler :: METHOD :: EMAIL :: {}", userData.getEmail());
            String password=userData.getPassword();
//            userData.setPassword(passwordEncoder.encode(password));
            userService.addUser(userData);
        log.info("END :: CLASS :: UserHandler :: METHOD :: EMAIL :: {}", userData.getEmail());
        Map<String, Object> result=new HashMap<>();
        result.put("success", true);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
