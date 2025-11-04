package com.ims.inventoryManagementSystem.response;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResponseHandler {

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    public static Map<String, Object> success(Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status-code", 1);
        map.put("status", STATUS_SUCCESS);
        map.put("data", data);
        return map;
    }

    public static Map<String, Object> error(Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status-code", 0);
        map.put("status", STATUS_ERROR);
        map.put("data", data);
        return map;
    }
}
