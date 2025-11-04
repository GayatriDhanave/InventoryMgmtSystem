package com.ims.inventoryManagementSystem.controller;

import com.ims.inventoryManagementSystem.entity.UserData;
import com.ims.inventoryManagementSystem.handler.IUserHandler;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserHandler userHandler;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> userLogin(
           @ApiParam(value="email", required=true) @RequestParam(value = "email") String email,
           @ApiParam(value="password", required=true)@RequestParam String password) {
        log.info("START :: CLASS :: UserController :: METHOD :: userLogin :: EMAIL :: {}", email);
        ResponseEntity<Map<String, Object>> response= userHandler.login(email, password);
        log.info("END :: CLASS :: UserController :: METHOD :: userLogin :: EMAIL :: {}", email);
        return  response;

    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> userRegister(@RequestBody UserData userData){
        log.info("START :: CLASS :: UserController :: METHOD :: userRegister :: EMAIL :: {}", userData.getEmail());
        ResponseEntity<Map<String, Object>> response= userHandler.register(userData);
        log.info("END :: CLASS :: UserController :: METHOD :: userRegister :: EMAIL :: {}", userData.getEmail());
        return response;
    }
}
