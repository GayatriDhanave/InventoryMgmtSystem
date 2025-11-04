package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.entity.UserData;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IUserHandler {
    ResponseEntity<Map<String, Object>> login (String email, String password);

    ResponseEntity<Map<String, Object>> register (UserData userData);
}
