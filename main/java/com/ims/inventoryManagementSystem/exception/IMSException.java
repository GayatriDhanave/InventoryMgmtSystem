package com.ims.inventoryManagementSystem.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IMSException extends RuntimeException{
    String code;
    String msg;

    public IMSException (String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
