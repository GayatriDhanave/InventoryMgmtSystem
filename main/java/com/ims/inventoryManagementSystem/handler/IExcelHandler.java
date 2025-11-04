package com.ims.inventoryManagementSystem.handler;

import jakarta.servlet.ServletOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IExcelHandler {
    ResponseEntity<Map<String, Object>> processExcel (MultipartFile file, ServletOutputStream outputStream) throws IOException;

    void downloadErrorfile (ServletOutputStream outputStream) throws IOException;
}
