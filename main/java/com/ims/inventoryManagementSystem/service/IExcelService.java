package com.ims.inventoryManagementSystem.service;

import jakarta.servlet.ServletOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IExcelService {
    Map<String, Object> processExcel (MultipartFile file, ServletOutputStream outputStream) throws IOException;

    void generateExcel (ServletOutputStream outputStream) throws IOException;
}
