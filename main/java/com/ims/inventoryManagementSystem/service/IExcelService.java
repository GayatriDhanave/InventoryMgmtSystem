package com.ims.inventoryManagementSystem.service;

import com.ims.inventoryManagementSystem.dto.FileDto;
import jakarta.servlet.ServletOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface IExcelService {
    FileDto uploadFile (MultipartFile file, String email , int validCount, int invalidCount)  throws IOException, Exception;

    void generateExcel (ServletOutputStream outputStream, String email) throws IOException;

    Map<String, Object> processExcel (MultipartFile file, String email) throws Exception;

    List<FileDto> getFileUploadHistory (String email);
}
