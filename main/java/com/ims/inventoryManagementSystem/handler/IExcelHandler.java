package com.ims.inventoryManagementSystem.handler;

import com.ims.inventoryManagementSystem.dto.FileDto;
import com.ims.inventoryManagementSystem.entity.UserData;
import jakarta.servlet.ServletOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IExcelHandler {
//    ResponseEntity<Map<String, Object>> processExcel (MultipartFile file, ServletOutputStream outputStream) throws IOException;
//
//    void downloadErrorfile (ServletOutputStream outputStream) throws IOException;
//FileDto uploadFile (MultipartFile file, String email , int validCount, int invalidCount)  throws IOException, Exception;

    void generateExcel (ServletOutputStream outputStream, String email) throws IOException;

    CompletableFuture<Map<String, Object>> processExcel (MultipartFile file, String email) throws Exception;

    List<FileDto> getFileUploadHistory (String email);

    boolean existsByEmailAndStatusNot (UserData userByEmail, String string);
}
