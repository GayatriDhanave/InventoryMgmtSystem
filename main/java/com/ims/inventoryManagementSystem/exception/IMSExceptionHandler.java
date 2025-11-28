package com.ims.inventoryManagementSystem.exception;

import com.ims.inventoryManagementSystem.response.ResponseHandler;
import com.ims.inventoryManagementSystem.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IMSExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleException(IMSException e){
        return new ResponseEntity<>(ResponseHandler.error(e.getMsg()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
